package com.rikyahmadfathoni.test.opaku.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityProductBinding;
import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.model.WishlistModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.CartRepository;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.CartViewModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.WishlistViewModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsList;
import com.rikyahmadfathoni.test.opaku.utils.UtilsMain;
import com.rikyahmadfathoni.test.opaku.utils.UtilsNumber;
import com.rikyahmadfathoni.test.opaku.utils.UtilsThread;

import java.util.List;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(Activity activity, ProductModel productModel) {
        if (activity != null) {
            final Intent intent = new Intent(activity, ProductActivity.class);
            intent.putExtra(ProductActivity.KEY_PRODUCT_MODEL, productModel);
            activity.startActivity(intent);
        }
    }

    public static final String KEY_PRODUCT_MODEL = "KEY_PRODUCT_MODEL";
    public static final String KEY_PRODUCT_ID = "KEY_PRODUCT_ID";
    public static final String KEY_ALLOW_ADD_CART = "KEY_ALLOW_ADD_CART";
    private FirebaseAnalytics analytics;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityProductBinding binding;
    private ProductModel productModel;
    private String productId;
    private CartViewModel cartViewModel;
    private WishlistViewModel wishlistViewModel;
    private boolean allowAddCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindObject();
        bindIntent(getIntent());
        bindProdukData(true);
        bindEvent();
        bindLiveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartViewModel != null) {
            cartViewModel.getLiveCartModel().removeObserver(observerCartModel);
        }
        if (wishlistViewModel != null) {
            wishlistViewModel.getLiveWishlistModel().removeObserver(observerWishlistModel);
            wishlistViewModel.getWishlistChangeLiveData().removeObserver(observerChangeWishlistModel);
        }
    }

    private void bindLiveData() {
        if (cartViewModel != null) {
            cartViewModel.getLiveCartModel().observe(this, observerCartModel);
        }
        if (wishlistViewModel != null) {
            wishlistViewModel.getLiveWishlistModel(productId).observe(this, observerWishlistModel);
            wishlistViewModel.getWishlistChangeLiveData().observe(this, observerChangeWishlistModel);
        }
    }

    private final Observer<List<CartModel>> observerCartModel = new Observer<List<CartModel>>() {
        @Override
        public void onChanged(List<CartModel> cartModels) {
            if (binding == null) {
                return;
            }
            binding.textCart.setText(String.valueOf(UtilsMain.getTotalCartItem(cartModels)));
        }
    };

    private final Observer<List<WishlistModel>> observerWishlistModel = new Observer<List<WishlistModel>>() {
        @Override
        public void onChanged(List<WishlistModel> wishlistModels) {
            if (binding == null) {
                return;
            }
            final WishlistModel wishlistModel = UtilsList.get(wishlistModels, 0);
            binding.content.iconLike.setImageResource(
                    wishlistModel != null ? R.drawable.ic_like_enabled : R.drawable.ic_like
            );
        }
    };

    private final Observer<WishlistModel> observerChangeWishlistModel = new Observer<WishlistModel>() {
        @Override
        public void onChanged(WishlistModel wishlistModel) {
            if (binding == null) {
                return;
            }
            if (wishlistModel != null) {
                //log analytics
                analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST,
                        UtilsMain.getAnalyticsWishlist(wishlistModel));
            }
        }
    };

    private void bindObject() {
        analytics = FirebaseAnalytics.getInstance(this);
        cartViewModel = CartViewModel.getInstance(getApplication());
        wishlistViewModel = WishlistViewModel.getInstance(getApplication());
    }

    private void bindIntent(Intent intent) {
        if (intent != null) {
            allowAddCart = intent.getBooleanExtra(KEY_ALLOW_ADD_CART, true);
            productId = intent.getStringExtra(KEY_PRODUCT_ID);
            if (productId == null) {
                productModel = intent.getParcelableExtra(KEY_PRODUCT_MODEL);
                if (productModel == null || productModel.getId() == null) {
                    UtilsDialog.showToast(this, getString(R.string.message_invalid_product_id));
                    finish();
                }
            }
        }
    }

    private void bindProdukData(boolean update) {
        if (productModel != null) {
            productId = productModel.getId();
            binding.content.textProductName.setText(productModel.getName());
            binding.content.textProductPrice.setText(UtilsNumber.formatRupiah(productModel.getPrice()));

            binding.content.infoStock.textName.setText(getString(R.string.title_stock));
            binding.content.infoBrand.textName.setText(getString(R.string.title_brand));
            binding.content.infoMaterial.textName.setText(getString(R.string.title_material));
            binding.content.infoWeight.textName.setText(getString(R.string.title_weight));

            binding.content.infoStock.textValue.setText(String.valueOf(productModel.getStock()));
            binding.content.infoBrand.textValue.setText(getValidBrand(productModel.getBrand()));
            binding.content.infoMaterial.textValue.setText(getValidMaterial(productModel.getMaterial()));
            binding.content.infoWeight.textValue.setText(String.format("%s %s", productModel.getWeight(), getString(R.string.title_gram)));

            binding.content.textProductDescription.setText(getValidDescription(productModel.getDescription()));

            Glide.with(this).load(productModel.getImage())
                    .placeholder(R.drawable.placeholder)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.imageShimmer.hideShimmer();
                            return false;
                        }
                    })
                    .into(binding.imageProduct);

            setVisibleContent(true);
        }
        if (update) {
            getProductData(productId);
        }
    }

    private void getProductData(String productId) {
        if (productId != null) {
            db.collection(Constants.DB_COLLECTION_PRODUCTS)
                    .document(productId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            final DocumentSnapshot ds = task.getResult();
                            if (ds != null) {
                                final String id = ds.getId();
                                final ProductModel productModel = ds.toObject(ProductModel.class);
                                if (productModel != null) {
                                    productModel.setId(id);
                                    ProductActivity.this.productModel = productModel;
                                    bindProdukData(false);
                                    cartViewModel.update(new CartModel(productModel));
                                }
                            }
                        } else {
                            UtilsDialog.showToast(getBaseContext(), getString(R.string.message_invalid_get_data));
                        }
                    });
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setVisibleContent(boolean visibleContent) {
        if (binding == null) {
            return;
        }
        binding.content.contentProduct.setVisibility(visibleContent ? View.VISIBLE : View.GONE);
        binding.content.placeholder.setVisibility(visibleContent ? View.GONE : View.VISIBLE);
        binding.buttonAddCart.setEnabled(visibleContent);
    }

    private String getValidBrand(String brand) {
        if (brand == null) {
            return getString(R.string.info_no_brand);
        }
        return brand;
    }

    private String getValidMaterial(String material) {
        if (material == null) {
            return getString(R.string.info_unknown);
        }
        return material;
    }

    private String getValidDescription(String description) {
        if (description == null) {
            return getString(R.string.info_no_desc);
        }
        return description;
    }

    private void bindEvent() {
        binding.buttonAddCart.setVisibility(allowAddCart ? View.VISIBLE : View.GONE);

        binding.imageProduct.setOnClickListener(this);
        binding.iconBack.setOnClickListener(this);
        binding.layoutCart.setOnClickListener(this);
        binding.content.iconLike.setOnClickListener(this);
        binding.buttonAddCart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.iconBack) {
            super.onBackPressed();
        } else if (view == binding.layoutCart) {
            CartActivity.start(this);
        } else if (view == binding.buttonAddCart) {
            if (productModel.getStock() > 0) {
                cartViewModel.insertOrUpdateStock(new CartModel(productModel), eventListener);
            } else {
                UtilsDialog.showToast(this, getString(R.string.message_no_stock));
            }
        } else if (view == binding.content.iconLike) {
            wishlistViewModel.toggle(new WishlistModel(productModel));
        } else if (view == binding.imageProduct) {
            ImagePreviewActivity.start(this, productModel);
        }
    }

    private final CartRepository.EventListener eventListener = new CartRepository.EventListener() {
        @Override
        public void onInsert(CartModel cartModel) {
            if (binding == null) {
                return;
            }
            if (cartModel != null) {
                //log analytics
                analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART,
                        UtilsMain.getAnalyticsCart(cartModel, true));
            }
        }

        @Override
        public void onDelete(CartModel cartModel) {

        }

        @Override
        public void onUpdate(CartModel cartModel) {

        }
    };
}