package com.example.watchfinder.data.UiState

// Puedes ponerlo en un archivo UiState.kt o dentro del ViewModel
data class RegisterUiState(
    val nameInput: String = "",
    val nickInput: String = "",
    val passwordInput: String = "",
    val repeatPasswordInput: String = "",
    val birthDateInput: String = "",
    val emailInput: String = "",
    val isLoading: Boolean = false,
    val registrationError: String? = null,
    val registrationSuccess: Boolean = false,
    val isPasswordVisible: Boolean = false, // Para el icono del ojo
    val isRepeatPasswordVisible: Boolean = false // Para el icono del ojo
)