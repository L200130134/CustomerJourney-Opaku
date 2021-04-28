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

import java.util.List;

@Dao
public interface CartDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(CartModel cartModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<CartModel> cartModels);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(CartModel cartModel);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(List<CartModel> cartModels);

    @Delete
    void delete(CartModel cartModel);

    @Delete
    void delete(List<CartModel> cartModels);

    @Query("DELETE FROM cart WHERE productId=:productId")
    void delete(String productId);

    @Query("DELETE FROM cart")
    void deleteAll();

    @Query("UPDATE cart SET productSelected=:productSelected WHERE productId=:productId")
    void updateProductTotal(int productSelected, String productId);

    @Query("SELECT COUNT(id) FROM cart")
    int getItemCount();

    @Query("SELECT COUNT(id) AS productTotal, SUM(productPrice) AS priceTotal FROM cart")
    SubTotalModel getSubTotal();

    @Query("SELECT * FROM cart WHERE productId=:productId")
    List<CartModel> getCartModelById(String productId);

    @Query("SELECT * FROM cart")
    List<CartModel> getCartModel();

    @Query("SELECT * FROM cart")
    LiveData<List<CartModel>> getLiveCartModel();
}