package com.example.simplechoose.pages.home

import com.example.simplechoose.bean.dto.TestPaperTypeDTO
import com.example.simplechoose.mvp.BasePresenter
import com.example.simplechoose.net.callback.BaseError
import com.example.simplechoose.net.callback.RequestCallback

class HomePresenter: BasePresenter<HomeContract.Model, HomeContract.View>(), HomeContract.Presenter {
    override fun createModel(): HomeContract.Model {
        return HomeModelImpl()
    }

    override fun getQuestionTypeList() {
        mModel?.getQuestionTypeList(object : RequestCallback<ArrayList<TestPaperTypeDTO>> {
            override fun onSuccess(data: ArrayList<TestPaperTypeDTO>?) {
                data?.let { mView?.getQuestionListTypeSuccess(it) }
            }

            override fun onFailure(error: BaseError) {
                mView?.getQuestionListTypeFailure(error)
            }

            override fun onRequestFinish() {
                mView?.onRequestFinish()
            }
        })
    }
}