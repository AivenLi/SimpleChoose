package com.aiven.simplechoose.pages.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.aiven.hfl.util.DeviceUtil
import com.aiven.hfl.util.FloatManager
import com.aiven.simplechoose.R
import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.databinding.ActivityHomeBinding
import com.aiven.simplechoose.databinding.DialogLoadingBinding
import com.aiven.simplechoose.mvp.MVPActivity
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.pages.CustomDialog
import com.aiven.simplechoose.pages.home.adapter.TestPaperTypeAdapter
import com.aiven.simplechoose.pages.setting.SettingActivity
import com.aiven.simplechoose.utils.Constant
import com.aiven.simplechoose.utils.ThemeUtils
import com.aiven.simplechoose.utils.setSingleClickListener
import com.aiven.updateapp.bean.UpdateAppBean
import com.aiven.updateapp.util.UpdateAppUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kennyc.view.MultiStateView
import io.reactivex.rxjava3.disposables.Disposable
import android.os.Build
import android.provider.ContactsContract

import android.provider.Settings
import androidx.annotation.Nullable


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

    companion object {
        fun start(context: Context) {
            context.startActivity(
                Intent(
                    context,
                    HomeActivity::class.java
                )
            )
        }
    }

    private lateinit var textView: TextView

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
        textView = TextView(this);
        textView.text = "This is a float window test";
        textView.textSize = DeviceUtil.sp2px(this, 20.0f);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        textView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        textView.gravity = Gravity.CENTER
        val lp = LinearLayout.LayoutParams(DeviceUtil.dp2px(this, 200),DeviceUtil.dp2px(this, 200))
        textView.layoutParams = lp

        if (!Settings.canDrawOverlays(this)) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 10000)
        } else {
            val floatManager = FloatManager.getInstance(this)
            floatManager.startFloat(textView)
            floatManager.setFloatViewVisible()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10000 && Settings.canDrawOverlays(this)) {
            val floatManager = FloatManager.getInstance(this)
            floatManager.startFloat(textView)
            floatManager.setFloatViewVisible()
        }
    }

    override fun initData() {
        viewBinding.multiStatView.viewState = MultiStateView.ViewState.LOADING
        mPresenter.getQuestionTypeList()
        mPresenter.checkAppUpdate()
    }

    override fun initClick() {
        viewBinding.imgSetting.setSingleClickListener {
            SettingActivity.start(this@HomeActivity)
        }
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

    override fun onRequestFinish() {
        if (viewBinding.smartRefresh.isRefreshing) {
            viewBinding.smartRefresh.finishRefresh()
        }
    }

    override fun checkAppUpdateSuccess(updateAppDTO: UpdateAppDTO) {
        val mode = UpdateAppUtil.getUpdateMode(this@HomeActivity, updateAppDTO.minVersion, updateAppDTO.versionName)
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
                this@HomeActivity,
                lifecycle
            ).setDownloadTempFileTag(Constant.UPDATE_APP_TEMP_FILENAME)
                .setDownloadDoneFileTag(Constant.UPDATE_APP_DONE_FILENAME)
                .setDownloadPath(cacheDir.absolutePath)
                .setUpdateAppBean(updateAppBean)
        }
    }

    override fun createPresenter(): HomePresenter {
        return HomePresenter()
    }

    override fun getDebugTAG(): String {
        return HomeActivity::class.java.simpleName
    }
}