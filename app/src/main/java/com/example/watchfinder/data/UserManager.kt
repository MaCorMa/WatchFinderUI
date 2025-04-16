package com.example.watchfinder.data

import com.example.watchfinder.api.ApiService
import com.example.watchfinder.data.dto.LoginRequest
import com.example.watchfinder.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// data/session/UserManager.kt
@Singleton // Hilt lo hará Singleton si lo provees como tal en un módulo
class UserManager @Inject constructor() { // @Inject constructor si no necesita nada más
    private val _userFlow = MutableStateFlow<User?>(null)
    val userFlow: StateFlow<User?> = _userFlow.asStateFlow()

    // Llamado después del login exitoso
    fun setCurrentUser(user: User?) {
        _userFlow.value = user
    }

    // Llamado al hacer logout
    fun clearCurrentUser() {
        _userFlow.value = null
    }

    // Propiedad para acceso síncrono si estás seguro que no es null (¡cuidado!)
    val currentUser: User?
        get() = _userFlow.value
}

