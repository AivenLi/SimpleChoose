package com.aiven.simplechoose.pages.qrcode

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.provider.CalendarContract.CalendarCache.URI
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aiven.qcc.OnQRCreateListener
import com.aiven.qcc.QRCreator
import com.aiven.simplechoose.R
import com.aiven.simplechoose.databinding.ActivityQrCodeBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.utils.setSingleClickListener
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

class QRCodeActivity : BaseActivity<ActivityQrCodeBinding>(ActivityQrCodeBinding::inflate) {

    private var bitmap: Bitmap? = null

    companion object {
        fun start(context: Context) {
            Intent(context, QRCodeActivity::class.java).run {
                context.startActivity(this)
            }
        }
    }

    override fun initView() {

    }

    override fun initClick() {
        viewBinding.imgBack.setSingleClickListener {
            finish()
        }
        viewBinding.tvCreateNow.setSingleClickListener {
            val content = viewBinding.edtContent.text.toString().trim()
            if (content.isNullOrEmpty()) {
                toast(getString(R.string.content_can_not_empty))
                return@setSingleClickListener
            }
            val size = viewBinding.edtQrSize.text.toString().toInt()
            if (size < 50) {
                toast(getString(R.string.qr_size_can_not_less_50))
                return@setSingleClickListener
            }
            Log.d(TAG, "Content: $content, Size: $size")
//            val bitmap = BitmapFactory.decodeResource(this@QRCodeActivity.resources, R.drawable.ic_app)
            QRCreator.Builder()
                .setContent(content)
                .setSize(size)
                .setColorBlack(Color.RED)
                .setColorWhite(Color.GREEN)
                .setOnQRCreatorListener(object : OnQRCreateListener {
                    override fun onSuccess(bitmap: Bitmap) {
                        this@QRCodeActivity.bitmap = bitmap
                        Glide.with(this@QRCodeActivity)
                            .load(bitmap)
                            .into(viewBinding.imgQrCode)
                        viewBinding.tvSaveQrCode.visibility = View.VISIBLE
                    }

                    override fun onFailure(error: String) {
                        Log.d(TAG, "error: $error")
                        toast(error)
                        this@QRCodeActivity.bitmap = null
                        viewBinding.tvSaveQrCode.visibility = View.GONE
                    }

                })
                .builder()
                .createQRCodeBitmap()
        }
        viewBinding.tvSaveQrCode.setSingleClickListener {
            if (hasReadWritePermission()) {
                doSaveBitmapToPhotos()
            }
        }
    }

    override fun getDebugTAG(): String {
        return QRCodeActivity::class.java.simpleName
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 5678) {
            var granted = true
            for (p in grantResults) {
                if (p != PackageManager.PERMISSION_GRANTED) {
                    granted = false
                    break
                }
            }
            if (granted) {
                doSaveBitmapToPhotos()
            } else {
                toast(getString(R.string.you_need_granted_permission))
            }
        }
    }

    private fun doSaveBitmapToPhotos() {
        val observable = Observable.create<String> { emitter ->
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "simple-choose-qr-code-${System.currentTimeMillis()}")
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
            contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis() / 1000)
            val contentResolver = this@QRCodeActivity.contentResolver
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { _uri ->
                var fileOutputStream: FileOutputStream? = null
                runCatching {
                    fileOutputStream = contentResolver.openOutputStream(_uri) as FileOutputStream?
                    fileOutputStream?.let { fos ->
                        bitmap?.let { bit ->
                            bit.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                            fos.close()
                            fileOutputStream = null
                            val cursor =
                                this@QRCodeActivity.contentResolver.query(
                                    _uri,
                                    null,
                                    null,
                                    null,
                                    null
                                )
                            cursor?.let { c ->
                                c.moveToFirst()
                                val idx = c.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                                emitter.onNext(c.getString(idx))
                                c.close()
                            }
                        }
                    }
                }.onFailure {
                    emitter.onError(it)
                }
                fileOutputStream?.close()
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Glide.with(this@QRCodeActivity)
                    .load(R.drawable.ic_app)
                    .into(viewBinding.imgQrCode)
                bitmap?.recycle()
                bitmap = null
                toast(getString(R.string.save_success, it), Toast.LENGTH_LONG)
                viewBinding.tvSaveQrCode.visibility = View.GONE
            },{
                var error = it.toString()
                error = error.substring(error.indexOf(':') + 1)
                toast(getString(R.string.save_failure, error))
            })
    }

    private fun hasReadWritePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this@QRCodeActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this@QRCodeActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@QRCodeActivity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                5678
            )
            return false
        } else {
            return true
        }
    }
}