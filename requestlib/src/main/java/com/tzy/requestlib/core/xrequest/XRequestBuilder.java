package com.tzy.requestlib.core.xrequest;

import okhttp3.Request;

/**
 * Created by tangzhiyuanon 2018/10/11.
 */

public class XRequestBuilder extends Request.Builder {

    public static XRequestBuilder create() {
        return new XRequestBuilder();
    }

}
