package com.rikyahmadfathoni.test.opaku.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityCartBinding;
import com.rikyahmadfathoni.test.opaku.fragment.CartFragment;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(Activity activity) {
        if (activity != null) {
            final Intent intent = new Intent(activity, CartActivity.class);
            activity.startActivity(intent);
        }
    }

    private ActivityCartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindData();
        bindEvent();
    }

    private void bindData() {
        binding.header.textTitle.setText(R.string.title_activity_cart);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, CartFragment.getInstance(false))
                .commit();
    }

    private void bindEvent() {
        binding.header.iconBack.setOnClickListener(this);
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