package com.aiven.simplechoose.pages.record

import android.content.Context
import android.content.Intent
import android.util.Log
import com.aiven.simplechoose.databinding.ActivityRecordBinding
import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.mvp.MVPActivity

class RecordActivity : MVPActivity<ActivityRecordBinding, RecordContract.View, RecordContract.Presenter>(
    ActivityRecordBinding::inflate
), RecordContract.View {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, RecordActivity::class.java))
        }
    }

    override fun createPresenter(): RecordContract.Presenter {
        return RecordPresenter()
    }

    override fun initData() {
        mPresenter.getRecordSuccess(true)
    }

    override fun initView() {

    }

    override fun initClick() {

    }

    override fun getDebugTAG(): String {
        return RecordActivity::class.java.simpleName
    }

    override fun getRecordSuccess(records: List<TestPaperRecord>) {
        Log.d(TAG, "获取成功：${records.size}")
    }

    override fun getRecordFailure(error: String) {
        Log.d(TAG, "获取失败：$error")
    }
}