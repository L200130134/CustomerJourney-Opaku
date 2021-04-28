package com.rikyahmadfathoni.test.opaku.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityImagePreviewBinding;
import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsMain;

public class ImagePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(Activity activity, ProductModel productModel) {
        if (activity != null) {
            final Intent intent = new Intent(activity, ImagePreviewActivity.class);
            intent.putExtra(KEY_PRODUCT_MODEL, productModel);
            activity.startActivity(intent);
        }
    }

    public static final String KEY_PRODUCT_MODEL = "KEY_PRODUCT_MODEL";
    private FirebaseAnalytics analytics;
    private ActivityImagePreviewBinding binding;
    private ProductModel productModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindIntent(getIntent());
        bindObject();
        bindEvent();
        bindData();
    }

    private void bindIntent(Intent intent) {
        if (intent != null) {
            productModel = intent.getParcelableExtra(KEY_PRODUCT_MODEL);
        }
    }

    private void bindObject() {
        analytics = FirebaseAnalytics.getInstance(this);
    }

    private void bindEvent() {
        binding.iconBack.setOnClickListener(this);
    }

    private void bindData() {
        if (productModel == null) {
            return;
        }
        //binding.textTitle.setText(productModel.getName());
        Glide.with(this).load(productModel.getImage())
                .placeholder(R.drawable.placeholder)
                .into(binding.imageProduct);

        //log analytics
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM,
                UtilsMain.getAnalyticsView(productModel));
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.iconBack) {
            super.onBackPressed();
        }
    }
}