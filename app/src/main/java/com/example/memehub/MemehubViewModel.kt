package com.example.memehub

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemehubViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth): ViewModel(){


    private val _user = MutableStateFlow<FirebaseUser?>(null)
           val user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            _user.value = FirebaseAuth.getInstance().currentUser
        }


        viewModelScope.launch {
            user.collect { user ->
                user?.let {
                    Log.d("CHECK USER", "User: ${it.displayName}, Email: ${it.email}")
                    //("Check user", "User: ${it.displayName}, Email: ${it.email}")

                } ?: run {
                    println("User is null")
                }
            }
        }
    }

}