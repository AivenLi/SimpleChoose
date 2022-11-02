package com.aiven.simplechoose.net

import android.content.Context
import com.aiven.hfl.util.FloatManager
import com.aiven.simplechoose.app.task.Task
import com.aiven.simplechoose.app.task.TaskApp
import com.aiven.simplechoose.net.api.ServiceApi
import com.aiven.simplechoose.net.interceptors.LogInterceptor
import com.aiven.simplechoose.net.interceptors.TokenInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RetrofitUtil: Task {

    private var retrofit: Retrofit? = null
    const val TIME_OUT = 8000L

    private var isInit = false

    override fun run(app: TaskApp) {
        if (isInit) {
            return
        }
        isInit = true
        getRetrofit()
    }

    private fun getRetrofit() : Retrofit {
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
            //.addInterceptor(TokenInterceptor())
            //.addInterceptor(LogInterceptor())
            .addInterceptor(com.aiven.hfl.LogInterceptor(FloatManager.getInstance(null).handler))
            .build()
    }
}