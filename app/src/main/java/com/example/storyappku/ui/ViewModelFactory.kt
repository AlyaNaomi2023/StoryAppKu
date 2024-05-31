package com.example.storyappku.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyappku.data.CustomDataRepository
import com.example.storyappku.di.Injection
import com.example.storyappku.ui.otentikasi.login.LoginViewModel
import com.example.storyappku.ui.otentikasi.register.RegisterViewModel
import com.example.storyappku.ui.main.MainViewModel

class ViewModelFactory private constructor(private val repo: CustomDataRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repo) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(repo) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repo) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}
