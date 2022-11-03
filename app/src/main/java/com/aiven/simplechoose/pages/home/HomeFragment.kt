package com.aiven.simplechoose.pages.home

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.databinding.FragmentHomeBinding
import com.aiven.simplechoose.mvp.MVPFragment
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.pages.home.adapter.TestPaperTypeAdapter
import com.google.gson.Gson
import com.kennyc.view.MultiStateView

class HomeFragment : MVPFragment<FragmentHomeBinding, HomeContract.View, HomeContract.Presenter>(
    FragmentHomeBinding::inflate
), HomeContract.View {

    private val testPaperTypeDTOList = ArrayList<TestPaperTypeDTO>()
    private val testPaperTypeAdapter by lazy {
        TestPaperTypeAdapter(requireContext(), testPaperTypeDTOList)
    }

    private val gson by lazy {
        Gson()
    }

    override fun createPresenter(): HomeContract.Presenter {
        return HomePresenter()
    }

    override fun initView() {
        viewBinding.recyclerView.adapter = testPaperTypeAdapter
        viewBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        viewBinding.smartRefresh.setOnRefreshListener {
            mPresenter.getQuestionTypeList()
        }
    }

    override fun initClick() {

    }

    override fun initData() {
        viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_LOADING
        mPresenter.getQuestionTypeList()
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

    override fun getFTAG(): String {
        return HomeFragment::class.java.simpleName
    }
}