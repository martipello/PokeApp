package com.sealstudios.pokemonApp.api.`object`

enum class ErrorCodes(val code: Int) {
    BAD_REQUEST(400),
    INTERNAL_SERVER(503),
    UNAUTHORIZED(403),
    NOT_FOUND(404)
}
