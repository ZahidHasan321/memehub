package com.example.memehub

import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.memehub.ui.screens.addItem.AddItemScreen
import com.example.memehub.ui.screens.discovery.DiscoverScreen
import com.example.memehub.ui.screens.favorite.FavoriteScreen
import com.example.memehub.ui.screens.home.HomeScreen
import com.example.memehub.ui.screens.login.LoginScreen
import com.example.memehub.ui.screens.post.PostScreen
import com.example.memehub.ui.screens.profile.ProfileScreen
import com.example.memehub.ui.screens.signup.SignupScreen
import com.example.memehub.ui.theme.MemehubTheme
import kotlinx.coroutines.CoroutineScope

sealed class Screen(val route: String, val icon: ImageVector, @StringRes val resourceId: Int) {
    data object Home : Screen(HOME_SCREEN, Icons.Default.Home, R.string.home)
    data object Profile : Screen(PROFILE_SCREEN, Icons.Default.Person, R.string.profile)
    data object Discover : Screen(DISCOVER_SCREEN, Icons.Default.Explore, R.string.discover)
    data object Favorites : Screen(FAVORITE_SCREEN, Icons.Default.Favorite, R.string.favorite)

    data object AddPost : Screen(ADD_ITEM_SCREEN, Icons.Default.Add, R.string.add_post)
}

val items = listOf(
    Screen.Home,
    Screen.Discover,
    Screen.AddPost,
    Screen.Favorites,
    Screen.Profile,
)


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainApp(viewModel: MemehubViewModel = hiltViewModel()) {
    val user by viewModel.user.collectAsState()

    MemehubTheme {
        Surface(modifier = Modifier, color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()
            val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val isBottomBarVisible = navBackStackEntry?.destination?.parent?.route != "auth" && currentDestination?.route != "posts/{postId}"

            val addItemDialogState = remember {
                mutableStateOf(false)
            }

            AddItemScreen(visible = addItemDialogState, appState.snackbarHostState)

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = appState.snackbarHostState)
                },
                bottomBar = {
                    AnimatedVisibility(visible = isBottomBarVisible) {
                        BottomAppBar(
                            tonalElevation = 10.dp,
                            containerColor = MaterialTheme.colorScheme.background
                        ) {
                            items.forEach { screen ->
                                NavigationBarItem(
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    label = { Text(text = stringResource(id = screen.resourceId)) },
                                    onClick = {
                                        if (screen.route == ADD_ITEM_SCREEN) {
                                            addItemDialogState.value = true
                                        }
                                        else{
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
                        appState
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


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.memehubGraph(
    appState: MainAppState,
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
        ) { HomeScreen(appState.navController) }
        composable(
            PROFILE_SCREEN,
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
        composable(FAVORITE_SCREEN) {
            FavoriteScreen(appState.navController)
        }
        composable(
            DISCOVER_SCREEN,
        ) { DiscoverScreen(appState.navController) }

        composable("posts/{postId}", enterTransition = {
            fadeIn(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            ) + slideIntoContainer(
                animationSpec = tween(300, easing = EaseIn),
                towards = AnimatedContentTransitionScope.SlideDirection.Start
            )
        },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }) { navBackStackEntry ->
            PostScreen(appState.snackbarHostState,appState.navController, navBackStackEntry.arguments?.getString("postId"))
        }
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