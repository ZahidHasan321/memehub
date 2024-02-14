package com.example.memehub.screens.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memehub.R
import com.example.memehub.common.composable.BasicButton
import com.example.memehub.common.composable.EmailField
import com.example.memehub.common.composable.PasswordField
import com.example.memehub.common.composable.RepeatPasswordField


@Composable
fun SignupScreen(openAndPopUp: (String, String) -> Unit) {
    val viewModel: SignupViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    if(uiState.isSuccess){
        openAndPopUp("protected", "auth");
    }

    SignUpScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onNameChange = viewModel::onNameChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSignUpClick = viewModel::signUpUser
    )
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    uiState: SignupUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth().padding(16.dp, 4.dp),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.username)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "username",
                    tint = Color.Black
                )
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
            text = R.string.register, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
        ) {
            onSignUpClick()
        }
    }

}