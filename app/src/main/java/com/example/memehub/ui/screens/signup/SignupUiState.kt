package com.example.memehub.ui.screens.signup

import com.memehub.CountriesQuery

data class SignupUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isUsernameExists: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val countryList: List<CountriesQuery.Country> = emptyList(),
    val country: CountriesQuery.Country = CountriesQuery.Country(name="", emoji = "" ,states = emptyList()),
    val state: CountriesQuery.State?  = null
)
