package com.rikyahmadfathoni.test.opaku.store.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListConverter<T> {

    @TypeConverter
    public String fromList(ArrayList<T> models) {
        if (models == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<T>>(){}.getType();
        return gson.toJson(models, type);
    }

    @TypeConverter
    public ArrayList<T> toList(String values) {
        if (values == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<T>>(){}.getType();
        return gson.fromJson(values, type);
    }
}
