package com.rikyahmadfathoni.test.opaku.store.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rikyahmadfathoni.test.opaku.store.dao.CartDao;
import com.rikyahmadfathoni.test.opaku.store.database.CartDatabase;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.model.SubTotalModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsThread;

import java.util.List;
import java.util.concurrent.Executors;

public class CartRepository implements CartDao {

    public interface EventListener {
        void onInsert(CartModel cartModel);
        void onDelete(CartModel cartModel);
        void onUpdate(CartModel cartModel);
    }

    private final CartDao cartDao;

    public CartRepository(Application application) {
        CartDatabase database = CartDatabase.getInstance(application);
        cartDao = database.cartDao();
    }

    public void insertOrUpdateStock(CartModel cartModel, EventListener eventListener) {
        UtilsThread.runOnThread(() -> {
            final List<CartModel> cartModels = cartDao.getCartModelById(cartModel.getProductId());
            if (cartModels == null || cartModels.isEmpty()) {
                cartDao.insert(cartModel);
                if (eventListener != null) {
                    eventListener.onInsert(cartModel);
                }
            } else if (cartModels.size() == 1) {
                final CartModel currentCartModel = cartModels.get(0);
                final int productTotal = currentCartModel.getProductSelected() + 1;
                final int stock = currentCartModel.getProductStock();
                if (stock > 0) {
                    if (productTotal <= stock) {
                        currentCartModel.setProductSelected(productTotal);
                        cartDao.updateProductTotal(productTotal, currentCartModel.getProductId());
                        if (eventListener != null) {
                            eventListener.onInsert(currentCartModel);
                        }
                    } else {
                        //over select
                    }
                } else {
                    //habis stock
                }
            } else {
                //something went wrong
                throw new RuntimeException("Product id may not corrected");
            }
        });
    }

    @Override
    public void insert(CartModel cartModel) {
        UtilsThread.runOnThread(() -> {
            cartDao.insert(cartModel);
        });
    }

    @Override
    public void insert(List<CartModel> cartModels) {
        UtilsThread.runOnThread(() -> {
            cartDao.insert(cartModels);
        });
    }

    @Override
    public void update(CartModel cartModel) {
        UtilsThread.runOnThread(() -> {
            cartDao.update(cartModel);
        });
    }

    @Override
    public void update(List<CartModel> cartModels) {
        UtilsThread.runOnThread(() -> {
            cartDao.update(cartModels);
        });
    }

    @Override
    public void delete(CartModel cartModel) {
        UtilsThread.runOnThread(() -> {
            cartDao.delete(cartModel);
            //cartAddedLiveData.postValue(cartModel);
        });
    }

    @Override
    public void delete(List<CartModel> cartModels) {
        UtilsThread.runOnThread(() -> {
            cartDao.delete(cartModels);
        });
    }

    @Override
    public void delete(String productId) {
        UtilsThread.runOnThread(() -> {
            cartDao.delete(productId);
        });
    }

    @Override
    public void deleteAll() {
        UtilsThread.runOnThread(cartDao::deleteAll);
    }

    @Override
    public void updateProductTotal(int productTotal, String productId) {
        UtilsThread.runOnThread(() -> {
            cartDao.updateProductTotal(productTotal, productId);
        });
    }

    @Override
    public int getItemCount() {
        return cartDao.getItemCount();
    }

    @Override
    public SubTotalModel getSubTotal() {
        return cartDao.getSubTotal();
    }

    @Override
    public List<CartModel> getCartModelById(String productId) {
        return cartDao.getCartModelById(productId);
    }

    @Override
    public List<CartModel> getCartModel() {
        return cartDao.getCartModel();
    }

    @Override
    public LiveData<List<CartModel>> getLiveCartModel() {
        return cartDao.getLiveCartModel();
    }
}