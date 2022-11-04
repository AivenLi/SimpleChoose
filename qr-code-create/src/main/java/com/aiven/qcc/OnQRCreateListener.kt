package com.aiven.qcc

import android.graphics.Bitmap

interface OnQRCreateListener {

    fun onSuccess(bitmap: Bitmap)
    fun onFailure(error: String)
}