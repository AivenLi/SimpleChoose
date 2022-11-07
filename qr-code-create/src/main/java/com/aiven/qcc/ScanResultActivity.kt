package com.aiven.qcc

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ScanResultActivity : AppCompatActivity() {

    private lateinit var tvValue: TextView
    private lateinit var tvCopy: TextView
    private lateinit var tvUrl: TextView
    private var result: String? = null

    companion object {
        fun start(context: Context, result: String?) {
            Intent(context, ScanResultActivity::class.java).let {
                result?.let { r ->
                    it.putExtra("result", r)
                }
                context.startActivity(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)
        result = intent.getStringExtra("result")
        tvValue = findViewById(R.id.tv_value)
        tvCopy  = findViewById(R.id.tv_copy)
        tvUrl   = findViewById(R.id.tv_url_prompt)
        tvValue.text = result
        tvUrl.visibility = View.GONE
        tvCopy.visibility = View.GONE
        if (!result.isNullOrEmpty()) {
            tvCopy.visibility = View.VISIBLE
            tvCopy.setOnClickListener {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("Label", tvValue.text.toString())
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this@ScanResultActivity, "已复制", Toast.LENGTH_SHORT).show()
            }
            if (result!!.startsWith("http://") || result!!.startsWith("https://")) {
                tvUrl.visibility = View.VISIBLE
                tvValue.paint.flags = Paint.UNDERLINE_TEXT_FLAG
                tvValue.setOnClickListener {
                    val uri = Uri.parse(result!!)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            }
        }
    }
}