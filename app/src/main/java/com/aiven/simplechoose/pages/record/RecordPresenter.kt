package com.aiven.simplechoose.pages.record

import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.mvp.BasePresenter
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.net.callback.RequestCallback

class RecordPresenter: BasePresenter<RecordContract.Model, RecordContract.View>(), RecordContract.Presenter {

    private val size = 20
    private var page = 0

    override fun createModel(): RecordContract.Model {
        return RecordModelImpl()
    }

    override fun getRecordSuccess(refresh: Boolean) {
        if (refresh) {
            page = 0
        }
        mModel?.getRecordByPage(
            page = page,
            size = size,
            requestCallback = object : RequestCallback<List<TestPaperRecord>> {
                override fun onSuccess(data: List<TestPaperRecord>?) {
                    data?.let { mView?.getRecordSuccess(it) }
                }

                override fun onFailure(error: BaseError) {
                    mView?.onRequestError(error)
                }

                override fun onRequestFinish() {
                    mView?.onRequestFinish()
                }
            }
        )
    }
}