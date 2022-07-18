package com.robomatic.core.v1.utils;

import cl.multicaja.digital.gateway.v1.models.ExceptionTrace;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static ExceptionTrace getExceptionTrace(Exception e) {
        return ExceptionTrace.builder()
            .message(e.getMessage())
            .stackTrace(getStackTrace(e))
            .build();
    }

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
