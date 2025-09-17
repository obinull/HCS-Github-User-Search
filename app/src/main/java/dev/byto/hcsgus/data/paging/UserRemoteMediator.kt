package dev.byto.hcsgus.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.entity.RemoteKeysEntity
import dev.byto.hcsgus.data.local.entity.UserEntity
import dev.byto.hcsgus.data.mapper.toEntity
import dev.byto.hcsgus.data.remote.api.ApiService
import dev.byto.hcsgus.util.orZero
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val apiService: ApiService,
    private val database: AppDatabase
) : RemoteMediator<Int, UserEntity>() {

    private val userDao = database.userDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = apiService.getUsers(
                since = loadKey,
                perPage = state.config.pageSize
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    userDao.clearAll()
                    remoteKeysDao.clearRemoteKeys()
                }

                val keys = response.map {
                    RemoteKeysEntity(
                        userId = it.id.orZero(),
                        prevKey = null,
                        nextKey = it.id
                    )
                }
                remoteKeysDao.insertAll(keys)
                userDao.upsertAll(response.map { it.toEntity() })
            }

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, UserEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { user ->
                remoteKeysDao.remoteKeysUserId(user.id.orZero())
            }
    }
}