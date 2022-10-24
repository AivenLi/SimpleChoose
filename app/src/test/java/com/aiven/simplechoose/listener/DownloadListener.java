package com.aiven.simplechoose.listener;

public interface DownloadListener {

    /**
     * 开始下载
     * */
    void startDownload();

    /**
     * 下载进度回调
     * @param percent   下载进度
     * @param netSpeed   网速
     * */
    void onProgress(float percent, float netSpeed);

    /**
     * 下载错误，一旦回调该方法，说明下载任务执行失败，同时终止
     * 所有下载线程。
     * @param error 错误原因
     * */
    void onError(String error);

    /**
     * 下载完成
     * @param path 文件路径
     * */
    void onFinish(String path);
}
