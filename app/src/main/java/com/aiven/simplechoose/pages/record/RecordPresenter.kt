package com.aiven.simplechoose.pages.record

import com.aiven.simplechoose.db.DBCallback
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

    override fun getRecord(refresh: Boolean) {
        if (refresh) {
            page = 0
        }
        mModel?.getRecordByPage(
            page = page,
            size = size,
            object : DBCallback<List<TestPaperRecord>> {
                override fun onDBResult(data: List<TestPaperRecord>?) {
                    if (!data.isNullOrEmpty()) {
                        mView?.getRecordSuccess(data, refresh)
                        page++
                    }
                }

                override fun onDBError(error: String) {
                    mView?.getRecordFailure(error)
                }

                override fun onDBFinish() {
                    mView?.getRecordFinish()
                }
            }
        )
    }

    override fun deleteRecord(id: Long) {
        mModel?.deleteRecord(id, object : DBCallback<Unit> {
            override fun onDBResult(data: Unit?) {
                mView?.deleteRecordSuccess()
            }

            override fun onDBError(error: String) {
                mView?.deleteRecordFailure(error)
            }
        })
    }

    override fun deleteRecord(record: TestPaperRecord) {
        mModel?.deleteRecord(record, object : DBCallback<Unit> {
            override fun onDBResult(data: Unit?) {
                mView?.deleteRecordSuccess()
            }

            override fun onDBError(error: String) {
                mView?.deleteRecordFailure(error)
            }
        })
    }
}