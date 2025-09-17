package dev.byto.hcsgus.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.mapper.toDomainModel
import dev.byto.hcsgus.data.paging.UserRemoteMediator
import dev.byto.hcsgus.data.paging.UserSearchPagingSource
import dev.byto.hcsgus.data.remote.api.ApiService
import dev.byto.hcsgus.domain.model.User
import dev.byto.hcsgus.domain.model.UserDetail
import dev.byto.hcsgus.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: AppDatabase
) : UserRepository {

    /**
     * Implements the offline-first user list.
     * The Pager uses the RemoteMediator to fetch from the network and save to Room,
     * while the PagingSource reads from Room to supply the UI.
     */
    @OptIn(ExperimentalPagingApi::class)
    override fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = 30),
            remoteMediator = UserRemoteMediator(apiService, database),
            pagingSourceFactory = { database.userDao().pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    override fun searchUsers(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = 30),
            pagingSourceFactory = { UserSearchPagingSource(apiService, query) }
        ).flow
    }

    override suspend fun getUserDetail(username: String): Flow<Result<UserDetail>> {
        return flow {
            val userDetail = apiService.getUserDetail(username).toDomainModel()
            emit(Result.success(userDetail))
        }.catch {
            emit(Result.failure(it))
        }
    }
}