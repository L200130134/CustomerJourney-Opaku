package com.rikyahmadfathoni.test.opaku.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityPersonalInfoBinding;
import com.rikyahmadfathoni.test.opaku.model.AddressModel;
import com.rikyahmadfathoni.test.opaku.model.CustomerModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsList;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;
import com.rikyahmadfathoni.test.opaku.view.ProgressView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(FragmentActivity activity) {
        if (activity != null) {
            final Intent intent = new Intent(activity, PersonalInfoActivity.class);
            activity.startActivity(intent);
        }
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityPersonalInfoBinding binding;
    private ProgressView progressView;
    private CustomerModel customerModel;
    private List<AddressModel> addressModels;
    private AddressModel addressModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindHeader();
        bindData();
        bindEvent();

        getUserData();
    }

    private void bindEvent() {
        binding.name.getRoot().setOnClickListener(this);
        binding.email.getRoot().setOnClickListener(this);
        binding.phone.getRoot().setOnClickListener(this);
        binding.address.getRoot().setOnClickListener(this);
        binding.header.iconBack.setOnClickListener(this);
        binding.buttonSave.setOnClickListener(this);
    }

    private void getUserData() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection(Constants.DB_COLLECTION_CUSTOMERS).whereEqualTo("id", user.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                final List<CustomerModel> customerModels = queryDocumentSnapshots.toObjects(CustomerModel.class);

                if (customerModels.size() == 1) {
                    customerModel = customerModels.get(0);
                    bindValue();
                }
            }).addOnFailureListener(e -> {

            });
        }
    }

    private void bindValue() {
        if (customerModel == null) {
            return;
        }
        binding.name.textDescription.setText(customerModel.getName());
        binding.email.textDescription.setText(customerModel.getEmail());
        binding.phone.textDescription.setText(customerModel.getPhoneNumber());
        binding.address.textDescription.setText(getPrimaryAddress());
        binding.textName.setText(customerModel.getName());
        binding.textEmail.setText(customerModel.getEmail());

        binding.textName.setVisibility(
                UtilsString.isEmpty(customerModel.getName()) ? View.GONE : View.VISIBLE
        );

        final String photoUrl = customerModel.getPhotoUrl();

        Glide.with(this).load(photoUrl != null ? photoUrl : R.drawable.account)
                .circleCrop()
                .placeholder(R.drawable.account)
                .into(binding.imageProfile);
    }

    private String getPrimaryAddress() {
        if (customerModel != null) {
            final List<AddressModel> addressModels = customerModel.getAddressModels();
            if (!UtilsList.isEmpty(addressModels)) {
                for (AddressModel addressModel : addressModels) {
                    if (addressModel.getId().equals(customerModel.getPrimaryAddressId())) {
                        return addressModel.getFullAddress();
                    }
                }
                return addressModels.get(0).getFullAddress();
            }
        }
        return null;
    }

    private void bindData() {
        progressView = new ProgressView(this);

        binding.name.textTitle.setText(R.string.info_name);
        binding.email.textTitle.setText(getString(R.string.info_email));
        binding.phone.textTitle.setText(getString(R.string.info_phone));
        binding.address.textTitle.setText(getString(R.string.info_address));

        binding.name.icon.setVisibility(View.GONE);
        binding.email.icon.setVisibility(View.GONE);
        binding.phone.icon.setVisibility(View.GONE);
        binding.address.icon.setVisibility(View.GONE);
    }

    private void bindHeader() {
        binding.header.textTitle.setText(R.string.info_personal);
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.header.iconBack) {
            super.onBackPressed();
        } else if (view == binding.name.getRoot()) {
            InputActivity.start(this, "Input Name",
                    binding.name.textDescription.getText().toString(), InputActivity.INPUT_TYPE_TEXT);
        } else if (view == binding.email.getRoot()) {
            InputActivity.start(this, "Input Email",
                    binding.email.textDescription.getText().toString(), InputActivity.INPUT_TYPE_EMAIL);
        } else if (view == binding.phone.getRoot()) {
            InputActivity.start(this, "Input Phone",
                    binding.phone.textDescription.getText().toString(), InputActivity.INPUT_TYPE_NUMBER);
        } else if (view == binding.address.getRoot()) {
            if (customerModel != null) {
                final List<AddressModel> addressModels = customerModel.getAddressModels();
                AddressActivity.start(this,
                        UtilsList.isEmpty(addressModels) ? null : new ArrayList<>(addressModels));
            }
        } else if (view == binding.buttonSave) {
            updateProfile();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == InputActivity.REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                if (data != null) {
                    final String value = data.getStringExtra(InputActivity.KEY_INPUT_VALUE);
                    final int inputType = data.getIntExtra(InputActivity.KEY_INPUT_TYPE, InputActivity.INPUT_TYPE_TEXT);
                    if (value != null) {
                        if (binding != null) {
                            if (inputType == InputActivity.INPUT_TYPE_TEXT) {
                                binding.name.textDescription.setText(value);
                            } else if (inputType == InputActivity.INPUT_TYPE_EMAIL) {
                                binding.email.textDescription.setText(value);
                            } else if (inputType == InputActivity.INPUT_TYPE_NUMBER) {
                                binding.phone.textDescription.setText(value);
                            }
                        }
                        inValidateInput();
                    }
                }
            }
        } else if (requestCode == AddressActivity.REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                if (data != null) {
                    addressModel = data.getParcelableExtra(AddressActivity.KEY_ADDRESS_MODEL);
                    addressModels = data.getParcelableArrayListExtra(AddressActivity.KEY_ADDRESS_MODELS);
                    if (addressModel != null) {
                        if (binding != null) {
                            binding.address.textDescription.setText(addressModel.getFullAddress());
                        }
                        inValidateInput();
                    }
                }
            }
        }
    }

    private void inValidateInput() {
        binding.buttonSave.setVisibility(View.VISIBLE);
    }

    private void updateProfile() {
        if (binding == null) {
            return;
        }
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String name = binding.name.textDescription.getText().toString();
            final String email = binding.email.textDescription.getText().toString();
            final String phone = binding.phone.textDescription.getText().toString();
            //final String address = binding.address.textDescription.getText().toString();

            if (UtilsString.isEmpty(name)) {
                UtilsDialog.showToast(this, "Nama tidak boleh kosong");
                return;
            }

            if (UtilsString.isEmpty(email)) {
                UtilsDialog.showToast(this, "Email tidak boleh kosong");
                return;
            }

            if (UtilsString.isEmpty(phone)) {
                UtilsDialog.showToast(this, "Nomor telepon tidak boleh kosong");
                return;
            }

            final Map<String, Object> map = new HashMap<>();

            map.put("name", name);
            map.put("email", email);
            map.put("phoneNumber", phone);

            if (addressModels != null) {
                map.put("addressModels", addressModels);
            }

            if (addressModel != null) {
                map.put("primaryAddressId", addressModel.getId());
            }

            if (progressView != null) {
                progressView.show(binding.getRoot());
            }

            db.collection(Constants.DB_COLLECTION_CUSTOMERS)
                    .document(user.getUid())
                    .update(map).addOnSuccessListener(aVoid -> {
                        if (progressView != null) {
                            progressView.dismiss();
                        }
                        finish();
                    }).addOnFailureListener(e -> {
                        if (progressView != null) {
                            progressView.dismiss();
                        }
                        UtilsDialog.showToast(getBaseContext(), "Gagal mengupdate data profile");
                    });
        }
    }
}