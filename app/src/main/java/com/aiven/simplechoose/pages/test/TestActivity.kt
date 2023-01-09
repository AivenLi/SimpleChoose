package com.aiven.simplechoose.pages.test

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.aiven.simplechoose.databinding.ActivityTestBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.pages.home.HomeActivity
import com.aiven.simplechoose.pages.setting.SettingActivity
import com.aiven.simplechoose.utils.setSingleClickListener
import me.jessyan.autosize.internal.CancelAdapt

class TestActivity: BaseActivity<ActivityTestBinding>(ActivityTestBinding::inflate), CancelAdapt {

    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = "${getDebugTAG()}-Debug"
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val displayMetricsScreen = DisplayMetrics()
        display.getMetrics(displayMetricsScreen)
        val screenWidth = displayMetricsScreen.widthPixels
        val screenHeight = displayMetricsScreen.heightPixels
        Log.d(TAG, "ScreenSize: ${screenWidth}, $screenHeight")
        val displayMetrics = this.resources.displayMetrics
        val xdpi = screenWidth / 1080f
        Log.d(TAG, "xdpi: ${displayMetrics.xdpi}, scaleDensity: ${displayMetrics.scaledDensity}")
        displayMetrics.xdpi = xdpi * 25.4f
     //   displayMetrics.scaledDensity = 1.0f
        Log.d(TAG, "xdpi: ${displayMetrics.xdpi}, scaleDensity: ${displayMetrics.scaledDensity}")
        val appDisplayMetrics = applicationContext.resources.displayMetrics
        Log.d(TAG, "app xdpi: ${appDisplayMetrics.xdpi}")
//        appDisplayMetrics.xdpi = xdpi * 25.4f
//        appDisplayMetrics.scaledDensity = 1.0f
        Log.d(TAG, "app xdpi: ${appDisplayMetrics.xdpi}")
        Log.d(TAG, "ScreenSize: ${screenWidth}, ${screenHeight}")
        val sizeInDp = 1080f
        val targetDensity = screenWidth / sizeInDp
        val targetDensityDpi = (targetDensity * 160).toInt()
        val targetScreenWidthDp = (screenWidth / targetDensity).toInt()
        val targetScreenHeightDp = (screenHeight / targetDensity).toInt()
//        displayMetrics.scaledDensity = targetDensity * (displayMetrics.scaledDensity * 1.0f / displayMetrics.density)
//        appDisplayMetrics.scaledDensity = displayMetrics.scaledDensity
        Log.d(TAG, "xdpi: ${displayMetrics.xdpi}, scaleDensity: ${displayMetrics.scaledDensity}")
//        if (isMiui()) {
//            Log.d(TAG, "isMiUI")
//            runCatching {
//                val field = Resources::class.java.getDeclaredField("mTmpMetrics")
//                field.isAccessible = true
//                field.get(resources) as DisplayMetrics
//            }.onSuccess {
//                Log.d(TAG, "miui xdpi: ${it.xdpi}")
//                it.xdpi = xdpi * 25.4f
//                Log.d(TAG, "miui xdpi: ${it.xdpi}")
//            }.onFailure {
//                Log.d(TAG, "Error: ${it.toString()}")
//            }
//        }
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
//        Log.d(TAG, "onStart-------------------")
//        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val display = windowManager.defaultDisplay
//        val displayMetricsScreen = DisplayMetrics()
//        display.getMetrics(displayMetricsScreen)
//        val screenWidth = displayMetricsScreen.widthPixels
//        val screenHeight = displayMetricsScreen.heightPixels
//        Log.d(TAG, "ScreenSize: ${screenWidth}, $screenHeight")
//        val displayMetrics = this.resources.displayMetrics
//        val xdpi = screenWidth / 1080f
//        Log.d(TAG, "xdpi: ${displayMetrics.xdpi}, scaleDensity: ${displayMetrics.scaledDensity}")
//        displayMetrics.xdpi = xdpi * 25.4f
//        displayMetrics.scaledDensity = 1.0f
//        Log.d(TAG, "xdpi: ${displayMetrics.xdpi}, scaleDensity: ${displayMetrics.scaledDensity}")
//        val appDisplayMetrics = applicationContext.resources.displayMetrics
//        Log.d(TAG, "app xdpi: ${appDisplayMetrics.xdpi}")
//        appDisplayMetrics.xdpi = xdpi * 25.4f
//        appDisplayMetrics.scaledDensity = 1.0f
//        Log.d(TAG, "app xdpi: ${appDisplayMetrics.xdpi}")
//        Log.d(TAG, "ScreenSize: ${screenWidth}, ${screenHeight}")
//        val sizeInDp = 1080f
//        val targetDensity = screenWidth / sizeInDp
//        val targetDensityDpi = (targetDensity * 160).toInt()
//        val targetScreenWidthDp = (screenWidth / targetDensity).toInt()
//        val targetScreenHeightDp = (screenHeight / targetDensity).toInt()
////        displayMetrics.scaledDensity = targetDensity * (displayMetrics.scaledDensity * 1.0f / displayMetrics.density)
////        appDisplayMetrics.scaledDensity = displayMetrics.scaledDensity
//        Log.d(TAG, "xdpi: ${displayMetrics.xdpi}, scaleDensity: ${displayMetrics.scaledDensity}")
////        if (isMiui()) {
////            Log.d(TAG, "isMiUI")
////            runCatching {
////                val field = Resources::class.java.getDeclaredField("mTmpMetrics")
////                field.isAccessible = true
////                field.get(resources) as DisplayMetrics
////            }.onSuccess {
////                Log.d(TAG, "miui xdpi: ${it.xdpi}")
////                it.xdpi = xdpi * 25.4f
////                Log.d(TAG, "miui xdpi: ${it.xdpi}")
////            }.onFailure {
////                Log.d(TAG, "Error: ${it.toString()}")
////            }
////        }
        super.onStart()
    }

    override fun initView() {
//        val layoutParams = viewBinding.constraintLayout.layoutParams as ConstraintLayout.LayoutParams
//        layoutParams.horizontalBias = 0.5f
//        layoutParams.verticalBias = 0.5f
//        viewBinding.constraintLayout.layoutParams = layoutParams
    }

    override fun initClick() {
        viewBinding.tvValue.setSingleClickListener {
            SettingActivity.start(this@TestActivity)
            toast("點擊時間")
        }
    }

    override fun getDebugTAG(): String {
        return TestActivity::class.java.simpleName
    }

    private fun isMiui(): Boolean {
        val str = applicationContext.resources::class.java.simpleName
        return TextUtils.equals("MiuiResources", str) || TextUtils.equals("XResources", str)
    }
}