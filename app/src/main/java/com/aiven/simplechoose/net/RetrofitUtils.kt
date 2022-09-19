package com.aiven.simplechoose.net

import com.aiven.simplechoose.net.api.ServiceApi
import com.aiven.simplechoose.net.interceptors.LogInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RetrofitUtils {

    private var retrofit: Retrofit? = null
    const val TIME_OUT = 8000L

    fun getRetrofit() : Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder().baseUrl("https://aivenli.github.io/cputest/")
                .client(getOkhttpClient())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun <T> getService(clazz: Class<T>): T {
        return getRetrofit().create(clazz)
    }

    fun <T> getService(): ((createService: Class<T>) -> T) {
        return getRetrofit()::create
    }

    private fun getOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .addInterceptor(LogInterceptor())
            .build()
    }
}