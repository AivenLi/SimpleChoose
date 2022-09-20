package com.aiven.simplechoose.pages.testPaper

import com.aiven.simplechoose.bean.dto.TestPaperDTO
import com.aiven.simplechoose.mvp.BaseModel
import com.aiven.simplechoose.net.BaseRequest
import com.aiven.simplechoose.net.callback.RequestCallback
import com.aiven.simplechoose.pages.testPaper.api.TestPaperListApi
import com.google.gson.reflect.TypeToken

class TestPaperListModelImpl: BaseModel<TestPaperListApi>(TestPaperListApi::class.java), TestPaperContract.Model {

    private val type = object : TypeToken<ArrayList<TestPaperDTO>>(){}.type

    override fun getTestPaperList(
        url: String,
        requestCallback: RequestCallback<ArrayList<TestPaperDTO>>
    ) {
        BaseRequest.requestWithCache(
            observable = service.getTestPaperList(url),
            key = url,
            type = type,
            requestCallback = requestCallback
        )
    }
}