package com.rikyahmadfathoni.test.opaku.store.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rikyahmadfathoni.test.opaku.store.dao.WishlistDao;
import com.rikyahmadfathoni.test.opaku.store.database.CartDatabase;
import com.rikyahmadfathoni.test.opaku.store.database.WishlistDatabase;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.model.SubTotalModel;
import com.rikyahmadfathoni.test.opaku.store.model.WishlistModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsList;
import com.rikyahmadfathoni.test.opaku.utils.UtilsThread;

import java.util.List;

public class WishlistRepository implements WishlistDao {

    private final WishlistDao wishlistDao;
    private final MutableLiveData<WishlistModel> wishlistChangeLiveData = new MutableLiveData<>();

    public WishlistRepository(Application application) {
        WishlistDatabase database = WishlistDatabase.getInstance(application);
        wishlistDao = database.wishlistDao();
    }

    public void toggle(WishlistModel model) {
        UtilsThread.runOnThread(() -> {
            final List<WishlistModel> wishlistModels = UtilsList.nonNull(getWishlistModelById(model.getProductId()));
            if (wishlistModels.size() == 1) {
                final WishlistModel wishlistModel = wishlistModels.get(0);
                if (wishlistModel != null) {
                    wishlistDao.delete(wishlistModel);
                    wishlistChangeLiveData.postValue(null);
                    return;
                }
            }
            wishlistDao.insert(model);
            wishlistChangeLiveData.postValue(model);
        });
    }

    @Override
    public void insert(WishlistModel wishlistModel) {
        UtilsThread.runOnThread(() -> {
            wishlistDao.insert(wishlistModel);
        });
    }

    @Override
    public void insert(List<WishlistModel> cartModels) {
        UtilsThread.runOnThread(() -> {
            wishlistDao.insert(cartModels);
        });
    }

    @Override
    public void update(WishlistModel cartModel) {
        UtilsThread.runOnThread(() -> {
            wishlistDao.update(cartModel);
        });
    }

    @Override
    public void delete(WishlistModel cartModel) {
        UtilsThread.runOnThread(() -> {
            wishlistDao.delete(cartModel);
        });
    }

    @Override
    public void delete(List<WishlistModel> cartModels) {
        UtilsThread.runOnThread(() -> {
            wishlistDao.delete(cartModels);
        });
    }

    @Override
    public void delete(String productId) {
        UtilsThread.runOnThread(() -> {
            wishlistDao.delete(productId);
        });
    }

    @Override
    public void deleteAll() {
        UtilsThread.runOnThread(wishlistDao::deleteAll);
    }

    @Override
    public int getItemCount() {
        return wishlistDao.getItemCount();
    }

    @Override
    public SubTotalModel getSubTotal() {
        return wishlistDao.getSubTotal();
    }

    @Override
    public List<WishlistModel> getWishlistModelById(String productId) {
        return wishlistDao.getWishlistModelById(productId);
    }

    @Override
    public List<WishlistModel> getWishlistModel() {
        return wishlistDao.getWishlistModel();
    }

    @Override
    public LiveData<List<WishlistModel>> getLiveWishlistModel() {
        return wishlistDao.getLiveWishlistModel();
    }

    @Override
    public LiveData<List<WishlistModel>> getLiveWishlistModel(String productId) {
        return wishlistDao.getLiveWishlistModel(productId);
    }

    public MutableLiveData<WishlistModel> getWishlistChangeLiveData() {
        return wishlistChangeLiveData;
    }
}