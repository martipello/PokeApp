package com.sealstudios.pokemonApp.di.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.sealstudios.pokemonApp.api.states.ErrorCodes
import com.sealstudios.pokemonApp.api.states.ErrorHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume

class NetworkResultExtension {
    fun asNetworkException(ex: Throwable): ErrorHolder {
        return when (ex) {
            is IOException -> {
                ErrorHolder.NetworkConnection(
                    "No Internet Connection"
                )
            }
            is HttpException -> extractHttpExceptions(ex)
            else -> ErrorHolder.UnExpected("Something went wrong...")
        }
    }

    private fun extractHttpExceptions(ex: HttpException): ErrorHolder {
        val body = ex.response()?.errorBody()
        val gson = GsonBuilder().create()
        val responseBody= gson.fromJson(body.toString(), JsonObject::class.java)
        val errorEntity = gson.fromJson(responseBody, HttpErrorEntity::class.java)
        return when (errorEntity.errorCode) {
            ErrorCodes.BAD_REQUEST.code ->
                ErrorHolder.BadRequest(errorEntity.errorMessage)

            ErrorCodes.INTERNAL_SERVER.code ->
                ErrorHolder.InternalServerError(errorEntity.errorMessage)

            ErrorCodes.UNAUTHORIZED.code ->
                ErrorHolder.UnAuthorized(errorEntity.errorMessage)

            ErrorCodes.NOT_FOUND.code ->
                ErrorHolder.ResourceNotFound(errorEntity.errorMessage)

            else ->
                ErrorHolder.Unknown(errorEntity.errorMessage)

        }
    }
}

data class HttpErrorEntity(
    @SerializedName("message") val errorMessage: String,
    @SerializedName("status") val errorCode: Int
)


@ExperimentalCoroutinesApi
suspend fun <T, R : Any> Call<T>.awaitResult(map: (T) -> R): com.sealstudios.pokemonApp.api.states.Result<R> = suspendCancellableCoroutine { continuation ->
    try {
        enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, throwable: Throwable) {
                errorHappened(throwable)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    try {
                        continuation.resume(com.sealstudios.pokemonApp.api.states.Result.Success(map(response.body()!!)))
                    } catch (throwable: Throwable) {
                        errorHappened(throwable)
                    }
                } else {
                    errorHappened(HttpException(response))
                }
            }

            private fun errorHappened(throwable: Throwable) {
                continuation.resume(
                    com.sealstudios.pokemonApp.api.states.Result.Failure(
                        NetworkResultExtension().asNetworkException(throwable)
                    )
                )
            }
        })
    } catch (throwable: Throwable) {
        continuation.resume(
            com.sealstudios.pokemonApp.api.states.Result.Failure(
                NetworkResultExtension().asNetworkException(
                    throwable
                )
            )
        )
    }

    continuation.invokeOnCancellation {
        cancel()
    }}