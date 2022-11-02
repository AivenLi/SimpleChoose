package com.aiven.simplechoose.net.api

import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.net.callback.BaseResponse
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface TokenApi {

    @GET("update_app.json")
    fun checkAppUpdate() : Call<ResponseBody>
}