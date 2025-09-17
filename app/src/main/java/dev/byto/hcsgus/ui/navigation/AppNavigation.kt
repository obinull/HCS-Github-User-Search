package dev.byto.hcsgus.ui.navigation


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.byto.hcsgus.ui.screen.Screen
import dev.byto.hcsgus.ui.screen.user_detail.UserDetailScreen
import dev.byto.hcsgus.ui.screen.user_list.UserListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.UserList.route
    ) {
        composable(route = Screen.UserList.route) {
            UserListScreen(
                viewModel = hiltViewModel(),
                onNavigateToDetail = { username ->
                    navController.navigate(Screen.UserDetail.createRoute(username))
                }
            )
        }
        composable(
            route = Screen.UserDetail.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) {
            UserDetailScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}