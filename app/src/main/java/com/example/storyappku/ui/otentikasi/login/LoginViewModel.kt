package com.example.storyappku.ui.otentikasi.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyappku.data.CustomDataRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: CustomDataRepository) : ViewModel() {

    fun login(email: String, password: String) = repo.login(email, password)

    fun getUser() = repo.getUser()

    fun saveUser(userName: String, userId: String, userToken: String) {
        viewModelScope.launch {
            repo.saveUser(userName, userId, userToken)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }

}