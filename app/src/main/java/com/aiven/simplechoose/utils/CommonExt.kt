package com.aiven.simplechoose.utils

import android.view.View
import android.widget.Checkable
import com.aiven.simplechoose.R

var <T: View> T.lastTime: Long
    set(value) = setTag(R.id.single_click, value)
    get() = getTag(R.id.single_click) as? Long ?: 0

inline fun <T: View> T.setSingleClickListener(timeout: Long = 1000, crossinline block: (T) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > timeout || this is Checkable) {
            lastTime = currentTime
            block(this)
        }
    }
}