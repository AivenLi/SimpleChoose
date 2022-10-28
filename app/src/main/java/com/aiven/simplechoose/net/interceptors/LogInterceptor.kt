package com.aiven.simplechoose.net.interceptors

import android.os.Handler
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LogInterceptor(
    private val handler: Handler
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val stringBuffer = StringBuffer();
        stringBuffer.append("Url: ")
        stringBuffer.append(request.url())
        stringBuffer.append("\nMethod: ")
        stringBuffer.append(request.method())
        Log.d("HttpLog, Request", "Url --> ${request.url()}")
        val response = chain.proceed(request)
        val responseBody = response.peekBody(1024 * 1024)
        stringBuffer.append("\nCode: ")
        stringBuffer.append(response.code())
        stringBuffer.append("\nData: \n")
        val data = responseBody.string()
        stringBuffer.append(data)
        Log.d("HttpLog, Response", "Url <-- ${request.url()}, code: ${response.code()}, body: $data")
        handler.sendMessage(handler.obtainMessage(12345, stringBuffer.toString()))
        return response
    }
}