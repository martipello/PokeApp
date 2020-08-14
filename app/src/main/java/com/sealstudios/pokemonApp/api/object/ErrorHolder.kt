package com.sealstudios.pokemonApp.api.`object`

sealed class ErrorHolder(override val message: String):Throwable(message) {
    data class NetworkConnection(override val message: String, val throwable: Throwable) : ErrorHolder(message)
    data class BadRequest(override val message: String, val throwable: Throwable) : ErrorHolder(message)
}