package com.example.memehub.network

import com.example.memehub.data.model.ImgBBResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ImgbbService {
    @Multipart
    @POST("/1/upload")
    suspend fun postImage(@Query("key") key: String, @Part image: MultipartBody.Part?): Response<ImgBBResponse>
}