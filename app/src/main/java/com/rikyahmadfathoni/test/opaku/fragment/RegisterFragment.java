package com.rikyahmadfathoni.test.opaku.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ContentPagerBinding;
import com.rikyahmadfathoni.test.opaku.databinding.FragmentRegisterBinding;
import com.rikyahmadfathoni.test.opaku.model.CustomerModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.CartViewModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;
import com.rikyahmadfathoni.test.opaku.view.ProgressView;

import java.util.Objects;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAnalytics analytics;
    private FragmentRegisterBinding binding;
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
        binding = FragmentRegisterBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressView = new ProgressView(getContext());

        bindPager();

        binding.header.textTitle.setText(R.string.button_register);
        binding.buttonRegister.setOnClickListener(this);
        binding.buttonLogin.setOnClickListener(this);
    }

    private void bindPager() {

    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.buttonRegister) {
            registerUser();
        } else if (view == binding.buttonLogin) {
            final Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof AuthFragment) {
                ((AuthFragment)parentFragment).setCurrentPosition(0);
            }
        }
    }

    private void registerUser() {
        final String email = binding.inputEmail.getText().toString();
        final String password = binding.inputPassword.getText().toString();
        final String passwordCheck = binding.inputPasswordCheck.getText().toString();
        if (UtilsString.isEmpty(email)) {
            UtilsDialog.showToast(getContext(), getString(R.string.message_invalid_empty_email));
            return;
        }
        if (!UtilsString.isValidEmail(email)) {
            UtilsDialog.showToast(getContext(), getString(R.string.message_invalid_empty_email));
            return;
        }
        if (!password.equals(passwordCheck)) {
            UtilsDialog.showToast(getContext(), getString(R.string.message_invalid_password_checker));
            return;
        }
        if (password.length() < 6) {
            UtilsDialog.showToast(getContext(), getString(R.string.message_invalid_password));
            return;
        }

        progressView.show(binding.getRoot());

        final Bundle registerEvent = new Bundle();
        registerEvent.putString("email", email);
        registerEvent.putLong("date", System.currentTimeMillis());

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            final FirebaseUser user = authResult.getUser();
            if (user != null) {
                registerEvent.putString("id", user.getUid());
                final CustomerModel customerModel = new CustomerModel();
                customerModel.setEmail(email);
                customerModel.setId(user.getUid());
                db.collection("customers").document(user.getUid()).set(customerModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (progressView != null) {
                            progressView.dismiss();
                        }
                    }
                }).addOnFailureListener(e -> {
                    if (progressView != null) {
                        progressView.dismiss();
                    }
                });
                analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, registerEvent);
            }
            UtilsDialog.showToast(getContext(), getString(R.string.message_success_register));
        }).addOnFailureListener(e -> {
            UtilsDialog.showToast(getContext(), getString(R.string.message_failed_register));
            if (progressView != null) {
                progressView.dismiss();
            }
        });
    }
}
