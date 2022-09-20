package com.aiven.simplechoose.mvp

import com.aiven.simplechoose.net.RetrofitUtil

open class BaseModel<T>(clazz: Class<T>) {

    protected val service: T

    init {
        service = RetrofitUtil.getService(clazz)
    }
}