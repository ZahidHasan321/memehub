package com.example.memehub.network

import com.example.memehub.data.model.User
import com.example.memehub.data.model.UserUpdateModel
import com.example.memehub.data.model.UsernameResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("/api/users/{uid}")
    suspend fun getUserByUid(@Path("uid") uid: String?) : Response<User>

    @POST("/api/users")
    suspend fun createUser(@Body user: User): Response<User>

    @GET("/api/check-username")
    suspend fun checkUsername(@Query("username") username: String) : Response<UsernameResponse>

    @PUT("/api/users/{uid}")
    suspend fun updateUser(@Path("uid") uid: String, @Body user: UserUpdateModel) : Response<User>
}