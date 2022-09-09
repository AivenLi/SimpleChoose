package com.example.simplechoose.pages.simpleChoose

import com.example.simplechoose.bean.dto.TestPaperDTO
import com.example.simplechoose.mvp.BaseModel
import com.example.simplechoose.net.callback.RequestCallback
import com.example.simplechoose.net.request.BaseRequest
import com.example.simplechoose.pages.simpleChoose.api.TestPaperListApi

class TestPaperListModelImpl: BaseModel<TestPaperListApi>(TestPaperListApi::class.java), TestPaperContract.Model {
    override fun getTestPaperList(
        url: String,
        requestCallback: RequestCallback<ArrayList<TestPaperDTO>>
    ) {
        BaseRequest.request(service.getTestPaperList(url), requestCallback)
    }
}