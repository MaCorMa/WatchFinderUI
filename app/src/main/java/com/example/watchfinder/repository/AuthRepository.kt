package com.example.watchfinder.repository

import com.example.watchfinder.api.ApiService
import com.example.watchfinder.api.RetroFitClient
import com.example.watchfinder.data.dto.LoginRequest
import com.example.watchfinder.data.dto.LoginResponse
import com.example.watchfinder.data.dto.RegisterRequest
import com.example.watchfinder.data.prefs.TokenManager

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager) {

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val loginResponse = response.body()!!
                tokenManager.saveToken(loginResponse.token)
                println("Exito: ${loginResponse.token}")
                Result.success(loginResponse)

            } else {
                println("Fallo al logear: ${response.code()}")
                Result.failure(Exception("Fallo al logear: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("Fallo al logear:" + e)
            Result.failure(e)
        }
    }

    suspend fun register(name: String, username: String, password: String, email: String): Result<String>{
        return try{
            val response = apiService.register(RegisterRequest(name, username, password, email))
            if(response.isSuccessful) {
                val responseString = response.body()?.string() ?: "Respuesta vacía"
                Result.success(responseString)
            } else {
                val errorString = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Fallo al registrar: ${response.code()} - $errorString"))
            }
        } catch (e: Exception) {
            println("Fallo al registrar:" + e)
            Result.failure(e)
        }
        }
    }

