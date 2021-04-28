package com.rikyahmadfathoni.test.opaku.utils;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

@SuppressWarnings("ALL")
public class UtilsString {

    public static boolean equals(String a, String b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.toString().equals(b.toString()));
    }

    public static boolean isEmpty(CharSequence value) {
        return value == null || value.toString().trim().isEmpty();
    }

    @NonNull
    public static String nonNull() {
        return "";
    }

    @NonNull
    public static String nonNull(Editable editable) {
        return editable != null ? nonNull(editable.toString()) : "";
    }

    @NonNull
    public static String nonNull(String value) {
        return value != null ? value.trim() : "";
    }

    public static boolean isValidEmail(String email) {
        if (email != null) {
            final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            return email.matches(emailPattern);
        }
        return false;
    }

    public static String formatURL(String url) {
        if (url != null) {
            return url.replace(" ", "%20");
        }
        return null;
    }

    public static String encodeURL(String url) {
        if (url != null) {
            try {
                String encoded = URLEncoder.encode(url, "UTF-8");
                return encoded.replace("+", "%20");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static RandomString getRandomString() {
        return new RandomString();
    }

    public static String getRandomImageName() {
        return String.format("%s_%s.jpg", getRandomString().nextPIN(), System.currentTimeMillis());
    }

    @Nullable
    public static String removeLastSeparator(String url) {
        if (url != null) {
            final String newUrl = url.trim();
            if (newUrl.endsWith("/")) {
                return newUrl.substring(0, Math.max(0, newUrl.length()-1));
            }
        }
        return url;
    }

    public static class RandomString {

        private final int length;

        private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        private static final String lower = upper.toLowerCase(Locale.ROOT);

        private static final String digits = "0123456789";

        private static final String alphanum = upper /*+ lower*/ + digits;

        private final Random random;

        private final char[] symbols;

        private final char[] buf;

        private RandomString(int length, Random random, String symbols) {
            this.length = length;
            if (length < 1) throw new IllegalArgumentException();
            if (symbols.length() < 2) throw new IllegalArgumentException();
            this.random = random;
            this.symbols = symbols.toCharArray();
            this.buf = new char[length];
        }

        /**
         * Create an alphanumeric string generator.
         */
        private RandomString(int length, Random random) {
            this(length, random, alphanum);
        }

        /**
         * Create an alphanumeric strings from a secure generator.
         */
        public RandomString(int length) {
            this(length, new SecureRandom());
        }

        /**
         * Create session identifiers.
         */
        public RandomString() {
            this(8);
        }

        /**
         * Generate a random string.
         */
        public String nextPIN() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }
}
