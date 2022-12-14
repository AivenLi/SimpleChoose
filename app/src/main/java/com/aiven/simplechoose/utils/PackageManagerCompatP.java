package com.aiven.simplechoose.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.aiven.simplechoose.receivers.InstallResultReceiver;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PackageManagerCompatP {
 
    private static final String TAG = PackageManagerCompatP.class.getSimpleName() + "-Debug";
 
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void install(Context context, String apkFilePath, PackageManager packageManager) {
        File apkFile = new File(apkFilePath);
        PackageInstaller packageInstaller = packageManager.getPackageInstaller();
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        sessionParams.setSize(apkFile.length());
 
        int sessionId = createSession(packageInstaller, sessionParams);
        if (sessionId != -1) {
            Log.d(TAG, "创建会话成功");
            boolean copySuccess = copyInstallFile(packageInstaller, sessionId, apkFilePath);
            if (copySuccess) {
                Log.d(TAG, "复制文件成功");
                execInstallCommand(context, packageInstaller, sessionId);
            } else {
                Log.d(TAG, "复制文件失败");
            }
        } else {
            Log.d(TAG, "创建绘画失败");
        }
    }
 
 
 
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static int createSession(PackageInstaller packageInstaller,
                              PackageInstaller.SessionParams sessionParams) {
        int sessionId = -1;
        try {
            sessionId = packageInstaller.createSession(sessionParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionId;
    }
 
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean copyInstallFile(PackageInstaller packageInstaller,
                                    int sessionId, String apkFilePath) {
        InputStream in = null;
        OutputStream out = null;
        PackageInstaller.Session session = null;
        boolean success = false;
        try {
            File apkFile = new File(apkFilePath);
            session = packageInstaller.openSession(sessionId);
            out = session.openWrite("base.apk", 0, apkFile.length());
            in = new FileInputStream(apkFile);
            int total = 0, c;
            byte[] buffer = new byte[65536];
            while ((c = in.read(buffer)) != -1) {
                total += c;
                out.write(buffer, 0, c);
            }
            session.fsync(out);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "复制出错：" + e.toString());
        } finally {
            closeQuietly(out);
            closeQuietly(in);
            closeQuietly(session);
        }
        return success;
    }
 
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void execInstallCommand(Context context, PackageInstaller packageInstaller, int sessionId) {
        PackageInstaller.Session session = null;
        try {
            session = packageInstaller.openSession(sessionId);
            Intent intent = new Intent(context, InstallResultReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            session.commit(pendingIntent.getIntentSender());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "安装失败： " + e.toString());
        } finally {
            closeQuietly(session);
        }
    }
    private static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }
    }
}