package com.aiven.simplechoose.net.interceptors

import android.os.Handler
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LogInterceptor(): Interceptor {

    companion object {
        private const val REQ_TAG = "HttpLogRequest"
        private const val RES_TAG = "HttpLogResponse"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val t1 = System.currentTimeMillis()
        Log.d(REQ_TAG, "Url --> ${request.url()}")
        Log.d(REQ_TAG, "Headers --> ${request.headers()}")
        Log.d(REQ_TAG, "body --> ${request.body()}")
        val response = chain.proceed(request)
        val t2 = System.currentTimeMillis()
        val responseBody = response.peekBody(1024 * 1024)
        val data = responseBody.string()
        Log.d(RES_TAG, "Url <-- ${request.url()}, code: ${response.code()}, times: ${(t2 - t1) / 1000}ms, \nbody: $data")
        return response
    }
}