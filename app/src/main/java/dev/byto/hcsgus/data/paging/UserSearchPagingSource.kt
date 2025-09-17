package dev.byto.hcsgus.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.byto.hcsgus.data.mapper.toDomainModel
import dev.byto.hcsgus.data.mapper.toEntity
import dev.byto.hcsgus.data.remote.api.ApiService
import dev.byto.hcsgus.domain.model.User
import retrofit2.HttpException
import java.io.IOException

class UserSearchPagingSource(
    private val apiService: ApiService,
    private val query: String
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        if (query.isBlank()) {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }
        val page = params.key ?: 1
        return try {
            val response = apiService.searchUsers(query, page, params.loadSize)
            val users = response.items.map { it.toEntity().toDomainModel() }

            LoadResult.Page(
                data = users,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (users.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}