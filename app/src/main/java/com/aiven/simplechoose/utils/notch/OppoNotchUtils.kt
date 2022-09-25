package com.aiven.simplechoose.utils.notch

import android.content.Context

/**
 * @author  : AivenLi
 * @date    : 2022/9/3 19:18
 * @desc    :
 * */
class OppoNotchUtils {

    companion object {
        fun hasNotch(context: Context) : Boolean {
            return context.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
        }
    }
}