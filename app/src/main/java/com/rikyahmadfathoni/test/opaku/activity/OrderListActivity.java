package com.rikyahmadfathoni.test.opaku.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.adapter.OrderAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityOrderBinding;
import com.rikyahmadfathoni.test.opaku.model.OrderModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsList;
import com.rikyahmadfathoni.test.opaku.view.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(FragmentActivity activity) {
        if (activity != null) {
            final Intent intent = new Intent(activity, OrderListActivity.class);
            activity.startActivity(intent);
        }
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressView progressView;
    private ActivityOrderBinding binding;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindData();
        bindList();
        bindEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void bindData() {
        progressView = new ProgressView(this);
        binding.header.textTitle.setText(R.string.info_orders);
    }

    private void bindList() {
        orderAdapter = new OrderAdapter();
        binding.list.setAdapter(orderAdapter);
    }

    private void bindEvent() {
        binding.header.iconBack.setOnClickListener(this);
    }

    private void getData() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            progressView.show(binding.getRoot());
            db.collection(Constants.DB_COLLECTION_ORDERS)
                    .whereEqualTo("customerId", user.getUid())
                    .get()
                    .addOnSuccessListener(qds -> {
                        final List<DocumentSnapshot> dss = qds.getDocuments();
                        final List<OrderModel> orderModels = new ArrayList<>();
                        for (DocumentSnapshot ds : dss) {
                            final OrderModel orderModel = ds.toObject(OrderModel.class);
                            if (orderModel != null) {
                                orderModel.setId(ds.getId());
                                orderModels.add(orderModel);
                            }
                        }
                        orderAdapter.setOrderModels(orderModels);
                        orderAdapter.notifyDataSetChanged();
                        binding.contentEmpty.getRoot().setVisibility(
                                UtilsList.isEmpty(orderModels) ? View.VISIBLE : View.GONE
                        );
                        if (progressView != null) {
                            progressView.dismiss();
                        }
                    }).addOnFailureListener(e -> {
                        if (progressView != null) {
                            progressView.dismiss();
                        }
                        UtilsDialog.showToast(getBaseContext(), "Gagal, tidak dapat melihat data.");
                    });
        }
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.header.iconBack) {
            super.onBackPressed();
        }
    }
}