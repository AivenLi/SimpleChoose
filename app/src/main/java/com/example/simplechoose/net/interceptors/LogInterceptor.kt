package com.example.simplechoose.net.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LogInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d("HttpLog, Request", "Url --> ${request.url()}")
        val response = chain.proceed(request)
        val responseBody = response.peekBody(1024 * 1024)
        Log.d("HttpLog, Response", "Url <-- ${request.url()}, code: ${response.code()}, body: ${responseBody.string()}")
        return response
    }
}