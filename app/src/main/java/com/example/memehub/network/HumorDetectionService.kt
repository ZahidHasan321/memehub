package com.example.memehub.network

import com.example.memehub.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HumorDetectionService {
    @POST("/")
    suspend fun predict(@Body text: String): Boolean
}