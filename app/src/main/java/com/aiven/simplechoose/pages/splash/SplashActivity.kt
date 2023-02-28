package com.aiven.simplechoose.pages.splash

import android.content.Intent
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.Nullable
import com.aiven.acode.MyAesUtil
import com.aiven.hfl.util.FloatManager
import com.aiven.simplechoose.R
import com.aiven.simplechoose.databinding.ActivitySplashBinding
import com.aiven.simplechoose.databinding.DialogYesNoBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.pages.CustomDialog
import com.aiven.simplechoose.pages.home.HomeActivity
import com.aiven.simplechoose.utils.Constant
import com.aiven.simplechoose.utils.notch.HuaWeiNotchUtils
import com.aiven.simplechoose.utils.notch.RomUtils
import com.aiven.simplechoose.utils.notch.XiaoMiNotchUtils
import com.aiven.simplechoose.utils.setSingleClickListener
import com.tencent.mmkv.MMKV
import java.io.File

/**
 * @author  : AivenLi
 * @date    : 2022/9/25 17:11
 * @desc    :
 * */
class SplashActivity: BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    private lateinit var mmkv: MMKV
    private lateinit var dialog: CustomDialog<DialogYesNoBinding>
    private var handler: Handler? = null
    private var accept = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
    }

    override fun initView() {
        initHandler()
        mmkv = MMKV.mmkvWithID(Constant.MMKV_FILE)
        accept = mmkv.decodeBool(Constant.MMKV_FIRST_RUN_ACCEPT_KEY, false)
        if (!accept) {
            dialog = CustomDialog<DialogYesNoBinding>(
                context = this,
                inflate = DialogYesNoBinding::inflate
            )
            dialog.binding.tvYesNoTitle.text = getString(R.string.first_run_accept_title)
            dialog.binding.tvContent.text = getString(R.string.first_run_accept_desc)
            dialog.binding.tvContent.visibility = View.VISIBLE
            dialog.binding.tvYesNoYes.setSingleClickListener {
                dialog.hide()
                mmkv.encode(Constant.MMKV_FIRST_RUN_ACCEPT_KEY, true)
                handler!!.sendEmptyMessage(1023)
            }
            dialog.binding.tvYesNoCancel.setSingleClickListener {
                dialog.hide()
                finish()
            }
            dialog.show()
        } else {
            handler!!.sendEmptyMessageDelayed(1023, 1000L)
        }
     //   val result = MyAesUtil.openFile("${cacheDir.absolutePath}${File.separator}readme.txt")
       // Log.d(TAG, "openResult: $result")
    }

    override fun initClick() {

    }

    override fun getDebugTAG(): String {
        return SplashActivity::class.java.simpleName
    }

    override fun onDestroy() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        super.onDestroy()
    }

    private fun initHandler() {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what == 1023) {
//                    if (!Settings.canDrawOverlays(this@SplashActivity)) {
//                        val intent =
//                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
//                        startActivityForResult(intent, 10000)
//                    } else {
//                        val floatManager = FloatManager.getInstance(this@SplashActivity)
//                        floatManager.startFloat()
//                        floatManager.setFloatViewVisible()
//                        HomeActivity.start(this@SplashActivity)
//                        finish()
//                    }
                    HomeActivity.start(this@SplashActivity)
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10000 && Settings.canDrawOverlays(this)) {
            val floatManager = FloatManager.getInstance(this)
            floatManager.startFloat()
            floatManager.setFloatViewVisible()
            HomeActivity.start(this@SplashActivity)
            finish()
        }
    }

    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        } else {
            if (RomUtils.isHuawei() && HuaWeiNotchUtils.hasNotch(this)) {
                HuaWeiNotchUtils.setFullScreenWindowLayoutInDisplayCutout(window)
            } else if (RomUtils.isXiaomi() && XiaoMiNotchUtils.hasNotch(this)) {
                XiaoMiNotchUtils.setFullScreenWindowLayoutInDisplayCutout(window)
            }
        }
    }
}