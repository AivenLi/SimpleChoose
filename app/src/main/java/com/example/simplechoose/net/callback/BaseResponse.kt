package com.example.simplechoose.net.callback

open class BaseResponse<T> : BaseError() {
    val data: T? = null
}