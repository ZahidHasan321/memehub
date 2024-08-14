package com.example.memehub.ui.screens.addItem

import android.net.Uri

data class AddItemUiState(
    val title: String = "",
    val caption: String = "",
    val image: Uri? = null
)
