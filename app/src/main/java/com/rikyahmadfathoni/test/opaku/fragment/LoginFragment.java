package com.rikyahmadfathoni.test.opaku.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.FragmentLoginBinding;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;
import com.rikyahmadfathoni.test.opaku.view.ProgressView;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAnalytics analytics;
    private FragmentLoginBinding binding;
    private ProgressView progressView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        analytics = FirebaseAnalytics.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressView = new ProgressView(getContext());

        bindPager();

        binding.header.textTitle.setText(R.string.button_login);
        binding.buttonLogin.setOnClickListener(this);
        binding.buttonRegister.setOnClickListener(this);
    }

    private void bindPager() {
        
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.buttonLogin) {
            loginUser();
        } else if (view == binding.buttonRegister) {
            final Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof AuthFragment) {
                ((AuthFragment)parentFragment).setCurrentPosition(1);
            }
        }
    }

    private void loginUser() {
        final String email = binding.inputEmail.getText().toString();
        final String password = binding.inputPassword.getText().toString();
        if (UtilsString.isEmpty(email)) {
            UtilsDialog.showToast(getContext(), getString(R.string.message_invalid_empty_email));
            return;
        }
        if (!UtilsString.isValidEmail(email)) {
            UtilsDialog.showToast(getContext(), getString(R.string.message_invalid_email));
            return;
        }
        if (password.length() < 6) {
            UtilsDialog.showToast(getContext(), getString(R.string.message_invalid_password));
            return;
        }

        progressView.show(binding.getRoot());

        final Bundle loginEvent = new Bundle();
        loginEvent.putString("email", email);
        loginEvent.putLong("date", System.currentTimeMillis());

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            final FirebaseUser user = authResult.getUser();
            if (user != null) {
                loginEvent.putString("id", user.getUid());
                analytics.logEvent(FirebaseAnalytics.Event.LOGIN, loginEvent);
                UtilsDialog.showToast(getContext(), getString(R.string.message_success_login));
            }
            if (progressView != null) {
                progressView.dismiss();
            }
        }).addOnFailureListener(e -> {
            UtilsDialog.showToast(getContext(), getString(R.string.message_failed_login));
            if (progressView != null) {
                progressView.dismiss();
            }
        });
    }
}
