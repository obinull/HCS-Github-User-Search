package dev.byto.hcsgus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.byto.hcsgus.data.local.dao.RemoteKeysDao
import dev.byto.hcsgus.data.local.dao.UserDao
import dev.byto.hcsgus.data.local.entity.RemoteKeysEntity
import dev.byto.hcsgus.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, RemoteKeysEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}