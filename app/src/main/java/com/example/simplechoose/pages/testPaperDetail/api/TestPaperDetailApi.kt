package com.example.simplechoose.pages.testPaperDetail.api

import com.example.simplechoose.bean.dto.QuestionDTO
import com.example.simplechoose.net.callback.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface TestPaperDetailApi {

    @GET
    fun getTestPaperDetail(
        @Url url: String
    ) : Observable<BaseResponse<ArrayList<QuestionDTO>>>
}