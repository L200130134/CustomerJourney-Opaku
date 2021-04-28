package com.rikyahmadfathoni.test.opaku.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.UUID;

public class UtilsInteger {

    public static boolean equals(int a, int b) {
        return (a == b);
    }

    public static int validateNull(Integer value) {
        return value != null ? value : 0;
    }

    @Nullable
    public static Integer parseInt(Object value) {
        try {
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            } else if (value instanceof Integer) {
                return (Integer) value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Long parseLong(Object value) {
        try {
            if (value instanceof String) {
                value = ((String) value).replace(".", "");
                return Long.parseLong((String) value);
            } else if (value instanceof Long) {
                return (Long) value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
