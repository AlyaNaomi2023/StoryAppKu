package com.example.storyappku.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.storyappku.data.model.response.AddStoryResponse
import com.example.storyappku.data.model.response.ListStoryItem
import com.example.storyappku.data.model.response.LoginResponse
import com.example.storyappku.data.model.response.LoginResult
import com.example.storyappku.data.model.response.RegisterResponse
import com.example.storyappku.data.model.response.StoryResponse
import java.io.File

interface DataSource {
    fun getUser(): LiveData<LoginResult>
    fun login(email: String, password: String): LiveData<LoginResponse>
    fun register(name: String, email: String, password: String): LiveData<RegisterResponse>
    fun postNewStory(
        token: String,
        imageFile: File,
        desc: String,
        lon: String?,
        lat: String?
    ): LiveData<AddStoryResponse>

    fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>>
    fun getListMapsStory(token: String): LiveData<StoryResponse>
}