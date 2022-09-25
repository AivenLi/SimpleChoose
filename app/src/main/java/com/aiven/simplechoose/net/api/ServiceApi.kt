package com.aiven.simplechoose.net.api

import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.net.callback.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface ServiceApi {

    @GET("update_app.json")
    fun checkAppUpdate() : Observable<BaseResponse<UpdateAppDTO>>
}