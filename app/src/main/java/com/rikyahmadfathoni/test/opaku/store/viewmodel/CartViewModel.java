package com.rikyahmadfathoni.test.opaku.store.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.store.dao.CartDao;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.model.SubTotalModel;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends AndroidViewModel implements CartDao {

    public static CartViewModel getInstance(Application application) {
        return new ViewModelProvider.AndroidViewModelFactory(application)
                .create(CartViewModel.class);
    }

    @Deprecated
    public static CartViewModel getInstance(@NonNull ViewModelStoreOwner owner) {
        return new ViewModelProvider(owner, new ViewModelProvider.NewInstanceFactory())
                .get(CartViewModel.class);
    }

    private final CartRepository repository;

    public CartViewModel(Application application) {
        super(application);
        this.repository = new CartRepository(application);
    }

    public void insertOrUpdateStock(CartModel cartModel, CartRepository.EventListener listener) {
        repository.insertOrUpdateStock(cartModel, listener);
    }

    @Override
    public void insert(CartModel cartModel) {
        repository.insert(cartModel);
    }

    @Override
    public void insert(List<CartModel> cartModels) {
        repository.insert(cartModels);
    }

    @Override
    public void update(CartModel cartModel) {
        repository.update(cartModel);
    }

    @Override
    public void update(List<CartModel> cartModels) {
        repository.update(cartModels);
    }

    public void updateBy(List<ProductModel> productModels) {
        if (productModels != null) {
            final List<CartModel> cartModels = new ArrayList<>();
            for (ProductModel productModel : productModels) {
                cartModels.add(new CartModel(productModel));
            }
            repository.update(cartModels);
        }
    }

    @Override
    public void delete(CartModel cartModel) {
        repository.delete(cartModel);
    }

    @Override
    public void delete(List<CartModel> cartModels) {
        repository.delete(cartModels);
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
    public void updateProductTotal(int productTotal, String productId) {
        repository.updateProductTotal(productTotal, productId);
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
    public List<CartModel> getCartModelById(String productId) {
        return repository.getCartModelById(productId);
    }

    @Override
    public List<CartModel> getCartModel() {
        return repository.getCartModel();
    }

    @Override
    public LiveData<List<CartModel>> getLiveCartModel() {
        return repository.getLiveCartModel();
    }
}