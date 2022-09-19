package com.aiven.simplechoose.net.callback

import io.reactivex.rxjava3.disposables.Disposable

interface RequestCallback<T> {

    /**
     * 开始请求
     * */
    fun onRequestStart(d: Disposable) {}

    /**
     * 请求成功
     * @param data
     * */
    fun onSuccess(data: T?)

    /**
     * 请求失败
     * @param error
     * */
    fun onFailure(error: BaseError)

    /**
     * 完成请求
     * */
    fun onRequestFinish() {}
}