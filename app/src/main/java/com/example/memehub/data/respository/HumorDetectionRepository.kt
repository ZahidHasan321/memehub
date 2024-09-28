package com.example.memehub.data.respository

interface HumorDetectionRepository {
    suspend fun predict(text: String): Boolean
}
