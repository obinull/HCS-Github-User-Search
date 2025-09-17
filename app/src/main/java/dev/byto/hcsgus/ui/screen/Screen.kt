package dev.byto.hcsgus.ui.screen

sealed class Screen(val route: String) {
    data object UserList : Screen("user_list")
    data object UserDetail : Screen("user_detail/{username}") {
        fun createRoute(username: String) = "user_detail/$username"
    }
}