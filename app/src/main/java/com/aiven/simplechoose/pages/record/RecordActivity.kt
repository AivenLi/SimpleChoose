package com.aiven.simplechoose.pages.record

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiven.simplechoose.databinding.ActivityRecordBinding
import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.mvp.MVPActivity
import com.aiven.simplechoose.pages.record.adapter.RecordAdapter
import com.aiven.simplechoose.utils.setSingleClickListener
import com.kennyc.view.MultiStateView

class RecordActivity : MVPActivity<ActivityRecordBinding, RecordContract.View, RecordContract.Presenter>(
    ActivityRecordBinding::inflate
), RecordContract.View {

    private val recordAdapter by lazy {
        RecordAdapter(this)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, RecordActivity::class.java))
        }
    }

    override fun createPresenter(): RecordContract.Presenter {
        return RecordPresenter()
    }

    override fun initData() {
        mPresenter.getRecord(true)
    }

    override fun initView() {
        viewBinding.recyclerView.adapter = recordAdapter
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun initClick() {
        viewBinding.imgBack.setSingleClickListener {
            finish()
        }
        viewBinding.smartRefresh.setOnRefreshListener {
            mPresenter.getRecord(true)
        }
        viewBinding.smartRefresh.setOnLoadMoreListener {
            mPresenter.getRecord(false)
        }
    }

    override fun getDebugTAG(): String {
        return RecordActivity::class.java.simpleName
    }

    override fun getRecordSuccess(records: List<TestPaperRecord>, isRefresh: Boolean) {
        Log.d(TAG, "获取成功：${records.size}, data: $records")
        if (isRefresh) {
            recordAdapter.updateData(records)
        } else {
            recordAdapter.appendData(records)
        }
    }

    override fun getRecordFailure(error: String) {
        Log.d(TAG, "获取失败：$error")
    }

    override fun getRecordFinish() {
        if (viewBinding.smartRefresh.isRefreshing) {
            viewBinding.smartRefresh.finishRefresh()
        }
        if (viewBinding.smartRefresh.isLoading) {
            viewBinding.smartRefresh.finishLoadMore()
        }
        if (recordAdapter.itemCount == 0) {
            viewBinding.multiStatView.viewState = MultiStateView.ViewState.EMPTY
        } else {
            viewBinding.multiStatView.viewState = MultiStateView.ViewState.CONTENT
        }
    }

    override fun deleteRecordSuccess() {

    }

    override fun deleteRecordFailure(error: String) {

    }
}