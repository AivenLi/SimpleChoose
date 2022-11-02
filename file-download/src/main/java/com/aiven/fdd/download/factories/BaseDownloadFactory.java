package com.aiven.fdd.download.factories;

import com.aiven.fdd.listener.DownloadListener;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.net.Proxy;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseDownloadFactory implements DownloadFactory {

    protected String url;
    protected String path;
    protected String filename;
    protected int threadCount;
    protected int retryTimes;
    protected long timeout;
    protected Proxy proxy;
    protected DownloadListener downloadListener;
    protected Map<String, Object> requestHeaderMap = new HashMap<>();
    protected BlockingQueue<byte[]> blockingQueue = new LinkedBlockingQueue<>();
    protected ExecutorService executorService;
    public static final float BLOCK_FACTOR = 1.15f;
    public static final int BYTE_COUNT = 4096;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public BaseDownloadFactory() {
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
    }

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

    @Override
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
