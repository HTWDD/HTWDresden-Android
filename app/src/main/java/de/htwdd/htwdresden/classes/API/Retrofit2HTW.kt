package de.htwdd.htwdresden.classes.API

import android.content.Context
import de.htwdd.htwdresden.utils.extensions.guard
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit2HTW private constructor(context: Context) {
    val retrofit: Retrofit

    init {
        val okHttpClient = OkHttpClient.Builder()
            .cache(Cache(context.applicationContext.cacheDir, (10 * 1024 * 1024).toLong()))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://www2.htw-dresden.de/~app/API/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        private val instance: Retrofit2HTW? = null

        fun getInstance(context: Context): Retrofit2HTW {
            instance.guard { return Retrofit2HTW(context) }
            return instance!!
        }
    }
}