package com.aiven.simplechoose.pages.home

import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.mvp.BaseModel
import com.aiven.simplechoose.net.BaseRequest
import com.aiven.simplechoose.net.callback.RequestCallback
import com.aiven.simplechoose.pages.home.api.HomeApi
import com.google.gson.reflect.TypeToken

class HomeModelImpl: BaseModel<HomeApi>(HomeApi::class.java), HomeContract.Model {

    private val type = object : TypeToken<ArrayList<TestPaperTypeDTO>>(){}.type

    override fun getQuestionTypeList(requestCallback: RequestCallback<ArrayList<TestPaperTypeDTO>>) {
        BaseRequest.requestWithCache(
            observable = service.getQuestionTypeList(),
            key = HomeApi.CACHE_KEY_QUESTION_LIST_JSON,
            type = type,
            requestCallback = requestCallback
        )
    }
}