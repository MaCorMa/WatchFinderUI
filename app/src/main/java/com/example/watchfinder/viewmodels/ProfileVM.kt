package com.example.watchfinder.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.example.watchfinder.api.ApiService
import com.example.watchfinder.api.RetroFitClient
import com.example.watchfinder.data.UiState.ProfileUiState
import com.example.watchfinder.data.UserManager
import com.example.watchfinder.data.dataStore
import com.example.watchfinder.data.dto.UserData
import com.example.watchfinder.data.model.User
import com.example.watchfinder.repository.AuthRepository
import com.example.watchfinder.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileVM @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val appContext: Context,
    private val userManager: UserManager,
    val imageLoader: ImageLoader,
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    //se importa extension de Context para acceder a DataStore
    private val dataStore: DataStore<Preferences> = appContext.dataStore

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    //estado para el dark mode
    private val _isDarkMode = MutableStateFlow(false) // Valor inicial: modo claro
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Estado para controlar si se está editando el perfil
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    // Estado para controlar si se está editando password
    private val _isEditingPass = MutableStateFlow(false)
    val isEditingPass: StateFlow<Boolean> = _isEditingPass.asStateFlow()

    // Estado para controlar la visibilidad del campo de confirmación de borrado
    private val _showDeleteConfirmation = MutableStateFlow(false)
    val showDeleteConfirmation: StateFlow<Boolean> = _showDeleteConfirmation.asStateFlow()

    // Estado para almacenar el texto de confirmación
    private val _deleteConfirmationText = MutableStateFlow("")
    val deleteConfirmationText: StateFlow<String> = _deleteConfirmationText.asStateFlow()

    //para salir a unathenticated tras borrado cuenta
    var onAccountDeleted: (() -> Unit)? = null

    init {
        // mira el userflow de usermanager para persistencia de datos
        viewModelScope.launch {
            userManager.userFlow.collect { user ->
                Log.d("ProfileVM", "userFlow collected: ${user?.username}, Image: ${user?.profileImageUrl}")
                _uiState.update { currentState ->
                    val fullUrl = if (!user?.profileImageUrl.isNullOrEmpty()) {
                        user?.profileImageUrl
                    } else {
                        null
                    }
                    currentState.copy(
                        user = user,
                        newEmail = user?.email ?: "",
                        newUserName = user?.username ?: "",
                        fullProfileImageUrl = fullUrl,
                        isLoading = false
                    )
                }
            }
        }
        // carga preferencia Darkmode
        loadDarkModePreference()

        // Actualiza perfil desde backend
        fetchUserProfile()
        fetchProfileImage()
    }
    // actualiza profile desde backend
    private fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getUserProfile()
                // si ok, actualiza UserManager
                // UserManager guarda en DataStore y pasa a userFlow
                userManager.setCurrentUser(user)
                // userFlow collector en el init actualiza _uiState con el user
                _uiState.update { it.copy(isLoading = false) }
                Log.d("ProfileVM", "Perfil de User cargado correctamente.")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar perfil: ${e.message ?: "Desconocido"}",
                        user = null
                    )
                }
                Log.e("ProfileVM", "Error al cargar perfil de usuario", e)
            }
        }
    }

    fun onEmailInputChanged(email: String) {
        _uiState.update { it.copy(newEmail = email, isEmailUpdateSuccess = false, error = null, emailEdited = true) }
    }

    fun onUsernameInputChanged(username: String) {
        _uiState.update { it.copy(newUserName = username, isUserNameUpdateSuccess = false, error = null, usernameEdited = true) }
    }
    fun updateProfileImage(imageUri: Uri?) {
        if (imageUri == null) {
            _uiState.update { it.copy(error = "No se ha seleccionado ninguna imagen") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, isImageUpdateSuccess = false) }

        viewModelScope.launch {
            try {
                val imageBytes = appContext.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    inputStream.readBytes()
                }

                if (imageBytes != null) {
                    val result = userRepository.uploadProfileImage(imageBytes)

                    result.onSuccess { newImageUrl ->
                        Log.d("ProfileVM", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA    Image upload OK, new URL: $newImageUrl")
                        Log.d("ProfileVM", "carga imagen OK, llamando a userManager.updateProfileImageUrl")
                        // actualiza UserManager con la neuva URL de imagen
                        // UserManager actualiza User y guarda en DataStore
                        userManager.updateProfileImageUrl(newImageUrl)

                        // userFlow collector actualiza uiState
                        _uiState.update { it.copy(isLoading = false, isImageUpdateSuccess = true) }
                        Log.d("ProfileVM", "carga imagen OK, nueva URL enviada a UserManager: $newImageUrl.")

                    }.onFailure { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message ?: "Error al subir imagen") }
                        Log.e("ProfileVM", "Error subiendo imagen", error)
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "No se pudieron leer los bytes de la imagen") }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error desconocido al procesar imagen") }
                Log.e("ProfileVM", "General exception processing image", e)
            }
        }
    }

    fun saveProfileChanges() {
        // Solo guardar si se está editando
        if (!_isEditing.value) {
            Log.d("ProfileVM", "No se está editando, no se guardan los cambios.")
            return
        }

        val currentState = _uiState.value

        // determina que campos se han modificado y necesitan actualizarse
        val emailToUpdate = if (currentState.emailEdited) currentState.newEmail else null
        val usernameToUpdate = if (currentState.usernameEdited) currentState.newUserName else null

        // sólo continua si al menos un campo ha sido modificado
        if (emailToUpdate == null && usernameToUpdate == null) {
            Log.d("ProfileVM", "No hay cambios en el perfil, guarda sólo Darkmode.")
            saveDarkModePreference(isDarkMode.value)
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, isEmailUpdateSuccess = false, isUserNameUpdateSuccess = false) }

        viewModelScope.launch {
            try {
                val response = apiService.updateProfile(UserData(email = emailToUpdate, username = usernameToUpdate))

                if (response.isSuccessful) {
                    val updatedUserData = response.body()
                    val newToken = response.headers()["Authorization"]?.substringAfter("Bearer ")

                    if (updatedUserData != null && newToken != null) {
                        Log.d("ProfileVM", "Actualización perfil OK, llamando userManager.setCurrentUser")

                        // actualiza token nuevo con AuthRepository
                        authRepository.updateToken(newToken)
                        //actualiza current user tras cambios
                        val user = userRepository.getUserProfile()
                        userManager.setCurrentUser(user)

                        Log.d("ProfileVM", "New token updated via AuthRepository")

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isEmailUpdateSuccess = emailToUpdate != null,
                                isUserNameUpdateSuccess = usernameToUpdate != null,
                                emailEdited = false,
                                usernameEdited = false
                            )
                        }
                        Log.d("ProfileVM", "Campos (email/username) actualizados con éxito.")
                        saveDarkModePreference(isDarkMode.value)

                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Error: Usuario actualizado pero no se recibió nuevo token.") }
                        Log.e("ProfileVM", "Error: Usuario actualizado pero no se recibió nuevo token.")
                    }

                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error al actualizar el perfil: ${response.message()}") }
                    Log.e("ProfileVM", "Error al actualizar el perfil: ${response.message()}")
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error desconocido al actualizar el perfil") }
                Log.e("ProfileVM", "Error desconocido al actualizar el perfil", e)
            } finally {
                saveDarkModePreference(isDarkMode.value)
            }
        }
    }
    fun setEditing(value: Boolean) {
        _isEditing.value = value
    }
    fun setPasswordEditing(value: Boolean) {
        _isEditingPass.value = value
        Log.d("ProfileVM", "isEditingPass: ${_isEditingPass.value}")
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccessFlags() {
        _uiState.update {
            it.copy(
                isEmailUpdateSuccess = false,
                isUserNameUpdateSuccess = false,
                isImageUpdateSuccess = false
            )
        }
    }

    private fun loadDarkModePreference() {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val darkMode = preferences[DARK_MODE_KEY] ?: false // Valor por defecto: false
                _isDarkMode.value = darkMode
            }
        }
    }
    fun setDarkMode(value: Boolean) {
        _isDarkMode.value = value
    }
    fun saveDarkModePreference(value: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[DARK_MODE_KEY] = value
            }
        }
    }
    fun fetchProfileImage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val imageUrlResult = userRepository.getProfileImageUrl()
                imageUrlResult.onSuccess { imageUrl ->
                    _uiState.update { it.copy(fullProfileImageUrl = imageUrl, isLoading = false) }
                    Log.d("ProfileVM", "fetchProfileImage: Image URL fetched successfully: $imageUrl")
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "Error fetching image URL") }
                    Log.e("ProfileVM", "fetchProfileImage: Error fetching image URL: $error", error)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error fetching image") }
                Log.e("ProfileVM", "fetchProfileImage: General error fetching image", e)
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.changePassword(currentPassword, newPassword)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, error = "Contraseña cambiada con éxito") }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Error al cambiar la contraseña") }
            }
        }
    }

    // Función para actualizar el texto de confirmación
    fun updateDeleteConfirmationText(text: String) {
        _deleteConfirmationText.value = text
    }

    fun toggleDeleteConfirmation() {
        val oldValue = _showDeleteConfirmation.value
        val newValue = !oldValue
        _showDeleteConfirmation.value = newValue
        Log.d("ProfileVM", "Toggle delete confirmation: $oldValue -> $newValue")
    }

    // Función para realizar el borrado de la cuenta
    fun deleteAccount() {
        viewModelScope.launch {
            if (_deleteConfirmationText.value.trim() == "DELETE") {
                _uiState.update { it.copy(isLoading = true, error = null) }
                try {
                    // Llama a la función del repositorio para eliminar la cuenta
                    userRepository.deleteAccount()

                    // Limpiar datos de usuario y token
                    authRepository.logout()

                    _uiState.update { it.copy(isLoading = false) }

                    onAccountDeleted?.invoke()
                    Log.d("ProfileVM", "onAccountDeleted callback invoked")


                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al eliminar la cuenta") }
                } finally {
                    // Ocultar el campo de confirmación
                    _showDeleteConfirmation.value = false
                    _deleteConfirmationText.value = ""
                }
            } else {
                _uiState.update { it.copy(error = "Texto de confirmación incorrecto") }
            }
        }
    }
}

