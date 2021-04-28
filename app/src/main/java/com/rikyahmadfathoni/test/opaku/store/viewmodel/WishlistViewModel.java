package com.rikyahmadfathoni.test.opaku.store.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.rikyahmadfathoni.test.opaku.store.dao.CartDao;
import com.rikyahmadfathoni.test.opaku.store.dao.WishlistDao;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.model.SubTotalModel;
import com.rikyahmadfathoni.test.opaku.store.model.WishlistModel;

import java.util.List;

public class WishlistViewModel extends AndroidViewModel implements WishlistDao {

    public static WishlistViewModel getInstance(Application application) {
        return new ViewModelProvider.AndroidViewModelFactory(application)
                .create(WishlistViewModel.class);
    }

    private final WishlistRepository repository;

    public WishlistViewModel(Application application) {
        super(application);
        this.repository = new WishlistRepository(application);
    }

    public void toggle(WishlistModel model) {
        repository.toggle(model);
    }

    @Override
    public void insert(WishlistModel wishlistModel) {
        repository.insert(wishlistModel);
    }

    @Override
    public void insert(List<WishlistModel> wishlistModels) {
        repository.insert(wishlistModels);
    }

    @Override
    public void update(WishlistModel wishlistModel) {
        repository.update(wishlistModel);
    }

    @Override
    public void delete(WishlistModel wishlistModel) {
        repository.delete(wishlistModel);
    }

    @Override
    public void delete(List<WishlistModel> wishlistModels) {
        repository.delete(wishlistModels);
    }

    @Override
    public void delete(String productId) {
        repository.delete(productId);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public int getItemCount() {
        return repository.getItemCount();
    }

    @Override
    public SubTotalModel getSubTotal() {
        return repository.getSubTotal();
    }

    @Override
    public List<WishlistModel> getWishlistModelById(String productId) {
        return repository.getWishlistModelById(productId);
    }

    @Override
    public List<WishlistModel> getWishlistModel() {
        return repository.getWishlistModel();
    }

    @Override
    public LiveData<List<WishlistModel>> getLiveWishlistModel() {
        return repository.getLiveWishlistModel();
    }

    @Override
    public LiveData<List<WishlistModel>> getLiveWishlistModel(String productId) {
        return repository.getLiveWishlistModel(productId);
    }

    public MutableLiveData<WishlistModel> getWishlistChangeLiveData() {
        return repository.getWishlistChangeLiveData();
    }
}