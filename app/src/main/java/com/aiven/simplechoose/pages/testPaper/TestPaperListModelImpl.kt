package com.aiven.simplechoose.pages.testPaper

import com.aiven.simplechoose.bean.dto.TestPaperDTO
import com.aiven.simplechoose.mvp.BaseModel
import com.aiven.simplechoose.net.callback.RequestCallback
import com.aiven.simplechoose.net.request.BaseRequest
import com.aiven.simplechoose.pages.testPaper.api.TestPaperListApi

class TestPaperListModelImpl: BaseModel<TestPaperListApi>(TestPaperListApi::class.java), TestPaperContract.Model {
    override fun getTestPaperList(
        url: String,
        requestCallback: RequestCallback<ArrayList<TestPaperDTO>>
    ) {
        BaseRequest.request(service.getTestPaperList(url), requestCallback)
    }
}