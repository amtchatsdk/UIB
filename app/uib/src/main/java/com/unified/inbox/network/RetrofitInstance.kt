package com.unified.inbox.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class RetrofitInstance {
    private var mRetrofit: Retrofit? = null

    fun getRetrofitInstance(BASE_URL: String): Retrofit {
        //val httpLoggingInterceptor = HttpLoggingInterceptor()
        //httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()

        // comment when going for Live

        // comment when going for Live
        //httpClient.addInterceptor(httpLoggingInterceptor)
        httpClient.connectTimeout(15000, TimeUnit.MILLISECONDS)
        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        return mRetrofit!!
    }
}