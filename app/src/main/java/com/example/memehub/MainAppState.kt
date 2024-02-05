package com.example.memehub

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Stable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope

@Stable
class MainAppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    coroutineScope: CoroutineScope
){

    fun popUp(){
        navController.popBackStack()
    }

    fun navigate(route: String){
        navController.navigate(route){
            launchSingleTop= true
        }
    }

    fun navigateAndPopUp(route: String, popUp: String){
        navController.navigate((route)){
            launchSingleTop = true
            popUpTo(popUp){
                inclusive = true
            }
        }
    }

    fun clearAndNavigate(route: String){
        navController.navigate(route){
            launchSingleTop = true
            popUpTo(0){
                inclusive = true
            }
        }
    }

}