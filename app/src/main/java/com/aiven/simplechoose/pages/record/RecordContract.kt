package com.aiven.simplechoose.pages.record

import com.aiven.simplechoose.db.DBCallback
import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.mvp.IModel
import com.aiven.simplechoose.mvp.IPresenter
import com.aiven.simplechoose.mvp.IView
import com.aiven.simplechoose.net.callback.RequestCallback

interface RecordContract {

    interface View: IView {

        fun getRecordSuccess(records: List<TestPaperRecord>, isRefresh: Boolean)
        fun getRecordFailure(error: String)
        fun getRecordFinish()

        fun deleteRecordSuccess()
        fun deleteRecordFailure(error: String)
    }

    interface Presenter: IPresenter<View> {

        fun getRecord(refresh: Boolean)

        fun deleteRecord(id: Long)

        fun deleteRecord(record: TestPaperRecord)
    }

    interface Model: IModel {

        fun getRecordByPage(page: Int, size: Int, dbCallback: DBCallback<List<TestPaperRecord>>)

        fun deleteRecord(id: Long, dbCallback: DBCallback<Unit>)

        fun deleteRecord(record: TestPaperRecord, dbCallback: DBCallback<Unit>)
    }
}