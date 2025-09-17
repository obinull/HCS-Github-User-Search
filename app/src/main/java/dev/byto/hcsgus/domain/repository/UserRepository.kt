package dev.byto.hcsgus.domain.repository

import androidx.paging.PagingData
import dev.byto.hcsgus.domain.model.User
import dev.byto.hcsgus.domain.model.UserDetail
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /**
     * Gets a paginated list of all users.
     */
    fun getUsers(): Flow<PagingData<User>>

    /**
     * Searches for users based on a query.
     * @param query The search term.
     */
    fun searchUsers(query: String): Flow<PagingData<User>>

    /**
     * Gets detailed information for a specific user.
     * @param username The login name of the user.
     */
    suspend fun getUserDetail(username: String): Flow<Result<UserDetail>>
}