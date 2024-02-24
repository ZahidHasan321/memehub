package com.example.memehub.helper

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@SuppressLint("Recycle")
fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
    val contentResolver: ContentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri)
    val file = File(getRealPathFromURI(context, uri))
    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("image", file.name, requestFile)
}

fun getRealPathFromURI(context: Context, uri: Uri): String {
    var result = ""
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = it.getString(columnIndex)
        }
    }
    return result
}