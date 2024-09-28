package com.example.memehub.di

import com.apollographql.apollo3.ApolloClient
import com.example.memehub.data.model.realmModels.Avatar
import com.example.memehub.data.model.realmModels.Comment
import com.example.memehub.data.model.realmModels.Post
import com.example.memehub.data.model.realmModels.Rating
import com.example.memehub.data.model.realmModels.Reactor
import com.example.memehub.data.model.realmModels.User
import com.example.memehub.data.respository.HumorDetectionRepository
import com.example.memehub.data.respository.HumorDetectionRepositoryImpl
import com.example.memehub.network.HumorDetectionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.104:8000")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesHumorDetectionService(retrofit: Retrofit): HumorDetectionService{
        return retrofit.create(HumorDetectionService::class.java)
    }

    @Provides
    @Singleton
    fun providesHumorDetectionRepositoryImp(humorDetectionService: HumorDetectionService): HumorDetectionRepository{
        return HumorDetectionRepositoryImpl(humorDetectionService)
    }

    @Singleton
    @Provides
    fun providesRealm(): Realm {
        val app: App = App.create("memehub-fnewv")

        return runBlocking {
            if (app.currentUser == null) {
                async(Dispatchers.IO) {
                    app.login(Credentials.anonymous())
                }.await()
            }

            app.currentUser.run {
                val config = SyncConfiguration.Builder(
                    user = app.currentUser!!,
                    schema = setOf(
                        User::class,
                        Post::class,
                        Comment::class,
                        Reactor::class,
                        Avatar::class,
                        Rating::class
                    )
                )
                    .initialSubscriptions { realm ->
                        add(realm.query<User>(), name = "user-list")
                    }
                    .build()


                Realm.open(config)
            }
        }
    }

    @Singleton
    @Provides
    fun providesApolloClient(): ApolloClient {
        return ApolloClient.Builder().serverUrl("https://countries.trevorblades.com/graphql")
            .build()
    }
}