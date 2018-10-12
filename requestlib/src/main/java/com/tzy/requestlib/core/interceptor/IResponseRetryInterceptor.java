package com.tzy.requestlib.core.interceptor;

import com.tzy.requestlib.core.xrequest.XRequest;

/**
 * Created by tangzhiyuanon 2018/10/11.
 */

public interface IResponseRetryInterceptor {
    boolean onResponseRetryIntercept(XRequest xRequest, Integer retryCount, Throwable throwable) throws Exception;
}
