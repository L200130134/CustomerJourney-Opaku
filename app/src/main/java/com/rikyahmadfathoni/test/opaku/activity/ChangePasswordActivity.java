package com.rikyahmadfathoni.test.opaku.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityChangePasswordBinding;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.view.ProgressView;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(FragmentActivity activity) {
        if (activity != null) {
            final Intent intent = new Intent(activity, ChangePasswordActivity.class);
            activity.startActivity(intent);
        }
    }

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private ActivityChangePasswordBinding binding;
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindData();
        bindEvent();
    }

    private void bindData() {
        progressView = new ProgressView(this);
        binding.header.textTitle.setText(R.string.title_activity_change_password);
    }

    private void bindEvent() {
        binding.header.iconBack.setOnClickListener(this);
        binding.buttonSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.header.iconBack) {
            super.onBackPressed();
        } else if (view == binding.buttonSave) {
            changePassword();
        }
    }

    private void changePassword() {
        if (binding == null) {
            return;
        }

        final FirebaseUser user = auth.getCurrentUser();
        final String currentPassword = binding.inputConfirmPassword.getText().toString();
        final String newPassword = binding.inputNewPassword.getText().toString();

        if (user == null) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_user));
            return;
        }

        if (currentPassword.length() < 6) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_password));
            return;
        }

        if (newPassword.length() < 6) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_new_password));
            return;
        }

        if (currentPassword.equals(newPassword)) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_password_not_equal));
            return;
        }

        if (progressView != null) {
            progressView.show(binding.getRoot());
        }

        final String email = user.getEmail();

        if (email == null) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_email));
            return;
        }

        auth.signInWithEmailAndPassword(user.getEmail(), currentPassword)
                .addOnSuccessListener(authResult -> user.updatePassword(newPassword).addOnSuccessListener(aVoid -> {
                    if (progressView != null) {
                        progressView.dismiss();
                    }
                    UtilsDialog.showToast(getBaseContext(), getString(R.string.message_success_change_password));
                    finish();
                }).addOnFailureListener(e -> {
                    if (progressView != null) {
                        progressView.dismiss();
                    }
                    UtilsDialog.showToast(getBaseContext(), getString(R.string.message_error_change_password));
                })).addOnFailureListener(e -> {
                    if (progressView != null) {
                        progressView.dismiss();
                    }
                    UtilsDialog.showToast(this, getString(R.string.message_invalid_password));
                });
    }
}