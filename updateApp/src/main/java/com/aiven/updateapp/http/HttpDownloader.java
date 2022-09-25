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
     * http下载文件，本方法不检测是否具有读写文件权限，所以请确保调用本方法前取得读写文件的权限。
     * @param url 下载链接
     * @param savePath 保存文件的路径，不含文件名，不要在路径的最后加上“/”，例如保存在“D:/download”目录下，
     *                 传递：“D:/download”即可，不要传递：“D:/download/”。也不要用windows的写法：“D:\\download”
     * @param filename 下载后保存的文件名
     * @param fileSize 文件大小
     * @param md5 文件md5
     * @param downloadTempTag 下载时的标识文件
     * @param downloadDoneTag 下载完成后的标识文件
     * @param mode 下载模式，当文件已经存在时的处理方式
     * @param downloadListener 下载监听回调，该回调为空则不执行下载。
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
                         * 下载完成后的文件
                         * */
                        Log.d(TAG, "目标文件名：" + targetFilePath);
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
                         * 下载过程中的临时文件
                         * */
                        Log.d(TAG, "临时文件名：" + tempFilePath);
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
                        Log.d(TAG, "下载地址：" + url);
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
                            Log.d(UpdateAppService.class.getSimpleName(), "比较Md5：" + md5 + "," + fileMd5);
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

                        Log.d(TAG, "抛异常了：" + e.toString());
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
                            Log.d(TAG, "下载错误：" + e.toString());
                            String error;
                            String ee = e.toString();
                            if (ee.contains("java.net.UnknownHostException")) {
                                error = "网络错误，点击重试";
                            } else if (ee.contains("java.io.IOException")) {
                                error = "数据错误，请重启应用更新";
                            } else if (ee.contains("java.net.SocketTimeoutException")) {
                                error = "连接超时，请检查网络";
                            } else if (ee.contains(DOWNLOAD_PAUSE_TAG)) {
                                error = "已暂停";
                            } else if (ee.contains("The file has been tampered with, please download again")) {
                                error = "文件被篡改，请重新下载";
                            } else if (ee.contains(url)) {
                                error = "无法连接服务器";
                            } else if (ee.contains("FileNotFoundException")) {
                                error = "其他程序正在打开该文件";
                            } else if (ee.contains("Software caused connection abort")) {
                                error = "连接已断开，点击重试";
                            } else if (ee.contains("java.net.SocketException:")) {
                                error = "网络被重置，点击重试";
                            }

                            else {
                                error = "下载失败，点击重试";
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
     * 下载回调接口
     * */
    public interface DownloadListener {

        /**
         * 下载进度
         * @param ratio 百分比
         * */
        void downloadProgress(float ratio);

        /**
         * 下载成功
         * @param filepath 文件路径，即savePath + filename
         * */
        void downloadSuccess(String filepath);

        /**
         * 下载失败
         * @param error 失败原因
         * */
        void downloadFailure(String error);

        /**
         * 下载Apk成功后，调用次方法获取Activity，调起安装界面
         * */
        default Context getContext() {return null;}
    }

    /**
     * 下载模式
     * */
    public enum Mode {

        // 覆盖文件
        OVERRIDE,
        // 续传
        APPEND,
        // 不执行下载
        RETURN
    }
}
