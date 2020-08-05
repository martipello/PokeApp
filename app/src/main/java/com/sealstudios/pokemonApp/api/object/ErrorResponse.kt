package com.sealstudios.pokemonApp.api.`object`

class ErrorResponse(code: Int, message: String) : Throwable("($code) $message")