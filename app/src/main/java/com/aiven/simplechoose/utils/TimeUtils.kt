package com.aiven.simplechoose.utils

class TimeUtils {

    companion object {

        fun millionToHourMinuteSecond(million: Long) : String {
            val hour = (million / 1000) / 3600
            val stringBuffer = StringBuffer()
            if (hour > 1) {
                if (hour < 10) {
                    stringBuffer.append("0")
                }
                stringBuffer.append(hour)
                stringBuffer.append(":")
            }
            val minutes = ((million / 1000) % 3600) / 60
            if (minutes < 10) {
                stringBuffer.append("0")
            }
            stringBuffer.append(minutes)
            stringBuffer.append(":")
            val seconds = ((million / 1000) % 3600) % 60
            if (seconds < 10) {
                stringBuffer.append("0")
            }
            stringBuffer.append(seconds)
            return stringBuffer.toString()
        }
    }
}