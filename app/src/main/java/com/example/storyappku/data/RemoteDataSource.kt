package com.example.storyappku.data

import androidx.lifecycle.MutableLiveData
import com.example.storyappku.data.model.response.AddStoryResponse
import com.example.storyappku.data.model.response.LoginResponse
import com.example.storyappku.data.model.response.RegisterResponse
import com.example.storyappku.data.model.response.StoryResponse
import com.example.storyappku.api.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RemoteDataSource {

    // LiveData untuk mengirimkan pesan error
    val error = MutableLiveData("")
    var responsecode = ""

    // Fungsi untuk melakukan login
    fun login(callback: LoginCallback, email: String, password: String) {
        callback.onLogin(
            LoginResponse(
                null,
                true,
                ""
            )
        )

        val client = ApiConfig.getApiService().doLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onLogin(it) }
                } else {
                    handleErrorResponse(response.code())
                    callback.onLogin(
                        LoginResponse(
                            null,
                            true,
                            responsecode
                        )
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                handleError(t.message.toString(), callback)
            }
        })
    }

    // Fungsi untuk melakukan registrasi
    fun register(callback: RegisterCallback, name: String, email: String, password: String) {
        val registerinfo = RegisterResponse(
            true,
            ""
        )
        callback.onRegister(registerinfo)

        val client = ApiConfig.getApiService().doRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onRegister(it) }
                    responsecode = "201"
                    callback.onRegister(
                        RegisterResponse(
                            true,
                            responsecode
                        )
                    )
                } else {
                    handleErrorResponse(response.code())
                    callback.onRegister(
                        RegisterResponse(
                            true,
                            responsecode
                        )
                    )
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                handleError(t.message.toString(), callback)
            }
        })
    }

    // Fungsi untuk mengirim cerita baru
    fun postNewStory(
        callback: AddNewStoryCallback,
        token: String,
        imageFile: File,
        desc: String,
        lon: String? = null,
        lat: String? = null
    ) {
        callback.onAddStory(
            createStoryResponse = AddStoryResponse(
                true,
                ""
            )
        )

        val description = desc.toRequestBody("text/plain".toMediaType())
        val latitude = lat?.toRequestBody("text/plain".toMediaType())
        val longitude = lon?.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        val client = ApiConfig.getApiService().postNewStory(
            bearer = "Bearer $token",
            imageMultipart,
            description,
            latitude!!,
            longitude!!
        )

        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback.onAddStory(responseBody)
                    } else {
                        callback.onAddStory(
                            createStoryResponse = AddStoryResponse(
                                true,
                                "Failed to upload file"
                            )
                        )
                    }
                } else {
                    callback.onAddStory(
                        createStoryResponse = AddStoryResponse(
                            true,
                            "Failed to upload file"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                handleError(t.message.toString(), callback)
            }
        })
    }

    // Fungsi untuk mendapatkan daftar cerita pada peta
    fun getListMapsStory(callback: GetListMapsStoryCallback, token: String) {
        val client = ApiConfig.getApiService().getListMapsStory(bearer = "Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onMapsStoryLoad(it) }
                } else {
                    val storyResponse = StoryResponse(
                        emptyList(),
                        true,
                        "Load Failed!"
                    )
                    callback.onMapsStoryLoad(storyResponse)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                handleError(t.message.toString(), callback)
            }
        })
    }

    // Fungsi untuk menangani respons error
    private fun handleErrorResponse(code: Int) {
        when (code) {
            200 -> responsecode = "200"
            400 -> responsecode = "400"
            401 -> responsecode = "401"
            else -> error.postValue("ERROR $code : ${responsecode}")
        }
    }

    // Fungsi untuk menangani kesalahan umum
    private fun handleError(message: String, callback: Any) {
        error.postValue("ERROR: $message")
        when (callback) {
            is LoginCallback -> {
                callback.onLogin(
                    LoginResponse(
                        null,
                        true,
                        message
                    )
                )
            }
            is RegisterCallback -> {
                callback.onRegister(
                    RegisterResponse(
                        true,
                        message
                    )
                )
            }
            is AddNewStoryCallback -> {
                callback.onAddStory(
                    createStoryResponse = AddStoryResponse(
                        true,
                        message
                    )
                )
            }
            is GetListMapsStoryCallback -> {
                val storyResponse = StoryResponse(
                    emptyList(),
                    true,
                    message
                )
                callback.onMapsStoryLoad(storyResponse)
            }
        }
    }

    interface LoginCallback {
        fun onLogin(loginResponse: LoginResponse)
    }

    interface RegisterCallback {
        fun onRegister(registerResponse: RegisterResponse)
    }

    interface GetListMapsStoryCallback {
        fun onMapsStoryLoad(storyResponse: StoryResponse)
    }

    interface AddNewStoryCallback {
        fun onAddStory(createStoryResponse: AddStoryResponse)
    }

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }
}