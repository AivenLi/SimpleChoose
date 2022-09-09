package com.example.simplechoose.pages.home

import com.example.simplechoose.bean.dto.TestPaperTypeDTO
import com.example.simplechoose.mvp.BaseModel
import com.example.simplechoose.net.callback.RequestCallback
import com.example.simplechoose.net.request.BaseRequest
import com.example.simplechoose.pages.home.api.HomeApi

class HomeModelImpl: BaseModel<HomeApi>(HomeApi::class.java), HomeContract.Model {
    override fun getQuestionTypeList(requestCallback: RequestCallback<ArrayList<TestPaperTypeDTO>>) {
        BaseRequest.request(service.getQuestionTypeList(), requestCallback)
    }
}