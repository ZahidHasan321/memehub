package com.example.memehub.di

import com.example.memehub.data.respository.ImgbbRepositry
import com.example.memehub.data.respository.ImgbbRepositryImpl
import com.example.memehub.network.ImgbbService
import com.example.memehub.network.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class ImgBBModule {
    @Provides
    @Named("ImgBB")
    fun providesRetrofit(): Retrofit {
        return  Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun providesImgbbService(@Named("ImgBB") retrofit: Retrofit): ImgbbService {
        return retrofit.create(ImgbbService::class.java)
    }

    @Provides
    fun providesImgbbRepository(imgbbService: ImgbbService): ImgbbRepositry{
        return ImgbbRepositryImpl(imgbbService)
    }
}

