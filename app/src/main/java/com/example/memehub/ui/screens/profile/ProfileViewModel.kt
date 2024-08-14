package com.example.memehub.ui.screens.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memehub.data.model.UserUpdateModel
import com.example.memehub.data.model.realmModels.Avatar
import com.example.memehub.data.model.realmModels.Rating
import com.example.memehub.data.model.realmModels.User
import com.example.memehub.data.respository.ImgbbRepositry
import com.example.memehub.data.respository.UserRepository
import com.example.memehub.helper.uriToMultipart
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.sum
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val imgbbRepositry: ImgbbRepositry,
    private val realm: Realm
) : ViewModel() {
    var uiState = mutableStateOf(ProfileUiState())
        private set

    var realmUser = realm.query<User>("uid == $0", firebaseAuth.currentUser?.uid).first().asFlow()
            .map { results -> results.obj }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), User())

    var avgRating = realm.query<Rating>().sum<Float>("rating").asFlow().map { results -> results.toFloat() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0f)
    var totalRatingCount = realm.query<Rating>().count().asFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    fun setImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                //get multipart from url and upload it to imageBB
                val multipartImage = uriToMultipart(context, uri)
                val response =  imgbbRepositry.postImage(image = multipartImage)

                if(response.isSuccessful){
                    realm.write {
                        val liveUser = query<User>("uid == $0", firebaseAuth.currentUser?.uid).find().first()

                        var newAvatar = Avatar().apply {
                            displayUrl = response.body()?.data?.url?: ""
                            thumbnailUrl = response.body()?.data?.thumb?.url ?: ""
                        }

                        liveUser.avatar = newAvatar
                    }
                }
                else{
                    uiState.value = uiState.value.copy(errorMessage = "Something went wrong with imgbb!!")
                }


            }
            catch (e:HttpException){
                uiState.value = uiState.value.copy(errorMessage = "Something went wrong!!")
            }

        }
    }


    fun updateBirthday(birthday : String){
        viewModelScope.launch {
            try {
                realm.write {
                    val liveUser = query<User>("uid == $0", firebaseAuth.currentUser?.uid).find().first()

                    liveUser.birthDate = birthday
                }
                uiState.value = uiState.value.copy(successMessage = "Birthdate updated")
            }
            catch (e : Exception){
                uiState.value = uiState.value.copy(errorMessage = "Something went wrong!! Could not update birthdate")
            }
        }
    }

    fun updateGender(gender : String){
        viewModelScope.launch {
            try {
                realm.write {
                    val liveUser = query<User>("uid == $0", firebaseAuth.currentUser?.uid).find().first()

                    liveUser.gender = gender
                }
                uiState.value = uiState.value.copy(successMessage = "Gender updated")
            }
            catch (e : Exception){
                uiState.value = uiState.value.copy(errorMessage = "Something went wrong!! Could not update birthdate")
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

    fun rateApp(rate: Float){
        viewModelScope.launch {
            try {
                realm.write {
                    val liveRating = query<Rating>("user._id == $0", realmUser.value!!._id).find()
                    val liveUser = query<User>("uid == $0", firebaseAuth.currentUser?.uid).find().first()

                    if(liveRating.isNotEmpty()){
                        liveRating.first().rating = rate
                    }
                    else {
                        val newRating = Rating().apply {
                            rating = rate
                            user = liveUser
                        }

                        copyToRealm(newRating)
                    }

                    uiState.value = uiState.value.copy(successMessage = "App rated")
                }
            }catch (e : Exception){
                Log.d("RATE ERROR", e.message.toString())
                uiState.value = uiState.value.copy(errorMessage = "Something went wrong!! Could not rate the app")
            }
        }
    }
}