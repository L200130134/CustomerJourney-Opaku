package com.rikyahmadfathoni.test.opaku.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.adapter.WishlistAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.FragmentWishlistBinding;
import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.store.model.WishlistModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.WishlistViewModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsThread;

import java.util.List;

public class WishlistFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentWishlistBinding binding;
    private WishlistViewModel wishlistViewModel;
    private WishlistAdapter wishlistAdapter;
    private int maxPosition;
    private int counterPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindHeader();
        bindEvent();

        wishlistViewModel = WishlistViewModel.getInstance(getActivity().getApplication());
    }

    private void bindEvent() {
        binding.swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wishlistAdapter != null) {
            return;
        }
        bindList();
    }

    private void bindHeader() {
        binding.header.textTitle.setText(R.string.title_bottom_wishlist);
    }

    public void bindList() {
        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        wishlistAdapter = new WishlistAdapter(getActivity());
        wishlistAdapter.setPlaceholderMode(true);
        binding.productList.setLayoutManager(llm);
        binding.productList.setAdapter(wishlistAdapter);

        bindLiveDataProduct();
    }

    private void bindLiveDataProduct() {
        wishlistViewModel.getLiveWishlistModel().observe(getViewLifecycleOwner(), this::notifyList);
    }

    private void notifyList(List<WishlistModel> cartModels) {
        final boolean isEmpty = cartModels.isEmpty();
        wishlistAdapter.setPlaceholderMode(false);
        wishlistAdapter.setEmptyMode(isEmpty);
        if (!isEmpty) {
            wishlistAdapter.submitList(cartModels);
        }
        setSwipeRefresing(false);
    }

    private void getProductData() {
        if (wishlistAdapter == null) {
            return;
        }
        final List<WishlistModel> wishlistModels = wishlistAdapter.getWishlistModels();
        if (wishlistModels != null) {
            maxPosition = wishlistModels.size();
            counterPosition = 0;
            for (WishlistModel wishlistModel : wishlistModels) {
                if (wishlistModel == null) {
                    counterPosition ++;
                    continue;
                }
                db.collection("products")
                        .document(wishlistModel.getProductId())
                        .get()
                        .addOnSuccessListener(dssl)
                        .addOnFailureListener(onFailureListener);
            }
            if (counterPosition >= maxPosition) {
                counterPosition = 0;
                setSwipeRefresing(false);
            }
        }
    }

    private final OnSuccessListener<DocumentSnapshot> dssl = new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            counterPosition ++;
            final ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
            if (productModel != null) {
                UtilsThread.runOnThread(() -> wishlistViewModel.update(new WishlistModel(productModel)));
            }
            if (counterPosition >= maxPosition) {
                counterPosition = 0;
                setSwipeRefresing(false);
            }
        }
    };

    private final OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            counterPosition ++;
            if (counterPosition >= maxPosition) {
                counterPosition = 0;
                setSwipeRefresing(false);
            }
        }
    };

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
        if (wishlistAdapter == null) {
            bindList();
            return;
        }
        getProductData();
    }

    @Override
    public void onClick(View view) {

    }
}
