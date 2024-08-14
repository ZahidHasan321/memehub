package com.example.memehub.ui.screens.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.memehub.data.model.realmModels.Post
import com.example.memehub.data.model.realmModels.User
import com.example.memehub.ui.components.PostItem
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun FavoriteScreen(navController : NavController){
    val viewModel:FavoriteViewModel = hiltViewModel()
    val user by viewModel.realmUser.collectAsState()

    Surface {
        if (user?.favoritePosts?.isNotEmpty() == true) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(user?.favoritePosts!!.toList()) { post ->
                    PostItem(
                        post = post,
                        user = user ?: User(),
                        updateScore = viewModel::updateScore,
                        navController = navController
                    )
                }
            }
        }
        else{
            Text(text = "No items found")
        }
    }
}