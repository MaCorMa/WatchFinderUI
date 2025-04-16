package com.example.watchfinder.repository

import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.dto.Item
import com.example.watchfinder.data.prefs.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager){


    suspend fun addToList(id: String, state: String, type: String): String {
        val item = Item(id, state, type)
        apiService.addToList(item)
        return "Enviada"
    }
}
