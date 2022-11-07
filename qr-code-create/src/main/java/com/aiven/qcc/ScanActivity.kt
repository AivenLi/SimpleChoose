package com.aiven.qcc

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.ZXingView

class ScanActivity : AppCompatActivity(), QRCodeView.Delegate {

    private lateinit var zxing: ZXingView
    private lateinit var lightView: LinearLayout
    private lateinit var photoView: LinearLayout
    private lateinit var tvLight: TextView
    private var setResult = false
    private var resultKey: String? = null
    private var result: String? = null

    companion object {

        @JvmStatic
        fun start(activity: Activity, resultKey: String, requestCode: Int) {
            Intent(activity, ScanActivity::class.java).let {
                it.putExtra("result_key", resultKey)
                it.putExtra("set_result", true)
                activity.startActivityForResult(it, requestCode)
            }

        }

        @JvmStatic
        fun start(context: Context) {
            Intent(context, ScanActivity::class.java).let {
                context.startActivity(it)
            }
        }

        private const val TAG = "ScanActivity-Debug"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        setResult = intent.getBooleanExtra("set_result", false)
        resultKey = intent.getStringExtra("result_key")
        zxing = findViewById(R.id.zxing)
        lightView = findViewById(R.id.lyt_light)
        photoView = findViewById(R.id.lyt_photo)
        tvLight = findViewById(R.id.tv_light)
        zxing.scanBoxView?.let {
            it.isAutoZoom = true
            it.isShowLocationPoint = true
        }
        zxing.hiddenScanRect()
        zxing.setDelegate(this)
        tvLight.text = "开灯"
        lightView.setOnClickListener {
            if (tvLight.text.toString() == "关灯") {
                zxing.closeFlashlight()
                tvLight.text = "开灯"
            } else {
                zxing.openFlashlight()
                tvLight.text = "关灯"
            }
        }
        photoView.setOnClickListener {
            openPhoto()
        }
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onStop() {
        super.onStop()
        zxing.stopSpot()
        zxing.stopCamera()
    }

    override fun onScanQRCodeSuccess(result: String?) {
        if (result.isNullOrEmpty()) {
            Toast.makeText(this@ScanActivity, "未找到二维码/条形码", Toast.LENGTH_SHORT).show()
        } else {
            zxing.stopSpot()
            Log.d(TAG, "成功：$result")
            zxing.stopCamera()
            this@ScanActivity.result = result
            if (!setResult) {
                ScanResultActivity.start(this@ScanActivity, result)
            }
            finish()
        }
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        //Toast.makeText(this@ScanActivity, "onCameraAmbientBrightnessChanged: $isDark", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onCameraAmbientBrightnessChanged: $isDark")
    }

    override fun onScanQRCodeOpenCameraError() {
        zxing.stopSpot()
    }

    override fun finish() {
        super.finish()
        if (setResult && !resultKey.isNullOrEmpty() && !result.isNullOrEmpty()) {
            val intent = Intent()
            intent.putExtra(resultKey, result)
            setResult(RESULT_OK, intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        zxing.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 7890 && resultCode == RESULT_OK) {
            data?.let { d ->
                val uri = d.data
                uri?.let { u ->
                    val cursor: Cursor? = this@ScanActivity.contentResolver
                        .query(u, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
                    cursor?.let { c ->
                        if (c.moveToFirst()) {
                            val value = c.getColumnIndex(MediaStore.Images.Media.DATA)
                            if (value >= 0) {
                                val path = c.getString(value)
                                zxing.decodeQRCode(path)
                            }
                        }
                    }
                    cursor?.close()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 5678) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult")
                openCamera()
            }
        } else if (requestCode == 6789) {
            var granted = true
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    granted = false
                    break
                }
            }
            if (granted) {
                openPhoto()
            } else {
                Toast.makeText(this@ScanActivity, "您需要授予读写权限才能使用该功能", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        if (hasCameraAndLightPermission()) {
            zxing.startCamera()
            zxing.startSpot()
        }
    }

    private fun openPhoto() {
        if (hasReadWritePermission()) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 7890)
        }
    }

    private fun hasCameraAndLightPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this@ScanActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ScanActivity,
                arrayOf(
                    Manifest.permission.CAMERA
                ),
                5678
            )
            return false
        } else {
            return true
        }
    }

    private fun hasReadWritePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this@ScanActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this@ScanActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ScanActivity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                6789
            )
            return false
        } else {
            return true
        }
    }
}