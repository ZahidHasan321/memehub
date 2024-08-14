package com.example.memehub.data.respository
import com.example.memehub.data.model.User
import com.example.memehub.data.model.UserUpdateModel
import com.example.memehub.data.model.response.UsernameResponse
import retrofit2.Response

interface UserRepository {
    suspend fun getUserByUid(uid: String?) : Response<User>
    suspend fun createUser(user: User) : Response<User>
    suspend fun checkUsername(username: String) : Response<UsernameResponse>

    suspend fun updateUser(uid:String, user: UserUpdateModel) : Response<User>
}