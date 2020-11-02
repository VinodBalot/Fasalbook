package com.wasfat.network

import android.util.Log
import com.wasfat.base.WasFatApp
import com.wasfat.utils.BuildConfig
import com.wasfat.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestApiFactory {

    fun create(): RestApi {
        val sessionManager: SessionManager = SessionManager.getInstance(WasFatApp.wasfatApplication!!)
        val httpClient = OkHttpClient.Builder()
        val logLevel = HttpLoggingInterceptor.Level.BODY
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = logLevel
        httpClient.addInterceptor { chain ->
            val request = chain.request().newBuilder().build()
            if (BuildConfig.DEBUG)
                Log.d("authToken", sessionManager.authToken!!)
            chain.proceed(request)
        }.addInterceptor(interceptor)
        httpClient.connectTimeout(120, TimeUnit.SECONDS)

        val retrofit = Retrofit.Builder().baseUrl(BuildConfig.SERVER_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
        return retrofit.create(RestApi::class.java)
    }

    private var retrofit: Retrofit? = null

    @JvmStatic
    fun getClient(): Retrofit? {
        if (retrofit == null) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
            httpClient.connectTimeout(120, TimeUnit.SECONDS)
            httpClient.readTimeout(120, TimeUnit.SECONDS)
            httpClient.writeTimeout(120, TimeUnit.SECONDS)
            httpClient.addInterceptor(logging)
            retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.SERVER_PATH).client(httpClient.build()).build()
        }
        return retrofit
    }


}