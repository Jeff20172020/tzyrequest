package com.tzy.requestlib.core.body;

import okhttp3.RequestBody;

/**
 * Created by tangzhiyuanon 2018/10/11.
 */

public class XMultiBody {
    private String fileName;
    private RequestBody requestBody;

    public XMultiBody(String fileName, RequestBody requestBody) {
        this.fileName = fileName;
        this.requestBody = requestBody;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }
}
