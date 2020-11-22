package com.sealstudios.pokemonApp.api.`object`

import com.sealstudios.pokemonApp.api.states.ErrorCodes
import retrofit2.HttpException
import java.net.SocketTimeoutException

open class ResponseHandler {
    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(getErrorMessage(ErrorCodes.SOCKET_TIME_OUT.code), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            ErrorCodes.BAD_REQUEST.code -> "Timeout"
            ErrorCodes.FORBIDDEN.code -> "Unauthorised"
            ErrorCodes.INTERNAL_SERVER.code -> "Not found"
            ErrorCodes.NOT_FOUND.code -> "Not found"
            ErrorCodes.UNAUTHORIZED.code -> "Not found"
            ErrorCodes.SOCKET_TIME_OUT.code -> "Not found"
            else -> "Something went wrong"
        }
    }
}