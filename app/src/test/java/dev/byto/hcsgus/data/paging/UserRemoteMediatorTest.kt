package dev.byto.hcsgus.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.dao.RemoteKeysDao
import dev.byto.hcsgus.data.local.dao.UserDao
import dev.byto.hcsgus.data.local.entity.RemoteKeysEntity
import dev.byto.hcsgus.data.local.entity.UserEntity
import dev.byto.hcsgus.data.mapper.toEntity // For UserDto -> UserEntity
import dev.byto.hcsgus.data.remote.api.ApiService
import dev.byto.hcsgus.data.remote.dto.UserDto
import dev.byto.hcsgus.util.orZero
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediatorTest {

    private lateinit var apiService: ApiService
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var remoteKeysDao: RemoteKeysDao
    private lateinit var remoteMediator: UserRemoteMediator

    private val pageSize = 20

    private fun createUserDto(id: Int, login: String): UserDto {
        return UserDto(id = id, login = login, avatarUrl = "avatar_url_$id")
    }

    private fun createUserEntity(id: Int, login: String): UserEntity {
        // Assuming UserDto.toEntity() is the conversion path
        return createUserDto(id, login).toEntity()
    }

    @Before
    fun setUp() {
        apiService = mockk()
        database = mockk()
        userDao = mockk(relaxUnitFun = true) // For clearAll, upsertAll
        remoteKeysDao = mockk(relaxUnitFun = true) // For clearRemoteKeys, insertAll, remoteKeysUserId

        every { database.userDao() } returns userDao
        every { database.remoteKeysDao() } returns remoteKeysDao

        // Mock the withTransaction block to execute the lambda passed to it
        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        remoteMediator = UserRemoteMediator(apiService, database)
    }

    private fun createPagingState(lastItem: UserEntity? = null): PagingState<Int, UserEntity> {
        val pages = if (lastItem != null) {
            listOf(PagingSource.LoadResult.Page(data = listOf(lastItem), prevKey = null, nextKey = null))
        } else {
            emptyList()
        }
        return PagingState(
            pages = pages,
            anchorPosition = null,
            config = PagingConfig(pageSize = pageSize),
            leadingPlaceholderCount = 0
        )
    }

    @Test
    fun `load - REFRESH success - clears db, fetches new data, inserts, returns Success`() = runTest {
        val userDtoList = listOf(createUserDto(1, "user1"), createUserDto(2, "user2"))
        coEvery { apiService.getUsers(since = 0, perPage = pageSize) } returns userDtoList

        val pagingState = createPagingState()
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify(exactly = 1) { database.withTransaction(any()) }
        coVerify(exactly = 1) { userDao.clearAll() }
        coVerify(exactly = 1) { remoteKeysDao.clearRemoteKeys() }

        val expectedUserEntities = userDtoList.map { it.toEntity() }
        coVerify(exactly = 1) { userDao.upsertAll(expectedUserEntities) }

        val expectedRemoteKeys = userDtoList.map {
            RemoteKeysEntity(userId = it.id.orZero(), prevKey = null, nextKey = it.id)
        }
        coVerify(exactly = 1) { remoteKeysDao.insertAll(expectedRemoteKeys) }
    }

    @Test
    fun `load - REFRESH success - empty api response - returns Success and endOfPagination true`() = runTest {
        coEvery { apiService.getUsers(since = 0, perPage = pageSize) } returns emptyList()

        val pagingState = createPagingState()
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify(exactly = 1) { database.withTransaction(any()) }
        coVerify(exactly = 1) { userDao.clearAll() }
        coVerify(exactly = 1) { remoteKeysDao.clearRemoteKeys() }
        coVerify(exactly = 1) { userDao.upsertAll(emptyList()) }
        coVerify(exactly = 1) { remoteKeysDao.insertAll(emptyList()) }
    }

    @Test
    fun `load - REFRESH api IOException - returns Error`() = runTest {
        val exception = IOException("Network error")
        coEvery { apiService.getUsers(any(), any()) } throws exception

        val pagingState = createPagingState()
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals(exception, (result as RemoteMediator.MediatorResult.Error).throwable)
        coVerify(exactly = 0) { database.withTransaction(any()) } // Transaction should not be started
    }

    @Test
    fun `load - REFRESH api HttpException - returns Error`() = runTest {
        val exception = HttpException(Response.error<Any>(500, mockk(relaxed = true)))
        coEvery { apiService.getUsers(any(), any()) } throws exception

        val pagingState = createPagingState()
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals(exception, (result as RemoteMediator.MediatorResult.Error).throwable)
        coVerify(exactly = 0) { database.withTransaction(any()) }
    }

    @Test
    fun `load - PREPEND - returns Success and endOfPagination true`() = runTest {
        val pagingState = createPagingState()
        val result = remoteMediator.load(LoadType.PREPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify(exactly = 0) { apiService.getUsers(any(), any()) } // No API call for PREPEND
    }

    // --- APPEND Tests --- (More will be added here)
    @Test
    fun `load - APPEND success - remote key found, fetches new data, inserts, returns Success`() = runTest {
        val lastUserEntity = createUserEntity(10, "user10")
        val remoteKeyForLastUser = RemoteKeysEntity(userId = lastUserEntity.id, prevKey = null, nextKey = lastUserEntity.id)
        coEvery { remoteKeysDao.remoteKeysUserId(lastUserEntity.id) } returns remoteKeyForLastUser

        val newUserDtoList = listOf(createUserDto(11, "user11"), createUserDto(12, "user12"))
        coEvery { apiService.getUsers(since = remoteKeyForLastUser.nextKey!!, perPage = pageSize) } returns newUserDtoList

        val pagingState = createPagingState(lastItem = lastUserEntity)
        val result = remoteMediator.load(LoadType.APPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify(exactly = 1) { database.withTransaction(any()) }
        // For APPEND, clearAll should NOT be called
        coVerify(exactly = 0) { userDao.clearAll() }
        coVerify(exactly = 0) { remoteKeysDao.clearRemoteKeys() }

        val expectedNewUserEntities = newUserDtoList.map { it.toEntity() }
        coVerify(exactly = 1) { userDao.upsertAll(expectedNewUserEntities) }

        val expectedNewRemoteKeys = newUserDtoList.map {
            RemoteKeysEntity(userId = it.id.orZero(), prevKey = null, nextKey = it.id)
        }
        coVerify(exactly = 1) { remoteKeysDao.insertAll(expectedNewRemoteKeys) }
    }

     @Test
    fun `load - APPEND success - no remote key for last item - returns Success and endOfPagination true`() = runTest {
        val lastUserEntity = createUserEntity(20, "user20")
        coEvery { remoteKeysDao.remoteKeysUserId(lastUserEntity.id) } returns null // No key found

        val pagingState = createPagingState(lastItem = lastUserEntity)
        val result = remoteMediator.load(LoadType.APPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify(exactly = 0) { apiService.getUsers(any(), any()) } // No API call
        coVerify(exactly = 0) { database.withTransaction(any()) } // No transaction
    }

    @Test
    fun `load - APPEND success - remote key nextKey is null - returns Success and endOfPagination true`() = runTest {
        val lastUserEntity = createUserEntity(21, "user21")
        val remoteKeyForLastUser = RemoteKeysEntity(userId = lastUserEntity.id, prevKey = null, nextKey = null) // nextKey is null
        coEvery { remoteKeysDao.remoteKeysUserId(lastUserEntity.id) } returns remoteKeyForLastUser

        val pagingState = createPagingState(lastItem = lastUserEntity)
        val result = remoteMediator.load(LoadType.APPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify(exactly = 0) { apiService.getUsers(any(), any()) } // No API call
        coVerify(exactly = 0) { database.withTransaction(any()) } // No transaction
    }

    @Test
    fun `load - APPEND success - api returns empty list - returns Success and endOfPagination true`() = runTest {
        val lastUserEntity = createUserEntity(22, "user22")
        val remoteKeyForLastUser = RemoteKeysEntity(userId = lastUserEntity.id, prevKey = null, nextKey = lastUserEntity.id)
        coEvery { remoteKeysDao.remoteKeysUserId(lastUserEntity.id) } returns remoteKeyForLastUser
        coEvery { apiService.getUsers(since = remoteKeyForLastUser.nextKey!!, perPage = pageSize) } returns emptyList()

        val pagingState = createPagingState(lastItem = lastUserEntity)
        val result = remoteMediator.load(LoadType.APPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify(exactly = 1) { database.withTransaction(any()) }
        coVerify(exactly = 1) { userDao.upsertAll(emptyList()) }
        coVerify(exactly = 1) { remoteKeysDao.insertAll(emptyList()) }
    }

    @Test
    fun `load - APPEND api IOException - returns Error`() = runTest {
        val lastUserEntity = createUserEntity(23, "user23")
        val remoteKeyForLastUser = RemoteKeysEntity(userId = lastUserEntity.id, prevKey = null, nextKey = lastUserEntity.id)
        coEvery { remoteKeysDao.remoteKeysUserId(lastUserEntity.id) } returns remoteKeyForLastUser

        val exception = IOException("Network error on append")
        coEvery { apiService.getUsers(since = remoteKeyForLastUser.nextKey!!, perPage = pageSize) } throws exception

        val pagingState = createPagingState(lastItem = lastUserEntity)
        val result = remoteMediator.load(LoadType.APPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals(exception, (result as RemoteMediator.MediatorResult.Error).throwable)
        coVerify(exactly = 0) { database.withTransaction(any()) } // No transaction on error before it
    }
}
