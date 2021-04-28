package com.rikyahmadfathoni.test.opaku.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class UtilsBitmap {

    public static Size getThumbnailSize(String path) {
        return getThumbnailSize(path, 96);
    }

    public static Size getThumbnailSize(String path, int maxSize) {
        final Size size = getSize(path);
        return properlyResizeBitmap(size.getWidth(), size.getHeight(), maxSize);
    }

    public static Size getSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return new Size(imageWidth, imageHeight);
    }

    @Nullable
    public static Bitmap getThumbnail(String path) {
        try {
            return BitmapFactory.decodeFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static String getStringImage(Bitmap bmp) {
        if (bmp != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        return null;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        final Size size = properlyResizeBitmap(image.getWidth(), image.getHeight(), maxSize);

        return Bitmap.createScaledBitmap(image, size.getWidth(), size.getHeight(), true);
    }

    public static Size properlyResizeBitmap(int width, int height, int maxSize) {
        if (width > maxSize || height > maxSize) {
            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
        }
        return new Size(width, height);
    }

    public static byte[] getByteImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static InputStream getInputStreamBitmap(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    @Nullable
    public static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable != null) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }

    @Nullable
    public static Bitmap getBitmapV2(@Nullable Drawable drawable, @Nullable Integer bitmapSize,
                                     @Nullable Integer maxSize) {
        try {
            if (drawable != null) {

                Bitmap bitmap;

                if (bitmapSize != null) {
                    // Single color bitmap will be created of 1x1 pixel
                    final Size size = properlyResizeBitmap(drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(), bitmapSize);
                    bitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                }

                if (bitmap != null) {
                    Bitmap mutableBitmap;

                    if (!bitmap.isMutable()) {
                        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    } else {
                        mutableBitmap = bitmap;
                    }

                    System.err.println("=========================================================");

                    final Canvas canvas = new Canvas(mutableBitmap);
                    final int canvasWidth = canvas.getWidth();
                    final int canvasHeight = canvas.getHeight();

                    System.err.println("canvasWidth : " + canvasWidth);
                    System.err.println("canvasHeight : " + canvasHeight);

                    if (maxSize != null) {
                        final Size size = properlyResizeBitmap(canvasWidth, canvasHeight, maxSize);
                        final int newWidth = size.getWidth();
                        final int newHeight = size.getHeight();
                        final int offsetX = (canvasWidth  - newWidth) /2;
                        final int offsetY = (canvasHeight - newHeight) /2;
                        drawable.setBounds(offsetX, offsetY, offsetX + newWidth, offsetY + newHeight);

                        System.err.println("newWidth : " + newWidth);
                        System.err.println("newHeight : " + newHeight);
                    } else {
                        drawable.setBounds(0, 0, canvasWidth, canvasHeight);
                    }

                    drawable.draw(canvas);

                    return mutableBitmap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            return Bitmap.createBitmap(
                    bitmap, 0, 0, width, height, matrix, false);
        }
        return null;
    }
}
