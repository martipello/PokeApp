package com.sealstudios.pokemonApp.api.`object`

sealed class ErrorHolder(override val message: String):Throwable(message){
    data class NetworkConnection(override val message: String) : ErrorHolder(message)
    data class BadRequest(override val message: String) : ErrorHolder(message)
    data class UnAuthorized(override val message: String) : ErrorHolder(message)
    data class InternalServerError(override val message: String) :ErrorHolder(message)
    data class ResourceNotFound(override val message: String) : ErrorHolder(message)
    data class UnExpected(override val message: String) : ErrorHolder(message)
    data class Unknown(override val message: String) : ErrorHolder(message)
}