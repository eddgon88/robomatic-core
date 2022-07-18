package com.robomatic.core.v1.utils;

import cl.multicaja.digital.gateway.v1.models.ExceptionTrace;
import com.google.gson.Gson;

import java.util.Map;

public final class LogUtils {

    private static Gson gson = new Gson();
    private static Trace trace;

    private LogUtils() {
    }

    public static String formatObjectToJson(String title, String message, long orderId, String virtualId, String referenceId) {
        trace = Trace.builder().message(message).title(title).orderId(orderId).virtualId(virtualId).referenceId(referenceId).build();
        return gson.toJson(trace);
    }

    public static String formatObjectToJson(String title, String message, String referenceId) {
        trace = Trace.builder().title(title).message(message).referenceId(referenceId).build();
        return gson.toJson(trace);
    }

    public static String formatObjectToJson(String title, String message, long orderId, Map<String, Object> customs, String referenceId) {
        trace = Trace.builder().title(title).message(message).customs(customs).orderId(orderId).referenceId(referenceId).build();
        return gson.toJson(trace);
    }

    public static String formatObjectToJson(String title, String message, Map<String, Object> customs, String referenceId) {
        trace = Trace.builder().title(title).message(message).customs(customs).referenceId(referenceId).build();
        return gson.toJson(trace);
    }

    public static String formatObjectToJson(String title, String message, Map<String, Object> customs) {
        trace = Trace.builder().title(title).message(message).customs(customs).build();
        return gson.toJson(trace);
    }

    public static String formatObjectToJson(String title, String message) {
        trace = Trace.builder().title(title).message(message).build();
        return gson.toJson(trace);
    }

    public static String getStackTrace(Exception e) {
        ExceptionTrace exceptionTrace = ExceptionUtils.getExceptionTrace(e);
        return gson.toJson(exceptionTrace);
    }
}
