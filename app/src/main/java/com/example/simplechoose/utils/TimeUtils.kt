package com.example.simplechoose.utils

class TimeUtils {

    companion object {

        fun millionToHourMinuteSecond(million: Long) : String {
            val hour = (million / 1000) / 3600
            val stringBuffer = StringBuffer()
            if (hour > 1) {
                stringBuffer.append(hour)
                stringBuffer.append(":")
            }
            val minutes = ((million / 1000) % 3600) / 60
            stringBuffer.append(minutes)
            stringBuffer.append(":")
            val seconds = ((million / 1000) % 3600) % 60
            stringBuffer.append(seconds)
            return stringBuffer.toString()
        }
    }
}