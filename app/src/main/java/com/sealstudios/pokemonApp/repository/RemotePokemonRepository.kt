package com.sealstudios.pokemonApp.repository

import android.util.Log
import com.sealstudios.pokemonApp.api.PokemonService
import com.sealstudios.pokemonApp.api.`object`.PokemonAbility
import com.sealstudios.pokemonApp.api.`object`.PokemonListResponse
import com.sealstudios.pokemonApp.api.`object`.PokemonMove
import com.sealstudios.pokemonApp.api.`object`.PokemonSpecies
import retrofit2.Response
import javax.inject.Inject
import com.sealstudios.pokemonApp.api.`object`.Pokemon as apiPokemon


class RemotePokemonRepository @Inject constructor(
    private val pokemonService: PokemonService
) {

    suspend fun getAllPokemonResponse(): Response<PokemonListResponse> {
        return pokemonService.getPokemon(offset = 0, limit = 200)
    }

    suspend fun pokemonById(id: Int): Response<apiPokemon> {
        return pokemonService.getPokemonById(id, offset = 0, limit = 1)
    }

    suspend fun speciesForId(id: Int): Response<PokemonSpecies> {
        return pokemonService.getPokemonSpeciesForId(id)
    }

    suspend fun moveForId(id: Int): Response<PokemonMove> {
        return pokemonService.getPokemonMoveForId(id)
    }

    suspend fun getRemotePokemonAbilitiesForId(id: Int): Response<PokemonAbility> {
        return pokemonService.getPokemonAbilityForId(id)
    }

    suspend fun getRemotePokemonEvolutionChainForId(id: Int): Response<PokemonSpecies> {
        return pokemonService.getPokemonSpeciesForId(id)
    }

}
//
//private fun <T> Result<T>.awaitResult(any: Any): Result<T> {
//    TODO("Not yet implemented")
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
//                continuation.resume(Result.Error(asNetworkException(throwable)))
//            }
//        })
//    } catch (throwable: Throwable) {
//        continuation.resume(Result.Error(asNetworkException(throwable)))
//    }
//
//    continuation.invokeOnCancellation {
//        cancel()
//    }}
//
//
//private fun asNetworkException(ex: Throwable): ErrorHolder {
//    return when (ex) {
//        is IOException -> {
//            ErrorHolder.NetworkConnection(
//                "No Internet Connection"
//            )
//        }
//        is HttpException -> extractHttpExceptions(ex)
//        else -> ErrorHolder.UnExpected("Something went wrong...")
//    }
//}
//
//private fun extractHttpExceptions(ex: HttpException): ErrorHolder {
//    val body = ex.response()?.errorBody()
//    val gson = GsonBuilder().create()
//    val responseBody = gson.fromJson(body.toString(), JsonObject::class.java)
//    val errorEntity = gson.fromJson(responseBody, HttpErrorEntity::class.java)
//    return when (errorEntity.errorCode) {
//        ErrorCodes.BAD_REQUEST.code ->
//            ErrorHolder.BadRequest(errorEntity.errorMessage)
//
//        ErrorCodes.INTERNAL_SERVER.code ->
//            ErrorHolder.InternalServerError(errorEntity.errorMessage)
//
//        ErrorCodes.UNAUTHORIZED.code ->
//            ErrorHolder.UnAuthorized(errorEntity.errorMessage)
//
//        ErrorCodes.NOT_FOUND.code ->
//            ErrorHolder.ResourceNotFound(errorEntity.errorMessage)
//
//        else ->
//            ErrorHolder.Unknown(errorEntity.errorMessage)
//
//    }
//}

