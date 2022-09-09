package com.example.simplechoose.pages.simpleChoose

import com.example.simplechoose.bean.dto.TestPaperDTO
import com.example.simplechoose.mvp.BasePresenter
import com.example.simplechoose.net.callback.BaseError
import com.example.simplechoose.net.callback.RequestCallback

class TestPaperListPresenter: BasePresenter<TestPaperContract.Model, TestPaperContract.View>(), TestPaperContract.Presenter {
    override fun createModel(): TestPaperContract.Model {
        return TestPaperListModelImpl()
    }

    override fun getTestPaperList(url: String) {
        mModel?.getTestPaperList(url, object : RequestCallback<ArrayList<TestPaperDTO>> {
            override fun onSuccess(data: ArrayList<TestPaperDTO>?) {
                data?.let { mView?.getTestPaperListSuccess(it) }
            }

            override fun onFailure(error: BaseError) {
                mView?.onRequestError(error)
            }

            override fun onRequestFinish() {
                mView?.onRequestFinish()
            }
        })
    }
}