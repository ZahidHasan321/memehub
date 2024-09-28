package com.example.memehub.ui.screens.discovery

data class DiscoverUiState(
    val text: String = "",
    val response: Boolean? = null,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isLoading: Boolean = false
)
