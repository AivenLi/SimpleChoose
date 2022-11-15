package com.aiven.simplechoose.pages.imagecompress

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import cc.shinichi.library.ImagePreview
import com.aiven.simplechoose.R
import com.aiven.simplechoose.databinding.ActivityImageCompressBinding
import com.aiven.simplechoose.databinding.DialogYesNoBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.pages.CustomDialog
import com.aiven.simplechoose.utils.getImageFromPhoto
import com.aiven.simplechoose.utils.grantedPermission
import com.aiven.simplechoose.utils.setSingleClickListener
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class ImageCompressActivity : BaseActivity<ActivityImageCompressBinding>(ActivityImageCompressBinding::inflate) {

    /**
     * 最终图片文件路径（相机或相册）
     * */
    private var imgPath: String? = null
    private var compressImgPath: String? = null
    /**
     * 拍照后的图片文件路径
     * */
    private var cameraPhoto: String? = null

    private val dialogYesNo by lazy {
        CustomDialog<DialogYesNoBinding>(
            context = this,
            inflate = DialogYesNoBinding::inflate
        ).apply {
            binding.tvYesNoTitle.text = getString(R.string.choose_image)
            binding.tvYesNoYes.text = getString(R.string.camera)
            binding.tvYesNoCancel.text = getString(R.string.photo)
            binding.tvYesNoYes.setSingleClickListener {
                if (hasCameraPermissionNoRequest()) {
                    openCamera()
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                hide()
            }
            binding.tvYesNoCancel.setSingleClickListener {
                if (hasReadWritePermissionNoRequest()) {
                    openPhoto()
                } else {
                    readWritePermissionLauncher.launch(arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ))
                }
                hide()
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            loadChooseImage(cameraPhoto)
        } else {
            loadChooseImage(null)
        }
    }

    private val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            loadChooseImage(it.data?.getImageFromPhoto(this@ImageCompressActivity))
        } else {
            loadChooseImage(null)
        }
    }

    private val readWritePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (grantedPermission(it)) {
            openPhoto()
        } else {
            toast(getString(R.string.you_need_granted_permission_to_read_photo))
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            openCamera()
        } else {
            toast(getString(R.string.you_need_granted_permission_to_read_photo))
        }
    }

    private lateinit var photoOfCameraPath: String

    companion object {
        fun start(context: Context) {
            Intent(context, ImageCompressActivity::class.java).let {
                context.startActivity(it)
            }
        }
    }

    override fun initView() {
        photoOfCameraPath = "${cacheDir.absolutePath}${File.separator}compress_from_camera_"
        viewBinding.tvSave.visibility = View.GONE
        viewBinding.tvCompress.visibility = View.GONE
    }

    override fun initClick() {
        viewBinding.tvSave.setSingleClickListener {
            if (compressImgPath.isNullOrEmpty()) {
                toast(getString(R.string.please_compress_image_first))
            } else {
                saveCompressImage(compressImgPath!!)
            }
        }
        viewBinding.tvChooseImage.setSingleClickListener {
            dialogYesNo.show()
        }
        viewBinding.tvCompress.setSingleClickListener {
            if (imgPath.isNullOrEmpty()) {
                toast(getString(R.string.please_choose_image_first))
            } else {
                doLubanCompress(imgPath!!)
            }
        }
        viewBinding.imgDest.setSingleClickListener {
            if (!compressImgPath.isNullOrEmpty()) {
                val imgList = arrayListOf(
                    compressImgPath
                )
                if (!imgPath.isNullOrEmpty()) {
                    imgList.add(0, imgPath)
                }
                ImagePreview.getInstance()
                    .setContext(this@ImageCompressActivity)
                    .setImageList(imgList)
                    .setIndex(imgList.size - 1)
                    .setEnableClickClose(true)
                    .setEnableDragClose(true)
                    .setEnableUpDragClose(true)
                    .setShowCloseButton(true)
                    .setShowDownButton(false)
                    .setShowErrorToast(true)
                    .start()
            }
        }
        viewBinding.imgSrc.setSingleClickListener {
            if (!imgPath.isNullOrEmpty()) {
                val imgList = arrayListOf(
                    imgPath
                )
                if (!compressImgPath.isNullOrEmpty()) {
                    imgList.add(compressImgPath)
                }
                ImagePreview.getInstance()
                    .setContext(this@ImageCompressActivity)
                    .setImageList(imgList)
                    .setIndex(0)
                    .setEnableClickClose(true)
                    .setEnableDragClose(true)
                    .setEnableUpDragClose(true)
                    .setShowCloseButton(true)
                    .setShowDownButton(false)
                    .setShowErrorToast(true)
                    .start()
            }
        }
    }

    override fun getDebugTAG(): String {
        return ImageCompressActivity::class.java.simpleName
    }

    private fun loadChooseImage(path: String?) {
        imgPath = path
        viewBinding.tvDestSize.text = null
        viewBinding.imgDest.setImageDrawable(null)
        compressImgPath = null
        if (!imgPath.isNullOrEmpty()) {
            Glide.with(this@ImageCompressActivity)
                .load(imgPath)
                .into(viewBinding.imgSrc)
            viewBinding.tvCompress.visibility = View.VISIBLE
            val file = File(path!!)
            viewBinding.tvSrcSize.text = formatSize(file.length())
        } else {
            viewBinding.imgSrc.setImageDrawable(null)
            viewBinding.tvCompress.visibility = View.GONE
        }
        viewBinding.tvSave.visibility = View.GONE
    }

    private fun openPhoto() {
        photoLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }

    private fun openCamera() {
        cameraPhoto = "$photoOfCameraPath${System.currentTimeMillis()}.jpg"
        val uri = FileProvider.getUriForFile(
            this@ImageCompressActivity,
            "${packageName}.fileprovider",
            File(cameraPhoto!!)
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        cameraLauncher.launch(intent)
    }

    private fun getFileDirFromFilename(filename: String): String {
        return filename.substring(0, filename.lastIndexOf(File.separator))
    }

    private fun getFilename(filename: String): String {
        return filename.substring(filename.lastIndexOf(File.separator) + 1)
    }

    private fun doLubanCompress(path: String) {
        compressImgPath = null
        Luban.with(this@ImageCompressActivity)
            .load(path)
            .ignoreBy(100)
            .setTargetDir(cacheDir.absolutePath)
            .filter { path1 ->
                !(TextUtils.isEmpty(path1) || path1!!.lowercase(Locale.getDefault())
                    .endsWith(".gif"))
            }
            .setRenameListener {
                "simplechoose_${getFilename(path)}"
            }

            .setCompressListener(object : OnCompressListener {
                override fun onStart() {
                    Log.d(TAG, "开始压缩")
                }

                override fun onSuccess(file: File?) {
                    file?.let {
                        Log.d(TAG, "压缩成功：${it.absolutePath}")
                        Glide.with(this@ImageCompressActivity)
                            .load(it.absolutePath)
                            .into(viewBinding.imgDest)
                        viewBinding.imgDest.visibility = View.VISIBLE
                        viewBinding.tvSave.visibility = View.VISIBLE
                        viewBinding.tvCompress.visibility = View.GONE
                        compressImgPath = it.absolutePath
                        viewBinding.tvDestSize.text = formatSize(it.length())
                    }
                }

                override fun onError(e: Throwable?) {
                    Log.d(TAG, "压缩失败：${e.toString()}")
                    viewBinding.imgDest.visibility = View.GONE
                    viewBinding.tvSave.visibility = View.GONE
                    viewBinding.tvCompress.visibility = View.VISIBLE
                }
            })
            .launch()
    }

    private fun saveCompressImage(path: String) {
        val observable = Observable.create<String> { emitter ->
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, getFilename(path))
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
            contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis() / 1000)
            val contentResolver = this@ImageCompressActivity.contentResolver
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                val fileOutputStream = contentResolver.openOutputStream(uri) as FileOutputStream
                val file = File(path)
                val fileInputStream = FileInputStream(file)
                while (true) {
                    val byteArray = fileInputStream.readBytes()
                    if (byteArray.isEmpty()) {
                        break
                    }
                    fileOutputStream.write(byteArray)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                fileInputStream.close()
                this@ImageCompressActivity.contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )?.let { cursor ->
                    cursor.moveToFirst()
                    val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    emitter.onNext(cursor.getString(idx))
                    file.delete()
                    Log.d(TAG, "保存路径：${cursor.getString(idx)}")
                    cursor.close()
                }
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                toast("保存成功：$it")
                viewBinding.tvSave.visibility = View.GONE
                imgPath = null
                compressImgPath = null
                viewBinding.imgSrc.setImageDrawable(null)
                viewBinding.tvSrcSize.text = null
                viewBinding.imgDest.setImageDrawable(null)
                viewBinding.tvDestSize.text = null
            }
    }

    private fun formatSize(size: Long): String {
        val sizeByte: Double = size.toDouble()
        val format = DecimalFormat("0.##")
        format.roundingMode = RoundingMode.FLOOR
        return when {
            sizeByte < 1024.0 -> { // 不足一K
                "${format.format(sizeByte)}B"
            }
            sizeByte < (1024.0 * 1024.0) -> { // 不足一M
                "${format.format(sizeByte / 1024.0)}KB"
            }
            sizeByte < (1024 * 1024.0 * 1024.0) -> { // 不足一G
                "${format.format(sizeByte / (1024 * 1024.0))}MB"
            }
            sizeByte < (1024 * 1024 * 1024.0 * 1024.0) -> {
                "${format.format(sizeByte / (1024 * 1024 * 1024.0))}GB"
            }
            else -> {
                "${format.format(sizeByte / (1024 * 1024 * 1024.0 * 1024))}TB"
            }
        }
    }
}