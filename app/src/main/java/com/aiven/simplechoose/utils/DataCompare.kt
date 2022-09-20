package com.aiven.simplechoose.utils

import android.text.TextUtils
import com.google.gson.Gson

class DataCompare {

    companion object {

        fun <T> sameData(new: T, old: T, gson: Gson): Boolean {
            return TextUtils.equals(gson.toJson(new), gson.toJson(old))
        }
    }
}