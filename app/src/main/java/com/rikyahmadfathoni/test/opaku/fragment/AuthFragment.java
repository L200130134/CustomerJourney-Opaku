package com.rikyahmadfathoni.test.opaku.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.adapter.FragmentAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.ContentPagerBinding;
import com.rikyahmadfathoni.test.opaku.model.FragmentModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.CartViewModel;

public class AuthFragment extends Fragment {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private ContentPagerBinding binding;
    private AccountFragment accountFragment;
    private RegisterFragment registerFragment;
    private LoginFragment loginFragment;
    private FragmentAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ContentPagerBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindPager();

        auth.addAuthStateListener(authStateListener);
    }

    private void bindPager() {
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        accountFragment = new AccountFragment();
        adapter = new FragmentAdapter(this);
        adapter.addFragment(loginFragment)
                .addFragment(registerFragment)
                .addFragment(accountFragment);

        binding.fragmentPager.setOffscreenPageLimit(3);
        binding.fragmentPager.setUserInputEnabled(false);
        binding.fragmentPager.setAdapter(adapter);
    }

    public void setCurrentPosition(int position) {
        if (binding != null) {
            binding.fragmentPager.setCurrentItem(position, false);
        }
    }

    private final FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (binding == null) {
                return;
            }
            if (adapter == null) {
                return;
            }
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            binding.fragmentPager.setCurrentItem(user != null ? 2 : 0, false);
        }
    };
}
