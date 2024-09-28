package com.example.memehub.data.respository

import com.example.memehub.network.HumorDetectionService
import javax.inject.Inject

class HumorDetectionRepositoryImpl @Inject constructor (private val humorDetectionService: HumorDetectionService) : HumorDetectionRepository {
    override suspend fun predict(text: String): Boolean {
        return humorDetectionService.predict(text)
    }
}