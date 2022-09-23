package com.aiven.simplechoose.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding>(private val inflate: (layout: LayoutInflater) -> VB): Fragment() {


    protected lateinit var viewBinding: VB

    protected lateinit var TAG: String

    /**
     * 第一次加载
     */
    private var firstLoad = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = getFTAG()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = inflate(layoutInflater)
        initView()
        initClick()
        return viewBinding.root
    }

    override fun onResume() {

        super.onResume()
        if (!firstLoad) {
            firstLoad = true
            initData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firstLoad = false
    }

    protected open fun toast(msg: String?) {
        if (activity != null) {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun loaded(): Boolean {
        return firstLoad
    }

    /**
     * 初始化View
     */
    protected abstract fun initView()

    /**
     * 初始化点击事件
     * */
    protected abstract fun initClick()

    /**
     * 初始化数据
     * */
    protected open fun initData() {}

    /**
     * 获取TAG（调试使用）
     * */
    protected abstract fun getFTAG(): String
}