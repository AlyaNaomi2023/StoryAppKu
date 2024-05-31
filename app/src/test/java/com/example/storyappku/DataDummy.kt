package com.example.storyappku

import com.example.storyappku.data.model.response.AddStoryResponse
import com.example.storyappku.data.model.response.ListStoryItem
import com.example.storyappku.data.model.response.LoginResponse
import com.example.storyappku.data.model.response.LoginResult
import com.example.storyappku.data.model.response.RegisterResponse
import com.example.storyappku.data.model.response.StoryResponse

/**
 * Kelas untuk menghasilkan data dummy yang digunakan dalam uji coba.
 */
object DataDummy {

    /**
     * Membuat daftar item cerita dummy.
     *
     * @return Daftar item cerita dummy.
     */
    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val storyItem = ListStoryItem(
                i.toString(),
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "Alya $i",
                "Ini adalah unit testing $i",
                "2023-08-08T06:34:18.598Z",
                -10.212,
                -10.212
            )
            items.add(storyItem)
        }
        return items
    }

    /**
     * Membuat respons cerita dummy.
     *
     * @return Respons cerita dummy.
     */
    fun generateDummyStoryResponse(): StoryResponse {
        return StoryResponse(
            generateDummyListStoryItem(),
            false,
            "Stories fetched successfully"
        )
    }

    /**
     * Membuat respons penambahan cerita dummy.
     *
     * @return Respons penambahan cerita dummy.
     */
    fun generateDummyAddStoryResponse(): AddStoryResponse {
        return AddStoryResponse(
            false,
            "success",
        )
    }

    /**
     * Membuat respons login sukses dummy.
     *
     * @return Respons login sukses dummy.
     */
    fun generateDummyResponseLoginSuccess(): LoginResponse {
        val loginResult = LoginResult(
            "user-H5CTfuK4Cvq5FK1B",
            "Alya Naomi",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUg1Q1RmdUs0Q3ZxNUZLMUIiLCJpYXQiOjE2Njc0NDcyNjZ9.53P9_qZ5Y0ZxShzNsr14EXIaCg9Qfq1sNack8U-cT0s"
        )
        return LoginResponse(
            loginResult,
            error = false,
            message = "200"
        )
    }

    /**
     * Membuat respons login dengan kesalahan format email tidak valid dummy.
     *
     * @return Respons login dengan kesalahan format email tidak valid dummy.
     */
    fun generateDummyResponseLoginErrorInvalidEmailFormat(): LoginResponse {
        return LoginResponse(
            null,
            true,
            "400"
        )
    }

    /**
     * Membuat respons login dengan kesalahan pengguna tidak ditemukan dummy.
     *
     * @return Respons login dengan kesalahan pengguna tidak ditemukan dummy.
     */
    fun generateDummyResponseLoginErrorUserNotFound(): LoginResponse {
        return LoginResponse(
            null,
            true,
            "401"
        )
    }

    /**
     * Membuat hasil login dummy.
     *
     * @return Hasil login dummy.
     */
    fun generateDummyLoginResult(): LoginResult {
        return LoginResult(
            "Alya Naomi",
            "akukamu123@gmail.com",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUg1Q1RmdUs0Q3ZxNUZLMUIiLCJpYXQiOjE2Njc0NDcyNjZ9.53P9_qZ5Y0ZxShzNsr14EXIaCg9Qfq1sNack8U-cT0s"
        )
    }

    /**
     * Membuat hasil login kosong dummy.
     *
     * @return Hasil login kosong dummy.
     */
    fun generateDummyLoginResultEmpty(): LoginResult {
        return LoginResult(
            "",
            "",
            ""
        )
    }

    /**
     * Membuat respons registrasi dummy.
     *
     * @return Respons registrasi dummy.
     */
    fun generateDummyResponseRegister(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }

    /**
     * Membuat respons registrasi sukses dummy.
     *
     * @return Respons registrasi sukses dummy.
     */
    fun generateDummyResponseRegisterSuccess(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "200"
        )
    }

    /**
     * Membuat respons registrasi gagal dummy.
     *
     * @return Respons registrasi gagal dummy.
     */
    fun generateDummyResponseRegisterFailed(): RegisterResponse {
        return RegisterResponse(
            true,
            "400"
        )
    }
}