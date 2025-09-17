package dev.byto.hcsgus.ui.screen

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `userList route is correct`() {
        assertEquals("user_list", Screen.UserList.route)
    }

    @Test
    fun `userDetail route is correct`() {
        assertEquals("user_detail/{username}", Screen.UserDetail.route)
    }

    @Test
    fun `userDetail createRoute formats correctly`() {
        val username = "testuser"
        assertEquals("user_detail/testuser", Screen.UserDetail.createRoute(username))
    }
}
