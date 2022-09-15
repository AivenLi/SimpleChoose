package com.example.simplechoose.pages.testPaperDetail

import com.example.simplechoose.bean.dto.QuestionDTO
import com.example.simplechoose.mvp.BasePresenter
import com.example.simplechoose.net.callback.BaseError
import com.example.simplechoose.net.callback.RequestCallback
import com.example.simplechoose.pages.result.bean.ResultBean
import io.reactivex.rxjava3.disposables.Disposable

class TestPaperDetailPresenter: BasePresenter<TestPaperDetailContract.Model, TestPaperDetailContract.View>(), TestPaperDetailContract.Presenter {

    private var isLoading = false

    override fun createModel(): TestPaperDetailContract.Model {
        return TestPaperDetailModelImpl()
    }

    override fun getTestPaperDetail(url: String) {
        if (isLoading) {
            return
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return
        }
        isLoading = true
        mModel?.getTestPaperDetail(url, object : RequestCallback<ArrayList<QuestionDTO>> {
            override fun onRequestStart(d: Disposable) {
                mView?.onRequestStart(d)
            }

            override fun onSuccess(data: ArrayList<QuestionDTO>?) {
                data?.let { mView?.getTestPaperDetailSuccess(it) }
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

    override fun submitTestPaper(
        title: String,
        useTime: Long,
        questionDTOList: ArrayList<QuestionDTO>
    ) {
        mModel?.submitTestPaper(
            title,
            useTime,
            questionDTOList,
            object : RequestCallback<ResultBean> {

                override fun onSuccess(data: ResultBean?) {
                    mView?.getTestPaperResultSuccess(data!!)
                }

                override fun onFailure(error: BaseError) {

                }
            }
        )
    }
}