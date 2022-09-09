package com.example.simplechoose.mvp

interface IPresenter<in V: IView> {

    /**
     * 与试图绑定
     * @param view
     * */
    fun onViewAttached(view: V)

    /**
     * 与视图分离
     * */
    fun onViewDetached()

    /**
     * 判断是否与视图绑定
     * Kotlin用不到这个方法，但是java用得到
     * @return 绑定返回true，分离返回false
     * */
    fun viewIsAttached(): Boolean
}