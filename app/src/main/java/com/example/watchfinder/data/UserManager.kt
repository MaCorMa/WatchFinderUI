package com.example.watchfinder.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.watchfinder.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


// Clave para guardar el usuario serializado en DataStore
private val USER_PREFERENCES_KEY = stringPreferencesKey("current_user")
@Singleton
class UserManager @Inject constructor(
    @ApplicationContext private val appContext: Context
) { // @Inject constructor si no necesita nada más

    private var _currentUser: User? = null

    // Accede a la instancia de DataStore definida en DataStoreUtils
    private val dataStore: DataStore<Preferences> = appContext.dataStore

    private val _userFlow = MutableStateFlow<User?>(null)
    val userFlow: StateFlow<User?> = _userFlow.asStateFlow()

    // CoroutineScope para lanzar operaciones de DataStore que necesitan persistir
    private val userScope = kotlinx.coroutines.GlobalScope

    private val userObjectKey = stringPreferencesKey("user_object_data")

    init {
        Log.d("UserManager", "Init UserManager (hashCode: ${this.hashCode()})")
        loadUserFromDataStore()
    }

    // Function to load user from DataStore
    private fun loadUserFromDataStore() = userScope.launch {
        Log.d("UserManager", "Attempting to load user from DataStore...")
        try {
            dataStore.data
                .map { preferences ->
                    val userJson = preferences[userObjectKey] // Use the key for the User object
                    userJson?.let {
                        Json.decodeFromString<User>(it)
                    }
                }
                .collect { user ->
                    // Update both the backing property and the StateFlow
                    _currentUser = user
                    _userFlow.value = user
                    if (user == null) {
                        Log.d("UserManager", "User loaded from DataStore: null")
                    } else {
                        Log.d("UserManager", "User loaded from DataStore: ${user.username}, Image: ${user.profileImageUrl}")
                    }
                }
        } catch (e: Exception) {
            // Log any errors during DataStore loading
            Log.e("UserManager", "Error loading user from DataStore", e)
            e.printStackTrace()
            // Ensure state is set to null on error
            _currentUser = null
            _userFlow.value = null
        }
    }


    // Llamado después del login exitoso
    suspend fun setCurrentUser(user: User?) {
        _currentUser = user
        _userFlow.value = user
        if (user != null) {
            user.profileImageUrl?.let { Log.e("UserManager", it) }
            saveUserToDataStore(user)
        }else{
            clearUserFromDataStore()
        }
    }

    // Llamado al hacer logout
    suspend fun clearCurrentUser() {
        _currentUser = null
        _userFlow.value = null
        clearUserFromDataStore()
    }

    suspend fun updateProfileImageUrl(newImageUrl: String?) {
        Log.d("UserManager", "Attempting to update profile image URL to: $newImageUrl")
        // Access the current user from the backing property
        val currentUser = _currentUser

        if (currentUser != null) {
            // Create a new User object with the updated image URL
            val updatedUser = currentUser.copy(profileImageUrl = newImageUrl)
            // Set this updated user as the current user.
            // setCurrentUser will update _currentUser, _userFlow, and save to DataStore.
            setCurrentUser(updatedUser)
            Log.d("UserManager", "Profile image URL updated and setCurrentUser called.")
        } else {
            // Log an error if the user is null when trying to update the image URL
            Log.e("UserManager", "Cannot update profile image URL: current user is null.")
        }
    }

    // Function to save User object to DataStore
    private suspend fun saveUserToDataStore(user: User) {
        Log.d("UserManager", "Attempting to save user to DataStore: ${user.username}")
        try {
            dataStore.edit { preferences ->
                // Serialize the User object to a JSON string
                val userJson = Json.encodeToString(user)
                // Save the JSON string using the defined key
                preferences[userObjectKey] = userJson
            }
            // Log success after the edit block completes
            Log.d("UserManager", "User successfully saved to DataStore: ${user.username}")
        } catch (e: Exception) {
            // Log any errors during DataStore saving
            Log.e("UserManager", "Error saving user to DataStore: ${user.username}", e)
            e.printStackTrace()
            // Depending on your error handling strategy, you might want to
            // clear the current user or indicate a save failure here.
            // _currentUser = null
            // _userFlow.value = null
        }
    }

    // Function to clear User data from DataStore
    private suspend fun clearUserFromDataStore() {
        Log.d("UserManager", "Attempting to clear user from DataStore.")
        try {
            dataStore.edit { preferences ->
                // Remove the user data using the defined key
                preferences.remove(userObjectKey)
            }
            Log.d("UserManager", "User successfully cleared from DataStore.")
        } catch (e: Exception) {
            // Log any errors during DataStore clearing
            Log.e("UserManager", "Error clearing user from DataStore", e)
            e.printStackTrace()
        }
    }

    //para el token de reseteo contraseña
    var resetToken: String? = null
        private set
    fun setResetToken(token: String?) {
        resetToken = token
    }
}

