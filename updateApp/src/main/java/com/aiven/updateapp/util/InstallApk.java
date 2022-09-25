package com.aiven.updateapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.File;

public class InstallApk {

    private static final String type = "application/vnd.android.package-archive";

    public static void install(Context context, String path) {

        if (context == null || TextUtils.isEmpty(path)) {
            return;
        }
        File apk = new File(path);
        if (!apk.exists()) {
            return;
        }
        Intent installIntent = new Intent();
        installIntent.setAction(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.addCategory(Intent.CATEGORY_DEFAULT);
        String packageName = context.getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(context, packageName + ".fileprovider", apk);
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.setDataAndType(uri, type);
        } else {
            installIntent.setDataAndType(Uri.fromFile(apk), type);
        }
        context.startActivity(installIntent);
        //activity.startActivityForResult(installIntent, 0);
    }
}
