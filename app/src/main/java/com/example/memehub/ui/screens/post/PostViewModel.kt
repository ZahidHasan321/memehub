package com.example.memehub.ui.screens.post

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memehub.data.model.realmModels.Comment
import com.example.memehub.data.model.realmModels.Post
import com.example.memehub.data.model.realmModels.Reactor
import com.example.memehub.data.model.realmModels.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmSetOf
import io.realm.kotlin.mongodb.annotations.ExperimentalFlexibleSyncApi
import io.realm.kotlin.mongodb.ext.subscribe
import io.realm.kotlin.mongodb.sync.WaitForSync
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@OptIn(ExperimentalFlexibleSyncApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseAuth: FirebaseAuth,
    private val realm: Realm
) : ViewModel() {
    private val postId: String = checkNotNull(savedStateHandle["postId"])
    var _uiState = mutableStateOf(PostUiState())
        private set

    var realmUser = realm.query<User>("uid == $0", firebaseAuth.currentUser?.uid).first().asFlow()
        .map { results -> results.obj }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), User())


    var post = realm.query<Post>("_id == $0", ObjectId(postId)).first().asFlow().map { results -> results.obj }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Post())

    fun setComment(newVal : String){
        _uiState.value = _uiState.value.copy(comment = newVal)
    }
    fun handleSubmit(){
        viewModelScope.launch {
            try{
                realm.write {
                    val livePost = query<Post>("_id == $0", ObjectId(postId)).first().find()
                    val liveUser = query<User>("uid == $0", firebaseAuth.currentUser?.uid).first().find()

                    val newComment = Comment().apply {
                        commentor = liveUser
                        content = _uiState.value.comment
                    }

                    livePost?.comments?.add(newComment)

                    setComment("")
                }
            }
            catch (e:Exception){
                e.message?.let { Log.d("POST COMMENT ERROR", it) }

            }
        }
    }
    fun updateScore(postId: ObjectId, userId: ObjectId, userScore: Int) {
        viewModelScope.launch {
            realm.write {
                val livePost = query<Post>("_id == $0", postId).first().find()
                val liveUser = query<User>("_id == $0", userId).first().find()


                val newReact = Reactor().apply {
                    user = liveUser
                    score = userScore
                }

                if(userScore == 1){
                    if (livePost != null) {
                        Log.d("LIKED", "HELLO")
                        liveUser?.favoritePosts?.add(livePost)
                    }
                }
                else{
                    liveUser?.favoritePosts?.remove(livePost)
                }


                if (livePost?.reactors?.isEmpty() == true) {
                    livePost.reactors.add(
                        newReact
                    )
                } else {
                    for (post in livePost?.reactors!!) {
                        if (post.user?._id == userId) {
                            if (post.score == 1 && livePost.likes.toInt() != 0) livePost.likes--
                            if (post.score == -1 && livePost.dislikes.toInt() != 0) livePost.dislikes--

                            if (userScore == 1) {
                                livePost.likes += 1

                            } else if (userScore == -1) {
                                livePost.dislikes += 1
                            }


                            post.score = userScore
                            return@write
                        }
                    }
                    livePost.reactors.add(
                        newReact
                    )
                }

                if (userScore == 1) {
                    livePost.likes += 1
                } else if (userScore == -1) {
                    livePost.dislikes += 1
                }
            }
        }
    }
}
