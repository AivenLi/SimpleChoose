package com.aiven.simplechoose.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {

        const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

        fun timestampToDateFormat(timestamp: Long): String {
            return timestampToDateFormat(timestamp, DEFAULT_DATE_FORMAT)
        }

        @SuppressLint("SimpleDateFormat")
        fun timestampToDateFormat(timestamp: Long, format: String): String {
            return SimpleDateFormat(format).format(Date(timestamp))
        }
    }
}