package com.example.simplechoose.mvp

abstract class BasePresenter<M: IModel, V: IView> : IPresenter<V> {

    protected var mModel: M? = null
    protected var mView: V? = null

    override fun onViewAttached(view: V) {
        mView = view
        mModel = createModel()
    }

    override fun onViewDetached() {
        mView = null
        mModel = null
    }

    override fun viewIsAttached(): Boolean {
        return mView != null
    }

    abstract fun createModel(): M
}