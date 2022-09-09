package com.example.simplechoose.mvp

import com.example.simplechoose.net.RetrofitUtils

open class BaseModel<T>(clazz: Class<T>) {

    protected val service: T

    init {
        service = RetrofitUtils.getService(clazz)
    }
}