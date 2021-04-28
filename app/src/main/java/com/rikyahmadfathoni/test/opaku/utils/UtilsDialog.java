package com.rikyahmadfathoni.test.opaku.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class UtilsDialog {

    private static final String LOG = "LOG : ";

    public static void postLongToast(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            showLongToast(context, message);
        });
    }

    public static void showToast(Context context, String message) {
        showToast(context, message, Gravity.BOTTOM);
    }

    public static void showToast(Context context, String message, int gravity) {
        if (context != null) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        }
    }

    public static void showLongToast(Context context, String message) {
        if (context != null) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void showLog(Class<?> mClass, String message) {
        showLog(mClass.getSimpleName(), message, null);
    }

    public static void showLog(String TAG, String message) {
        showLog(TAG, message, null);
    }

    public static void showLog(String TAG, String message, String throwable) {
        if (throwable != null) {
            Log.e(LOG + TAG, message, new Throwable(throwable));
        } else {
            Log.e(LOG + TAG, message);
        }
    }
}
