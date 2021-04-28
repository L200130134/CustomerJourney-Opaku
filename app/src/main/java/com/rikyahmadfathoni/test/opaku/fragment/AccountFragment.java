package com.rikyahmadfathoni.test.opaku.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.activity.ChangePasswordActivity;
import com.rikyahmadfathoni.test.opaku.activity.OrderListActivity;
import com.rikyahmadfathoni.test.opaku.activity.PersonalInfoActivity;
import com.rikyahmadfathoni.test.opaku.databinding.FragmentAccountBinding;
import com.rikyahmadfathoni.test.opaku.databinding.FragmentCartBinding;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.CartViewModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;

import java.util.List;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FragmentAccountBinding binding;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindUser();
        bindView();
        bindEvent();
    }

    private void bindView() {
        binding.personalInfo.textTitle.setText(R.string.info_personal);
        binding.personalInfo.textDescription.setText(getString(R.string.info_desc_personal));
        binding.password.textTitle.setText(getString(R.string.info_password));
        binding.password.textDescription.setText(getString(R.string.info_desc_password));
        binding.purchaseList.textTitle.setText(getString(R.string.info_orders));
        binding.purchaseList.textDescription.setText(getString(R.string.info_desc_orders));

        binding.personalInfo.icon.setImageResource(R.drawable.ic_user);
        binding.password.icon.setImageResource(R.drawable.ic_key);
        binding.purchaseList.icon.setImageResource(R.drawable.ic_cash);
    }

    private void bindUser() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            binding.textAccountName.setText(currentUser.getDisplayName());
            binding.textAccountEmail.setText(currentUser.getEmail());
            binding.textAccountName.setVisibility(
                    UtilsString.isEmpty(currentUser.getDisplayName()) ? View.GONE : View.VISIBLE
            );

            final Uri profileUrl = currentUser.getPhotoUrl();

            Glide.with(this).load(profileUrl != null ? profileUrl : R.drawable.account)
                    .circleCrop()
                    .placeholder(R.drawable.account)
                    .into(binding.imageProfile);
        }
    }

    private void bindEvent() {
        binding.personalInfo.getRoot().setOnClickListener(this);
        binding.password.getRoot().setOnClickListener(this);
        binding.purchaseList.getRoot().setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.personalInfo.getRoot()) {
            PersonalInfoActivity.start(getActivity());
        } else if (view == binding.password.getRoot()) {
            ChangePasswordActivity.start(getActivity());
        } else if (view == binding.purchaseList.getRoot()) {
            OrderListActivity.start(getActivity());
        }
    }
}
