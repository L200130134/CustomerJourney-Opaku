package com.rikyahmadfathoni.test.opaku.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.activity.CheckoutActivity;
import com.rikyahmadfathoni.test.opaku.activity.MainActivity;
import com.rikyahmadfathoni.test.opaku.activity.ProductActivity;
import com.rikyahmadfathoni.test.opaku.adapter.CartAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.FragmentCartBinding;
import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.CartViewModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsMain;
import com.rikyahmadfathoni.test.opaku.utils.UtilsNumber;
import com.rikyahmadfathoni.test.opaku.utils.UtilsThread;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public static CartFragment getInstance(boolean isShowTitle) {
        final CartFragment cartFragment = new CartFragment();
        final Bundle bundle = new Bundle();
        bundle.putBoolean(CartFragment.KEY_SHOW_TITLE, isShowTitle);
        cartFragment.setArguments(bundle);
        return cartFragment;
    }

    public static final String KEY_SHOW_TITLE = "KEY_SHOW_TITLE";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAnalytics analytics;
    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private CartAdapter cartAdapter;
    private List<CartModel> cartModels;
    private List<ProductModel> productModels = new ArrayList<>();
    private int maxPosition;
    private int counterPosition;
    private boolean isShowTitle = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        analytics = FirebaseAnalytics.getInstance(requireContext());
        cartViewModel = CartViewModel.getInstance(getActivity().getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindBundle(getArguments());
        bindHeader();
        bindEvent();
        bindInitialList();
    }

    private void bindBundle(Bundle arguments) {
        if (arguments != null) {
            isShowTitle = arguments.getBoolean(KEY_SHOW_TITLE, true);
        }
    }

    private void bindEvent() {
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.buttonCheckout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cartAdapter == null) {
            bindList();
            if (cartViewModel != null) {
                cartViewModel.getLiveCartModel().observe(getViewLifecycleOwner(), observerLiveData);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cartViewModel != null) {
            cartViewModel.getLiveCartModel().removeObserver(observerLiveData);
        }
    }

    private void bindHeader() {
        binding.header.textTitle.setText(R.string.title_bottom_cart);
        binding.header.getRoot().setVisibility(isShowTitle ? View.VISIBLE : View.GONE);
    }

    public void bindList() {
        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        cartAdapter = new CartAdapter();
        cartAdapter.setEventListener(eventListener);
        cartAdapter.setPlaceholderMode(true);
        binding.productList.setLayoutManager(llm);
        binding.productList.setAdapter(cartAdapter);
    }

    private void bindInitialList() {
        UtilsThread.runOnThread(() -> {
            final List<CartModel> cartModels = cartViewModel.getCartModel();
            if (cartModels != null) {
                //log analytics
                analytics.logEvent(FirebaseAnalytics.Event.VIEW_CART,
                        UtilsMain.getAnalyticsCarts(cartModels));
            }
        });
    }

    private void getSubTotal() {
        if (cartAdapter == null) {
            return;
        }
        UtilsThread.runOnThread(() -> {
            if (cartModels != null) {
                long totalPrice = 0;
                int totalItem = 0;
                for (CartModel cartModel : cartModels) {
                    final int selected = cartModel.getProductSelected();
                    totalPrice += (selected * cartModel.getProductPrice());
                    totalItem += selected;
                }
                final long price = totalPrice;
                final long items = totalItem;
                UtilsThread.runOnUiThread(() -> {
                    binding.textTotalPrice.setText(UtilsNumber.formatRupiah(price));
                    binding.textTotalItem.setText(String.format("%s %s", items, getString(R.string.info_item)));
                });
            }
        });
    }

    private final Observer<List<CartModel>> observerLiveData = new Observer<List<CartModel>>() {
        @Override
        public void onChanged(List<CartModel> models) {
            cartModels = models;
            notifyList(cartModels);
        }
    };

    private void notifyList(List<CartModel> cartModels) {
        if (cartAdapter == null) {
            return;
        }
        binding.productList.post(() -> {
            final boolean isEmpty = cartModels.isEmpty();
            cartAdapter.setPlaceholderMode(false);
            cartAdapter.setEmptyMode(isEmpty);
            binding.footer.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            if (!isEmpty) {
                cartAdapter.submitList(cartModels);
                getSubTotal();
            }
            setSwipeRefresing(false);
        });
    }

    private void getProductData() {
        if (cartAdapter == null) {
            return;
        }
        final List<CartModel> cartModels = cartAdapter.getCartModels();
        if (cartModels != null) {
            productModels = new ArrayList<>();
            maxPosition = cartModels.size();
            counterPosition = 0;
            for (CartModel cartModel : cartModels) {
                if (cartModel == null) {
                    counterPosition ++;
                    continue;
                }
                db.collection(Constants.DB_COLLECTION_PRODUCTS)
                        .document(cartModel.getProductId())
                        .get()
                        .addOnSuccessListener(dssl)
                        .addOnFailureListener(onFailureListener);
            }
            endGet();
        }
    }

    private final OnSuccessListener<DocumentSnapshot> dssl = new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if (binding == null) {
                return;
            }
            counterPosition ++;
            final ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
            if (productModel != null) {
                productModels.add(productModel);
            }
            endGet();
        }
    };

    private final OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            counterPosition ++;
            endGet();
        }
    };

    private void endGet() {
        if (cartAdapter == null) {
            return;
        }
        cartViewModel.updateBy(productModels);
        if (counterPosition >= maxPosition) {
            counterPosition = 0;
            setSwipeRefresing(false);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setSwipeRefresing(boolean refresing) {
        if (binding != null) {
            if (binding.swipeRefresh.isRefreshing() && refresing) {
                return;
            }
            if (!binding.swipeRefresh.isRefreshing() && !refresing) {
                return;
            }
            binding.swipeRefresh.post(() -> binding.swipeRefresh.setRefreshing(refresing));
        }
    }

    @Override
    public void onRefresh() {
        if (cartAdapter == null) {
            bindList();
            return;
        }
        getProductData();
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.buttonCheckout) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                final Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                intent.putExtra(CheckoutActivity.KEY_MODEL_LIST, new ArrayList<>(cartAdapter.getCartModels()));
                startActivity(intent);
            } else {
                UtilsDialog.showToast(getContext(), "To continue checkout required login");
                final Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity)activity).setCurrentItem(3, true);
                }
            }
        }
    }

    private final CartAdapter.EventListener eventListener = new CartAdapter.EventListener() {
        @Override
        public void onClick(CartModel cartModel) {
            final Context context = getContext();
            if (context instanceof Activity) {
                final Activity activity = (Activity) context;
                final Intent intent = new Intent(activity, ProductActivity.class);
                intent.putExtra(ProductActivity.KEY_PRODUCT_ID, cartModel.getProductId());
                activity.startActivity(intent);
            }
        }

        @Override
        public void onUpdateStock(String productId, int stock) {
            if (cartViewModel != null) {
                cartViewModel.updateProductTotal(stock, productId);
            }
        }

        @Override
        public void onRemoveItem(CartModel cartModel) {
            if (cartViewModel != null) {
                cartViewModel.delete(cartModel);

                //log analytics
                analytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART,
                        UtilsMain.getAnalyticsCart(cartModel, false));
            }
        }
    };
}
