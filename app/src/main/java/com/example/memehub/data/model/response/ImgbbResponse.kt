package com.example.memehub.data.model.response

import com.squareup.moshi.Json

data class ImgBBResponse(
    val data: ImgBBData,
    val success: Boolean,
    val status: Int
)

data class ImgBBData(
    val id: String,
    val title: String,
    @Json(name = "url_viewer") val urlViewer: String,
    val url: String,
    @Json(name = "display_url") val displayUrl: String,
    val width: String,
    val height: String,
    val size: String,
    val time: String,
    val expiration: String,
    val image: ImgBBImage,
    val thumb: ImgBBThumbnail,
    val medium: ImgBBMedium,
    @Json(name = "delete_url") val deleteUrl: String
)

data class ImgBBImage(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)

data class ImgBBThumbnail(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)

data class ImgBBMedium(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)