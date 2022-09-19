package com.aiven.simplechoose.pages.testPaper

import com.aiven.simplechoose.bean.dto.TestPaperDTO
import com.aiven.simplechoose.mvp.BasePresenter
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.net.callback.RequestCallback
import io.reactivex.rxjava3.disposables.Disposable

class TestPaperListPresenter: BasePresenter<TestPaperContract.Model, TestPaperContract.View>(), TestPaperContract.Presenter {

    private var isLoading = false

    override fun createModel(): TestPaperContract.Model {
        return TestPaperListModelImpl()
    }

    override fun getTestPaperList(url: String) {
        if (isLoading) {
            return
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return
        }
        isLoading = true
        mModel?.getTestPaperList(url, object : RequestCallback<ArrayList<TestPaperDTO>> {
            override fun onRequestStart(d: Disposable) {
                mView?.onRequestStart(d)
            }
            override fun onSuccess(data: ArrayList<TestPaperDTO>?) {
                data?.let { mView?.getTestPaperListSuccess(it) }
            }

            override fun onFailure(error: BaseError) {
                mView?.onRequestError(error)
            }

            override fun onRequestFinish() {
                isLoading = false
                mView?.onRequestFinish()
            }
        })
    }
}