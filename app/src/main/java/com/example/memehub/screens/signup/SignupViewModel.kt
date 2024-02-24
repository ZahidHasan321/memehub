package com.example.memehub.screens.signup

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memehub.data.model.Resource
import com.example.memehub.data.model.User
import com.example.memehub.data.respository.AuthRepository
import com.example.memehub.data.respository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth, private val repository: AuthRepository, private val userRepository: UserRepository) : ViewModel() {
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

    private val firstName
        get() = uiState.value.firstName

    private val lastName
        get() = uiState.value.lastName

    fun onNameChange(newValue: String) {
        viewModelScope.launch {
        }
        uiState.value = uiState.value.copy(name = newValue)
    }

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onConfirmPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(confirmPassword = newValue)
    }

    fun onFirstNameChange(newValue: String) {
        uiState.value = uiState.value.copy(firstName = newValue)
    }

    fun onLastNameChange(newValue: String) {
        uiState.value = uiState.value.copy(lastName = newValue)
    }

    fun clearErrorState(){
        uiState.value = uiState.value.copy(isError = false, errorMessage = "")
    }

    fun checkUsername() = viewModelScope.launch {
        val response = userRepository.checkUsername(name)
        if(response.isSuccessful)
        {
            val result = response.body()
            if (result != null) {
                uiState.value = uiState.value.copy(isUsernameExists = result.isFound)
            }
        }

    }

    fun signUpUser() = viewModelScope.launch {
        repository.registerUser(name, email, password).collectLatest { result ->
            when (result) {
                is Resource.Loading -> {
                    uiState.value = uiState.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    val user = result.data?.user

                    user?.let {
                        try {
                            val response = userRepository.createUser(
                                User(
                                    it.uid,
                                    name,
                                    email,
                                    firstName,
                                    lastName
                                )
                            )
                            if (response.isSuccessful) {
                                uiState.value = uiState.value.copy(isSuccess = true)
                            } else {
                                uiState.value =
                                    uiState.value.copy(errorMessage = "Could not create user in database")
                                firebaseAuth.currentUser?.delete()
                                firebaseAuth.signOut()
                            }
                        }
                        catch (e: Exception){
                            uiState.value =
                                uiState.value.copy(errorMessage = "Could not create user in database")
                            firebaseAuth.currentUser?.delete()
                            firebaseAuth.signOut()
                        }
                    }
                    uiState.value = uiState.value.copy(isLoading = false)
                }

                is Resource.Error -> {
                    uiState.value = uiState.value.copy(isError = true, isLoading = false)
                    result.message?.let {
                        uiState.value = uiState.value.copy(errorMessage = result.message)
                    }
                }

                else -> {}
            }
        }
    }
}