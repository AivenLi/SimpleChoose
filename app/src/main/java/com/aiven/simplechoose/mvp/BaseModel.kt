package com.aiven.simplechoose.mvp

import com.aiven.simplechoose.net.RetrofitUtils

open class BaseModel<T>(clazz: Class<T>) {

    protected val service: T

    init {
        service = RetrofitUtils.getService(clazz)
    }
}