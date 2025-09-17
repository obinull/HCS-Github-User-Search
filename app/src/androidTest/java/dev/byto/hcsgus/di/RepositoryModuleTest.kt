package dev.byto.hcsgus.di

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.byto.hcsgus.data.repository.UserRepositoryImpl
import dev.byto.hcsgus.domain.repository.UserRepository
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RepositoryModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testUserRepositoryBinding() {
        // Assert that Hilt has injected an instance
        assertNotNull("UserRepository should be injected by Hilt", userRepository)

        // Assert that the injected instance is of the expected implementation type
        assertTrue(
            "Injected UserRepository should be an instance of UserRepositoryImpl",
            userRepository is UserRepositoryImpl
        )
    }
}
