package com.rikyahmadfathoni.test.opaku.store.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.model.SubTotalModel;
import com.rikyahmadfathoni.test.opaku.store.model.WishlistModel;

import java.util.List;

@Dao
public interface WishlistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WishlistModel wishlistModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<WishlistModel> wishlistModels);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(WishlistModel wishlistModel);

    @Delete
    void delete(WishlistModel wishlistModel);

    @Delete
    void delete(List<WishlistModel> wishlistModels);

    @Query("DELETE FROM wishlist WHERE productId=:productId")
    void delete(String productId);

    @Query("DELETE FROM wishlist")
    void deleteAll();

    @Query("SELECT COUNT(id) FROM wishlist")
    int getItemCount();

    @Query("SELECT COUNT(id) AS productTotal, SUM(productPrice) AS priceTotal FROM wishlist")
    SubTotalModel getSubTotal();

    @Query("SELECT * FROM wishlist WHERE productId=:productId")
    List<WishlistModel> getWishlistModelById(String productId);

    @Query("SELECT * FROM wishlist")
    List<WishlistModel> getWishlistModel();

    @Query("SELECT * FROM wishlist")
    LiveData<List<WishlistModel>> getLiveWishlistModel();

    @Query("SELECT * FROM wishlist WHERE productId=:productId")
    LiveData<List<WishlistModel>> getLiveWishlistModel(String productId);
}