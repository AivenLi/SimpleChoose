package com.aiven.simplechoose.pages.home


import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.Contacts
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.ViewPager2FragmentAdapter
import com.aiven.simplechoose.databinding.ActivityHomeBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.utils.*
import com.aiven.updateapp.util.InstallApk
import com.google.android.material.navigation.NavigationBarView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File


class HomeActivity : BaseActivity<ActivityHomeBinding>(
    ActivityHomeBinding::inflate
), NavigationBarView.OnItemSelectedListener {

    private var curIndex = 0
    private var exitTime = 0L

//    private val handler by lazy {
//        object : WeakHandler(Looper.getMainLooper()) {
//            override fun handleMessage(msg: Message?) {
//                if (msg?.what == 1111) {
//                    if ((System.currentTimeMillis() - BaseActivity.getLastOptTime()) / 1000L >= 30) {
//                        Log.d(TAG, "30s无操作，退出所有界面除了home")
//                        ActivityManager.finishAll(this@HomeActivity)
//                    }
//                    sendEmptyMessageDelayed(1111, 2000L)
//                }
//            }
//        }
//    }

    companion object {
        fun start(context: Context) {
            Intent(context, HomeActivity::class.java).run {
                context.startActivity(this)
            }
        }
    }

    private lateinit var disablepose: Disposable
    override fun initView() {
        val fragments = arrayListOf<Fragment>(
            HomeFragment(),
            MineFragment()
        )
        viewBinding.viewPager2.adapter = ViewPager2FragmentAdapter(
            supportFragmentManager,
            lifecycle,
            fragments
        )
        viewBinding.navigationBottom.setOnItemSelectedListener(this)
        viewBinding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setBottomNavigationSelected(position)
            }
        })
        removeNavigationBottomLongClickToast()
    }

    private fun hasInstallPermission(): Boolean {
        return if ((ContextCompat.checkSelfPermission(
                this@HomeActivity,
                Manifest.permission.INSTALL_PACKAGES
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                this@HomeActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                this@HomeActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this@HomeActivity,
                arrayOf(
                    Manifest.permission.INSTALL_PACKAGES,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                23454
            )
            false
        } else {
            true
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_READ_WRITE_PERMISSION) {
            var granted = true
            for (g in grantResults) {
                if (g != PackageManager.PERMISSION_GRANTED) {
                    granted = false
                    break
                }
            }
            if (granted) {
                installApk()
            }
        }
    }

    private fun installApk() {
        val filename = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}${File.separator}SimpleChoose_Debug_1.0.0_1_2022-12-19_05_47_48.apk"
        val file = File(filename)
        if (file.exists()) {
            InstallApk.install(this@HomeActivity, file.absolutePath)
        }
//        val observable = Observable.create<String> { emitter ->
//            Thread.sleep(5000)
//            runCatching {
//                val httpURLConnection =
//                    URL("http://192.168.5.37:5767/school/springboot/20221213/SimpleChoose_Debug_1.0.0_1_2022-12-19_05_16_31.apk").openConnection() as HttpURLConnection
//                httpURLConnection.readTimeout = 5000
//                httpURLConnection.connectTimeout = 5000
//                httpURLConnection.connect()
//                if (httpURLConnection.responseCode == 200) {
//                    Log.d(TAG, "连接成功，开始下载")
//                    val size = httpURLConnection.contentLength
//                    Log.d(TAG, "文件大小: $size")
//                    val inputStream = httpURLConnection.inputStream
//                    val file = File("${cacheDir.absolutePath}${File.separator}test.apk")
//                    val outputStream = FileOutputStream(file)
//                    var n: Int;
//                    val byteArray = ByteArray(4096)
//                    do {
//                        n = inputStream.read(byteArray)
//                        if (n == -1) {
//                            break
//                        }
//                        outputStream.write(byteArray, 0, n)
//                    } while (n != -1)
//                    outputStream.flush()
//                    outputStream.close()
//                    inputStream.close()
//                    httpURLConnection.disconnect()
//                    Log.d(TAG, "下载完毕")
//                    file.absolutePath
//                } else {
//                    throw Throwable("下载失败：${httpURLConnection.responseCode}")
//                }
//            }.onSuccess {
//                emitter.onNext(it)
//            }.onFailure {
//                Log.d(TAG, "下载错误：${it}")
//            }
//        }
//        observable.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                Log.d(TAG, "开始安装App1: $it")
//                PackageManagerCompatP.install(
//                    applicationContext,
//                    it,
//                    packageManager
//                )
//            }
//
    }

    override fun initClick() {
    }

    override fun getDebugTAG(): String {
        return HomeActivity::class.java.simpleName
    }

    override fun onBackPressed() {
        exit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var index = curIndex
        when (item.itemId) {
            R.id.item_home -> index = 0
            R.id.item_mine -> index = 1
        }
        if (index == curIndex) {
            return false
        }
        curIndex = index
        viewBinding.viewPager2.currentItem = index
        return true
    }

    private fun removeNavigationBottomLongClickToast() {
        val bottomNavigationView =  viewBinding.navigationBottom.getChildAt(0)
        bottomNavigationView.findViewById<View>(R.id.item_home).setOnLongClickListener { true }
        bottomNavigationView.findViewById<View>(R.id.item_mine).setOnLongClickListener { true }
    }

    private fun setBottomNavigationSelected(position: Int) {
        if (position == 0) {
            viewBinding.navigationBottom.selectedItemId = R.id.item_home
        } else {
            viewBinding.navigationBottom.selectedItemId = R.id.item_mine
        }
    }

    private fun exit() {
        val interval = System.currentTimeMillis() - exitTime
        if (interval > 2000L) {
            toast(getString(R.string.enter_to_exit_again))
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }
}