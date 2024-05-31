package com.example.storyappku.di

import android.content.Context
import com.example.storyappku.data.CustomDataRepository
import com.example.storyappku.data.RemoteDataSource
import com.example.storyappku.data.model.UserDataStorePreferences
import com.example.storyappku.data.model.database.CustomStoryDatabase
import com.example.storyappku.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): CustomDataRepository {
        val apiService = ApiConfig.getApiService()
        val userDataStorePreferences = UserDataStorePreferences.getInstance(context)
        val remoteDataSource = RemoteDataSource.getInstance()
        val customStoryDatabase = CustomStoryDatabase.getDatabase(context)
        return CustomDataRepository.getInstance(
            apiService,
            userDataStorePreferences,
            remoteDataSource,
            customStoryDatabase
        )
    }
}