package com.example.memehub.ui.screens.signup

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.example.memehub.data.model.Resource
import com.example.memehub.data.model.realmModels.User
import com.example.memehub.data.respository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.memehub.CountriesQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val repository: AuthRepository,
    private val realm: Realm,
    private val apolloClient: ApolloClient
) : ViewModel() {
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

    private  val country
        get() = uiState.value.country

    private  val state
        get() = uiState.value.state


    init {
        viewModelScope.launch {
           val result =  apolloClient.query(CountriesQuery()).execute()
           if(result.data != null){
               uiState.value = uiState.value.copy(countryList = result.data!!.countries.toList())
           }
        }
    }

    fun onCountryChange(newValue: CountriesQuery.Country){
        uiState.value = uiState.value.copy(country = newValue)
    }


    fun onStateChange(newValue: CountriesQuery.State){
        uiState.value = uiState.value.copy(state = newValue)
    }


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
        val usernameExists =
            realm.query<User>("name == $0", name, firebaseAuth.currentUser?.uid).find().isEmpty()
            uiState.value = uiState.value.copy(isUsernameExists = !usernameExists)
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
                            realm.write {
                                val newUser = User().apply {
                                    name = uiState.value.name
                                    email = uiState.value.email
                                    firstName = uiState.value.firstName
                                    lastName = uiState.value.lastName
                                    uid = user.uid
                                }

                                copyToRealm(newUser)

                                uiState.value = uiState.value.copy(isSuccess = true)
                            }
                        }
                        catch (e: Exception){
                            e.message?.let { it1 -> Log.d("USER CREATION ERROR", it1) }

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