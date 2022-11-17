package com.aiven.simplechoose.pages.landscaps

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import com.aiven.simplechoose.databinding.ActivityLandscapeAutoLayoutBinding
import com.aiven.simplechoose.pages.BaseActivity
import me.jessyan.autosize.internal.CustomAdapt

class LandscapeAutoLayoutActivity:
    BaseActivity<ActivityLandscapeAutoLayoutBinding>(ActivityLandscapeAutoLayoutBinding::inflate),
    CustomAdapt {

    companion object {
        fun start(context: Context) {
            Intent(context, LandscapeAutoLayoutActivity::class.java).let {
                context.startActivity(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * API 30（Andrord 11）之后设置全屏的方法
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                /**
                 * 设置全屏
                 * */
                it.hide(WindowInsets.Type.systemBars())
                /**
                 * 必须设置该行为属性，否则下拉显示状态栏时，整个布局会向下移
                 * */
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun initView() {
    }

    override fun initClick() {

    }

    override fun getDebugTAG(): String {
        return LandscapeAutoLayoutActivity::class.java.simpleName
    }

    override fun isBaseOnWidth(): Boolean {
        return false
    }

    override fun getSizeInDp(): Float {
        return 1080f
    }
}