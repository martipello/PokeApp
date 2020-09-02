package com.sealstudios.pokemonApp.api.`object`

import com.google.gson.annotations.SerializedName

data class HttpErrorEntity(
    @SerializedName("message") val errorMessage: String,
    @SerializedName("status") val errorCode: Int
)