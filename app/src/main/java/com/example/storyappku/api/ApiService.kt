package com.example.storyappku.api

import com.example.storyappku.data.model.response.AddStoryResponse
import com.example.storyappku.data.model.response.LoginResponse
import com.example.storyappku.data.model.response.RegisterResponse
import com.example.storyappku.data.model.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun doRegister(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun doLogin(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getListStory(
        @Header("Authorization") bearer: String?,
        @QueryMap queries: Map<String, Int>,
    ): StoryResponse

    @Multipart
    @POST("stories")
    fun postNewStory(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<AddStoryResponse>

    @GET("stories?location=1")
    fun getListMapsStory(
        @Header("Authorization") bearer: String?
    ): Call<StoryResponse>
}