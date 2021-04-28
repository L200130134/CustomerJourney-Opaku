package com.rikyahmadfathoni.test.opaku.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.adapter.SimpleProductAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityOrderDetailsBinding;
import com.rikyahmadfathoni.test.opaku.databinding.MergeProductListBinding;
import com.rikyahmadfathoni.test.opaku.model.OrderModel;
import com.rikyahmadfathoni.test.opaku.model.ProductOrderModel;
import com.rikyahmadfathoni.test.opaku.model.RefundModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDate;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsMain;
import com.rikyahmadfathoni.test.opaku.utils.UtilsNumber;
import com.rikyahmadfathoni.test.opaku.view.ProgressView;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(Activity activity, OrderModel orderModel) {
        if (activity != null) {
            final Intent intent = new Intent(activity, OrderDetailsActivity.class);
            intent.putExtra(KEY_ORDER_MODEL, orderModel);
            activity.startActivity(intent);
        }
    }

    private static final String KEY_ORDER_MODEL = "KEY_ORDER_MODEL";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAnalytics analytics;
    private ActivityOrderDetailsBinding binding;
    private MergeProductListBinding productListBinding;
    private OrderModel orderModel;
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        productListBinding = MergeProductListBinding.bind(binding.contentList);
        setContentView(binding.getRoot());


        bindIntent(getIntent());
        bindObject();
        bindData();
        bindOrderDetails();
        bindProductPrice();
        bindEvent();
    }

    private void bindEvent() {
        binding.header.iconBack.setOnClickListener(this);
        binding.header.button.setOnClickListener(this);
    }

    private void bindIntent(Intent intent) {
        if (intent != null) {
            orderModel = intent.getParcelableExtra(KEY_ORDER_MODEL);
        }
    }

    private void bindObject() {
        analytics = FirebaseAnalytics.getInstance(this);
        progressView = new ProgressView(this);
    }

    private void bindData() {
        binding.header.textTitle.setText(R.string.title_activity_order_details);
        binding.header.button.setText(R.string.info_refund);

        if (orderModel != null) {
            binding.header.button.setVisibility(
                    UtilsMain.isRefundable(orderModel.getStatus()) ? View.VISIBLE : View.GONE);
        }
    }

    private void bindOrderDetails() {
        binding.titleOrder.icon.setImageResource(R.drawable.ic_about);
        productListBinding.title.icon.setImageResource(R.drawable.ic_sheets);

        binding.titleOrder.text.setText(R.string.title_info_order);

        binding.orderId.textName.setText(R.string.info_order_id);
        binding.customerId.textName.setText(R.string.info_customer_id);
        binding.status.textName.setText(getString(R.string.info_status));
        binding.customerName.textName.setText(R.string.info_name);
        binding.customerPhone.textName.setText(R.string.info_phone);
        binding.customerAddress.textName.setText(R.string.info_address);
        binding.customerPayment.textName.setText(R.string.info_payment);
        binding.totalWeight.textName.setText(getString(R.string.info_total_weight));
        binding.totalItem.textName.setText(getString(R.string.info_total_item));
        binding.createDate.textName.setText(getString(R.string.info_created));

        binding.orderId.textValue.setText(orderModel.getId());
        binding.customerId.textValue.setText(orderModel.getCustomerId());
        binding.status.textValue.setText(UtilsMain.getOrderStatus(orderModel.getStatus()));
        binding.customerName.textValue.setText(orderModel.getCustomerName());
        binding.customerPhone.textValue.setText(orderModel.getCustomerPhone());
        binding.customerAddress.textValue.setText(orderModel.getDestinationAddress());
        binding.customerPayment.textValue.setText(orderModel.getPaymentService());
        binding.totalWeight.textValue.setText(String.format("%s %s", orderModel.getTotalWeight(), getString(R.string.title_gram)));
        binding.totalItem.textValue.setText(String.format("%s %s", orderModel.getTotalItem(), getString(R.string.info_item)));
        binding.createDate.textValue.setText(UtilsDate.getStringDate(orderModel.getCreateDate().toDate().getTime()));

        if (orderModel.getStatus() == Constants.ORDER_STATUS_WAITING_FOR_PAYMENT) {
            binding.expired.textName.setText(getString(R.string.info_expired_payment));
            binding.expired.getRoot().setVisibility(View.VISIBLE);
            countdownTime();
        }
    }

    private void bindProductPrice() {
        productListBinding.message.textValue.setClickable(false);
        productListBinding.message.textValue.setFocusable(false);
        productListBinding.message.textValue.setEnabled(false);

        productListBinding.title.text.setText(R.string.info_detail_produk);
        productListBinding.message.textName.setText(getString(R.string.info_message));
        productListBinding.message.textValue.setHint(getString(R.string.hint_leave_message));
        productListBinding.productPrice.textName.setText(getString(R.string.info_sub_product_price));
        productListBinding.shipmentPrice.textName.setText(getString(R.string.info_sub_payment_price));
        productListBinding.totalPrice.textName.setText(getString(R.string.info_total_price));
        productListBinding.message.textValue.setHint(null);
        productListBinding.message.textValue.setText(orderModel.getCustomerMessage());

        productListBinding.productPrice.textValue.setText(UtilsNumber.formatRupiah(orderModel.getProductPrice()));
        productListBinding.shipmentPrice.textValue.setText(UtilsNumber.formatRupiah(orderModel.getShipmentPrice()));
        productListBinding.totalPrice.textValue.setText(UtilsNumber.formatRupiah(orderModel.getTotalPrice()));

        SimpleProductAdapter simpleProductAdapter = new SimpleProductAdapter(this);
        productListBinding.listItem.setAdapter(simpleProductAdapter);

        simpleProductAdapter.submitList(orderModel.getProducts());
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.header.iconBack) {
            super.onBackPressed();
        } else if (view == binding.header.button) {
            refund();
        }
    }

    private void refund() {
        if (progressView != null) {
            progressView.show(binding.getRoot());
        }
        final RefundModel refundModel = new RefundModel(orderModel);

        WriteBatch batch = db.batch();

        batch.set(db.collection(Constants.DB_COLLECTION_REFUND).document(), refundModel);
        batch.update(db.collection(Constants.DB_COLLECTION_ORDERS).document(orderModel.getId()),
                "status", Constants.ORDER_STATUS_REFUND);

        batch.commit().addOnCompleteListener(task -> {
            UtilsDialog.showToast(getBaseContext(),
                    getString(R.string.info_success_refund));

            updateStockProduct();

            if (progressView != null) {
                progressView.dismiss();
            }

            //log analytics
            analytics.logEvent(FirebaseAnalytics.Event.REFUND,
                    UtilsMain.getAnalyticsRefund(orderModel));

            finish();
        }).addOnFailureListener(e -> {
            if (progressView != null) {
                progressView.dismiss();
            }
            UtilsDialog.showToast(getBaseContext(),
                    getString(R.string.info_failed_refund));
        });
    }

    private void updateStockProduct() {
        for (ProductOrderModel pom : orderModel.getProducts()) {
            final DocumentReference docRef = db.collection(Constants.DB_COLLECTION_PRODUCTS)
                    .document(pom.getProductId());
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                final DocumentSnapshot snapshot = transaction.get(docRef);
                long stock = snapshot.getLong("stock") + pom.getProductAmount();
                transaction.update(docRef, "stock", stock);
                return null;
            }).addOnSuccessListener(aVoid -> {

            }).addOnFailureListener(e -> {

            });
        }
    }

    private void countdownTime() {
        final long time = orderModel.getCreateDate().toDate().getTime() + Constants.MAX_ON_PAYMENT_TIME;
        final long counterTime = UtilsDate.getTimeLeft(time);

        if (counterTime >= 0) {
            new CountDownTimer(counterTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    if (binding == null) {
                        return;
                    }
                    binding.expired.textValue.setText(UtilsDate.parseTime(millisUntilFinished));
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    binding.expired.textValue.setText(R.string.info_expired);
                }

            }.start();
        } else {
            binding.expired.textValue.setText(R.string.info_expired);
        }
    }
}