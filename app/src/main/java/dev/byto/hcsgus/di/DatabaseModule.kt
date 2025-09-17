package dev.byto.hcsgus.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.byto.hcsgus.BuildConfig
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.dao.RemoteKeysDao
import dev.byto.hcsgus.data.local.dao.UserDao
import dev.byto.hcsgus.util.constant.DatabaseConstant
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // NOTE: In a real app, the passphrase should be fetched securely, e.g., from Android Keystore.
        // For DEBUG builds: Create a standard, unencrypted database.
        // App Inspection can read this. ✅
        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DatabaseConstant.NAME
        )

        if (!BuildConfig.DEBUG) {
            // For RELEASE builds: Create a secure, encrypted database.
            // App Inspection cannot read this. 🔒
            val passphrase = getSecurePassphrase()
            val factory = SupportFactory(passphrase)
            builder.openHelperFactory(factory)
        }
        return builder.build()
    }

    private fun getSecurePassphrase(): ByteArray {
        return SQLiteDatabase.getBytes(DatabaseConstant.PASS_PHRASE.toCharArray())
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeysDao(appDatabase: AppDatabase): RemoteKeysDao {
        return appDatabase.remoteKeysDao()
    }
}