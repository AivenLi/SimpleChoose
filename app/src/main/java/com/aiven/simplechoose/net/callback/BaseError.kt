package com.aiven.simplechoose.net.callback

open class BaseError(
    val code: Int,
    val msg: String? = null
) {
    constructor() : this(-1, null)

    override fun toString(): String {
        return "BaseError: {\"code\": $code, \"msg\": \"$msg\"}"
    }
}
