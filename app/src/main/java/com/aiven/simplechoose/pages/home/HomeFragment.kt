package com.aiven.simplechoose.pages.home

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.databinding.FragmentHomeBinding
import com.aiven.simplechoose.db.SimpleDataBase
import com.aiven.simplechoose.db.entity.InsertUpdateTestEntity
import com.aiven.simplechoose.mvp.MVPFragment
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.pages.home.adapter.TestPaperTypeAdapter
import com.aiven.simplechoose.utils.Constant
import com.aiven.simplechoose.view.SimpleChooseView
import com.aiven.updateapp.bean.UpdateAppBean
import com.aiven.updateapp.util.UpdateAppUtil
import com.google.gson.Gson
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.launch

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
        viewBinding.multiStatView.viewState = MultiStateView.ViewState.LOADING
        mPresenter.getQuestionTypeList()
        mPresenter.checkAppUpdate()
        //mPresenter.findById("123")
    }

    override fun getQuestionListTypeSuccess(testPaperTypeDTOList: ArrayList<TestPaperTypeDTO>) {
        if (testPaperTypeDTOList.isEmpty() && this.testPaperTypeDTOList.isEmpty()) {
            viewBinding.multiStatView.viewState = MultiStateView.ViewState.EMPTY
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
        viewBinding.multiStatView.viewState = MultiStateView.ViewState.CONTENT
    }

    override fun getQuestionListTypeFailure(baseError: BaseError) {
        toast(baseError.msg!!)
        if (testPaperTypeDTOList.isEmpty()) {
            viewBinding.multiStatView.viewState = MultiStateView.ViewState.ERROR
        }
    }

    override fun checkAppUpdateSuccess(updateAppDTO: UpdateAppDTO) {
        val mode = UpdateAppUtil.getUpdateMode(requireActivity(), updateAppDTO.minVersion, updateAppDTO.versionName)
        if (mode != -1) {
            val updateAppBean = UpdateAppBean()
            updateAppBean.version = updateAppDTO.versionName
            updateAppBean.minVersion = updateAppDTO.minVersion
            updateAppBean.desc = updateAppDTO.desc
            updateAppBean.url = updateAppDTO.url
            updateAppBean.apkSize = updateAppDTO.apkSize
            updateAppBean.md5 = updateAppDTO.md5
            updateAppBean.mode = mode
            UpdateAppUtil(
                requireActivity(),
                lifecycle
            ).setDownloadTempFileTag(Constant.UPDATE_APP_TEMP_FILENAME)
                .setDownloadDoneFileTag(Constant.UPDATE_APP_DONE_FILENAME)
                .setDownloadPath(requireActivity().cacheDir.absolutePath)
                .setUpdateAppBean(updateAppBean)
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

    override fun getLifecycleScope(): LifecycleCoroutineScope {
        return lifecycleScope
    }

    override fun updateFindById(insertUpdateTestEntity: InsertUpdateTestEntity) {
        Log.d(TAG, "查询成功：$insertUpdateTestEntity, 线程：${Thread.currentThread().name}")
    }
}