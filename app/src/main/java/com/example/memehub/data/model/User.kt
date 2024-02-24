package com.example.memehub.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String? = "",
    val gender: String? = null,
    val birthDate: String? = null,
    val avatar: String? = null
)
