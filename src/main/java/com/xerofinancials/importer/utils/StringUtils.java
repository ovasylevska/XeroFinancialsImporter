package com.xerofinancials.importer.utils;

public class StringUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    public static String replaceEmptyWithNull(String value) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return null;
        }
        if (value.trim().isEmpty()) {
            return null;
        }
        return value;
    }
}
