package com.aiven.simplechoose.pages.home

import android.util.Log
import com.aiven.simplechoose.bean.dto.TestBinDTO
import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.bean.vo.TestBinVo
import com.aiven.simplechoose.mvp.BasePresenter
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.net.callback.RequestCallback
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch

class HomePresenter: BasePresenter<HomeContract.Model, HomeContract.View>(), HomeContract.Presenter {

    private var isLoading = false

    override fun createModel(): HomeContract.Model {
        return HomeModelImpl()
    }

    override fun getQuestionTypeList() {
        if (isLoading) {
            return
        }
        isLoading = true
        mModel?.getQuestionTypeList(object : RequestCallback<ArrayList<TestPaperTypeDTO>> {
            override fun onRequestStart(d: Disposable?) {
                mView?.onRequestStart(d)
            }
            override fun onSuccess(data: ArrayList<TestPaperTypeDTO>?) {
                data?.let { mView?.getQuestionListTypeSuccess(it) }
            }

            override fun onFailure(error: BaseError) {
                mView?.getQuestionListTypeFailure(error)
            }

            override fun onRequestFinish() {
                isLoading = false
                mView?.onRequestFinish()
            }
        })
    }

    override fun checkAppUpdate() {
        mModel?.checkAppUpdate(object : RequestCallback<UpdateAppDTO> {
            override fun onSuccess(data: UpdateAppDTO?) {
                data?.let { mView?.checkAppUpdateSuccess(it) }
            }

            override fun onFailure(error: BaseError) {

            }
        })
    }

    override fun findById(id: String) {
        mView?.getLifecycleScope()?.launch {
            runCatching {
                Log.d("HomeFragment-Debug", "runcatching里面，线程：${Thread.currentThread().name}")
                mModel?.findById(id)!!
            }.onSuccess {
                Log.d("HomeFragment-Debug", "onSuccess里面，线程：${Thread.currentThread().name}")
                mView?.updateFindById(it)
            }.onFailure {
                Log.d("HomeFragment-Debug", "查询失败：${it.toString()}, 线程：${Thread.currentThread().name}")
            }
        }
    }

    override fun testBin(testBinVo: TestBinVo) {
        mModel?.testBin(testBinVo, object : RequestCallback<Void> {
            override fun onSuccess(data: Void?) {
                Log.d("-Debug", "结果：$data")
            }

            override fun onFailure(error: BaseError) {
                Log.d("-Debug", "错误：$error")
            }

        })
    }

    override fun selectBin() {
        mModel?.selectBin(object : RequestCallback<List<TestBinDTO>> {
            override fun onSuccess(data: List<TestBinDTO>?) {
                if (!data.isNullOrEmpty()) {
                    var right = true
                    var a: Byte = 0x00
                    val testBinDTO = data[0]
                    testBinDTO.text?.let {
                        val bytes = it.toByteArray()
                        Log.d("-Debug", "长度不够：${bytes.size}")
                        for (i in 0 until 2048) {
                            if (a++ != bytes[i]) {
                                Log.d("-Debug", "第${i}个开始不一致")
                                right = false
                                break
                            }
                        }
                        Log.d("-Debug", "数值一致：$right")
                    }
                }
            }

            override fun onFailure(error: BaseError) {

            }
        })
    }
}