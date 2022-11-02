package com.aiven.fdd.download.factories;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.aiven.fdd.exceptions.M3u8Exception;
import com.aiven.fdd.utils.MediaFormat;
import com.aiven.fdd.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class M3u8DownloadFactory extends BaseDownloadFactory {

    private static final String TAG = M3u8DownloadFactory.class.getSimpleName();

    //已完成ts片段个数
    private int finishedCount = 0;

    private boolean shutDown = false;

    //解密算法名称
    private String method;

    //密钥
    private String key = "";

    //密钥字节
    private byte[] keyBytes = new byte[16];

    //key是否为字节
    private boolean isByte = false;

    //IV
    private String iv = "";

    //所有ts片段下载链接
    private Set<String> tsSet = new LinkedHashSet<>();

    //解密后的片段
    private Set<File> finishedFiles;
    private Set<String> failedUrls;

    //已经下载的文件大小
    private BigDecimal downloadBytes = new BigDecimal(0);

    public M3u8DownloadFactory() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            finishedFiles = new ConcurrentSkipListSet<>(Comparator.comparingInt(o -> Integer.parseInt(o.getName().replace(".xyz", ""))));
        }
        failedUrls = new ConcurrentSkipListSet<>();
    }

    @Override
    public void startDownload() {
        try {
            if ("m3u8".compareTo(MediaFormat.getMediaFormat(url)) != 0) {
                throw new M3u8Exception(url + "不是一个完整m3u8链接！");
            }
            finishedCount = 0;
            method = "";
            key = "";
            isByte = false;
            iv = "";
            tsSet.clear();
            finishedFiles.clear();
            downloadBytes = new BigDecimal(0);
            getTsUrl();
            executorService = doDownload();
        } catch (M3u8Exception e) {
            if (downloadListener != null) {
                downloadListener.onError(e.toString());
            }
        }
    }

    @Override
    public void stopDownload(boolean clearFiles) {
        Log.d(TAG, "停止下载，清除文件：" + clearFiles);
        if (executorService != null) {
            Log.d(TAG, "立即停止下载");
            executorService.shutdownNow();
        }
        shutDown = true;
        if (clearFiles) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File dir = new File(path);
                    if (dir.exists() && dir.isDirectory()) {
                        File[] files = dir.listFiles();
                        if (files != null && files.length > 0) {
                            for (File subFile: files) {
                                if (subFile.exists()) {
                                    subFile.delete();
                                }
                            }
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public boolean isDownload() {
        return executorService != null && executorService.isTerminated();
    }

    private String getTsUrl() throws M3u8Exception {
        StringBuilder content = getUrlContent(url, false);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        //判断是否是m3u8链接
        if (!content.toString().contains("#EXTM3U"))
            throw new M3u8Exception(url + "不是m3u8链接！");
        String[] split = content.toString().split("\\n");
        String keyUrl = "";
        boolean isKey = false;
        for (String s : split) {
            //如果含有此字段，则说明只有一层m3u8链接
            if (s.contains("#EXT-X-KEY") || s.contains("#EXTINF")) {
                isKey = true;
                keyUrl = url;
                break;
            }
            //如果含有此字段，则说明ts片段链接需要从第二个m3u8链接获取
            if (s.contains(".m3u8")) {
                if (StringUtils.isUrl(s))
                    return s;
                String relativeUrl = url.substring(0, url.lastIndexOf("/") + 1);
                if (s.startsWith("/"))
                    s = s.replaceFirst("/", "");
                keyUrl = mergeUrl(relativeUrl, s);
                break;
            }
        }
        if (StringUtils.isEmpty(keyUrl))
            throw new M3u8Exception("未发现key链接！");
        //获取密钥
        String key1 = isKey ? getKey(keyUrl, content) : getKey(keyUrl, null);
        if (StringUtils.isNotEmpty(key1))
            key = key1;
        else key = null;
        return key;
    }

    /**
     * 模拟http请求获取内容
     *
     * @param urls  http链接
     * @param isKey 这个url链接是否用于获取key
     * @return 内容
     */
    private StringBuilder getUrlContent(String urls, boolean isKey) throws M3u8Exception {
        int count = 1;
        HttpURLConnection httpURLConnection = null;
        StringBuilder content = new StringBuilder();
        while (count <= retryTimes) {
            try {
                URL url = new URL(urls);
                if (proxy == null) {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                }else {
                    httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
                }
                httpURLConnection.setConnectTimeout((int) timeout);
                httpURLConnection.setReadTimeout((int) timeout);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setDoInput(true);
                for (Map.Entry<String, Object> entry : requestHeaderMap.entrySet())
                    httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue().toString());
                String line;
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                if (isKey) {
                    byte[] bytes = new byte[128];
                    int len;
                    len = inputStream.read(bytes);
                    isByte = true;
                    if (len == 1 << 4) {
                        keyBytes = Arrays.copyOf(bytes, 16);
                        content.append("isByte");
                    } else
                        content.append(new String(Arrays.copyOf(bytes, len)));
                    return content;
                }
                while ((line = bufferedReader.readLine()) != null)
                    content.append(line).append("\n");
                bufferedReader.close();
                inputStream.close();
                Log.d(TAG, content.toString());
                break;
            } catch (Exception e) {
                Log.d(TAG, "第" + count + "获取链接重试！\t" + urls);
                count++;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }
        if (count > retryTimes) {
            throw new M3u8Exception("Connect time out");
        }
        return content;
    }

    private String mergeUrl(String start, String end) {
        if (end.startsWith("/"))
            end = end.replaceFirst("/", "");
        int position = 0;
        String subEnd, tempEnd = end;
        while ((position = end.indexOf("/", position)) != -1) {
            subEnd = end.substring(0, position + 1);
            if (start.endsWith(subEnd)) {
                tempEnd = end.replaceFirst(subEnd, "");
                break;
            }
            ++position;
        }
        return start + tempEnd;
    }

    /**
     * 获取ts解密的密钥，并把ts片段加入set集合
     *
     * @param url     密钥链接，如果无密钥的m3u8，则此字段可为空
     * @param content 内容，如果有密钥，则此字段可以为空
     * @return ts是否需要解密，null为不解密
     */
    private String getKey(String url, StringBuilder content) throws M3u8Exception {
        StringBuilder urlContent;
        if (content == null || StringUtils.isEmpty(content.toString())) {
            urlContent = getUrlContent(url, false);
        } else {
            urlContent = content;
        }
        if (!urlContent.toString().contains("#EXTM3U")) {
            throw new M3u8Exception(url + "不是m3u8链接！");
        }
        String[] split = urlContent.toString().split("\\n");
        for (String s : split) {
            //如果含有此字段，则获取加密算法以及获取密钥的链接
            if (s.contains("EXT-X-KEY")) {
                String[] split1 = s.split(",");
                for (String s1 : split1) {
                    if (s1.contains("METHOD")) {
                        method = s1.split("=", 2)[1];
                        continue;
                    }
                    if (s1.contains("URI")) {
                        key = s1.split("=", 2)[1];
                        continue;
                    }
                    if (s1.contains("IV")) {
                        iv = s1.split("=", 2)[1];
                    }
                }
            }
        }
        String relativeUrl = url.substring(0, url.lastIndexOf("/") + 1);
        //将ts片段链接加入set集合
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (s.contains("#EXTINF")) {
                String s1 = split[++i];
                tsSet.add(StringUtils.isUrl(s1) ? s1 : mergeUrl(relativeUrl, s1));
            }
        }
        if (!StringUtils.isEmpty(key)) {
            key = key.replace("\"", "");
            return getUrlContent(StringUtils.isUrl(key) ? key : mergeUrl(relativeUrl, key), true).toString().replaceAll("\\s+", "");
        }
        return null;
    }

    private ExecutorService doDownload() {
        //线程池
        final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadCount);
        int i = 0;
        //如果生成目录不存在，则创建
        File file1 = new File(path);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        //执行多线程下载
        for (String s : tsSet) {
            i++;
            fixedThreadPool.execute(getThread(s, i));
        }
        fixedThreadPool.shutdown();
        startListener(fixedThreadPool);
        return fixedThreadPool;
    }

    private void startListener(ExecutorService fixedThreadPool) {
        new Thread(() -> {
            if (downloadListener != null) {
                downloadListener.startDownload();
            }
            //轮询是否下载成功
            while (!fixedThreadPool.isTerminated() && !shutDown) {
                try {
                    BigDecimal bigDecimal = new BigDecimal(downloadBytes.toString());
                    Thread.sleep(1000L);
                    String speedStr = StringUtils.convertToDownloadSpeed(
                            new BigDecimal(downloadBytes.toString())
                                    .subtract(bigDecimal),
                            3
                    );
                    if (downloadListener != null) {
                        downloadListener.onProgress(
                                new BigDecimal(finishedCount)
                                        .divide(
                                                new BigDecimal(
                                                        tsSet.size()
                                                ),
                                                4,
                                                BigDecimal.ROUND_HALF_UP
                                        )
                                        .multiply(new BigDecimal(100))
                                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                                        .floatValue(),
                                speedStr
                        );
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!shutDown) {
                mergeTs();
                //删除多余的ts片段
                deleteFiles();
                if (downloadListener != null) {
                    downloadListener.onFinish(path + File.separator + filename + ".mp4");
                }
            } else {
                if (downloadListener != null) {
                    downloadListener.onError("Download pause");
                }
            }
        }).start();
    }

    /**
     * 开启下载线程
     *
     * @param urls ts片段链接
     * @param i    ts片段序号
     * @return 线程
     */
    private Thread getThread(String urls, int i) {
        return new Thread(() -> {
            int count = 1;
            HttpURLConnection httpURLConnection = null;
            // xyz为已解密的ts片段，如果存在说明该片段已下载，就不执行下载了。
            File file = new File(path + File.separator + i + ".xyz");
            if (file.exists() && file.length() > 0) {
                finishedFiles.add(file);
                finishedCount++;
                return;
            }
            //xy为未解密的ts片段，如果存在，则删除
            File file2 = new File(path + File.separator + i + ".xy");
            if (file2.exists()) {
                file2.delete();
            }
            OutputStream outputStream = null;
            InputStream inputStream1 = null;
            FileOutputStream outputStream1 = null;
            byte[] bytes;
            try {
                bytes = blockingQueue.take();
            } catch (InterruptedException e) {
                bytes = new byte[BYTE_COUNT];
            }
            //重试次数判断
            while (count <= retryTimes && !shutDown) {
                try {
                    //模拟http请求获取ts片段文件
                    URL url = new URL(urls);
                    if (proxy ==null) {
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                    }else {
                        httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
                    }
                    httpURLConnection.setConnectTimeout((int) timeout);
                    for (Map.Entry<String, Object> entry : requestHeaderMap.entrySet()) {
                        httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue().toString());
                    }
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setReadTimeout((int) timeout);
                    httpURLConnection.setDoInput(true);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    try {
                        outputStream = new FileOutputStream(file2);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        continue;
                    }
                    int len;
                    //将未解密的ts片段写入文件
                    while ((len = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, len);
                        synchronized (this) {
                            downloadBytes = downloadBytes.add(new BigDecimal(len));
                        }
                    }
                    outputStream.flush();
                    inputStream.close();
                    inputStream1 = new FileInputStream(file2);
                    int available = inputStream1.available();
                    if (bytes.length < available) {
                        bytes = new byte[available];
                    }
                    inputStream1.read(bytes);
                    outputStream1 = new FileOutputStream(file);
                    //开始解密ts片段，这里我们把ts后缀改为了xyz，改不改都一样
                    byte[] decrypt = decrypt(bytes, available, key, iv, method);
                    if (decrypt == null) {
                        outputStream1.write(bytes, 0, available);
                    } else {
                        outputStream1.write(decrypt);
                    }
                    finishedFiles.add(file);
                    break;
                } catch (Exception e) {
                    if (e instanceof InvalidKeyException || e instanceof InvalidAlgorithmParameterException) {
                        Log.e(TAG,"解密失败！");
                        break;
                    }
                    Log.d(TAG, "第" + count + "获取链接重试！\t" + urls);
                    count++;
                } finally {
                    try {
                        if (inputStream1 != null) {
                            inputStream1.close();
                        }
                        if (outputStream1 != null) {
                            outputStream1.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        blockingQueue.put(bytes);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
            if (count > retryTimes) {
                Log.d(TAG, "连接超时：" + urls);
                failedUrls.add(urls);
            }
            finishedCount++;
        });
    }

    /**
     * 解密ts
     *
     * @param sSrc   ts文件字节数组
     * @param length
     * @param sKey   密钥
     * @return 解密后的字节数组
     */
    private byte[] decrypt(byte[] sSrc, int length, String sKey, String iv, String method) throws Exception {
        if (StringUtils.isNotEmpty(method) && !method.contains("AES")) {
            throw new M3u8Exception("未知的算法！");
        }
        // 判断Key是否正确
        if (StringUtils.isEmpty(sKey)) {
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16 && !isByte) {
            throw new M3u8Exception("Key长度不是16位！");
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec keySpec = new SecretKeySpec(isByte ? keyBytes : sKey.getBytes(StandardCharsets.UTF_8), "AES");
        byte[] ivByte;
        if (iv.startsWith("0x")) {
            ivByte = StringUtils.hexStringToByteArray(iv.substring(2));
        } else {
            ivByte = iv.getBytes();
        }
        if (ivByte.length != 16) {
            ivByte = new byte[16];
        }
        //如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
        return cipher.doFinal(sSrc, 0, length);
    }

    /**
     * 合并下载好的ts片段
     */
    private void mergeTs() {
        try {
            File file = new File(path + File.separator + filename + ".mp4");
            System.gc();
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] b = new byte[4096];
            for (File f : finishedFiles) {
                FileInputStream fileInputStream = new FileInputStream(f);
                int len;
                while ((len = fileInputStream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, len);
                }
                fileInputStream.close();
                fileOutputStream.flush();
            }
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除下载好的片段
     */
    private void deleteFiles() {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File f : files) {
                if (f.getName().endsWith(".xy") || f.getName().endsWith(".xyz")) {
                    f.delete();
                }
            }
        }
    }
}