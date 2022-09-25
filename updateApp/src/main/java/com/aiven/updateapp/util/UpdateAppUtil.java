package com.aiven.updateapp.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.aiven.updateapp.R;
import com.aiven.updateapp.bean.UpdateAppBean;
import com.aiven.updateapp.http.HttpDownloader;
import com.aiven.updateapp.service.UpdateAppService;
import com.aiven.updateapp.ui.dialog.LayoutDialog;
import com.aiven.updateapp.ui.widget.ProgressButton;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import io.reactivex.rxjava3.annotations.NonNull;

public class UpdateAppUtil {

    private Context context;
    private LayoutDialog dialogUpdate;
    private TextView mTvVersion;
    private UpdateAppService updateAppService;
    private TextView mTvUpdateDesc;
    private TextView mTvUpdateAfter;
    private ProgressButton progressButton;
    private View dialogBottomSpace;
    private ServiceConnection serviceConnection;
    private UpdateAppBean updateAppBean;
    private String downloadPath;
    private String downloadTempTag;
    private String downloadDoneTag;
    private Intent downloadIntent = null;
    private boolean isDownload = false;
    private Handler handler;
    private String filePath;

    public UpdateAppUtil(@NonNull Context context, @NonNull Lifecycle lifecycle) {

        this.context = context.getApplicationContext();
        dialogUpdate   = new LayoutDialog(context, R.layout.dialog_update_app_prompt);
        dialogUpdate.setCancelable(false);
        mTvUpdateDesc = dialogUpdate.getRootView().findViewById(R.id.tv_version_desc_content);
        mTvUpdateDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
        progressButton = dialogUpdate.getRootView().findViewById(R.id.progress_btn);
        mTvUpdateAfter = dialogUpdate.getRootView().findViewById(R.id.tv_update_after);
        mTvVersion = dialogUpdate.getRootView().findViewById(R.id.tv_version);
        dialogBottomSpace = dialogUpdate.getRootView().findViewById(R.id.view_bottom_space);
        progressButton.setOnClickListener(v -> {
            int status = progressButton.getStatus();
            if (status == ProgressButton.STATUS_START) {
                start();
            } else if (status == ProgressButton.STATUS_DOWNLOADING) {
                if (updateAppService != null) {
                  //  progressButton.setStatus(ProgressButton.STATUS_PAUSE);
                    updateAppService.setPause(true);
                }
            } else if (status == ProgressButton.STATUS_PAUSE) {
                start();
            } else if (status == ProgressButton.STATUS_FAILED) {
                start();
            } else if (status == ProgressButton.STATUS_DONE) {
                dialogUpdate.dismiss();
                InstallApk.install(UpdateAppUtil.this.context, filePath);
            }
        });
        mTvUpdateAfter.setOnClickListener(v -> {
            if (UpdateAppUtil.this
                    .context
                    .getString(R.string.background_download)
                    .equals(mTvUpdateAfter.getText().toString())) {
                if (updateAppService != null) {
                    updateAppService.doInBackground(true);
                    dialogUpdate.dismiss();
                }
            } else {
                if (updateAppService != null) {
                    updateAppService.stopSelf();
                    if (serviceConnection != null) {
                        UpdateAppUtil.this.context.unbindService(serviceConnection);
                    }
                }
                dialogUpdate.dismiss();
            }
        });
        initLifeCycle(lifecycle);
        initServiceConnection();
        initHandler();
    }

    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@androidx.annotation.NonNull Message msg) {
                if (msg.what == 1024) {
                    mTvUpdateAfter.setText(UpdateAppUtil.this.context.getString(R.string.update_after));
                    Toast.makeText(
                            UpdateAppUtil.this.context,
                            (String)msg.obj,
                            Toast.LENGTH_SHORT
                    ).show();
                } else if (msg.what == 1025) {
                    filePath = (String)msg.obj;
                    progressButton.setStatus(ProgressButton.STATUS_DONE);
                    mTvUpdateAfter.setVisibility(View.GONE);
                    dialogBottomSpace.setVisibility(View.VISIBLE);
                    dialogUpdate.dismiss();
                    Log.d(UpdateAppService.class.getSimpleName(), "UpdateUtils，下载完成，隐藏弹窗");
                }
            }
        };
    }

    /**
     * 设置正在下载时的文件标识，可用于事后清理文件
     * @param tag 标识
     * */
    public UpdateAppUtil setDownloadTempFileTag(String tag) {
        downloadTempTag = tag;
        return this;
    }

    /**
     * 设置下载完成后的文件表示，可用于事后清理文件
     * @param tag 标识
     * */
    public UpdateAppUtil setDownloadDoneFileTag(String tag) {
        downloadDoneTag = tag;
        return this;
    }

    public UpdateAppUtil setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        return this;
    }

    public void setUpdateAppBean(UpdateAppBean updateAppBean) {

        if (!isDownload && progressButton.getValue() < 100.00f) {
            this.updateAppBean = updateAppBean;
            mTvUpdateDesc.setText(updateAppBean.getDesc());
            if (updateAppBean.getMode() == 0) {

                mTvUpdateAfter.setVisibility(View.GONE);
                dialogBottomSpace.setVisibility(View.VISIBLE);
            } else {

                mTvUpdateAfter.setVisibility(View.VISIBLE);
                dialogBottomSpace.setVisibility(View.GONE);
            }
            ((TextView)(dialogUpdate.getRootView().findViewById(R.id.tv_app_size)))
                    .setText(context.getString(R.string.app_size_title_number, formatSize(updateAppBean.getApkSize())));
            mTvVersion.setText(context.getString(R.string.new_version, updateAppBean.getVersion()));
            dialogUpdate.show();
        }
    }

    public UpdateAppUtil setUpdateAppService(UpdateAppService updateAppService) {

        this.updateAppService = updateAppService;
        return this;
    }

    public UpdateAppUtil setUpdateCancelable(boolean flag) {

        dialogUpdate.setCancelable(flag);
        return this;
    }

    public UpdateAppUtil setEnableBackground(boolean enableBackground) {

        if (enableBackground) {
            mTvUpdateAfter.setVisibility(View.VISIBLE);
            dialogBottomSpace.setVisibility(View.GONE);
        } else {
            mTvUpdateAfter.setVisibility(View.GONE);
            dialogBottomSpace.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public void showUpdate() {

        dialogUpdate.show();
    }

    public void setProgress(int progress) {
        progressButton.setValue(progress);
    }

    public void hideUpdate() {

        dialogUpdate.dismiss();
    }

    public void hideProgress() {

        if (progressButton.getValue() != 100 && updateAppService != null) {
            updateAppService.doInBackground(true);
        }
    }

    private void initLifeCycle(@NonNull Lifecycle lifecycle) {

        lifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@androidx.annotation.NonNull LifecycleOwner source, @androidx.annotation.NonNull Lifecycle.Event event) {

                if (event.getTargetState() == Lifecycle.State.DESTROYED) {

                    if (updateAppService != null) {

                        updateAppService.doInBackground(true);
                        updateAppService.setDownloadListener(null);
                        updateAppService = null;
                    }
                    if (dialogUpdate != null && dialogUpdate.isShowing()) {
                        dialogUpdate.dismiss();
                    }
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                        handler = null;
                    }
                }
            }
        });
    }

    private void initServiceConnection() {

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                updateAppService = ((UpdateAppService.MyBinder)service).getUpdateAppService();
                /**
                 * 绑定服务是为了能够在UI中刷新下载进度，startService是为了
                 * 保证当前UI被销毁了也不影响下载任务。
                 * */
                updateAppService.doInBackground(false);
                updateAppService.setDownloadPath(downloadPath);
                updateAppService.setDownloadTempTag(downloadTempTag);
                updateAppService.setDownloadDoneTag(downloadDoneTag);
                updateAppService.setMd5(updateAppBean.getMd5());
                updateAppService.start(updateAppBean.getUrl(), updateAppBean.getVersion().replaceAll("\\.", "_"), updateAppBean.getApkSize());
                if (downloadIntent == null) {
                    downloadIntent = new Intent(context, UpdateAppService.class);
                }
                context.startService(downloadIntent);
                updateAppService.setDownloadListener(new HttpDownloader.DownloadListener() {
                    @Override
                    public void downloadProgress(float ratio) {
                        progressButton.setValue(ratio);
                    }

                    @Override
                    public void downloadSuccess(String filepath) {
                        Log.d(UpdateAppService.class.getSimpleName(), "UpdateUtil，下载完成");
                        handler.sendMessage(handler.obtainMessage(1025, filepath));
                        isDownload = false;
                    }

                    @Override
                    public void downloadFailure(String error) {
                        if (!"已暂停".equals(error)) {
                            progressButton.setFailedTextNoInvalidate(error);
                            progressButton.setStatus(ProgressButton.STATUS_FAILED);
                        } else {
                            progressButton.setStatus(ProgressButton.STATUS_PAUSE);
                        }
                        handler.sendMessage(handler.obtainMessage(1024, error));
                        isDownload = false;
                    }

                    @Override
                    public Context getContext() {
                        return context;
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (updateAppService != null) {
                    updateAppService.doInBackground(true);
                    updateAppService.setDownloadListener(null);
                    updateAppService = null;
                    serviceConnection = null;
                }
            }
        };
    }

    private void start() {
        String version = updateAppBean.getVersion().replaceAll("\\.", "_");
        if (!TextUtils.isEmpty(downloadDoneTag)) {
            File file = new File(downloadPath + File.separator + downloadDoneTag + version + ".apk");
            if (file.exists() && file.length() == updateAppBean.getApkSize()) {
                dialogUpdate.dismiss();
                InstallApk.install(context, file.getAbsolutePath());
                return;
            }
        }
        isDownload = true;
        progressButton.setStatus(ProgressButton.STATUS_DOWNLOADING);
        mTvUpdateAfter.setText(UpdateAppUtil.this.context.getString(R.string.background_download));
        if (updateAppService != null) {
            updateAppService.start(updateAppBean.getUrl(), version, updateAppBean.getApkSize());
            return;
        }
        if (downloadIntent == null) {
            downloadIntent = new Intent(context, UpdateAppService.class);
        }
        UpdateAppUtil.this.context.bindService(downloadIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 判断更新模式
     * @param context
     * @param minVersion 最低支持版本
     * @param newlyVersion 最新版本
     * @return -1 不更新，0：强制更新，1：选择更新
     * */
    public static int getUpdateMode(Context context, String minVersion, String newlyVersion) {
        try {
            String currentVersionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName
                    .replaceAll("\\.", "");
            int currentVersionCode = Integer.parseInt(currentVersionName);
            int minVersionCode = Integer.parseInt(minVersion.replaceAll("\\.", ""));
            int newVersionCode = Integer.parseInt(newlyVersion.replaceAll("\\.", ""));
            if (currentVersionCode >= minVersionCode) {
                if (newVersionCode > currentVersionCode) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return 0;
            }
        } catch (PackageManager.NameNotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getDownloadAppPath(Context context) {

        String path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            try {
                path = context.getExternalCacheDir().getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(path)) {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            }
        } else {
            path = context.getCacheDir().getAbsolutePath();
        }
        return path;
    }

    public static int getAppVersionCode(String versionName) {

        if (!TextUtils.isEmpty(versionName)) {

            versionName = versionName.replaceAll("\\.", "");
            try {

                return Integer.parseInt(versionName);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public static String getAppVersionName(Context context) {

        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatSize(Long size) {
        double sizeByte = size.doubleValue();
        DecimalFormat format = new DecimalFormat("0.##");
        format.setRoundingMode(RoundingMode.FLOOR);
        if (sizeByte < 1024.0) { // 不足一K
            return format.format(sizeByte) + "B";
        } else if (sizeByte < (1024.0 * 1024.0)) { // 不足一M
            return format.format(sizeByte / 1024.0) + "KB";
        } else if (sizeByte < (1024 * 1024.0 * 1024.0)) { // 不足一G
            return format.format(sizeByte / (1024 * 1024.0)) + "MB";
        } else if (sizeByte < (1024 * 1024 * 1024.0 * 1024.0)) {
            return format.format(sizeByte / (1024 * 1024 * 1024.0)) + "GB";
        } else {
            return format.format(sizeByte / (1024 * 1024 * 1024.0 * 1024)) + "TB";
        }
    }
}
