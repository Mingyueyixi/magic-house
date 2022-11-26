package com.lu.magic.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GsonUtil {

    private static final Gson GSON = createGson(true);

    private static final Gson GSON_NO_NULLS = createGson(false);
    private static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

    private GsonUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    public static Gson getGson() {
        return getGson(true);
    }

    public static Gson getGson(final boolean serializeNulls) {
        return serializeNulls ? GSON_NO_NULLS : GSON;
    }

    /**
     * 打印优化的gson
     *
     * @return
     */
    public static Gson getPrettyGson() {
        return GSON_PRETTY;
    }

    public static String toJson(final Object object) {
        return toJson(object, true);
    }

    public static String toJson(final Object object, final boolean includeNulls) {
        return includeNulls ? GSON.toJson(object) : GSON_NO_NULLS.toJson(object);
    }

    public static String toJson(final Object src, final Type typeOfSrc) {
        return toJson(src, typeOfSrc, true);
    }


    public static String toJson(final Object src, final Type typeOfSrc, final boolean includeNulls) {
        return includeNulls ? GSON.toJson(src, typeOfSrc) : GSON_NO_NULLS.toJson(src, typeOfSrc);
    }


    public static <T> T fromJson(final String json, final Class<T> type) {
        return GSON.fromJson(json, type);
    }

    public static <T> T fromJson(final String json, final Type type) {
        return GSON.fromJson(json, type);
    }

    public static <T> T fromJson(final Reader reader, final Class<T> type) {
        return GSON.fromJson(reader, type);
    }

    public static <T> T fromJson(final Reader reader, final Type type) {
        return GSON.fromJson(reader, type);
    }

    public static <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return GSON.fromJson(reader, typeOfT);
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(JsonElement json, Type typeOfT) throws JsonSyntaxException {
        return GSON.fromJson(json, typeOfT);
    }

    public static JsonWriter newJsonWriter(Writer writer) throws IOException {
        return GSON.newJsonWriter(writer);
    }

    public static JsonReader newJsonReader(Reader reader) {
        return GSON.newJsonReader(reader);
    }

    public static void toJson(Object src, Appendable writer) throws JsonIOException {
        GSON.toJson(src, writer);
    }

    public static void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
        GSON.toJson(src, typeOfSrc, writer);
    }

    public static void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
        GSON.toJson(src, typeOfSrc, writer);
    }

    public static String toJson(JsonElement jsonElement) {
        return GSON.toJson(jsonElement);
    }

    public static void toJson(JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
        GSON.toJson(jsonElement, writer);
    }

    public static void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
        GSON.toJson(jsonElement, writer);
    }

    public static JsonElement toJsonTree(Object src) {
        return GSON.toJsonTree(src);
    }

    public static JsonElement toJsonTree(Object src, Type typeOfSrc) {
        return GSON.toJsonTree(src, typeOfSrc);
    }

    public static Type getListType(final Type type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }

    public static Type getSetType(final Type type) {
        return TypeToken.getParameterized(Set.class, type).getType();
    }

    public static Type getMapType(final Type keyType, final Type valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }


    public static Type getArrayType(final Type type) {
        return TypeToken.getArray(type).getType();
    }

    public static Type getType(final Type rawType, final Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }

    private static Gson createGson(final boolean serializeNulls) {
        final GsonBuilder builder = new GsonBuilder();
        if (serializeNulls) builder.serializeNulls();
        return builder.create();
    }

}
