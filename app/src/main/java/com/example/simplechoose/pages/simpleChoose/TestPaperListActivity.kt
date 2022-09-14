package com.example.simplechoose.pages.simpleChoose

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplechoose.bean.dto.TestPaperDTO
import com.example.simplechoose.databinding.ActivityTestPaperBinding
import com.example.simplechoose.mvp.MVPActivity
import com.example.simplechoose.net.callback.BaseError
import com.example.simplechoose.pages.simpleChoose.adapter.TestPaperAdapter
import com.google.gson.Gson
import com.kennyc.view.MultiStateView

class TestPaperListActivity : MVPActivity<ActivityTestPaperBinding, TestPaperContract.View, TestPaperContract.Presenter>(
    ActivityTestPaperBinding::inflate
), TestPaperContract.View {

    private lateinit var url: String
    private val testPaperDTOList = ArrayList<TestPaperDTO>()
    private val testPaperAdapter by lazy {
        TestPaperAdapter(testPaperDTOList)
    }

    private val gson by lazy {
        Gson()
    }

    companion object {

        fun start(context: Context, title: String, url: String) {
            Intent(context, TestPaperListActivity::class.java).run {
                putExtra("title", title)
                putExtra("url", url)
                context.startActivity(this)
            }
        }
    }

    override fun initView() {
        url = intent.getStringExtra("url")!!
        viewBinding.tvPageTitle.text = intent.getStringExtra("title") ?: "试卷列表"
        viewBinding.recyclerView.adapter = testPaperAdapter
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun initClick() {
        viewBinding.imgBack.setOnClickListener {
            finish()
        }
    }

    override fun initData() {
        viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_LOADING
        mPresenter.getTestPaperList(url)
    }

    override fun getDebugTAG(): String {
        return TestPaperListActivity::class.java.simpleName
    }

    override fun getTestPaperListSuccess(testPaperDTOList: ArrayList<TestPaperDTO>) {
        if (testPaperDTOList.isEmpty() && this.testPaperDTOList.isEmpty()) {
            viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_EMPTY
            return
        }
        if (this.testPaperDTOList.size == testPaperDTOList.size) {
            if (gson.toJson(this.testPaperDTOList) == gson.toJson(testPaperDTOList)) {
                return
            }
        }
        this.testPaperDTOList.clear()
        this.testPaperDTOList.addAll(testPaperDTOList)
        testPaperAdapter.notifyDataSetChanged()
        viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_CONTENT
    }

    override fun onRequestError(baseError: BaseError) {
        baseError.msg?.let { toast(it) }
        if (testPaperDTOList.isEmpty()) {
            viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_ERROR
        }
    }

    override fun onRequestFinish() {
        if (viewBinding.smartRefresh.isRefreshing) {
            viewBinding.smartRefresh.finishRefresh()
        }
    }

    override fun createPresenter(): TestPaperContract.Presenter {
        return TestPaperListPresenter()
    }
}