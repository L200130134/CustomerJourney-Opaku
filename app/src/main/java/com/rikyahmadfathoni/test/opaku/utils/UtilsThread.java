package com.rikyahmadfathoni.test.opaku.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UtilsThread {

    public static void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static void runOnUiThread(Runnable runnable, long delayTime) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayTime);
    }

    public static void runOnUiThread(View view, Runnable runnable) {
        if (view != null) {
            view.post(runnable);
        }
    }

    public static void runOnUiThread(View view, Runnable runnable, long delayTime) {
        if (view != null) {
            view.postDelayed(runnable, delayTime);
        }
    }

    public static void runOnUiThread(Activity activity, Runnable runnable) {
        if (activity != null) {
            activity.runOnUiThread(runnable);
        }
    }

    public static Future<?> runOnThread(Runnable runnable) {
        return Executors.newFixedThreadPool(1).submit(runnable);
    }

    public static void runOnThread(Runnable runnable, long delayTime) {
        new Handler().postDelayed(runnable, delayTime);
    }
}
