package com.example.memehub.screens.login

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.memehub.data.model.Resource
import com.example.memehub.data.respository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val application: Application, private val repository: AuthRepository): AndroidViewModel(application = application) {
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email

    private val password
        get() = uiState.value.password

    fun onEmailChange(newValue: String){
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPassChange(newValue: String){
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun clearErrorState(){
        uiState.value = uiState.value.copy(isError = false, errorMessage = "")
    }

    fun loginUser() = viewModelScope.launch {
        repository.loginUser(email, password).collectLatest { result ->
            when(result){
                is Resource.Loading -> {
                    uiState.value = uiState.value.copy(isLoading = true)
                }is Resource.Success -> {
                    uiState.value = uiState.value.copy(isSuccess = true, isLoading = false)
                }
                is Resource.Error -> {
                    uiState.value = uiState.value.copy(isError = true, isLoading = false)
                    result.message?.let {
                        uiState.value = uiState.value.copy(errorMessage = result.message)
                    }
                }
            }
        }
    }

}