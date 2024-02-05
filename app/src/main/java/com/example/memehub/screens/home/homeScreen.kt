package com.example.memehub.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    var isModalOpen by remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize()) {
        // Creating a list of funny texts
        val funnyTexts = listOf(
            "Why don't scientists trust atoms? Because they make up everything!",
            "I told my wife she was drawing her eyebrows too high. She looked surprised.",
            "Why did the scarecrow win an award? Because he was outstanding in his field!"
            // Add more funny texts as needed
        )

        // Creating a LazyColumn to display the list of cards
        LazyColumn {
            items(funnyTexts) { funnyText ->
                // Creating a card for each funny text
                FunnyTextCard(funnyText = funnyText)
            }
        }
    }

    // Displaying the modal when isModalOpen is true

}

@Composable
fun FunnyTextCard(funnyText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Displaying the funny text
            Text(
                text = funnyText
            )

            // Adding like and comment icons at the bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* Handle like button click */ }
                )

                Icon(
                    imageVector = Icons.Default.CommentBank,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* Handle comment button click */ }
                )
            }
        }
    }
}