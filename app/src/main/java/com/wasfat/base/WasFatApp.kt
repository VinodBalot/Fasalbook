package com.wasfat.base

import android.app.Application
import com.wasfat.utils.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class WasFatApp : Application() {

    companion object {
        var wasfatApplication: WasFatApp? = null
    }

    override fun onCreate() {
        super.onCreate()
        wasfatApplication = this
    }


    private var retrofit: Retrofit? = null


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