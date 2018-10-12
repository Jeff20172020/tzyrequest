package com.tzy.requestlib.gson;

import com.tzy.requestlib.core.converter.ResponseConverter;
import com.tzy.requestlib.core.xrequest.XRequest;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 23/03/2018.
 */
public class DefaultGsonResponseConverter implements ResponseConverter {

    private Gson gson;

    public static DefaultGsonResponseConverter create() {
        return new DefaultGsonResponseConverter(DalGsonHelper.getOriginalGson());
    }

    public static DefaultGsonResponseConverter create(Gson gson) {
        return new DefaultGsonResponseConverter(gson);
    }

    private DefaultGsonResponseConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <T> T convert(XRequest xRequest, byte[] responseBytes, Type responseType) {
        Reader reader = null;
        try {
            return gson.fromJson(reader = new InputStreamReader(new ByteArrayInputStream(responseBytes)), responseType);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
