package com.sealstudios.pokemonApp.api.states

enum class ErrorCodes(val code: Int, val message: String) {
    INTERNAL_SERVER(code = 500, message = "Internal server error"),
    BAD_REQUEST(code = 400, message = "Bad request"),
    UNAUTHORIZED(code = 401, message = "Unauthorized"),
    FORBIDDEN(code = 403, message = "Forbidden"),
    NOT_FOUND(code = 404, message = "Not found"),
    SOCKET_TIME_OUT(code = -1, message = "Socket timeout"),
    SUCCESS(code = 200, message = "Success");
}


