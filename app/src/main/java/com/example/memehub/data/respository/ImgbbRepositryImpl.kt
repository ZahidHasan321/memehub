package com.example.memehub.data.respository

import com.example.memehub.data.model.ImgBBResponse
import com.example.memehub.network.ImgbbService
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class ImgbbRepositryImpl @Inject constructor(private val imgbbService: ImgbbService) : ImgbbRepositry {
    override suspend fun postImage(key:String, image: MultipartBody.Part?): Response<ImgBBResponse> {
        return imgbbService.postImage(key , image)
    }
}