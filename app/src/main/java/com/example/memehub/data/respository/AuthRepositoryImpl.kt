package com.example.memehub.data.respository

import android.util.Log
import com.example.memehub.data.model.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(value = Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(value = Resource.Success(data = result))
        }.catch {
            emit(value = Resource.Error(message = it.message.toString()))
        }
    }

    override fun registerUser(name:String, email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(value = Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            var photoUrl = firebaseAuth.currentUser?.photoUrl

            result.user?.updateProfile(userProfileChangeRequest {
                displayName = name
                photoUri = photoUrl
            })?.await()
            result.user?.sendEmailVerification()?.await()
            emit(value = Resource.Success(data = result))
        }.catch {
            emit(value = Resource.Error(message = it.message.toString()))
        }
    }
}