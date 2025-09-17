package dev.byto.hcsgus.ui.navigation

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.byto.hcsgus.ui.screen.Screen
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        // Context will be provided by composeTestRule
        composeTestRule.setContent {
            context = LocalContext.current
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            navController.navigatorProvider.addNavigator(DialogNavigator()) // If you use dialog destinations
            AppNavigation(navController = navController)
        }
    }

    @Test
    fun appNavigation_verifyStartDestination() {
        // Assert that the start destination is UserListScreen
        assertEquals(Screen.UserList.route, navController.currentDestination?.route)
    }

    @Test
    fun appNavigation_navigateToUserDetailScreen_withArgument() {
        val testUsername = "testGithubUser"

        // Simulate the navigation action that would be triggered from UserListScreen
        composeTestRule.runOnUiThread {
            navController.navigate(Screen.UserDetail.createRoute(testUsername))
        }

        // Assert that the current destination is UserDetailScreen's route definition
        assertEquals(Screen.UserDetail.route, navController.currentDestination?.route)

        // Assert that the argument was passed correctly
        val arguments = navController.currentBackStackEntry?.arguments
        assertEquals(testUsername, arguments?.getString("username"))
    }

    @Test
    fun appNavigation_navigateBackFromUserDetailScreen_toUserListScreen() {
        val testUsername = "anotherUser"

        // First, navigate to UserDetailScreen
        composeTestRule.runOnUiThread {
            navController.navigate(Screen.UserDetail.createRoute(testUsername))
        }
        assertEquals(Screen.UserDetail.route, navController.currentDestination?.route) // Pre-condition

        // Simulate the popBackStack action
        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }

        // Assert that the current destination is back to UserListScreen
        assertEquals(Screen.UserList.route, navController.currentDestination?.route)
    }
}
