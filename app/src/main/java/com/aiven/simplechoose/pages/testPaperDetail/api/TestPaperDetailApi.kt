package com.aiven.simplechoose.pages.testPaperDetail.api

import com.aiven.simplechoose.bean.dto.QuestionDTO
import com.aiven.simplechoose.net.callback.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface TestPaperDetailApi {

    @GET
    fun getTestPaperDetail(
        @Url url: String
    ) : Observable<BaseResponse<ArrayList<QuestionDTO>>>
}