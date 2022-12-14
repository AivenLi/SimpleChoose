package com.aiven.simplechoose.pages.testPaper.api

import com.aiven.simplechoose.bean.dto.TestPaperDTO
import com.aiven.simplechoose.net.callback.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface TestPaperListApi {

    @GET
    fun getTestPaperList(
        @Url url: String
    ) : Observable<BaseResponse<ArrayList<TestPaperDTO>>>
}