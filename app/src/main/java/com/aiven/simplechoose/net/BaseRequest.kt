package com.aiven.simplechoose.net


import com.aiven.simplechoose.net.cache.CacheListener
import com.aiven.simplechoose.net.cache.CacheUtil
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.net.callback.BaseResponse
import com.aiven.simplechoose.net.callback.RequestCallback
import com.aiven.simplechoose.net.rx.SwitchScheduleNet
import com.aiven.simplechoose.utils.DataCompare
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.reflect.Type

/**
 * @author  : AivenLi
 * @date    : 2022/8/9 18:38
 * */
class BaseRequest {

    companion object {

        /**
         * 请求数据，先返回缓存中的数据（如果有的话），然后再返回请求结果的数据。
         * 如果请求回来的数据和缓存中的数据一致，则不返回请求得到的数据，如果不一致，
         * 则返回请求回来的数据。
         * 也就是说如果缓存中有数据且请求的数据和缓存的不一致，则会回调两次onSuccess，并且缓存
         * 新的数据。
         * @param observable
         * @param key
         * @param type
         * @param requestCallback
         * */
        fun <D, T: BaseResponse<D>> requestWithCache(
            observable: Observable<T>,
            key: String,
            type: Type,
            requestCallback: RequestCallback<D>
        ) {
            var cacheData: D? = null
            CacheUtil.get(key, type, object : CacheListener<D> {
                override fun getDataStart() {
                    requestCallback.onRequestStart(null)
                }
                override fun getDataSuccess(data: D) {
                    cacheData = data
                    requestCallback.onSuccess(cacheData)
                }

                override fun getDataFailure(error: String) {
                }

                override fun getDataFinish() {
                    observable.compose(SwitchScheduleNet<T>())
                        .subscribe(object : DataObserver<D>() {

                            override fun onSuccess(data: D?) {
                                if (cacheData != null && data != null) {
                                    if (!DataCompare.sameData(data, cacheData, Gson())) {
                                        requestCallback.onSuccess(data)
                                        CacheUtil.save(key, data)
                                    }
                                } else {
                                    requestCallback.onSuccess(data)
                                    data?.let {
                                        CacheUtil.save(key, it)
                                    }
                                }
                                requestCallback.onRequestFinish()
                            }

                            override fun onFailure(error: BaseError) {
                                requestCallback.onFailure(error)
                                requestCallback.onRequestFinish()
                            }
                        })
                }
            })
        }

        /**
         * 请求数据，先从缓存中获取，等待接口请求回来。
         * 数据以接口返回的为准，如果接口请求失败，则
         * 返回缓存数据，如果缓存数据为空，则返回失败。
         * @param observable
         * @param key
         * @param type
         * @param requestCallback
         * */
        fun <D, T: BaseResponse<D>> requestWithCacheWaitNet(
            observable: Observable<T>,
            key: String,
            type: Type,
            requestCallback: RequestCallback<D>
        ) {
            var cacheData: D? = null
            CacheUtil.get(key, type, object : CacheListener<D> {
                override fun getDataStart() {
                    requestCallback.onRequestStart(null)
                }
                override fun getDataSuccess(data: D) {
                    cacheData = data
                }

                override fun getDataFailure(error: String) {
                }

                override fun getDataFinish() {
                    observable.compose(SwitchScheduleNet<T>())
                        .subscribe(object : DataObserver<D>() {

                            override fun onSuccess(data: D?) {
                                requestCallback.onSuccess(data)
                                requestCallback.onRequestFinish()
                                data?.let {
                                    CacheUtil.save(key, it)
                                }
                            }

                            override fun onFailure(error: BaseError) {
                                if (cacheData == null) {
                                    requestCallback.onFailure(error)
                                } else {
                                    requestCallback.onSuccess(cacheData)
                                }
                                requestCallback.onRequestFinish()
                            }
                        })
                }
            })
        }

        /**
         * 请求数据，数据保存到缓存中。
         * @param observable
         * @param key
         * @param requestCallback
         * */
        fun <D, T: BaseResponse<D>> requestSaveToCache(
            observable: Observable<T>,
            key: String,
            requestCallback: RequestCallback<D>
        ) {
            observable.compose(SwitchScheduleNet<T>())
                .subscribe(object : DataObserver<D>(key) {
                    override fun onSubscribe(d: Disposable) {
                        requestCallback.onRequestStart(d)
                    }
                    override fun onSuccess(data: D?) {
                        requestCallback.onSuccess(data)
                        requestCallback.onRequestFinish()
                    }

                    override fun onFailure(error: BaseError) {
                        requestCallback.onFailure(error)
                        requestCallback.onRequestFinish()
                    }
                })
        }

        /**
         * 请求数据
         * @param observable
         * @param requestCallback
         * */
        fun <D, T: BaseResponse<D>> request(
            observable: Observable<T>,
            requestCallback: RequestCallback<D>
        ) {
            observable.compose(SwitchScheduleNet<T>())
                .subscribe(object : DataObserver<D>() {
                    override fun onSubscribe(d: Disposable) {
                        requestCallback.onRequestStart(d)
                    }
                    override fun onSuccess(data: D?) {
                        requestCallback.onSuccess(data)
                        requestCallback.onRequestFinish()
                    }

                    override fun onFailure(error: BaseError) {
                        requestCallback.onFailure(error)
                        requestCallback.onRequestFinish()
                    }
                })
        }
    }
}