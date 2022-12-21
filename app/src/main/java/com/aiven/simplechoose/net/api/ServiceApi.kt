package com.aiven.simplechoose.net.api

import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.net.callback.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Url

interface ServiceApi {

    @Headers("accessToken:123", "testHeader:true")
    @GET("update_app.json")
    fun checkAppUpdate() : Observable<BaseResponse<UpdateAppDTO>>
}