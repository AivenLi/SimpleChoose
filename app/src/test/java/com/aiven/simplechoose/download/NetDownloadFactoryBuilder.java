package com.aiven.simplechoose.download;

import com.aiven.simplechoose.download.factories.DownloadFactory;
import com.aiven.simplechoose.download.factories.M3u8DownloadFactory;
import com.aiven.simplechoose.download.factories.OtherDownloadFactory;
import com.aiven.simplechoose.listener.DownloadListener;

public class NetDownloadFactoryBuilder {

    private String url;
    private String path;
    private String filename;
    private int threadCount;
    private int retryTimes;
    private DownloadListener downloadListener;

    NetDownloadFactoryBuilder() {
        
    }

    public NetDownloadFactoryBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public NetDownloadFactoryBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public NetDownloadFactoryBuilder setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public NetDownloadFactoryBuilder setThreadCount(int count) {
        this.threadCount = count;
        return this;
    }

    public NetDownloadFactoryBuilder setRetryTimes(int times) {
        this.retryTimes = times;
        return this;
    }

    public NetDownloadFactoryBuilder setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    public DownloadFactory builder() {
        checkParams();
        return setDownloadFactoryParam(createDownloadFactory());
    }

    private boolean strIsEmpty(String str) {
        return str == null || "".equals(str);
    }

    private String getFilenameFromUrl(String url) {
        int index = url.lastIndexOf("/");
        if (index == -1 || index + 1 == url.length()) {
            return null;
        }
        return url.substring(index + 1);
    }

    private void checkParams() {
        if (strIsEmpty(url)) {
            throw new IllegalArgumentException("Url is empty!!!");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("Url must startWith \"http://\" or \"https://\"");
        }
        if (strIsEmpty(path)) {
            throw new IllegalArgumentException("Path is empty!!!");
        }
        if (strIsEmpty(filename)) {
            filename = getFilenameFromUrl(url);
            if (strIsEmpty(filename)) {
                filename = System.currentTimeMillis() + "";
            }
        }
        if (threadCount <= 0) {
            threadCount = 10;
        }
        if (retryTimes <= 0) {
            retryTimes = 10;
        }
    }

    private DownloadFactory createDownloadFactory() {
        if (url.endsWith(".m3u8")) {
            return new M3u8DownloadFactory();
        } else {
            return new OtherDownloadFactory();
        }
    }

    private DownloadFactory setDownloadFactoryParam(DownloadFactory downloadFactory) {
        downloadFactory.setUrl(url);
        downloadFactory.setPath(path);
        downloadFactory.setFilename(filename);
        downloadFactory.setThreadCount(threadCount);
        downloadFactory.setRetryTimes(retryTimes);
        downloadFactory.setDownloadListener(downloadListener);
        return downloadFactory;
    }
}
