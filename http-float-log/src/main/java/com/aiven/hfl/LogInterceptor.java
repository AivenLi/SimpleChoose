package com.aiven.hfl;

import android.os.Handler;

import com.aiven.hfl.bean.HttpLogBean;
import com.aiven.hfl.util.FloatManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * @author : AivenLi
 * @date : 2022/10/25 23:00
 * @desc : 请求日志拦截器
 */
public class LogInterceptor implements Interceptor {

    private static final String TAG = "HttpRequest";
    private Handler handler;

    public LogInterceptor(Handler handler) {
        this.handler = handler;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String body = null;
        HttpLogBean httpLogBean = new HttpLogBean();
        long t1 = System.currentTimeMillis();
        if (request.body() != null) {
            RequestBody requestBody = request.body();
            if (requestBody.contentType() != null) {
                Charset charset = requestBody.contentType().charset(StandardCharsets.UTF_8);
                if (charset != null) {
                    Buffer buffer = new Buffer();
                    request.body().writeTo(buffer);
                    body = buffer.readString(charset);
                }
            }
        }
        httpLogBean.setUrl(request.url().toString());
        httpLogBean.setMethod(request.method());
        httpLogBean.setHeaders(request.headers().toString());
        httpLogBean.setBody(body);
        Response response = chain.proceed(request);
        long t2 = System.currentTimeMillis();
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        httpLogBean.setCode(response.code());
        httpLogBean.setMilliseconds(t2 - t1);
        httpLogBean.setData(responseBody.string());
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(FloatManager.HTTP_LOG_WHAT, httpLogBean));
        }
        return response;
    }
}
