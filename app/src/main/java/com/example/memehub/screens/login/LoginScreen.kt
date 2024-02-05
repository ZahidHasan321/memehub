package com.example.memehub.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memehub.common.composable.BasicButton
import com.example.memehub.common.composable.EmailField
import com.example.memehub.common.composable.PasswordField
import com.example.memehub.R.string as AppText

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPassChange,
        onSignInClick = viewModel::loginUser
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(
            uiState.email, onEmailChange,
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
        )
        PasswordField(
            uiState.password,
            onPasswordChange,
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
        )

        BasicButton(
            AppText.sign_in,
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp)
        ) { onSignInClick() }
    }
}