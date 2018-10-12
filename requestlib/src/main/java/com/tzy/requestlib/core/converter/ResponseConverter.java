package com.tzy.requestlib.core.converter;

import com.tzy.requestlib.core.xrequest.XRequest;

import java.lang.reflect.Type;

/**
 * Created by tangzhiyuanon 2018/10/11.
 */

public interface ResponseConverter {
    <T> T convert(XRequest xRequest, byte[] responseBytes, Type responseType);
}
