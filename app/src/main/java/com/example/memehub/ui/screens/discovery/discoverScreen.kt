package com.example.memehub.ui.screens.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun DiscoverScreen(navController: NavController){
    val viewModel:DiscoverViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    val modifier:Modifier = Modifier
    Surface (modifier = modifier.fillMaxSize()){
        Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.imePadding()){
                Text(text= "Check if funny", modifier = modifier.padding(bottom = 10.dp), fontSize = 20.sp)
                Card (modifier = modifier.padding(bottom = 10.dp) ,elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)){
                    Column (verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.padding(20.dp)){
                        OutlinedTextField(value = uiState.text, placeholder = { Text(text = "Write a joke...") }, onValueChange = {
                            viewModel.onTextChange(it)
                        })

                        Button(enabled = !uiState.isLoading ,onClick = { viewModel.predict() }) {
                            Text(text = "Predict")
                        }
                    }
                }

            if(uiState.isLoading){
                viewModel.onResponseChange(null)
                CircularProgressIndicator()
            }
            if(uiState.response != null){
                if(uiState.response == true){
                    Text(text = "Funny", color = Color.Green)
                }
                else{
                    Text(text = "Unfunny", color = Color.Red)
                }
            }
            }
    }
}