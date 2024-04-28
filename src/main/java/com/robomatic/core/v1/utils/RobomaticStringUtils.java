package com.robomatic.core.v1.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public final class RobomaticStringUtils extends org.apache.commons.lang3.StringUtils {

    private RobomaticStringUtils() {
    }

    public static String createRandomId(String prefix) {

        return org.apache.commons.lang3.StringUtils.join(prefix,
                UUID.randomUUID().toString().replace("-", ""));

    }

    public static Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

}
