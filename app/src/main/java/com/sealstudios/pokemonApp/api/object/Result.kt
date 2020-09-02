package com.sealstudios.pokemonApp.api.`object`


sealed class Result<out T : Any> {
    data class Success<out T : Any>(val value: T) : Result<T>()
    data class Error(val errorHolder: ErrorHolder) : Result<Nothing>()
}
