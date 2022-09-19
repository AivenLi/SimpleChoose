package com.aiven.simplechoose.net.request

import android.util.Log
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.net.callback.BaseResponse
import com.aiven.simplechoose.net.callback.RequestCallback
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException


class BaseRequest {

    companion object {

        fun <D, T: BaseResponse<D>> request(observable: Observable<T>, requestCallback: RequestCallback<D>) {
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BaseResponse<D>> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d("HttpLog", "开始请求")
                        requestCallback.onRequestStart(d)
                    }

                    override fun onNext(t: BaseResponse<D>?) {
                        Log.d("HttpLog", "onNext")
                        when {
                            t == null -> {
                                requestCallback.onFailure(BaseError(0x7f000004, "数据为空"))
                            }
                            t.code == 0 -> {
                                requestCallback.onSuccess(t.data)
                            }
                            else -> {
                                requestCallback.onFailure(t as BaseError)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        requestCallback.onFailure(dealError(e))
                        Log.d("HttpLog", "onError")
                        requestCallback.onRequestFinish()
                    }

                    override fun onComplete() {
                        Log.d("HttpLog", "OnFinish")
                        requestCallback.onRequestFinish()
                    }
                })
        }

        private fun dealError(e: Throwable) : BaseError {
            Log.e("HttpLogError", e.toString())
            return when (e) {
                is SocketTimeoutException -> {
                    BaseError(0x7f000001, "请求超时")
                }
                is ConnectException -> {
                    BaseError(0x7f000002, "无法连接服务器")
                }
                is HttpException -> {
                    BaseError(0x7f000003, "Http not found 404")
                }
                else -> {
                    BaseError(0x7fffffff, "未知错误")
                }
            }
        }
    }
}