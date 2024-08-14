package com.example.memehub.data.respository

import com.example.memehub.data.model.response.ImgBBResponse
import okhttp3.MultipartBody
import retrofit2.Response

interface ImgbbRepositry {
    suspend fun postImage(key:String = "afc8c694e18a5ea37d748c1c5bc84eac", image: MultipartBody.Part?) : Response<ImgBBResponse>
}