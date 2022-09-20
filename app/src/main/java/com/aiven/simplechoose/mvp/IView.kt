package com.aiven.simplechoose.mvp

import com.aiven.simplechoose.net.callback.BaseError
import io.reactivex.rxjava3.disposables.Disposable

interface IView {

    /**
     * 开始请求
     * @param d
     * */
    fun onRequestStart(d: Disposable?) {}

    /**
     * 请求错误
     * @param baseError
     * */
    fun onRequestError(baseError: BaseError) {}

    /**
     * 请求完成
     * */
    fun onRequestFinish() {}
}