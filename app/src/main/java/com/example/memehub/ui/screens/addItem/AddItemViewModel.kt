package com.example.memehub.ui.screens.addItem

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memehub.data.model.realmModels.Post
import com.example.memehub.data.model.realmModels.User
import com.example.memehub.data.respository.ImgbbRepositry
import com.example.memehub.helper.uriToMultipart
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(private val realm: Realm, private val firebaseAuth: FirebaseAuth, private  val imgbbRepositry: ImgbbRepositry) : ViewModel() {
    var _uiState = mutableStateOf(AddItemUiState())
        private set

    fun setTitle(newValue: String) {
        _uiState.value = _uiState.value.copy(title = newValue)
    }

    fun setCaption(newValue: String) {
        _uiState.value = _uiState.value.copy(caption = newValue)
    }

    fun setUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(image = uri)
    }

    fun handleSubmit(context:Context){
        viewModelScope.launch {
            try {
                if(_uiState.value.title == "") return@launch
                var response: String = ""
                if(_uiState.value.image != null){
                    val multipartImage = uriToMultipart(context, _uiState.value.image!!)
                    response =  imgbbRepositry.postImage(image = multipartImage).body()?.data?.url?: ""
                }

                realm.write {
                    val liveUser = query<User>("uid == $0", firebaseAuth.currentUser?.uid).find().first()

                    val newPost = Post().apply {
                        title = _uiState.value.title
                        caption = _uiState.value.caption
                        image = response
                    }

                    liveUser.posts.add(newPost)
                }

                Log.d("SUBMIT WORKS 2", realm.query<User>().find().first().posts.toString())
            }
            catch (e: Exception){
                Log.d("SUBMIT WORK", e.message.toString())
                e.printStackTrace()
            }
        }
    }
}