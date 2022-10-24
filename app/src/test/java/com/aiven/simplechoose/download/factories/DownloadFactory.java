package com.aiven.simplechoose.download.factories;

import com.aiven.simplechoose.listener.DownloadListener;

public interface DownloadFactory {

    /**
     * 设置下载链接，下载任务开始后调用无效
     * @param url 下载链接
     * */
    void setUrl(String url);

    /**
     * 设置保存路径，下载任务开始后调用无效
     * @param path 路径
     * */
    void setPath(String path);

    /**
     * 设置文件名，下载任务开始后调用无效
     * @param filename 文件名
     * */
    void setFilename(String filename);

    /**
     * 设置下载重试次数，下载失败超过该次数，则终止下载任务。
     * 下载任务开始后调用无效
     * */
    void setRetryTimes(int times);

    /**
     * 设置核心线程数量，下载任务开始后调用无效
     * @param count 核心线程数量
     * */
    void setThreadCount(int count);

    /**
     * 设置下载监听回调
     * @param downloadListener 下载监听回调
     * */
    void setDownloadListener(DownloadListener downloadListener);

    /**
     * 开始下载，下载任务开始后调用无效
     * */
    void startDownload();

    /**
     * 终止下载，如果下载正在进行并且调用该方法终止下载，则需要给该
     * 方法传递一个参数，用以判断终止下载任务的同时是否需要删除已下载
     * 的部分文件。
     * 如果下载已经完成，调用该方法则什么都不做。
     * @param clearFiles 是否清除下载产生的文件
     * */
    void stopDownload(boolean clearFiles);

    /**
     * 是否正在下载
     * @return 正在下载返回true，否则返回false。
     * */
    boolean isDownload();
}
