package com.rikyahmadfathoni.test.opaku.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.activity.CartActivity;
import com.rikyahmadfathoni.test.opaku.activity.ProductActivity;
import com.rikyahmadfathoni.test.opaku.adapter.ProductAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.FragmentDiscoverBinding;
import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.CartViewModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsMain;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAnalytics analytics;
    private FragmentDiscoverBinding binding;
    private ProductAdapter productAdapter;
    private String lastCategoryId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        analytics = FirebaseAnalytics.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindHeader();
        bindList();
        bindLiveData();
        bindEvent();
    }

    private void bindEvent() {
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.layoutCart.setOnClickListener(this);

        productAdapter.setEventListener(new ProductAdapter.EventListener() {
            @Override
            public void onFilterClick(View view, String id) {
                getProductData(id);
            }

            @Override
            public void onClick(ProductModel productModel, int position) {
                final Context context = getContext();
                if (context instanceof Activity) {
                    final Activity activity = (Activity) context;
                    ProductActivity.start(activity, productModel);

                    //log analytics
                    analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM,
                            UtilsMain.getAnalyticsSelect(productModel));
                }
            }
        });
    }

    private void bindHeader() {

    }

    private void bindList() {
        final GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        glm.setSpanSizeLookup(sizeLookup);
        productAdapter = new ProductAdapter();
        binding.productList.setLayoutManager(glm);
        binding.productList.setAdapter(productAdapter);

        getProductData(null);
    }

    @SuppressWarnings("SameParameterValue")
    private void setRefreshing(boolean refresh) {
        if (binding != null) {
            if (refresh && binding.swipeRefresh.isRefreshing()) {
                return;
            }
            if (!refresh && !binding.swipeRefresh.isRefreshing()) {
                return;
            }
            binding.swipeRefresh.post(() -> binding.swipeRefresh.setRefreshing(refresh));
        }
    }

    private void getProductData(String categoryId) {
        setRefreshing(false);
        productAdapter.setEnableMessage(false);
        productAdapter.setModels(new ArrayList<>());
        productAdapter.notifyDataSetChanged();
        getQuery(categoryId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final QuerySnapshot qs = task.getResult();
                if (qs != null) {
                    lastCategoryId = categoryId;
                    notifyAdapter(qs);
                }
            } else {
                UtilsDialog.showToast(getContext(), "Can't get data");
            }
        });
    }

    private Query getQuery(String categoryId) {
        return categoryId != null ? db.collection(Constants.DB_COLLECTION_PRODUCTS)
                .whereEqualTo("idCategory", categoryId) : db.collection(Constants.DB_COLLECTION_PRODUCTS);
    }

    private void notifyAdapter(QuerySnapshot qs) {
        final List<ProductModel> productModels = new ArrayList<>();
        final List<DocumentSnapshot> dsList = qs.getDocuments();
        for (DocumentSnapshot ds : dsList) {
            final String id = ds.getId();
            final ProductModel productModel = ds.toObject(ProductModel.class);
            if (productModel != null) {
                productModel.setId(id);
                productModels.add(productModel);
            }
        }
        productAdapter.setEnableMessage(productModels.isEmpty());
        productAdapter.setModels(productModels);
        productAdapter.notifyDataSetChanged();

        //log analytics
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST,
                UtilsMain.getAnalyticsProducts(productModels));
    }

    private final GridLayoutManager.SpanSizeLookup sizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return position == 0 ? 2 : productAdapter.isEnableMessage() ? 2 : 1;
        }
    };

    private void bindLiveData() {
        CartViewModel cartViewModel = CartViewModel.getInstance(requireActivity().getApplication());
        cartViewModel.getLiveCartModel().observe(getViewLifecycleOwner(), observerCartModel);
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

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.layoutCart) {
            CartActivity.start(getActivity());
        }
    }

    @Override
    public void onRefresh() {
        if (productAdapter != null) {
            if (lastCategoryId == null) {
                productAdapter.setResetFilter(true);
            }
            getProductData(lastCategoryId);
        }
    }
}
