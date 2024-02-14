package com.example.memehub

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.memehub.screens.discovery.DiscoverScreen
import com.example.memehub.screens.home.HomeScreen
import com.example.memehub.screens.login.LoginScreen
import com.example.memehub.screens.profile.ProfileScreen
import com.example.memehub.screens.signup.SignupScreen
import com.example.memehub.ui.theme.MemehubTheme
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope

sealed class Screen(val route: String, val icon: ImageVector, @StringRes val resourceId: Int) {
    object Home : Screen(HOME_SCREEN, Icons.Default.Home, R.string.home)
    object Discover : Screen(DISCOVER_SCREEN, Icons.Default.Explore, R.string.discover)
    object Profile : Screen(PROFILE_SCREEN, Icons.Default.Person, R.string.profile)
}

val items = listOf(
    Screen.Home,
    Screen.Discover,
    Screen.Profile,
)

@Composable
fun MainApp(user: FirebaseUser?) {
    MemehubTheme {
        Surface(modifier = Modifier, color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()
            Scaffold(
                bottomBar = {
                    if(user != null) {
                        val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        BottomAppBar(
                            modifier = Modifier
                        ) {
                            items.forEach { screen ->
                                NavigationBarItem(
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    label = { Text(text = stringResource(id = screen.resourceId)) },
                                    onClick = {
                                        appState.navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(appState.navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // re-selecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = appState.navController,
                    startDestination = if(user != null) "protected" else "auth",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    memehubGraph(appState)
                }
            }
        }
    }

}


@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(scaffoldState, navController, coroutineScope) {
        MainAppState(scaffoldState, navController, coroutineScope)
    }

fun NavGraphBuilder.memehubGraph(appState: MainAppState) {
    navigation(startDestination = LOGIN_SCREEN, route = "auth") {
        composable(LOGIN_SCREEN) {
            LoginScreen(navigate = {route -> appState.navigate(route)}, openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp)})
        }

        composable(SIGN_UP_SCREEN){
            SignupScreen( openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp)})
        }
    }

    navigation(startDestination = HOME_SCREEN, route = "protected") {
        composable(
            HOME_SCREEN
        ) { HomeScreen(appState.navController) }
        composable(
            PROFILE_SCREEN,

        ) { ProfileScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp)}) }
        composable(
            DISCOVER_SCREEN,
        ) { DiscoverScreen(appState.navController) }
    }
}


//enterTransition = {
//    slideIntoContainer(
//        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
//        animationSpec = tween(700)
//    )
//},
//exitTransition = {
//    slideOutOfContainer(
//        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
//        animationSpec = tween(700)
//    )
//},
//popEnterTransition = {
//    slideIntoContainer(
//        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
//        animationSpec = tween(700)
//    )
//},
//popExitTransition = {
//    slideOutOfContainer(
//        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
//        animationSpec = tween(700)
//    )
//}