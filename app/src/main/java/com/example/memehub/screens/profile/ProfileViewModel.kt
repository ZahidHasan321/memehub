package com.example.memehub.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor (private val firebaseAuth: FirebaseAuth): ViewModel() {
    var uiState = mutableStateOf(ProfileUiState())
        private set
    fun signOutUser() = viewModelScope.launch {
        try {
            firebaseAuth.signOut()
            uiState.value = uiState.value.copy(hasSignedOut = true)
        }
        catch (e: Exception){
            Log.d("SignOut", "Sign out failed")
        }
    }
}