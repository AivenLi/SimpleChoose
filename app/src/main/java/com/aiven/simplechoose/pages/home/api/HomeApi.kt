package com.aiven.simplechoose.pages.home.api

import com.aiven.simplechoose.bean.dto.TestBinDTO
import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.bean.vo.TestBinVo
import com.aiven.simplechoose.net.api.ServiceApi
import com.aiven.simplechoose.net.callback.BaseResponse
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface HomeApi : ServiceApi {

    @GET("question-list.json")
    fun getQuestionTypeList() : Observable<BaseResponse<ArrayList<TestPaperTypeDTO>>>

    @POST
    @Multipart
    fun testFile(@Url url: String, @Part part: List<MultipartBody.Part>): Observable<BaseResponse<Unit>>

    @POST
    fun testBin(@Url url: String, @Body testBinVo: TestBinVo): Observable<BaseResponse<Void>>

    @GET
    fun selectBin(@Url url: String): Observable<BaseResponse<List<TestBinDTO>>>

    companion object {
        const val CACHE_KEY_QUESTION_LIST_JSON = "question-list.json"
    }
}