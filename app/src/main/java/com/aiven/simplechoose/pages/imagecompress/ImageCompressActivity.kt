package com.aiven.simplechoose.pages.imagecompress

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.aiven.simplechoose.R
import com.aiven.simplechoose.databinding.ActivityImageCompressBinding
import com.aiven.simplechoose.databinding.DialogYesNoBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.pages.CustomDialog
import com.aiven.simplechoose.utils.getImageFromPhoto
import com.aiven.simplechoose.utils.grantedPermission
import com.aiven.simplechoose.utils.setSingleClickListener
import com.bumptech.glide.Glide
import java.io.File

class ImageCompressActivity : BaseActivity<ActivityImageCompressBinding>(ActivityImageCompressBinding::inflate) {

    private var imgPath: String? = null
    private var compressImgPath: String? = null

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
            loadChooseImage(photoOfCameraPath)
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
        photoOfCameraPath = "${cacheDir.absolutePath}${File.separator}compress_from_camera.jpg"
        viewBinding.tvSave.visibility = View.GONE
        viewBinding.tvCompress.visibility = View.GONE
        viewBinding.lytSeekBar.visibility = View.GONE
    }

    override fun initClick() {
        viewBinding.tvSave.setSingleClickListener {

        }
        viewBinding.tvChooseImage.setSingleClickListener {
            dialogYesNo.show()
        }
        viewBinding.tvCompress.setSingleClickListener {
            if (imgPath.isNullOrEmpty()) {
                toast(getString(R.string.please_choose_image_first))
            } else {

            }
        }
        viewBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewBinding.tvSeekValue.text = getString(R.string.percent, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    override fun getDebugTAG(): String {
        return ImageCompressActivity::class.java.simpleName
    }

    private fun loadChooseImage(path: String?) {
        imgPath = path
        if (!imgPath.isNullOrEmpty()) {
            Glide.with(this@ImageCompressActivity)
                .load(imgPath)
                .into(viewBinding.imgSrc)
            viewBinding.tvCompress.visibility = View.VISIBLE
            viewBinding.lytSeekBar.visibility = View.VISIBLE
        } else {
            viewBinding.imgSrc.setImageDrawable(null)
            viewBinding.tvCompress.visibility = View.GONE
            viewBinding.lytSeekBar.visibility = View.GONE
        }
        viewBinding.tvSave.visibility = View.GONE
    }

    private fun openPhoto() {
        photoLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }

    private fun openCamera() {
        val uri = FileProvider.getUriForFile(this@ImageCompressActivity, "${packageName}.fileprovider", File(photoOfCameraPath))
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        cameraLauncher.launch(intent)
    }
}