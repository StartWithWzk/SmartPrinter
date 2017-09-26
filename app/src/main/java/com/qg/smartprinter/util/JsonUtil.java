package com.qg.smartprinter.util;

import com.google.gson.GsonBuilder;

public class JsonUtil {

    public static String toJson(Object o)  {
        return new GsonBuilder()
                .create()
                .toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        return new GsonBuilder()
                .create()
                .fromJson(json, cls);
    }

}
