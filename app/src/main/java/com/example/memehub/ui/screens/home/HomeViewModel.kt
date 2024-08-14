package com.example.memehub.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memehub.data.model.realmModels.Post
import com.example.memehub.data.model.realmModels.Reactor
import com.example.memehub.data.model.realmModels.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.annotations.ExperimentalFlexibleSyncApi
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val realm: Realm,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    val postList = realm.query<Post>().sort("_id",Sort.DESCENDING).asFlow().map { results -> results.list.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var realmUser = realm.query<User>("uid == $0", firebaseAuth.currentUser?.uid).first().asFlow()
        .map { results -> results.obj }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), User())


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

