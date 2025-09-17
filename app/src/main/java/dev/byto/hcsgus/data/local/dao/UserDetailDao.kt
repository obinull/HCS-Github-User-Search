package dev.byto.hcsgus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import dev.byto.hcsgus.data.local.entity.UserDetailEntity

@Dao
interface UserDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetail(userDetailEntity: UserDetailEntity)
}