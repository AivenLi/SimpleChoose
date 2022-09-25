package com.aiven.simplechoose.pages.home

import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.mvp.BasePresenter
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.net.callback.RequestCallback
import io.reactivex.rxjava3.disposables.Disposable

class HomePresenter: BasePresenter<HomeContract.Model, HomeContract.View>(), HomeContract.Presenter {

    private var isLoading = false

    override fun createModel(): HomeContract.Model {
        return HomeModelImpl()
    }

    override fun getQuestionTypeList() {
        if (isLoading) {
            return
        }
        isLoading = true
        mModel?.getQuestionTypeList(object : RequestCallback<ArrayList<TestPaperTypeDTO>> {
            override fun onRequestStart(d: Disposable?) {
                mView?.onRequestStart(d)
            }
            override fun onSuccess(data: ArrayList<TestPaperTypeDTO>?) {
                data?.let { mView?.getQuestionListTypeSuccess(it) }
            }

            override fun onFailure(error: BaseError) {
                mView?.getQuestionListTypeFailure(error)
            }

            override fun onRequestFinish() {
                isLoading = false
                mView?.onRequestFinish()
            }
        })
    }

    override fun checkAppUpdate() {
        mModel?.checkAppUpdate(object : RequestCallback<UpdateAppDTO> {
            override fun onSuccess(data: UpdateAppDTO?) {
                data?.let { mView?.checkAppUpdateSuccess(it) }
            }

            override fun onFailure(error: BaseError) {

            }
        })
    }
}