package com.example.memehub.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memehub.LOGIN_SCREEN
import com.example.memehub.R
import com.example.memehub.SIGN_UP_SCREEN
import com.example.memehub.common.composable.BasicButton
import com.example.memehub.common.composable.EmailField
import com.example.memehub.common.composable.PasswordField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.memehub.R.string as AppText

@Composable
fun LoginScreen(scope: CoroutineScope, snackbarHostState: SnackbarHostState,navigate: (String) -> Unit ,openAndPopUp: (String, String) -> Unit) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    if(uiState.isSuccess) {
        openAndPopUp("protected", "auth");
    }

    LaunchedEffect(key1 = uiState.errorMessage){
        if(uiState.errorMessage.isNotEmpty()){
            val result = snackbarHostState.showSnackbar(
                message = uiState.errorMessage,
                actionLabel = "dismiss"
            )
            viewModel.clearErrorState()
        }
    }

    LoginScreenContent(
        scope = scope,
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPassChange,
        onSignInClick = viewModel::loginUser,
        navigate = navigate,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    navigate: (String) -> Unit
) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Sign In", fontWeight = FontWeight(800), fontSize = 24.sp)}
            )
        }
    ){innerPadding ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
            ,
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
                    .padding(16.dp, 8.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.email.isEmpty() || uiState.password.isEmpty()) {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Fields cannot be empty",
                            actionLabel = "Dismiss"
                        )
                    }
                } else
                    onSignInClick()
            }


//        BasicButton(text = R.string.create_account , modifier = Modifier.fillMaxWidth(), action = { navigate(
//            SIGN_UP_SCREEN)
//        })

            TextButton(onClick = { navigate(SIGN_UP_SCREEN) }) {
                Text(text = stringResource(id = R.string.create_account))
            }
        }
    }
}