package com.aiven.simplechoose.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.util.Log

class InstallResultReceiver: BroadcastReceiver() {

    companion object {
        private const val TAG = "InstallResultReceiver-Debug";
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "有广播")
        intent?.let {
            when (it.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)) {
                PackageInstaller.STATUS_SUCCESS -> Log.d(TAG, "安装成功")
                else -> Log.d(TAG, "安装失败")
            }
        }
    }
}