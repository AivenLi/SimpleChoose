package com.aiven.simplechoose.mvp

import android.content.Context
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.aiven.simplechoose.pages.BaseFragment

/**
 * MVP Fragment
 * @param <VB> ViewBinding
 * @param <V> View of MVP
 * @param <P> Presenter of MVP
 * @param inflate ViewBinding.LayoutInflater
 * */
abstract class MVPFragment<VB: ViewBinding, in V: IView, P: IPresenter<V>>(private val inflate: (layout: LayoutInflater) -> VB)
    : BaseFragment<VB>(inflate), IView {

    protected lateinit var mPresenter: P

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPresenter = createPresenter()
        mPresenter.onViewAttached(this as V)
    }

    override fun onDetach() {
        super.onDetach()
        mPresenter.onViewDetached()
    }

    protected abstract fun createPresenter(): P
}