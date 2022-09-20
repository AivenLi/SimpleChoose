package com.aiven.simplechoose.net.error

import com.aiven.simplechoose.R
import com.aiven.simplechoose.app.task.impl.AppContext
import com.aiven.simplechoose.net.callback.BaseError
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author  : AivenLi
 * @date    : 2022/8/6 14:04
 * */
class ErrorHandle {

    companion object {
        fun parseError(e: Throwable): BaseError {
            val errorResult = BaseError()
            val error = e.toString()
            when (e) {
                is SocketTimeoutException -> {
                    errorResult.msg = AppContext.context?.getString(R.string.socket_timeout) ?: ""
                    errorResult.code = SOCKET_TIMEOUT_CODE
                }
                is UnknownHostException -> {
                    errorResult.msg = AppContext.context?.getString(R.string.no_net) ?: ""
                    errorResult.code = UN_KNOW_HOST_CODE
                }
                is ConnectException -> {
                    errorResult.msg = AppContext.context?.getString(R.string.http_not_found) ?: ""
                    errorResult.code = HTTP_404_NOT_FOUND_CODE
                }
                is HttpException -> {
                    errorResult.msg = AppContext.context?.getString(R.string.http_server_error) ?: ""
                    errorResult.code = 500
                }
                else -> {
                    errorResult.msg = error
                    errorResult.code = OTHERS_ERROR_CODE
                }
            }
            return errorResult
        }

        const val SOCKET_TIMEOUT_CODE = 0x01000001
        const val UN_KNOW_HOST_CODE = 0x01000002
        const val HTTP_404_NOT_FOUND_CODE = 0x01000003
        const val OTHERS_ERROR_CODE = 0x0100000f
        const val NETWORK_ERROR_MASK = 0x01000000
    }
}