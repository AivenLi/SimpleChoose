package com.aiven.updateapp.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.aiven.updateapp.service.UpdateAppService;
import com.aiven.updateapp.util.Md5Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HttpDownloader {

    private boolean pause = false;
    private static final String DOWNLOAD_PAUSE_TAG = "DownloadPause";
    private static final String TAG = UpdateAppService.class.getSimpleName();

    public static class Builder {

        private String url;
        private String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        private String downloadTempTag = ".temp";
        private String downloadDoneTag = "";
        private String filename;
        private long   fileSize;
        private String md5;
        private Mode mode = Mode.OVERRIDE;
        private DownloadListener downloadListener = null;

        public Builder() {
            this(null);
        }

        public Builder(String url) {
            this(url, null);
        }

        public Builder(String url, String filename) {
            this(url, filename, null);
        }

        public Builder(String url, String filename, DownloadListener downloadListener) {
            this.url = url;
            this.filename = filename;
            this.downloadListener = downloadListener;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setSavePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public Builder setDownloadTempTag(String tag) {
            this.downloadTempTag = tag;
            return this;
        }

        public Builder setDownloadDoneTag(String tag) {
            this.downloadDoneTag = tag;
            return this;
        }

        public Builder setFilename(String filename) {
            this.filename = filename;
            if (!this.filename.endsWith(".apk")) {
                this.filename = this.filename + ".apk";
            }
            return this;
        }

        public Builder setFileSize(long size) {
            fileSize = size;
            return this;
        }

        public Builder setMd5(String md5) {
            this.md5 = md5;
            return this;
        }

        public Builder setMode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder setDownloadListener(DownloadListener downloadListener) {
            this.downloadListener = downloadListener;
            return this;
        }

        public HttpDownloader builder() {
            return new HttpDownloader().download(
                    url,
                    savePath,
                    filename,
                    fileSize,
                    md5,
                    downloadTempTag,
                    downloadDoneTag,
                    mode,
                    downloadListener
            );
        }
    }

    public void setPause(boolean pause) {
        synchronized (HttpDownloader.class) {
            this.pause = pause;
        }
    }

    /**
     * http?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param url ????????????
     * @param savePath ???????????????????????????????????????????????????????????????????????????/????????????????????????D:/download???????????????
     *                 ????????????D:/download??????????????????????????????D:/download/??????????????????windows???????????????D:\\download???
     * @param filename ???????????????????????????
     * @param fileSize ????????????
     * @param md5 ??????md5
     * @param downloadTempTag ????????????????????????
     * @param downloadDoneTag ??????????????????????????????
     * @param mode ??????????????????????????????????????????????????????
     * @param downloadListener ?????????????????????????????????????????????????????????
     * */
    @SuppressLint("CheckResult")
    private HttpDownloader download(
            final String url,
            final String savePath,
            final String filename,
            final long   fileSize,
            final String md5,
            final String downloadTempTag,
            final String downloadDoneTag,
            final Mode mode,
            final DownloadListener downloadListener
    ) {
        if (downloadListener == null) {
            return null;
        }
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(savePath) || TextUtils.isEmpty(filename)) {
            downloadListener.downloadFailure("Url, savePath and filename cannot be null!");
            return null;
        }
        final String targetFileName = downloadDoneTag + filename;
        final String tempFileName = filename.replace(".apk", "_apk") + downloadTempTag;
        final String targetFilePath = savePath + File.separator + targetFileName;
        final String tempFilePath = savePath + File.separator + tempFileName;
        Observable<Float> observable = Observable.create(
                (ObservableEmitter<Float> emitter) -> {
                    File file = null;
                    try {

                        File dir = new File(savePath);
                        if (!dir.exists()) {
                            if (!dir.mkdirs()) {
                                emitter.onError(newThrowable("Can't create directory: " + savePath));
                                return;
                            }
                        }
                        if (!dir.isDirectory()) {
                            emitter.onError(newThrowable("Can't download file from " + url + ", because " + savePath + " is not a directory!"));
                            return;
                        }
                        /**
                         * ????????????????????????
                         * */
                        Log.d(TAG, "??????????????????" + targetFilePath);
                        File targetFile = new File(targetFilePath);
                        if (targetFile.exists()) {
                            if (fileSize != 0 && targetFile.length() == fileSize) {
                                emitter.onNext(100.0f);
                                emitter.onNext(-1.0f);
                                return;
                            }
                            targetFile.delete();
                        }
                        /**
                         * ??????????????????????????????
                         * */
                        Log.d(TAG, "??????????????????" + tempFilePath);
                        file = new File(tempFilePath);
                        long downloadedSize = 0;
                        if (file.exists()) {
                            if (file.isDirectory()) {
                                emitter.onError(newThrowable("Can't download file from " + url + ", bacause " + filename + " has exists and it's a directory!"));
                                return;
                            }
                            if (mode == Mode.APPEND) {
                                downloadedSize = file.length();
                            } else if (mode == Mode.RETURN) {
                                emitter.onNext(-1.0f);
                                return;
                            }
                        } else if (!file.createNewFile()) {
                            emitter.onError(newThrowable("Can't download file, create file " + filename + " failed!"));
                            return;
                        }
                        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
                        URL downloadUrl = new URL(url);
                        Log.d(TAG, "???????????????" + url);
                        HttpURLConnection httpURLConnection = (HttpURLConnection)downloadUrl.openConnection();
                        httpURLConnection.setConnectTimeout(10 * 1000);
                        httpURLConnection.setReadTimeout(10 * 1000);
                        if (downloadedSize > 0) {
                            httpURLConnection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");
                        }
                        int totalSize = httpURLConnection.getContentLength();
                        if (totalSize == 0) {
                            emitter.onNext(-1.0f);
                            httpURLConnection.disconnect();
                            return;
                        }
                        totalSize += downloadedSize;
                        randomAccessFile.seek(downloadedSize);
                        InputStream inputStream = httpURLConnection.getInputStream();

                        int currentSize = (int)downloadedSize;
                        int len;
                        byte[] buffer = new byte[4096];
                        while ((len = inputStream.read(buffer)) != -1) {
                            currentSize += len;
                            randomAccessFile.write(buffer, 0, len);
                            emitter.onNext(currentSize / (float)totalSize * 100.0f);
                            synchronized (HttpDownloader.class) {
                                if (pause) {
                                    break;
                                }
                            }
                        }
                        inputStream.close();
                        randomAccessFile.close();
                        httpURLConnection.disconnect();
                        if (pause) {
                            emitter.onError(newThrowable(DOWNLOAD_PAUSE_TAG));
                        } else if (!TextUtils.isEmpty(md5)) {
                            String fileMd5 = Md5Utils.encode(file);
                            Log.d(UpdateAppService.class.getSimpleName(), "??????Md5???" + md5 + "," + fileMd5);
                            if (md5.equals(fileMd5)) {
                                file.renameTo(targetFile);
                                file.delete();
                                emitter.onNext(-1.0f);
                                emitter.onComplete();
                            } else {
                                file.delete();
                                targetFile.delete();
                                emitter.onError(newThrowable("The file has been tampered with, please download again"));
                            }
                        } else {
                            file.renameTo(targetFile);
                            file.delete();
                            emitter.onNext(-1.0f);
                            emitter.onComplete();
                        }
                    } catch (Exception e) {

                        Log.d(TAG, "???????????????" + e.toString());
                        e.printStackTrace();
                        if (!pause) {
                            emitter.onError(newThrowable(e.toString()));
                        } else {
                            emitter.onError(newThrowable(DOWNLOAD_PAUSE_TAG));
                        }
                    }
                }
        );
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        (Float progress) -> {

                            if (progress == -1.0f) {
                                downloadListener.downloadSuccess(targetFilePath);
                            } else {
                                downloadListener.downloadProgress(progress);
                            }
                        }, (Throwable e) -> {
                            Log.d(TAG, "???????????????" + e.toString());
                            String error;
                            String ee = e.toString();
                            if (ee.contains("java.net.UnknownHostException")) {
                                error = "???????????????????????????";
                            } else if (ee.contains("java.io.IOException")) {
                                error = "????????????????????????????????????";
                            } else if (ee.contains("java.net.SocketTimeoutException")) {
                                error = "??????????????????????????????";
                            } else if (ee.contains(DOWNLOAD_PAUSE_TAG)) {
                                error = "?????????";
                            } else if (ee.contains("The file has been tampered with, please download again")) {
                                error = "?????????????????????????????????";
                            } else if (ee.contains(url)) {
                                error = "?????????????????????";
                            } else if (ee.contains("FileNotFoundException")) {
                                error = "?????????????????????????????????";
                            } else if (ee.contains("Software caused connection abort")) {
                                error = "??????????????????????????????";
                            } else if (ee.contains("java.net.SocketException:")) {
                                error = "??????????????????????????????";
                            }

                            else {
                                error = "???????????????????????????";
                            }
                            downloadListener.downloadFailure(error);
                        }
                );
        return this;
    }

    private static Throwable newThrowable(String msg) {
        return new Throwable(msg);
    }

    /**
     * ??????????????????
     * */
    public interface DownloadListener {

        /**
         * ????????????
         * @param ratio ?????????
         * */
        void downloadProgress(float ratio);

        /**
         * ????????????
         * @param filepath ??????????????????savePath + filename
         * */
        void downloadSuccess(String filepath);

        /**
         * ????????????
         * @param error ????????????
         * */
        void downloadFailure(String error);

        /**
         * ??????Apk?????????????????????????????????Activity?????????????????????
         * */
        default Context getContext() {return null;}
    }

    /**
     * ????????????
     * */
    public enum Mode {

        // ????????????
        OVERRIDE,
        // ??????
        APPEND,
        // ???????????????
        RETURN
    }
}
