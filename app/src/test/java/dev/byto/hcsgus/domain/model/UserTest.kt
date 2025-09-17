package dev.byto.hcsgus.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UserTest {

    @Test
    fun `user properties are assigned correctly`() {
        val id = 123
        val login = "testuser"
        val avatarUrl = "https://example.com/avatar.jpg"

        val user = User(
            id = id,
            login = login,
            avatarUrl = avatarUrl
        )

        assertEquals(id, user.id)
        assertEquals(login, user.login)
        assertEquals(avatarUrl, user.avatarUrl)
    }
}
