package com.aiven.simplechoose.pages.record

import com.aiven.simplechoose.db.DBCallback
import com.aiven.simplechoose.db.SimpleDataBase
import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.utils.doSql


class RecordModelImpl: RecordContract.Model {

    private val recordDao by lazy {
        SimpleDataBase.getInstance().testPaperRecordDao()
    }

    override fun getRecordByPage(
        page: Int,
        size: Int,
        dbCallback: DBCallback<List<TestPaperRecord>>
    ) {
        recordDao.selectByPage(size, page).doSql(dbCallback)
    }

    override fun deleteRecord(id: Long, dbCallback: DBCallback<Unit>) {
        recordDao.deleteById(id).doSql(dbCallback)
    }

    override fun deleteRecord(record: TestPaperRecord, dbCallback: DBCallback<Unit>) {
        recordDao.delete(record).doSql(dbCallback)
    }
}