package dev.byto.hcsgus.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class UserDetailStateTest {

    private val sampleUserDetail = UserDetail(
        id = 1,
        login = "testuser",
        avatarUrl = "url",
        name = "Test User",
        company = "Test Co",
        location = "Test City",
        bio = "Test bio",
        blog = "test.blog",
        followers = 10,
        following = 5,
        publicRepos = 2
    )

    @Test
    fun `userDetailState default values are correct`() {
        val state = UserDetailState()

        assertTrue(state.isLoading)
        assertNull(state.data)
    }

    @Test
    fun `userDetailState with isLoading false and data null`() {
        val state = UserDetailState(isLoading = false)

        assertFalse(state.isLoading)
        assertNull(state.data)
    }

    @Test
    fun `userDetailState with isLoading false and data provided`() {
        val state = UserDetailState(isLoading = false, data = sampleUserDetail)

        assertFalse(state.isLoading)
        assertEquals(sampleUserDetail, state.data)
    }

    @Test
    fun `userDetailState with isLoading true (default) and data provided`() {
        val state = UserDetailState(data = sampleUserDetail)

        assertTrue(state.isLoading) // Default isLoading is true
        assertEquals(sampleUserDetail, state.data)
    }

    @Test
    fun `userDetailState with isLoading true (explicit) and data provided`() {
        val state = UserDetailState(isLoading = true, data = sampleUserDetail)

        assertTrue(state.isLoading)
        assertEquals(sampleUserDetail, state.data)
    }
}
