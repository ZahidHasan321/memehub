package com.example.memehub.screens.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.memehub.R
import com.example.memehub.common.composable.BasicButton
import com.example.memehub.common.composable.BasicToolbar
import com.example.memehub.common.composable.EmailField
import com.example.memehub.common.composable.PasswordField
import com.example.memehub.common.composable.RepeatPasswordField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun SignupScreen(
    navController: NavController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    openAndPopUp: (String, String) -> Unit
) {
    val viewModel: SignupViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    if (uiState.isSuccess) {
        openAndPopUp("protected", "auth");
    }

    LaunchedEffect(key1 = uiState.name) {
        if (uiState.name.isNotEmpty()) {
            viewModel.checkUsername()
        }
    }

    LaunchedEffect(key1 = uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = uiState.errorMessage,
                actionLabel = "dismiss"
            )
            viewModel.clearErrorState()
        }
    }

    SignUpScreenContent(
        navController = navController,
        scope = scope,
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onNameChange = viewModel::onNameChange,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSignUpClick = viewModel::signUpUser,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenContent(
    navController: NavController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    uiState: SignupUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit
) {
    var isNavigationInProgress by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Sign Up",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight(800),
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isNavigationInProgress) {
                            isNavigationInProgress = true
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Go Back"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.username)
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "username")
                }
            )

            if (uiState.name.isNotEmpty()) {
                UsernameCheck(
                    uiState = uiState,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 16.dp)
                )
            }


            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = onFirstNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.first_name)
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "firstname")
                }
            )

            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = onLastNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.last_name)
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "lastname")
                }
            )

            EmailField(
                value = uiState.email, onNewValue = onEmailChange, Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
            )

            PasswordField(
                value = uiState.password, onNewValue = onPasswordChange, Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
            )

            RepeatPasswordField(
                value = uiState.confirmPassword, onNewValue = onConfirmPasswordChange,
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
            )

            BasicButton(
                enabled = !uiState.isLoading,
                text = R.string.register, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
            ) {
                if (uiState.name.isEmpty() || uiState.email.isEmpty() || uiState.password.isEmpty() || uiState.confirmPassword.isEmpty()) {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Fields cannot be empty",
                            actionLabel = "Dismiss"
                        )
                    }

                } else if (!uiState.password.isValid()) {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Password must be 6 characters long",
                            actionLabel = "Dismiss"
                        )
                    }
                } else if (uiState.password != uiState.confirmPassword) {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Passwords doesn't match",
                            actionLabel = "Dismiss"
                        )
                    }
                } else if (uiState.isUsernameExists) {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Username needs to be unique",
                            actionLabel = "Dismiss"
                        )
                    }
                } else {
                    onSignUpClick()
                }
            }
        }
    }
}

@Composable
fun UsernameCheck(uiState: SignupUiState, modifier: Modifier = Modifier) {
    val usernameStatusText = if (uiState.isUsernameExists) {
        "Username exists"
    } else {
        "Username is unique"
    }

    val textColor = if (uiState.isUsernameExists) {
        Color.Red // Set the color for existing usernames
    } else {
        Color.Green // Set the color for unique usernames
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            imageVector = if (uiState.isUsernameExists) Icons.Default.Warning else Icons.Default.CheckCircle,
            contentDescription = null, // Provide appropriate content description
            tint = textColor, // Set the icon color based on username status
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = usernameStatusText,
            color = textColor, // Set the text color based on username status
            modifier = Modifier.padding(start = 4.dp),
            fontSize = 10.sp
        )
    }
}


fun String.isValid() = length >= 6