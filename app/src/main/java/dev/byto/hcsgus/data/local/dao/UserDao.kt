package dev.byto.hcsgus.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.byto.hcsgus.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, UserEntity>

    @Query("DELETE FROM users")
    suspend fun clearAll()
}