package com.example.memehub.ui.screens.profile

import android.net.Uri
import com.example.memehub.data.model.User

data class ProfileUiState(
    val hasSignedOut: Boolean = false,
    val firstName: String? = "Firstname",
    val lastName: String? = "Lastname",
    val username: String? = "Username",
    val picture: Uri? = null,
    val email: String? = "Email",
    val birthDate: String? = "",
    val gender: String? = "",
    val phoneNumber: String? = "Not found",
    val errorMessage: String = "",
    val successMessage: String = ""
)
