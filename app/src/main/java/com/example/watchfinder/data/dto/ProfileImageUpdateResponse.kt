package com.example.watchfinder.data.dto

import com.google.gson.annotations.SerializedName

//clase  para mapear el campo de la URL en el json del backend
data class ProfileImageUpdateResponse(
    @SerializedName("profileImageUrl")
    val profileImageUrl: String
)