package com.example.memehub.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memehub.R
import com.example.memehub.common.composable.BasicButton

@Composable
fun ProfileScreen(openAndPopUp: (String, String) -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    if(uiState.hasSignedOut){
        openAndPopUp("auth", "protected")
    }

    ProfileScreenContent(
        uiState = uiState,
        onSignOutClick = viewModel::signOutUser
    )
}

@Composable
fun ProfileScreenContent(uiState: ProfileUiState, onSignOutClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        BasicButton(text = R.string.logout, modifier = Modifier.fillMaxWidth().padding(16.dp, 4.dp)) {
            onSignOutClick()
        }
    }
}