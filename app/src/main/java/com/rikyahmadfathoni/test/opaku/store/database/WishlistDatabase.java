package com.rikyahmadfathoni.test.opaku.store.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.rikyahmadfathoni.test.opaku.store.dao.CartDao;
import com.rikyahmadfathoni.test.opaku.store.dao.WishlistDao;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.model.WishlistModel;

@Database(entities = {WishlistModel.class}, version = 1)
public abstract class WishlistDatabase extends RoomDatabase {

    private static WishlistDatabase instance;

    public abstract WishlistDao wishlistDao();

    public static synchronized WishlistDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WishlistDatabase.class, "WishlistDatabase")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private final static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //new PopulateDbAsyncTask(instance).execute();
        }
    };
}