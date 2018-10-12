package com.tzy.requestlib.core.xrequest;


import com.tzy.requestlib.XHttpObservable;
import com.tzy.requestlib.core.body.XMultiBody;
import com.tzy.requestlib.core.converter.ResponseConverter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.annotations.Nullable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by tangzhiyuanon 2018/10/11.
 */

public class XRequest {
    public static final String METHOD_POST = "Post";
    public static final String METHOD_GET = "Get";

    public static final int RETRY_MAX_COUNT = 3;
    public static final int TIME_OUT_SECONDS = 30;

    private static final String TAG = "XRequest";

    private String url;
    private String method = METHOD_GET;
    private int retryCount = RETRY_MAX_COUNT;
    private int timeoutSeconds = TIME_OUT_SECONDS;
    private TreeMap<String, String> headers;
    private HashMap<String, String> parameters;
    private HashMap<String, XMultiBody> fileParameters;
    private HashMap<String, String> submitParameters;
    private HashMap<String, Object> requestConfigurations;

    private ResponseConverter responseConverter;


    private XRequest() {

    }

    private XRequest(String url) {
        this.url = url;
    }

    public static XRequest create(String url) {
        return new XRequest(url);
    }

    public String getUrl() {
        return url;
    }

    public XRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public XRequest setMethod(String method) {
        this.method = method;
        return this;
    }

    public XRequest get() {
        this.method = METHOD_GET;
        return this;
    }

    public XRequest post() {
        this.method = METHOD_POST;
        return this;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public XRequest setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public XRequest setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        return this;
    }

    public ResponseConverter getResponseConverter() {
        return responseConverter;
    }

    public XRequest setResponseConverter(ResponseConverter responseConverter) {
        this.responseConverter = responseConverter;
        return this;
    }

    public TreeMap<String, String> getHeaders() {
        return headers;
    }

    public XRequest addHeader(String key, Object value) {
        if (headers == null) {
            headers = new TreeMap<>();
        }
        headers.put(key, String.valueOf(value));
        return this;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public XRequest addParameter(String key, @Nullable Object value) {
        if (value != null) {
            if (parameters == null) {
                parameters = new HashMap<>();
            }
            parameters.put(key, String.valueOf(value));
        }

        return this;
    }

    public HashMap<String, XMultiBody> getFileParameters() {
        return fileParameters;
    }

    public XRequest addFileParameter(String key, String fileName, String mediaType, File file) {
        if (fileParameters == null) {
            fileParameters = new HashMap<>();
        }
        fileParameters.put(key, new XMultiBody(fileName, RequestBody.create(MediaType.parse(mediaType), file)));
        return this;
    }

    public XRequest addFileParameter(String key, String fileName, String mediaType, byte[] fileBytes) {
        if (fileParameters == null) {
            fileParameters = new HashMap<>();
        }
        fileParameters.put(key, new XMultiBody(fileName, RequestBody.create(MediaType.parse(mediaType), fileBytes)));
        return this;
    }

    public XRequest addConfiguration(String key, Object configuration) {
        if (requestConfigurations == null) {
            requestConfigurations = new HashMap<>();
        }
        requestConfigurations.put(key, configuration);
        return this;
    }

    public <T> T getConfiguration(String key, Class<T> tClass) {
        if (requestConfigurations == null) {
            return null;
        }
        Object value = requestConfigurations.get(key);
        if (value == null) {
            return null;
        }
        Class aClass = value.getClass();
        if (aClass != tClass) {
            return null;
        }

        return tClass.cast(value);
    }


    public HashMap<String, String> getSubmitParameters() {
        return submitParameters;
    }

    public void setSubmitParameters(HashMap<String, String> submitParameters) {
        this.submitParameters = submitParameters;
    }


    public boolean hasHeader(String key) {
        return headers == null ? false : headers.containsKey(key);
    }

    public boolean hasParam(String key) {
        return parameters == null ? false : parameters.containsKey(key);
    }


    public <T> io.reactivex.Observable<T> observable(Type responseType) {
        return new XHttpObservable().create(this, responseType);
    }

    public <T> Observable<T> observable(Class<T> tClass) {
        return new XHttpObservable().create(this, tClass);
    }


}