package com.aiven.simplechoose.download.factories;

import com.aiven.simplechoose.listener.DownloadListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseDownloadFactory implements DownloadFactory {

    protected String url;
    protected String path;
    protected String filename;
    protected int threadCount;
    protected int retryTimes;
    protected DownloadListener downloadListener;
    protected BlockingQueue<byte[]> blockingQueue = new LinkedBlockingQueue<>();
    public static final float BLOCK_FACTOR = 1.15f;
    public static final int BYTE_COUNT = 4096;

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public void setRetryTimes(int times) {
        this.retryTimes = times;
    }

    @Override
    public void setThreadCount(int count) {
        this.threadCount = count;
        if (blockingQueue.size() < count) {
            for (int i = 0; i < count * BLOCK_FACTOR; ++i) {
                try {
                    blockingQueue.put(new byte[BYTE_COUNT]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }
}
