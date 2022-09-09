package com.example.simplechoose.pages.home.api

import com.example.simplechoose.bean.dto.TestPaperTypeDTO
import com.example.simplechoose.net.callback.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface HomeApi {

    @GET("question-list.json")
    fun getQuestionTypeList() : Observable<BaseResponse<ArrayList<TestPaperTypeDTO>>>
}