package com.aiven.updateapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.aiven.updateapp.R;
import com.aiven.updateapp.http.HttpDownloader;
import com.aiven.updateapp.util.Md5Utils;
import com.aiven.updateapp.util.UpdateAppUtil;
import com.aiven.updateapp.util.InstallApk;

import java.io.File;

public class UpdateAppService extends Service {

    private static final String TAG = UpdateAppService.class.getSimpleName();
    private HttpDownloader.DownloadListener downloadListener;
    private boolean isBackground = false;
    private IBinder binder = new MyBinder();
    private boolean isDownload = false;
  //  private Notification notification;
    private PendingIntent pendingIntent;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private String downloadPath;
    private String md5 = null;
    private String downloadTempTag = ".temp";
    private String downloadDoneTag = "";
    private int progress = 0;
    private int preProgress = 0;
    private HttpDownloader httpDownloader;
    private String updateAppDestroyTitle;
    private String updateAppDestroyDesc;
    private static final int NOTIFICATION_CHANNEL_ID = 100;

    public UpdateAppService(){}

    @Override
    public void onCreate() {

        super.onCreate();

        updateAppDestroyTitle = getString(R.string.app_update_destroy_title);
        updateAppDestroyDesc = getString(R.string.app_update_destroy_desc);
//        Intent intent = new Intent(this, )
//        pendingIntent = PendingIntent.getActivity(this, 0, )

        builder = new NotificationCompat.Builder(this, TAG)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setContentTitle(getString(R.string.update_app))
                .setProgress(100, 0, false)
                .setColor(0xff00bfff)
                .setSmallIcon(R.drawable.ic_baseline_arrow_circle_down_24)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_app))
                .setWhen(0);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel =
                    new NotificationChannel(TAG, "updateApp", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        //isBind = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //isBind = false;
        Log.d(TAG, "取消绑定");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "下载服务onDestroy");
//        if (isBackground) {
//            Log.d(TAG, "后台下载，正在下载：" + isDownload);
//            if (isDownload) {
//                builder.setContentTitle(updateAppDestroyTitle)
//                        .setContentText(updateAppDestroyDesc);
//                notificationManager.notify(NOTIFICATION_CHANNEL_ID, builder.build());
//            }
//        } else {
//            notificationManager.cancel(NOTIFICATION_CHANNEL_ID);
//        }
        notificationManager.cancel(NOTIFICATION_CHANNEL_ID);
        if (httpDownloader != null) {
            httpDownloader.setPause(true);
        }
        super.onDestroy();
    }

    public void setDownloadServiceDestroyNotification(String title, String desc) {
        if (!TextUtils.isEmpty(title)) {
            updateAppDestroyTitle = title;
        }
        if (!TextUtils.isEmpty(desc)) {
            updateAppDestroyDesc = desc;
        }
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setDownloadTempTag(String tag) {
        downloadTempTag = tag;
    }

    public void setDownloadDoneTag(String tag) {
        downloadDoneTag = tag;
    }

    public void setDownloadListener(HttpDownloader.DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void setContentActivity(Intent intent) {

        if (builder != null ) {

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(UpdateAppService.this, 0, intent, 0);
            builder.setContentIntent(pendingIntent);
        }
    }

    public void test() {

        new Thread() {
            @Override
            public void run() {
                builder.setProgress(100, 50, false)
                        .setContentText(getString(R.string.background_download_percent, 50));
                notificationManager.notify(NOTIFICATION_CHANNEL_ID, builder.build());
            }
        }.start();
    }

    public synchronized void doInBackground(boolean enable) {
        isBackground = enable;
    }

    public boolean isBackground() {
        return isBackground;
    }

    public void setPause(boolean pause) {
        if (httpDownloader != null) {
            httpDownloader.setPause(pause);
        }
    }

    public synchronized void start(String url, String filename, long fileSize) {

        if (isDownload) {
            Log.d(TAG, "正在下载，返回");
            return;
        }
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(filename)) {
            if (downloadListener != null) {
                downloadListener.downloadFailure("Url or filename is null");
            }
            return;
        }
        Log.d(TAG, "执行下载...");
        String path;
        if (TextUtils.isEmpty(downloadPath)) {
            path = UpdateAppUtil.getDownloadAppPath(UpdateAppService.this);
        } else {
            path = downloadPath;
        }
        Log.d(TAG, "下载路径：" + path);
        httpDownloader = new HttpDownloader.Builder(url)
                .setSavePath(path)
                .setFilename(filename)
                .setFileSize(fileSize)
                .setMd5(md5)
                .setDownloadTempTag(downloadTempTag)
                .setDownloadDoneTag(downloadDoneTag)
                .setMode(HttpDownloader.Mode.APPEND)
                .setDownloadListener(new HttpDownloader.DownloadListener() {
                    @Override
                    public void downloadProgress(float ratio) {
                        Log.d(TAG, "下载进度：" + ratio);
                        isDownload = true;
                        progress = (int)ratio;
                        if (isBackground && preProgress < progress) {
                            // TODO 将下载进度放到通知栏
                            preProgress = progress;
                            builder.setProgress(100, progress, false)
                                    .setContentText(getString(R.string.background_download_percent, progress))
                                    .setAutoCancel(false)
                                    .setOnlyAlertOnce(true);
                            notificationManager.notify(NOTIFICATION_CHANNEL_ID, builder.build());
                        } else if (downloadListener != null) {
                            downloadListener.downloadProgress(ratio);
                        }
                    }

                    @Override
                    public void downloadSuccess(String filepath) {
                        isDownload = false;
                        Log.d(TAG, "下载回调：" + downloadListener == null ? "空" : "非空");
                        if (downloadListener != null) {
                            downloadListener.downloadSuccess(filepath);
                        }
                        Log.d(TAG, "下载完成Service: " + filepath);
                        InstallApk.install(UpdateAppService.this, filepath);
                        stopSelf();
                    }

                    @Override
                    public void downloadFailure(String error) {
                        Log.d(TAG, "下载失败Service：" + error);
                        isDownload = false;
                        if (isBackground && !"已暂停".equals(error)) {
                            builder.setProgress(100, progress, false)
                                    .setContentText(getString(R.string.download_failure_reason, error))
                                    .setAutoCancel(false);
                            notificationManager.notify(NOTIFICATION_CHANNEL_ID, builder.build());
                        }
                        if (downloadListener != null) {
                            downloadListener.downloadFailure(error);
                        }
                        if ("已暂停".equals(error)) {
                            httpDownloader = null;
                            System.gc();
                        }
                    }
                })
                .builder();
    }


    public class MyBinder extends Binder {

        public UpdateAppService getUpdateAppService() {
            return UpdateAppService.this;
        }
    }
}
