package com.aiven.simplechoose.pages.home

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.databinding.ActivityHomeBinding
import com.aiven.simplechoose.mvp.MVPActivity
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.pages.home.adapter.TestPaperTypeAdapter
import com.aiven.simplechoose.utils.ThemeUtils
import com.google.gson.Gson
import com.kennyc.view.MultiStateView
import io.reactivex.rxjava3.disposables.Disposable

class HomeActivity : MVPActivity<ActivityHomeBinding, HomeContract.View, HomeContract.Presenter>(
    ActivityHomeBinding::inflate
), HomeContract.View {

    private val testPaperTypeDTOList = ArrayList<TestPaperTypeDTO>()
    private val testPaperTypeAdapter by lazy {
        TestPaperTypeAdapter(this, testPaperTypeDTOList)
    }

    private val gson by lazy {
        Gson()
    }

    override fun initView() {
        if (ThemeUtils.isDarkMode(this)) {
            Log.d(TAG, "黑夜模式")
        } else {
            Log.d(TAG, "正常模式")
        }
        viewBinding.recyclerView.adapter = testPaperTypeAdapter
        viewBinding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        viewBinding.smartRefresh.setOnRefreshListener {
            mPresenter.getQuestionTypeList()
        }
    }

    override fun initData() {
        viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_LOADING
        mPresenter.getQuestionTypeList()
    }

    override fun initClick() {

    }

    override fun getQuestionListTypeSuccess(testPaperTypeDTOList: ArrayList<TestPaperTypeDTO>) {
        if (testPaperTypeDTOList.isEmpty() && this.testPaperTypeDTOList.isEmpty()) {
            viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_EMPTY
            return
        }
        if (this.testPaperTypeDTOList.size == testPaperTypeDTOList.size) {
            if (gson.toJson(this.testPaperTypeDTOList) == gson.toJson(testPaperTypeDTOList)) {
                Log.d(TAG, "数据一样，返回")
                return
            }
        }
        this.testPaperTypeDTOList.clear()
        this.testPaperTypeDTOList.addAll(testPaperTypeDTOList)
        testPaperTypeAdapter.notifyDataSetChanged()
        viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_CONTENT
    }

    override fun getQuestionListTypeFailure(baseError: BaseError) {
        toast(baseError.msg!!)
        if (testPaperTypeDTOList.isEmpty()) {
            viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_ERROR
        }
    }

    override fun onRequestFinish() {
        if (viewBinding.smartRefresh.isRefreshing) {
            viewBinding.smartRefresh.finishRefresh()
        }
    }

    override fun createPresenter(): HomePresenter {
        return HomePresenter()
    }

    override fun getDebugTAG(): String {
        return HomeActivity::class.java.simpleName
    }
}