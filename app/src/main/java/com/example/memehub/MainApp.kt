package com.example.memehub

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
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
import kotlinx.coroutines.CoroutineScope

sealed class Screen(val route: String, val icon: ImageVector, @StringRes val resourceId: Int) {
    object Home : Screen(HOME_SCREEN, Icons.Default.Home, R.string.home)
    object Profile : Screen(PROFILE_SCREEN, Icons.Default.Person, R.string.profile)
    object Discover : Screen(DISCOVER_SCREEN, Icons.Default.Explore, R.string.discover)
}

val items = listOf(
    Screen.Home,
    Screen.Discover,
    Screen.Profile,
)


@Composable
fun MainApp(viewModel: MemehubViewModel = hiltViewModel()) {
    val user by viewModel.user.collectAsState()

    MemehubTheme {
        Surface(modifier = Modifier, color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()
            val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val isBottomBarVisible = navBackStackEntry?.destination?.parent?.route != "auth"

            val TIME_DURATION = 500

            val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(
                            durationMillis = TIME_DURATION,
                            easing = LinearOutSlowInEasing
                        )
                    )
                }

            val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(
                            durationMillis = TIME_DURATION,
                            easing = LinearOutSlowInEasing
                        )
                    )
                }

            val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(
                            durationMillis = TIME_DURATION,
                            easing = LinearOutSlowInEasing
                        ) 
                    )
                }

            val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(
                            durationMillis = TIME_DURATION,
                            easing = LinearOutSlowInEasing
                        )
                    )
                }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = appState.snackbarHostState)
                },
                bottomBar = {
                    if (isBottomBarVisible) {
                        BottomAppBar(
                            modifier = Modifier,

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
                    startDestination = if (user != null) "protected" else "auth",
                    modifier = Modifier.padding(innerPadding),
                ) {
                    memehubGraph(
                        appState,
                        enterTransition,
                        exitTransition,
                        popEnterTransition,
                        popExitTransition
                    )
                }
            }
        }
    }

}


@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(scaffoldState, navController, snackbarHostState, resources, coroutineScope) {
        MainAppState(scaffoldState, navController, snackbarHostState, coroutineScope, resources)
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}


fun NavGraphBuilder.memehubGraph(
    appState: MainAppState,
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition,
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition,
    popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition,
    popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition
) {
    navigation(startDestination = LOGIN_SCREEN, route = "auth") {
        composable(LOGIN_SCREEN) {
            LoginScreen(
                appState.coroutineScope,
                appState.snackbarHostState,
                navigate = { route -> appState.navigate(route) },
                openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
        }

        composable(SIGN_UP_SCREEN) {
            SignupScreen(
                appState.navController,
                appState.coroutineScope,
                appState.snackbarHostState,
                openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
        }
    }

    navigation(
        startDestination = HOME_SCREEN,
        route = "protected"
    ) {
        composable(
            HOME_SCREEN,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) { HomeScreen(appState.navController) }
        composable(
            PROFILE_SCREEN,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            ProfileScreen(
                appState.snackbarHostState,
                openAndPopUp = { route, popUp ->
                    appState.navigateAndPopUp(
                        route,
                        popUp
                    )
                })
        }
        composable(
            DISCOVER_SCREEN,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
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


//snackbarHost = {
//    SnackbarHost(snackbarHostState) {data ->
//        val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
//        val buttonColor = if (isError) {
//            ButtonDefaults.textButtonColors(
//                containerColor = MaterialTheme.colorScheme.errorContainer,
//                contentColor = MaterialTheme.colorScheme.error
//            )
//        } else {
//            ButtonDefaults.textButtonColors(
//                contentColor = MaterialTheme.colorScheme.inversePrimary
//            )
//        }
//        Snackbar(
//            modifier = Modifier
//                .border(2.dp, MaterialTheme.colorScheme.secondary)
//                .padding(12.dp),
//            action = {
//                TextButton(
//                    onClick = { if (isError) data.dismiss() else data.performAction() },
//                    colors = buttonColor
//                ) { Text(data.visuals.actionLabel ?: "") }
//            }
//        ) {
//            Text(data.visuals.message)
//        }
//    }
//}