package com.aiven.simplechoose.mvp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.aiven.simplechoose.pages.BaseActivity

abstract class MVPActivity<VB: ViewBinding, in V: IView, P: IPresenter<V>>(
    inflate: ((layoutInflater: LayoutInflater) -> VB)
) : BaseActivity<VB>(
    inflate
) {

    protected lateinit var mPresenter: P
    private var isInitData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        mPresenter = createPresenter()
        super.onCreate(savedInstanceState)
        mPresenter.onViewAttached(this as V)
    }

    override fun onStart() {
        super.onStart()
        if (!isInitData) {
            isInitData = true
            initData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onViewDetached()
    }

    abstract fun createPresenter(): P

    abstract fun initData()
}