package com.example.watchfinder.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val username: String? = null, // Assuming username might be sent for identification or could be updated
    val email: String? = null,     // Email field for updates
    val profileImageUrl: String? = null // Include if this endpoint also handles image URL (though PATCH is separate)
    // Add other fields here that this endpoint is intended to update
    // Based on backend UserController.java updateProfile, it only uses email
)