package com.srihari.sriharimart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Utility class for JSON serialization and deserialization using Gson.
 */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .create();

    private JsonUtil() {
        // Prevent instantiation
    }

    /**
     * Serializes any object to its JSON representation.
     *
     * @param src Source object
     * @return JSON string
     */
    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    /**
     * Deserializes JSON string into specified class type.
     *
     * @param json     JSON string
     * @param classOfT Target class
     * @param <T>      Target type
     * @return Deserialized object
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * Deserializes JSON from Reader into specified class type.
     *
     * @param reader   Input reader
     * @param classOfT Target class
     * @param <T>      Target type
     * @return Deserialized object
     */
    public static <T> T fromJson(Reader reader, Class<T> classOfT) {
        return GSON.fromJson(reader, classOfT);
    }

    /**
     * Deserializes JSON string into specified generic Type.
     *
     * @param json    JSON string
     * @param typeOfT Target Type
     * @param <T>     Target type
     * @return Deserialized object
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }
}
