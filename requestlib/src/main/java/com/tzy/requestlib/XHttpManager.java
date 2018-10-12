package com.tzy.requestlib;

import com.tzy.requestlib.core.converter.ResponseConverter;
import com.tzy.requestlib.core.interceptor.IOriginResponseInterceptor;
import com.tzy.requestlib.core.interceptor.IRequestInterceptor;
import com.tzy.requestlib.core.interceptor.IResponseInterceptor;
import com.tzy.requestlib.core.interceptor.IResponseRetryInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tangzhiyuanon 2018/10/11.
 */

public class XHttpManager {
    private boolean isDebug = false;
    private List<IRequestInterceptor> iRequestInterceptors = new ArrayList<>();
    private List<IResponseInterceptor> iResponseInterceptors = new ArrayList<>();
    private IResponseRetryInterceptor iResponseRetryInterceptor;
    private IOriginResponseInterceptor iOriginResponseInterceptor;
    private ResponseConverter responseConverter;
    private EncryptPolicy encryptPolicy;

    public static XHttpManager getInstance() {
        return Holder.instance;
    }

    public ResponseConverter getResponseConverter() {
        return responseConverter;
    }

    public XHttpManager setResponseConverter(ResponseConverter responseConverter) {
        this.responseConverter = responseConverter;
        return this;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public XHttpManager setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    public XHttpManager addRequestInterceptor(IRequestInterceptor requestInterceptor) {
        if (!iRequestInterceptors.contains(requestInterceptor)) {
            iRequestInterceptors.add(requestInterceptor);
        }
        return this;
    }

    public XHttpManager addResponseInterceptor(IResponseInterceptor responseInterceptor) {
        if (!iResponseInterceptors.contains(responseInterceptor)) {
            iResponseInterceptors.add(responseInterceptor);
        }
        return this;
    }

    public List<IRequestInterceptor> getRequestInterceptors() {
        return iRequestInterceptors;
    }

    public List<IResponseInterceptor> getResponseInterceptors() {
        return iResponseInterceptors;
    }

    public IResponseRetryInterceptor getResponseRetryInterceptor() {
        return this.iResponseRetryInterceptor;
    }

    public XHttpManager setResponseRetryInterceptor(IResponseRetryInterceptor iResponseRetryInterceptor) {
        this.iResponseRetryInterceptor = iResponseRetryInterceptor;
        return this;
    }

    public IOriginResponseInterceptor getOriginResponseInterceptor() {
        return iOriginResponseInterceptor;
    }

    public XHttpManager setOriginResponseInterceptor(IOriginResponseInterceptor iOriginResponseInterceptor) {
        this.iOriginResponseInterceptor = iOriginResponseInterceptor;
        return this;
    }

    public EncryptPolicy getEncryptPolicy() {
        return encryptPolicy;
    }

    public XHttpManager setEncryptPolicy(EncryptPolicy encryptPolicy) {
        this.encryptPolicy = encryptPolicy;
        return this;
    }

    /**
     * 加密策略
     */
    public interface EncryptPolicy {
        HashMap<String, String> encrypt(HashMap<String, String> parameters);
    }

    public static class Holder {
        private static XHttpManager instance = new XHttpManager();
    }
}
