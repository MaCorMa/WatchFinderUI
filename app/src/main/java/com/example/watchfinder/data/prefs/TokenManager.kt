package com.example.watchfinder.data.prefs

import android.content.Context

class TokenManager(context: Context) {
    private val sPrefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String){
        sPrefs.edit().putString("apiToken", token).apply()
    }

    fun getToken(): String? {
         return sPrefs.getString("apiToken", null)
    }

    fun clearToken() {
        sPrefs.edit().remove("apiToken").apply()
    }

}