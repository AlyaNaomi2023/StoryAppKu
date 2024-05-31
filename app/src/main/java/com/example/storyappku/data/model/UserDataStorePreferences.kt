package com.example.storyappku.data.model

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyappku.data.model.response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * Kelas untuk mengelola penyimpanan data pengguna menggunakan DataStore Preferences.
 * @param context Konteks Aplikasi.
 */
class UserDataStorePreferences(val context: Context) {

    // DataStore untuk menyimpan preferensi pengguna
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("User")

    /**
     * Fungsi untuk menyimpan data pengguna ke DataStore.
     * @param userName Nama pengguna.
     * @param userId ID pengguna.
     * @param userToken Token pengguna.
     */
    suspend fun saveUser(userName: String, userId: String, userToken: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = userName
            preferences[USERID_KEY] = userId
            preferences[TOKEN_KEY] = userToken
        }
    }

    /**
     * Fungsi untuk mendapatkan data pengguna dari DataStore sebagai aliran Flow.
     * @return Aliran Flow berisi data hasil login.
     */
    fun getUser(): Flow<LoginResult> {
        return context.dataStore.data.map { preferences ->
            LoginResult(
                preferences[NAME_KEY] ?: "",
                preferences[USERID_KEY] ?: "",
                preferences[TOKEN_KEY] ?: ""
            )
        }
    }

    /**
     * Fungsi untuk melakukan logout dengan menghapus data pengguna dari DataStore.
     */
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = ""
            preferences[USERID_KEY] = ""
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: UserDataStorePreferences? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val USERID_KEY = stringPreferencesKey("userId")
        private val TOKEN_KEY = stringPreferencesKey("token")

        /**
         * Fungsi untuk mendapatkan instance dari UserDataStorePreferences.
         * @param context Konteks Aplikasi.
         * @return Instance dari UserDataStorePreferences.
         */
        fun getInstance(context: Context): UserDataStorePreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserDataStorePreferences(context)
                INSTANCE = instance
                instance
            }
        }
    }
}