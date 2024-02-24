package com.example.memehub.screens.profile

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memehub.data.model.Resource
import com.example.memehub.data.model.User
import com.example.memehub.data.model.UserUpdateModel
import com.example.memehub.data.respository.ImgbbRepositry
import com.example.memehub.data.respository.UserRepository
import com.example.memehub.helper.uriToMultipart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val imgbbRepositry: ImgbbRepositry
) : ViewModel() {
    var uiState = mutableStateOf(ProfileUiState())
        private set

    init {
        viewModelScope.launch {
            try {
                val user = firebaseAuth.currentUser

                user?.let{
                    uiState.value = uiState.value.copy(
                        email = user.email,
                        username = user.displayName,
                        picture = user.photoUrl
                    )
                }

                var mongoUser: User? = null
                val response = userRepository.getUserByUid(user?.uid)

                if (response.isSuccessful) {
                    mongoUser = response.body()
                } else {
                    uiState.value =
                        uiState.value.copy(errorMessage = "Oops! Something went wrong while fetching your data. Please try again later")
                }


                if (mongoUser != null) {
                    uiState.value = uiState.value.copy(
                        firstName = mongoUser.firstName,
                        lastName = mongoUser.lastName,
                        birthDate = mongoUser.birthDate,
                        gender = mongoUser.gender
                    )
                }
            }
            catch (e: Exception){
                uiState.value = uiState.value.copy(errorMessage = "Unable to connect to server. Please check your internet connection and try again later.")
            }
        }
    }



    fun setImage(context: Context, uri: Uri){
        viewModelScope.launch {
            try {
                //get multipart from url and upload it to imageBB
                val multipartImage = uriToMultipart(context, uri)
                val response = imgbbRepositry.postImage(image = multipartImage)
                val user = firebaseAuth.currentUser
                val uid = user?.uid

                if(response.isSuccessful){
                    //if successful update the image to firebase user auth
                    user?.updateProfile(userProfileChangeRequest {
                        photoUri = Uri.parse(response.body()?.data?.url)
                        displayName = user.displayName
                    })
                        ?.addOnCompleteListener{Task ->
                            uiState.value = uiState.value.copy(successMessage = "User profile picture updated", picture = firebaseAuth.currentUser?.photoUrl)
                        }

                    user?.uid?.let {
                        userRepository.updateUser(uid = it, UserUpdateModel(avatar = response.body()?.data?.url))
                    }
                }
                else{
                    uiState.value = uiState.value.copy(errorMessage = "could not update profile picture")
                }
            }
            catch (e:HttpException){
                uiState.value = uiState.value.copy(errorMessage = "Something went wrong!!")
            }

        }
    }


    fun updateUser(user: UserUpdateModel){
        viewModelScope.launch {
            val uid = firebaseAuth.currentUser?.uid
            try {
                val response = uid?.let { userRepository.updateUser(it, user) }

                if (response != null) {
                    if(response.isSuccessful){
                        uiState.value = uiState.value.copy(successMessage = "User data updated")
                    } else{
                        uiState.value = uiState.value.copy(errorMessage = "Unable to update user data")
                    }
                }

            }
            catch (e: Exception){
                uiState.value = uiState.value.copy(errorMessage = "Something went wrong!! Could not update user")
            }

        }
    }

    fun signOutUser() = viewModelScope.launch {
        try {
            firebaseAuth.signOut()
            uiState.value = uiState.value.copy(hasSignedOut = true, successMessage = "User signed out")
        } catch (e: Exception) {
            uiState.value = uiState.value.copy(hasSignedOut = false, errorMessage = "Something went wrong during signing out.")
        }
    }

    fun clearErrorMessage(){
        uiState.value = uiState.value.copy(errorMessage = "")
    }
    fun clearSuccessMessage() {
        uiState.value = uiState.value.copy(successMessage = "")
    }

    fun changeGender(gender: String){
        uiState.value = uiState.value.copy(gender = gender)
    }
}