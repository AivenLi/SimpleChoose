package com.aiven.simplechoose.net.cache

interface CacheListener<T> {

    fun getDataStart() {}
    fun getDataSuccess(data: T)
    fun getDataFailure(error: String)
    fun getDataFinish() {}
}