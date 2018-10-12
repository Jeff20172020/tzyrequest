package com.tzy.requestlib.gson;

import com.tzy.requestlib.gson.config.AnnotationDeserializationExclusionStrategy;
import com.tzy.requestlib.gson.config.AnnotationSerializationExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by tangzhiyuanon 2018/10/12.
 */

public class DalGsonHelper {
    private static Gson orginalGson;

    static {
        orginalGson = generateOriginalGson();
    }

    private DalGsonHelper() {

    }

    private static Gson generateOriginalGson() {
        return new GsonBuilder().addDeserializationExclusionStrategy(new AnnotationDeserializationExclusionStrategy())
                .addSerializationExclusionStrategy(new AnnotationSerializationExclusionStrategy())
                .create();
    }

    public static Gson getOriginalGson() {
        return orginalGson;
    }


}
