package com.example.memehub.ui.screens.discovery

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memehub.data.respository.HumorDetectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(private val humorDetectionRepository: HumorDetectionRepository): ViewModel() {

    var uiState = mutableStateOf(DiscoverUiState())
        private set

    private val text
        get() = uiState.value.text

    fun onTextChange(newValue: String){
        uiState.value = uiState.value.copy(text = newValue)
    }

    private fun onIsLoadingChange(newValue: Boolean){
        uiState.value = uiState.value.copy(isLoading = newValue)
    }

    fun onResponseChange(newValue: Boolean?){
        uiState.value = uiState.value.copy(response = newValue)
    }

    fun onErrorMessageChange(newValue: String){
        uiState.value = uiState.value.copy(errorMessage = newValue)
    }

    fun predict(){
        viewModelScope.launch {
            try {
                onIsLoadingChange(true)
                val res = humorDetectionRepository.predict(text)
                uiState.value = uiState.value.copy(response = res)
                onIsLoadingChange(false)
            }
            catch (e: Exception){
                onIsLoadingChange(false)
                onErrorMessageChange(e.message.toString())
            }
        }
    }
}