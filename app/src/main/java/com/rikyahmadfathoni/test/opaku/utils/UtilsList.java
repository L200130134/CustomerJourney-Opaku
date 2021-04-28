package com.rikyahmadfathoni.test.opaku.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class UtilsList {

    public static<T> ArrayList<T> singletonList(T object) {
        return new ArrayList<T>() {{
            add(object);
        }};
    }

    public static<T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static<T> boolean isEmpty(T[] array) {
        return array == null || array.length <= 0;
    }

    @NonNull
    public static<T> List<T> nonNull(List<T> models) {
        return models != null ? models : new ArrayList<>();
    }

    @Nullable
    public static<T> T get(List<T> models, int index) {
        try {
            return models.get(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static<T, V> V getMap(HashMap<T, V> map, T key) {
        try {
            return map.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static<T> T getFirst(List<T> models) {
        return get(models, 0);
    }

    @Nullable
    public static<T> T getEnd(List<T> models) {
        return get(models, Math.max(0, models.size() - 1));
    }

    public static<T> List<T> clone(List<T> models) {
        return new ArrayList<>(models);
    }

    @Nullable
    public static<T> Queue<T> asLinkedList(T[] paths) {
        return asLinkedList(Arrays.asList(paths));
    }

    @Nullable
    public static<T> Queue<T> asLinkedList(List<T> paths) {
        try {
            return new LinkedList<>(paths);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static<T> Queue<T> singletonLinkedList(T object) {
        return new LinkedList<T>() {{
            add(object);
        }};
    }

    public static<T> int getAddIndex(int position, List<T> list) {
        if (list != null) {
            final int listLength = list.size();
            if (position > listLength) {
                position = listLength;
            }
            if (position < 0) {
                position = 0;
            }
            return position;
        }
        return -1;
    }

    public static<T> ArrayList<T> dummyList(int totalData) {
        final ArrayList<T> list = new ArrayList<>();
        for (int i=0; i<totalData; i++) {
            list.add(null);
        }
        return list;
    }
}
