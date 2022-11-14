package com.aiven.simplechoose.pages

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.aiven.simplechoose.utils.Constant

abstract class BaseActivity<VB: ViewBinding>(
    private val inflate: ((layoutInflater: LayoutInflater) -> VB)
) : AppCompatActivity() {

    protected lateinit var viewBinding: VB
    protected lateinit var TAG: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = inflate(layoutInflater)
        setContentView(viewBinding.root)
        TAG = "${getDebugTAG()}-Debug"
        initView()
        initClick()
    }

    protected fun toast(msg: String) {
        toast(msg, Toast.LENGTH_SHORT)
    }

    protected fun toast(msg: String, duration: Int) {
        Toast.makeText(this, msg, duration).show()
    }

    protected fun hasCameraPermission(): Boolean {
        return if (!hasCameraPermissionNoRequest()) {
            ActivityCompat.requestPermissions(this@BaseActivity, arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CAMREA_PERMISSION)
            false
        } else {
            true
        }
    }

    protected fun hasCameraPermissionNoRequest(): Boolean {
        return ContextCompat.checkSelfPermission(this@BaseActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    protected fun hasReadWritePermission(): Boolean {
        return if (!hasReadWritePermissionNoRequest()) {
            ActivityCompat.requestPermissions(
                this@BaseActivity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                Constant.REQUEST_READ_WRITE_PERMISSION
            )
            false
        } else {
            true
        }
    }

    protected fun hasReadWritePermissionNoRequest(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@BaseActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this@BaseActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    abstract fun initView()

    abstract fun initClick()

    abstract fun getDebugTAG(): String
}