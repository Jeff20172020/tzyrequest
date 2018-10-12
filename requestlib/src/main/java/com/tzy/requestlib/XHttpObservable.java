package com.tzy.requestlib;

import android.net.Uri;
import android.util.Log;

import com.tzy.requestlib.core.XHttpClient;
import com.tzy.requestlib.core.body.XMultiBody;
import com.tzy.requestlib.core.converter.ResponseConverter;
import com.tzy.requestlib.core.interceptor.IOriginResponseInterceptor;
import com.tzy.requestlib.core.interceptor.IRequestInterceptor;
import com.tzy.requestlib.core.interceptor.IResponseInterceptor;
import com.tzy.requestlib.core.interceptor.IResponseRetryInterceptor;
import com.tzy.requestlib.core.xrequest.XRequest;
import com.tzy.requestlib.core.xrequest.XRequestBuilder;
import com.tzy.requestlib.util.ExceptionUtil;
import com.tzy.requestlib.util.XIOUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Function;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.GzipSource;
import okio.Okio;

/**
 * Created by tangzhiyuanon 2018/10/11.
 */

public class XHttpObservable {
    private static final String TAG = XHttpObservable.class.getSimpleName();

    public <T> Observable<T> create(final XRequest xRequest, final Type responseType) {

        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                ResponseBody responseBody = null;
                try {
                    XRequestBuilder builder = XRequestBuilder.create();
                    builder.url(xRequest.getUrl());
                    List<IRequestInterceptor> requestInterceptors = XHttpManager.getInstance().getRequestInterceptors();
                    for (IRequestInterceptor requestInterceptor : requestInterceptors) { //请求拦截器
                        requestInterceptor.onRequestIntercept(xRequest);
                    }
                    TreeMap<String, String> headers = xRequest.getHeaders();
                    if (headers != null && !headers.isEmpty()) { //添加公共头信息
                        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                            builder.addHeader(headerEntry.getKey(), headerEntry.getValue());
                        }
                    }

                    HashMap<String, XMultiBody> fileParameters = xRequest.getFileParameters();
                    if (fileParameters != null && !fileParameters.isEmpty()) {
                        addParametersForFiles(builder, xRequest);
                    } else {
                        switch (xRequest.getMethod()) {
                            case XRequest.METHOD_GET:
                                addParametersForGet(builder, xRequest);
                                break;
                            case XRequest.METHOD_POST:
                                addParametersForPost(builder, xRequest);
                                break;
                        }
                    }
                    Response response = XHttpClient.get(xRequest).newCall(builder.build()).execute();
                    responseBody = response.body();
                    byte[] responseBytes;
                    if ("gzip".equalsIgnoreCase(response.header("Content-Encoding"))) {
                        responseBytes = Okio.buffer(new GzipSource(responseBody.source())).readByteArray();
                    } else {
                        responseBytes = responseBody.bytes();
                    }
                    IOriginResponseInterceptor originResponseInterceptor = XHttpManager.getInstance().getOriginResponseInterceptor();
                    if (originResponseInterceptor != null) {
                        originResponseInterceptor.onOriginResponseIntercept(xRequest, responseBytes);
                    }
                    ResponseConverter responseConverter = xRequest.getResponseConverter();
                    if (responseConverter == null) {
                        responseConverter = XHttpManager.getInstance().getResponseConverter();
                    }
                    if (responseConverter == null) {
                        throw new RuntimeException("No available ResponseConverter!");
                    }

                    T t;
                    t = responseConverter.convert(xRequest, responseBytes, responseType);

                    if (XHttpManager.getInstance().isDebug()) {
                        Log.d(TAG, "xRequest-url: " + xRequest.getUrl());
                    }
                    if (null != t && XHttpManager.getInstance().isDebug()) {
                        Log.d(TAG, "response: " + t.toString());
                    }
                    if (!e.isDisposed()) {
                        e.onNext(t);
                        e.onComplete();
                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    if (XHttpManager.getInstance().isDebug()) {
                        Log.e(TAG, "xRequest-url: " + xRequest.getUrl());
                    }
                    Log.e(TAG, "", throwable);
                    if (!e.isDisposed()) {
                        e.onError(throwable);
                    }
                } finally {
                    //关闭流
                    XIOUtil.closeIO(responseBody);
                }
            }
        }).retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer integer, Throwable throwable) throws Exception {
                boolean retryFlag = false;
                IResponseRetryInterceptor responseRetryInterceptor = XHttpManager.getInstance().getResponseRetryInterceptor();
                if (responseRetryInterceptor != null) {
                    return responseRetryInterceptor.onResponseRetryIntercept(xRequest, integer, throwable);
                }
                retryFlag = integer <= xRequest.getRetryCount() && !ExceptionUtil.isNetworkError(throwable);
                return retryFlag;
            }
        }).map(new Function<T, T>() {
            @Override
            public T apply(T t) throws Exception {
                List<IResponseInterceptor> responseInterceptors = XHttpManager.getInstance().getResponseInterceptors();
                if (responseInterceptors != null && !responseInterceptors.isEmpty()) {
                    try {
                        for (IResponseInterceptor responseInterceptor : responseInterceptors) {
                            responseInterceptor.onResponseIntercept(xRequest, t);
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        throw new RuntimeException(throwable);
                    }
                }
                return t;
            }
        });


    }

    private void addParametersForFiles(XRequestBuilder builder, XRequest xRequest) {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        HashMap<String, String> parameters = xRequest.getParameters();
        HashMap<String, XMultiBody> fileParameters = xRequest.getFileParameters();
        if (parameters != null && !parameters.isEmpty()) {
            XHttpManager.EncryptPolicy encryptPolicy = XHttpManager.getInstance().getEncryptPolicy();
            if (encryptPolicy != null) {
                parameters = encryptPolicy.encrypt(parameters);
            }
            for (Map.Entry<String, String> paramsEntry : parameters.entrySet()) {
                multipartBodyBuilder.addFormDataPart(paramsEntry.getKey(), paramsEntry.getValue());
            }
        }

        if (fileParameters != null && !fileParameters.isEmpty()) {
            for (Map.Entry<String, XMultiBody> multiBodyEntry : fileParameters.entrySet()) {
                multipartBodyBuilder.addFormDataPart(multiBodyEntry.getKey(), multiBodyEntry.getValue().getFileName(), multiBodyEntry.getValue().getRequestBody());
            }
        }
        builder.post(multipartBodyBuilder.build());

    }

    private void addParametersForGet(XRequestBuilder builder, XRequest xRequest) {
        HashMap<String, String> parameters = xRequest.getParameters();
        if (parameters == null || parameters.isEmpty()) {
            return;
        }
        XHttpManager.EncryptPolicy encryptPolicy = XHttpManager.getInstance().getEncryptPolicy();
        if (encryptPolicy != null) {
            parameters = encryptPolicy.encrypt(parameters);
        }
        Uri.Builder uriBuilder = Uri.parse(xRequest.getUrl()).buildUpon();
        for (Map.Entry<String, String> paramsEntry : parameters.entrySet()) {
            uriBuilder.appendQueryParameter(paramsEntry.getKey(), paramsEntry.getValue());
        }
        builder.url(uriBuilder.build().toString());

    }

    private void addParametersForPost(XRequestBuilder builder, XRequest xRequest) {
        HashMap<String, String> parameters = xRequest.getParameters();
        if (parameters == null || parameters.isEmpty()) {
            return;
        }
        XHttpManager.EncryptPolicy encryptPolicy = XHttpManager.getInstance().getEncryptPolicy();
        if (encryptPolicy != null) {
            parameters = encryptPolicy.encrypt(parameters);
        }
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> paramsEntry : parameters.entrySet()) {
            bodyBuilder.add(paramsEntry.getKey(), paramsEntry.getValue());
        }
        builder.post(bodyBuilder.build());
    }
}
