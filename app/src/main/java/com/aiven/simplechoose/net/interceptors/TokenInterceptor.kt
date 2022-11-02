package com.aiven.simplechoose.net.interceptors;

import android.util.Log
import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.net.RetrofitUtil
import com.aiven.simplechoose.net.api.TokenApi
import com.aiven.simplechoose.net.callback.BaseResponse
import com.aiven.updateapp.bean.UpdateAppBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TokenInterceptor: Interceptor {

    private var token: String? = null

    companion object {
        private const val TAG = "TokenInterceptor"
    }

    private val tokenApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://aivenli.github.io/cputest/")
            .client(getOkhttpClient())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenApi::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val response = chain.proceed(request)
        Log.d(TAG, "假设token过期：${request.url()}")
        val token = refreshToken()
        if (token.isNullOrEmpty()) {
            Log.d(TAG, "重新刷新token，但是失败了")
            return response
        }
        Log.d(TAG, "Header[accessToken] = ${request.header("accessToken")}")
        Log.d(TAG, "Header[testHeader] = ${request.header("testHeader")}")
        request = request.newBuilder()
            .removeHeader("accessToken")
            .header("accessToken", token)
            .build()
        Log.d(TAG, "最终Headers：${request.headers()}")
        return chain.proceed(request)
    }

    @Synchronized
    private fun refreshToken(): String? {
        Log.d(TAG, "开始获取token")
        if (token.isNullOrEmpty()) {
            Log.d(TAG, "token为空，重新获取token")
            val call = tokenApi.checkAppUpdate()
            val responseBody = call.execute()
            if (responseBody.isSuccessful) {
                responseBody.body()?.string()?.let {
                    val gson = Gson()
                    val type = object : TypeToken<BaseResponse<UpdateAppDTO>>(){}.type
                    runCatching {
                        gson.fromJson<BaseResponse<UpdateAppDTO>>(it, type)
                    }.onSuccess {
                        if (it.code == 0) {
                            token = it.msg
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "token不为空，直接返回")
        }
        Log.d(TAG, "获取token结束，token：$token")
        return token
    }

    private fun getOkhttpClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(RetrofitUtil.TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(RetrofitUtil.TIME_OUT, TimeUnit.MILLISECONDS)
            .build()
    }
}
