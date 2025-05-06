package com.example.watchfinder.repository

import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.UserManager
import com.example.watchfinder.data.dto.ChangePasswordRequest
import com.example.watchfinder.data.dto.LoginRequest
import com.example.watchfinder.data.dto.LoginResponse
import com.example.watchfinder.data.dto.ForgotPasswordRequest
import com.example.watchfinder.data.dto.RegisterRequest
import com.example.watchfinder.data.dto.ResetPasswordRequest
import com.example.watchfinder.data.prefs.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val userManager: UserManager
) {

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val loginResponse = response.body()!!
                tokenManager.saveToken(loginResponse.token)
                println("Exito: ${loginResponse.token}")
                fetchAndStoreUserProfile()
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

    suspend fun validate(): Result<Boolean> {
        return try {
            val response = apiService.validate()
            if (response.isSuccessful) {
                fetchAndStoreUserProfile()
                Result.success(true) // Token es válido
            } else {
                // El token no es válido (401, 403, etc.) -> Limpiarlo localmente
                tokenManager.clearToken()
                userManager.clearCurrentUser()
                Result.failure(Exception("Token validation failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Error de red u otro problema
            // Podrías decidir si limpiar el token aquí también o no
            println("Network error during token validation: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String,
        username: String,
        password: String,
        email: String
    ): Result<String> {
        return try {
            val response = apiService.register(RegisterRequest(name, username, password, email))
            if (response.isSuccessful) {
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

    suspend fun fetchAndStoreUserProfile() {
        try {
            val userProfileResponse = apiService.getProfile()
            if (userProfileResponse.isSuccessful) {
                val user = userProfileResponse.body()
                userManager.setCurrentUser(user)
                println("Perfil de usuario obtenido y guardado.")
            } else {
                println("Error al obtener perfil de usuario: ${userProfileResponse.code()}")
                userManager.clearCurrentUser() // limpiar si no se obtiene perfil
            }
        } catch (e: Exception) {
            println("Excepción al obtener perfil de usuario: $e")
            userManager.clearCurrentUser() // Limpiar en caso de error
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
        userManager.clearCurrentUser()
        println("Usuario deslogueado.")
    }

    //Para reset password por mail
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            val response = apiService.sendPasswordResetEmail(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Fallo al enviar e-mail: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return try {
            val response = apiService.resetPassword(ResetPasswordRequest(token, newPassword))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error reseteando contraseña: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateToken(newToken: String) {
        tokenManager.saveToken(newToken)
    }
    // para reset password desde perfil
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val response = apiService.changePassword(ChangePasswordRequest(currentPassword, newPassword))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al cambiar la contraseña: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

