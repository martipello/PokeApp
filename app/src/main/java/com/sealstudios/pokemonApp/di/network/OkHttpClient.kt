package com.sealstudios.pokemonApp.di.network

import android.content.Context
import com.sealstudios.pokemonApp.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OkHttpClient {

    companion object {

        fun getOkHttpClient(context: Context): okhttp3.OkHttpClient.Builder {
            val okHttpClient = okhttp3.OkHttpClient.Builder()
            okHttpClient.connectTimeout(2, TimeUnit.MINUTES)
            okHttpClient.readTimeout(2, TimeUnit.MINUTES)
            okHttpClient.writeTimeout(2, TimeUnit.MINUTES)

            when {
                BuildConfig.DEBUG -> {
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BODY
                    okHttpClient.addInterceptor(logging)
                    okHttpClient.addInterceptor(NetworkConnectionInterceptor(context))
                }
            }
            return okHttpClient
        }
    }
}