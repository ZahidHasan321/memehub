package com.example.memehub

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.memehub.data.model.realmModels.Post
import com.example.memehub.data.model.realmModels.Rating
import com.example.memehub.ui.screens.discovery.DiscoverScreen
import com.example.memehub.ui.screens.home.HomeScreen
import com.example.memehub.ui.screens.profile.ProfileScreen
import com.example.memehub.ui.theme.MemehubTheme
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.annotations.ExperimentalFlexibleSyncApi
import io.realm.kotlin.mongodb.ext.subscribe
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.WaitForSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider
import kotlin.concurrent.thread

//sealed class Screen(val route: String, val icon: ImageVector, @StringRes val resourceId: Int) {
//    object Home : Screen("home", Icons.Default.Home, R.string.home)
//    object Discover : Screen("discover", Icons.Default.Explore, R.string.discover)
//    object Profile : Screen("profile", Icons.Default.Person, R.string.profile)
//}
//
//val items = listOf(
//    Screen.Home,
//    Screen.Discover,
//    Screen.Profile,
//)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appInit: ApplicationInitializer
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MainApp()
        }

        appInit.initRealm()
    }
}

class ApplicationInitializer @Inject constructor(private val provideRealm: Provider<Realm>) {
    @OptIn(ExperimentalFlexibleSyncApi::class)
    fun initRealm() {
        thread {
            val threadScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

            threadScope.launch {
                val realm = provideRealm.get()

                realm.query<Post>().subscribe(WaitForSync.ALWAYS)
                realm.query<Rating>().subscribe(WaitForSync.ALWAYS)
                if(realm.subscriptions.waitForSynchronization()){
                    Log.d("REALM_SUBSCRIBE", "realm subscription complete")
                }
            }
        }
    }
}
