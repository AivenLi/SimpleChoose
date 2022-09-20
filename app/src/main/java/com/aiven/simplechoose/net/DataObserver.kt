package com.aiven.simplechoose.net

import android.util.Log
import com.aiven.simplechoose.net.cache.CacheListener
import com.aiven.simplechoose.net.cache.CacheUtil
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.net.callback.BaseResponse
import com.aiven.simplechoose.net.error.ErrorHandle
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.reflect.Type

/**
 *
 * 统一解析数据，适用格式：
 *          T data
 *          int code
 *          String errorMsg
 * @param cacheKey 缓存的Key， 如果不需要缓存，可传null
 * @param type 缓存数据类型，如果不需要缓存，可传null
 * @param getFromCache 是否从缓存中获取数据，默认不获取
 * */
abstract class DataObserver<T>(
    private val cacheKey: String?,
    private val type: Type?,
    private val getFromCache: Boolean = false
) : Observer<BaseResponse<T>> {

    /**
     * 该构造方法只解析数据
     * */
    constructor() : this(null, null, false)

    /**
     * 该构造方法缓存数据
     * */
    constructor(cacheKey: String) : this(cacheKey, null, false)

    /**
     * 请求开始
     *
     * @param d
     * */
    override fun onSubscribe(d: Disposable) {}

    /**
     * 请求成功，解析数据。请求成功只能说明与服务器通信正常，不代表就是获取到正确的数据
     *
     * @param data 服务器返回的数据
     * */
    override fun onNext(data: BaseResponse<T>) {
        when (data.code) {
            /**
             * 服务器内部响应码，执行成功的代码为0
             * */
            0 -> {
                onSuccess(data.data)
                saveDataToCache(data.data)
            }
            /**
             * 其他情况均为失败
             * */
            else -> {
                Log.d("httprequest", "失败：$data")
                onFailure(BaseError(data.code, data.msg))
            }
        }
    }

    /**
     * 请求失败，与服务器通信有问题的错误。根据错误类型返回对应的数据
     *
     * @param e 错误类型
     * */
    override fun onError(e: Throwable) {
        Log.d("HttpRequest", "请求错误：$e")
        onFailure(ErrorHandle.parseError(e))
        if (getFromCache) {
            onCache()
        }
    }

    /**
     * 请求完成
     * */
    override fun onComplete() {}

    /**
     * 缓存数据，待后用
     *
     * @param data 要缓存的数据
     * */
    private fun saveDataToCache(data: Any?) {
        cacheKey?.let { key ->
            data?.let {
                CacheUtil.save(key, it)
            }
        }
    }

    /**
     * 获取缓存数据
     *
     * */
    private fun onCache() {
        cacheKey?.let { key ->
            type?.let { type ->
                CacheUtil.get(key, type, object : CacheListener<T> {
                    override fun getDataSuccess(data: T) {
                        onSuccess(data)
                    }

                    override fun getDataFailure(error: String) {

                    }
                })
            }
        }
    }

    /**
     * 真正的请求成功，即成功获取到数据，有的接口虽然请求成功，但是数据部分为空，因此这里的数据为可空的
     *
     * @param data 数据
     * */
    abstract fun onSuccess(data: T?)

    /**
     * 获取数据失败，只要失败，均通过本方法通知调用者。因为本方法统一了错误信息和错误码
     *
     * @param error 错误信息
     * */
    abstract fun onFailure(error: BaseError)
}