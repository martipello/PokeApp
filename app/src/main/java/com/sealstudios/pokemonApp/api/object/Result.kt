package com.sealstudios.pokemonApp.api.`object`

import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val value: T) : Result<T>()
    data class Error(val message: String, val cause: Exception? = null) : Result<Nothing>()
}

//sealed class PokemonResult {
//    data class Success(val pokemon: Pokemon) : PokemonResult()
//    data class Error(val message: String, val cause: Exception? = null) : PokemonResult()
//}

//suspend fun <T, R> Call<T>.awaitResult(map: (T) -> R): Result<R> = suspendCancellableCoroutine { continuation ->
//    try {
//        enqueue(object : Callback<T> {
//            override fun onFailure(call: Call<T>, throwable: Throwable) {
//                errorHappened(throwable)
//            }
//
//            override fun onResponse(call: Call<T>, response: Response<T>) {
//                if (response.isSuccessful) {
//                    try {
//                        continuation.resume(Result.Success(map(response.body()!!)))
//                    } catch (throwable: Throwable) {
//                        errorHappened(throwable)
//                    }
//                } else {
//                    errorHappened(HttpException(response))
//                }
//            }
//
//            private fun errorHappened(throwable: Throwable) {
//                continuation.resume(Result.Failure(asNetworkException(throwable)))
//            }
//        })
//    } catch (throwable: Throwable) {
//        continuation.resume(Result.Failure(asNetworkException(throwable)))
//    }
//
//    continuation.invokeOnCancellation {
//        cancel()
//    }}


//private fun asNetworkException(throwable: Throwable): ErrorHolder {
//    return ErrorHolder.NetworkConnection(throwable = throwable, message = throwable.message ?: "Empty error message")
//}