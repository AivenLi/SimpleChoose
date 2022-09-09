package com.example.simplechoose.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB: ViewBinding>(
    private val inflate: ((layoutInflater: LayoutInflater) -> VB)
) : AppCompatActivity() {

    protected lateinit var viewBinding: VB
    protected lateinit var TAG: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = inflate(layoutInflater)
        setContentView(viewBinding.root)
        TAG = getDebugTAG()
        initView()
        initClick()
    }

    protected fun toast(msg: String) {
        toast(msg, Toast.LENGTH_SHORT)
    }

    protected fun toast(msg: String, duration: Int) {
        Toast.makeText(this, msg, duration).show()
    }

    abstract fun initView()

    abstract fun initClick()

    abstract fun getDebugTAG(): String
}