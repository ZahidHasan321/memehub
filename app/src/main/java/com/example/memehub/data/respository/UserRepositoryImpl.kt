package com.example.memehub.data.respository

import android.util.Log
import com.example.memehub.data.model.User
import com.example.memehub.data.model.UserUpdateModel
import com.example.memehub.data.model.UsernameResponse
import com.example.memehub.network.UserService
import retrofit2.Response
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userService: UserService): UserRepository {
    override suspend fun getUserByUid(uid: String?): Response<User> {
        return userService.getUserByUid(uid)
    }

    override suspend fun createUser(user: User): Response<User> {
        return userService.createUser((user))
    }
    override suspend fun checkUsername(username: String): Response<UsernameResponse> {
        return  userService.checkUsername(username)
    }

    override suspend fun updateUser(uid: String, user: UserUpdateModel): Response<User> {
        return userService.updateUser(uid, user)
    }
}