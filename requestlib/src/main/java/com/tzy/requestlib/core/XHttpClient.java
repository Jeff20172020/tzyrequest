package com.tzy.requestlib.core;

import com.tzy.requestlib.core.xrequest.XRequest;
import com.tzy.requestlib.ssl.XHttpSSLBuilder;
import com.tzy.requestlib.ssl.XUnsafeHttpSSLBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * Created by tangzhiyuanon 2018/10/11.
 */

public class XHttpClient {
    private static XHttpSSLBuilder xHttpSSLBuilder = new XUnsafeHttpSSLBuilder();
    private static final OkHttpClient okhttpClient = getOkHttpClient();

    private XHttpClient() {

    }

    public static OkHttpClient get(@Nullable XRequest xRequest) {
        if (xRequest != null && xRequest.getTimeoutSeconds() != XRequest.TIME_OUT_SECONDS) {
            return createOkHttpClient(xRequest.getTimeoutSeconds());
        }
        return okhttpClient;
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.connectTimeout(XRequest.TIME_OUT_SECONDS, TimeUnit.SECONDS);
            builder.readTimeout(XRequest.TIME_OUT_SECONDS, TimeUnit.SECONDS);
            builder.writeTimeout(XRequest.TIME_OUT_SECONDS, TimeUnit.SECONDS);
            builder.protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2));
            builder.sslSocketFactory(xHttpSSLBuilder.getSslSocketFactory());
            builder.hostnameVerifier(xHttpSSLBuilder.getHostnameVerifier());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    private static OkHttpClient createOkHttpClient(int timeOutSeconds) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.connectTimeout(timeOutSeconds, TimeUnit.SECONDS);
            builder.readTimeout(timeOutSeconds, TimeUnit.SECONDS);
            builder.writeTimeout(timeOutSeconds, TimeUnit.SECONDS);
            builder.protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2));
            builder.sslSocketFactory(xHttpSSLBuilder.getSslSocketFactory());
            builder.hostnameVerifier(xHttpSSLBuilder.getHostnameVerifier());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }


}
