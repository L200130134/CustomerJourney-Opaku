package com.rikyahmadfathoni.test.opaku.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.adapter.FragmentAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityMainBinding;
import com.rikyahmadfathoni.test.opaku.fragment.AuthFragment;
import com.rikyahmadfathoni.test.opaku.fragment.CartFragment;
import com.rikyahmadfathoni.test.opaku.fragment.DiscoverFragment;
import com.rikyahmadfathoni.test.opaku.fragment.WishlistFragment;

public class MainActivity extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ActivityMainBinding binding;
    private FirebaseUser currentUser;
    private int lastPagerPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = mAuth.getCurrentUser();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindPager();
        bindFooter();

        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }

    private final FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null && binding != null) {
                binding.fragmentPager.setCurrentItem(3, false);
            }
        }
    };

    private void bindPager() {
        FragmentAdapter fragmentAdapter = new FragmentAdapter(this);
        fragmentAdapter.addFragment(new DiscoverFragment())
                .addFragment(new WishlistFragment())
                .addFragment(new CartFragment())
                .addFragment(new AuthFragment());
        binding.fragmentPager.setUserInputEnabled(false);
        binding.fragmentPager.setAdapter(fragmentAdapter);
        binding.fragmentPager.setOffscreenPageLimit(fragmentAdapter.getItemCount());
        binding.fragmentPager.registerOnPageChangeCallback(onPageChangeCallback);
    }

    private void bindFooter() {
        binding.buttonDiscover.icon.setImageResource(R.drawable.ic_cloth);
        binding.buttonLike.icon.setImageResource(R.drawable.ic_wishlist);
        binding.buttonAccount.icon.setImageResource(R.drawable.ic_user);
        binding.buttonCart.icon.setImageResource(R.drawable.ic_cart);

        binding.buttonDiscover.text.setText(R.string.title_bottom_discover);
        binding.buttonLike.text.setText(getString(R.string.title_bottom_wishlist));
        binding.buttonCart.text.setText(getString(R.string.title_bottom_cart));
        binding.buttonAccount.text.setText(getString(R.string.title_bottom_account));

        binding.buttonDiscover.getRoot().setOnClickListener(bottomClickListener);
        binding.buttonLike.getRoot().setOnClickListener(bottomClickListener);
        binding.buttonAccount.getRoot().setOnClickListener(bottomClickListener);
        binding.buttonCart.getRoot().setOnClickListener(bottomClickListener);
    }

    private final View.OnClickListener bottomClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (binding == null) {
                return;
            }
            if (view == binding.buttonDiscover.getRoot()) {
                binding.fragmentPager.setCurrentItem(0, false);
            } else if (view == binding.buttonLike.getRoot()) {
                binding.fragmentPager.setCurrentItem(1, false);
            } else if (view == binding.buttonCart.getRoot()) {
                binding.fragmentPager.setCurrentItem(2, false);
            } else if (view == binding.buttonAccount.getRoot()) {
                binding.fragmentPager.setCurrentItem(3, false);
            }
        }
    };

    private final ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            setSelectedBottom(lastPagerPosition, false);
            setSelectedBottom(position, true);
            lastPagerPosition = position;
        }
    };

    private void setSelectedBottom(int position, boolean isSelected) {
        if (binding == null) {
            return;
        }
        final View view = binding.contentFooter.getChildAt(position);
        if (view instanceof LinearLayout) {
            final ImageView icon = view.findViewById(R.id.icon);
            final TextView text = view.findViewById(R.id.text);
            if (icon != null && text != null) {
                icon.setSelected(isSelected);
                text.setSelected(isSelected);
            }
        }
    }

    public void setCurrentItem(int position, boolean smoothScroll) {
        if (binding == null) {
            return;
        }
        binding.fragmentPager.setCurrentItem(position, smoothScroll);

    }
}