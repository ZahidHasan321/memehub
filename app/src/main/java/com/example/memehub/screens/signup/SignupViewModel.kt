package com.example.memehub.screens.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memehub.data.model.Resource
import com.example.memehub.data.respository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {
    var uiState = mutableStateOf(SignupUiState())
        private set

    private val name
        get() = uiState.value.name
    private val email
        get() = uiState.value.email

    private val password
        get() = uiState.value.password


    private val confirmPassword
        get() = uiState.value.confirmPassword

    fun onNameChange(newValue: String){
        uiState.value = uiState.value.copy(name = newValue)
    }

    fun onEmailChange(newValue: String){
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String){
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onConfirmPasswordChange(newValue: String){
        uiState.value = uiState.value.copy(confirmPassword = newValue)
    }
    fun signUpUser() = viewModelScope.launch {
        repository.registerUser(email, password).collectLatest { result ->
            when(result){
                is Resource.Loading -> {
                    uiState.value = uiState.value.copy(isLoading = true)
                }is Resource.Success -> {
                uiState.value = uiState.value.copy(isSuccess = true)
            }
                is Resource.Error -> {
                    uiState.value = uiState.value.copy(isError = true)
                }
            }
        }
    }
}