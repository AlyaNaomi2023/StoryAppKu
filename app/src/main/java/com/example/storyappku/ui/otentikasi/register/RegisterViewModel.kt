package com.example.storyappku.ui.otentikasi.register

import androidx.lifecycle.ViewModel
import com.example.storyappku.data.CustomDataRepository

class RegisterViewModel(private val dataRepository: CustomDataRepository) : ViewModel() {
    fun registerUser(name: String, email: String, password: String) =
        dataRepository.register(name, email, password)
}
