package com.aiven.simplechoose.pages.record

import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.mvp.IModel
import com.aiven.simplechoose.mvp.IPresenter
import com.aiven.simplechoose.mvp.IView
import com.aiven.simplechoose.net.callback.RequestCallback

interface RecordContract {

    interface View: IView {

        fun getRecordSuccess(records: List<TestPaperRecord>)
        fun getRecordFailure(error: String)
    }

    interface Presenter: IPresenter<View> {

        fun getRecordSuccess(refresh: Boolean)
    }

    interface Model: IModel {

        fun getRecordByPage(page: Int, size: Int, requestCallback: RequestCallback<List<TestPaperRecord>>)
    }
}