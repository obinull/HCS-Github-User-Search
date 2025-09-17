package dev.byto.hcsgus.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.mapper.toDomainModel
import dev.byto.hcsgus.data.mapper.toEntity // Make sure this import is correct for UserDetail -> UserDetailEntity
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

    private val tag = "UserRepositoryImpl"

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
            // Fetch from network
            val userDetailResponse = apiService.getUserDetail(username).toEntity()
            val userDetailDomain = userDetailResponse.toDomainModel()

            // Save to local database
            try {
                // Assuming UserDetailDomain has a .toEntity() extension function
                // that maps it to your Room UserDetailEntity
                database.userDetailDao().insertUserDetail(userDetailResponse)
            } catch (e: Exception) {
                // Log the error or handle it as needed.
                // The flow will still proceed to emit the network response.
                Log.e(tag, "Failed to save user detail to database", e)
                // Optionally, you could rethrow or emit a different kind of error/state
                // if database saving is critical for this operation to be considered a success.
            }

            emit(Result.success(userDetailDomain))
        }.catch { e ->
            // This catches errors from apiService.getUserDetail() or other upstream issues
            Log.e(tag, "Failed to fetch user detail from network", e)
            emit(Result.failure(e))
        }
    }
}