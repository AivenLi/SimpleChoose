package com.aiven.simplechoose.pages.home

import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.mvp.BaseModel
import com.aiven.simplechoose.net.callback.RequestCallback
import com.aiven.simplechoose.net.request.BaseRequest
import com.aiven.simplechoose.pages.home.api.HomeApi

class HomeModelImpl: BaseModel<HomeApi>(HomeApi::class.java), HomeContract.Model {
    override fun getQuestionTypeList(requestCallback: RequestCallback<ArrayList<TestPaperTypeDTO>>) {
        BaseRequest.request(service.getQuestionTypeList(), requestCallback)
    }
}