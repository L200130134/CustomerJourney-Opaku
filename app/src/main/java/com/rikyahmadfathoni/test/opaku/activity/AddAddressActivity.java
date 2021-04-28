package com.rikyahmadfathoni.test.opaku.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityAddAddressBinding;
import com.rikyahmadfathoni.test.opaku.model.AddressModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;
import com.rikyahmadfathoni.test.opaku.view.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(Activity activity, AddressModel addressModel,
                             ArrayList<AddressModel> addressModels) {
        if (activity != null) {
            final Intent intent = new Intent(activity, AddAddressActivity.class);
            intent.putExtra(KEY_ADDRESS_MODEL, addressModel);
            intent.putParcelableArrayListExtra(KEY_ADDRESS_MODELS, addressModels);
            activity.startActivity(intent);
        }
    }

    public static final String KEY_ADDRESS_MODELS = "KEY_ADDRESS_MODELS";
    public static final String KEY_ADDRESS_MODEL = "KEY_ADDRESS_MODEL";

    private static final int UPDATE_TYPE = 1;
    private static final int ADD_TYPE = 2;
    private static final int DELETE_TYPE = 3;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityAddAddressBinding binding;
    private List<AddressModel> addressModels;
    private AddressModel addressModel;
    private ProgressView progressView;
    private boolean isUpdating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindIntent(getIntent());
        bindData();
        bindEvent();
    }

    private void bindIntent(Intent intent) {
        if (intent != null) {
            addressModel = intent.getParcelableExtra(KEY_ADDRESS_MODEL);
            addressModels = intent.getParcelableArrayListExtra(KEY_ADDRESS_MODELS);
            if (addressModels == null) {
                addressModels = new ArrayList<>();
            }
        }
    }

    private void bindData() {
        progressView = new ProgressView(this);
        binding.header.textTitle.setText(R.string.title_activity_add_address);

        if (addressModel != null) {
            binding.header.button.setText(R.string.button_delete);
            binding.inputName.setText(addressModel.getName());
            binding.inputPhone.setText(addressModel.getPhoneNumber());
            binding.inputPostalCode.setText(addressModel.getPostalCode());
            binding.inputAddress.setText(addressModel.getAddress());

            binding.buttonAdd.setText(R.string.button_save_change);
        } else {
            binding.header.button.setVisibility(View.GONE);
        }
    }

    private void bindEvent() {
        binding.header.iconBack.setOnClickListener(this);
        binding.header.button.setOnClickListener(this);
        binding.buttonAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.header.iconBack) {
            super.onBackPressed();
        } else if (view == binding.header.button) {
            updateAddress(DELETE_TYPE);
        } else if (view == binding.buttonAdd) {
            if (addressModel != null) {
                updateAddress(UPDATE_TYPE);
            } else {
                updateAddress(ADD_TYPE);
            }
        }
    }

    private void updateAddress(int type) {
        if (binding == null) {
            return;
        }
        if (addressModels == null) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_address_model));
            return;
        }
        if (type == DELETE_TYPE) {
            if (addressModel != null) {
                addressModels.remove(addressModel);
                notifyDatabase();
                return;
            }
        }

        final AddressModel newAddressModel = new AddressModel();
        final String name = binding.inputName.getText().toString();
        final String phone = binding.inputPhone.getText().toString();
        final String postalCode = binding.inputPostalCode.getText().toString();
        final String address = binding.inputAddress.getText().toString();

        if (UtilsString.isEmpty(name)) {
            UtilsDialog.showToast(this, getString(R.string.message_empty_name));
            return;
        }

        if (UtilsString.isEmpty(phone)) {
            UtilsDialog.showToast(this, getString(R.string.message_empty_phone_number));
            return;
        }

        if (UtilsString.isEmpty(postalCode)) {
            UtilsDialog.showToast(this, getString(R.string.message_empty_postal_code));
            return;
        }

        if (UtilsString.isEmpty(address)) {
            UtilsDialog.showToast(this, getString(R.string.message_empty_address));
            return;
        }

        newAddressModel.setName(name);
        newAddressModel.setPhoneNumber(phone);
        newAddressModel.setPostalCode(postalCode);
        newAddressModel.setAddress(address);

        if (type == UPDATE_TYPE) {
            final int indexOf = addressModels.indexOf(addressModel);
            if (indexOf >= 0) {
                newAddressModel.setId(addressModel.getId());
                addressModels.set(indexOf, newAddressModel);
            }
        } else {
            newAddressModel.setId(String.valueOf(addressModels.size() + 1));
            addressModels.add(newAddressModel);
        }

        notifyDatabase();
    }

    private void notifyDatabase() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_user));
            return;
        }

        if (isUpdating) {
            return;
        }

        isUpdating = true;

        if (progressView != null) {
            progressView.show(binding.getRoot());
        }

        db.collection(Constants.DB_COLLECTION_CUSTOMERS)
                .document(firebaseUser.getUid())
                .update("addressModels", addressModels).addOnSuccessListener(aVoid -> {
            isUpdating = false;
            if (progressView != null) {
                progressView.dismiss();
            }
            finish();
        }).addOnFailureListener(e -> {
            if (progressView != null) {
                progressView.dismiss();
            }
            isUpdating = false;
        });
    }
}