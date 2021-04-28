package com.rikyahmadfathoni.test.opaku.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class UtilsNumber {

    public static String formatRupiah(long value) {
        final DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getNumberInstance(getIDLocale());
        return String.format("%s %s", "Rp.", kursIndonesia.format(value));
    }

    public static Locale getIDLocale() {
        return  new Locale("in", "ID");
    }
}
