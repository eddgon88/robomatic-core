package com.robomatic.core.v1.utils;

import java.util.UUID;

public final class StringUtils extends org.apache.commons.lang3.StringUtils {

    private StringUtils() {
    }

    public static String createRandomId(String prefix) {

        return org.apache.commons.lang3.StringUtils.join(prefix,
                UUID.randomUUID().toString().replace("-", ""));

    }

}
