package com.example.memehub.ui.screens.home

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
import com.example.memehub.data.model.realmModels.User
import com.example.memehub.ui.components.PostItem
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

@Composable
fun HomeScreen(navController: NavController) {

    val viewModel: HomeViewModel = hiltViewModel()
    val postList by viewModel.postList.collectAsState()
    val user by viewModel.realmUser.collectAsState()


    Surface(modifier = Modifier.fillMaxSize()) {
        // Creating a list of funny texts
        if (postList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(postList) { post ->
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



fun getTimeElapsed(epochTimestamp: Long): String {
    val postDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTimestamp), ZoneId.systemDefault())
    val currentDateTime = LocalDateTime.now()
    val duration = Duration.between(postDateTime, currentDateTime)

    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toHours() < 1 -> "${duration.toMinutes()} minutes ago"
        duration.toDays() < 1 -> "${duration.toHours()} hours ago"
        else -> "${duration.toDays()} days ago"
    }
}
fun toCamelCase(input: String): String {
    return input.split(" ").joinToString("") { word ->
        word.lowercase(Locale.ROOT)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}
