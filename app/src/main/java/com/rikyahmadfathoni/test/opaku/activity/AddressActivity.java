package com.rikyahmadfathoni.test.opaku.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.adapter.AddressAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityAddressBinding;
import com.rikyahmadfathoni.test.opaku.model.AddressModel;
import com.rikyahmadfathoni.test.opaku.model.CustomerModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsList;
import com.rikyahmadfathoni.test.opaku.view.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(FragmentActivity activity) {
        start(activity, null);
    }

    public static void start(FragmentActivity activity, ArrayList<AddressModel> addressModels) {
        if (activity != null) {
            final Intent intent = new Intent(activity, AddressActivity.class);
            intent.putParcelableArrayListExtra(KEY_ADDRESS_MODELS, addressModels);
            activity.startActivityForResult(intent, REQUEST_CODE);
        }
    }

    public static final String KEY_ADDRESS_MODELS = "KEY_ADDRESS_MODELS";
    public static final String KEY_ADDRESS_MODEL = "KEY_ADDRESS_MODEL";
    public static final int REQUEST_CODE = 200;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityAddressBinding binding;
    private AddressAdapter addressAdapter;
    private List<AddressModel> addressModels;
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindIntent(getIntent());
        bindData();
        bindEvent();
        bindSnapshot();
    }

    private void bindIntent(Intent intent) {
        if (intent != null) {
            addressModels = intent.getParcelableArrayListExtra(KEY_ADDRESS_MODELS);
        }
    }

    private void bindData() {
        binding.header.textTitle.setText(R.string.title_activity_address);
        binding.header.button.setText(getString(R.string.button_add));

        addressAdapter = new AddressAdapter();
        addressAdapter.setEventListener(new AddressAdapter.EventListener() {
            @Override
            public void onClick(AddressModel addressModel, int position) {
                AddAddressActivity.start(
                        AddressActivity.this,
                        addressModel,
                        UtilsList.isEmpty(addressModels) ? null : new ArrayList<>(addressModels)
                );
            }

            @Override
            public void onSelect(AddressModel addressModel) {
                final Intent returnIntent = new Intent();
                returnIntent.putExtra(KEY_ADDRESS_MODEL, addressModel);
                returnIntent.putParcelableArrayListExtra(KEY_ADDRESS_MODELS, new ArrayList<>(addressModels));
                setResult(Activity.RESULT_OK,returnIntent);
                AddressActivity.this.finish();
            }
        });
        addressAdapter.setAddressModels(addressModels);
        binding.list.setAdapter(addressAdapter);

        notifyUpdate();
        getData();
    }

    private void bindEvent() {
        binding.header.button.setOnClickListener(this);
        binding.header.iconBack.setOnClickListener(this);
    }

    private void bindSnapshot() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection(Constants.DB_COLLECTION_CUSTOMERS).document(user.getUid()).addSnapshotListener((value, error) -> {
                if (value != null) {
                    final CustomerModel customerModel = value.toObject(CustomerModel.class);
                    if (customerModel != null) {
                        addressModels = UtilsList.nonNull(customerModel.getAddressModels());
                        addressAdapter.setAddressModels(addressModels);
                        addressAdapter.notifyDataSetChanged();
                        notifyUpdate();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.header.iconBack) {
            onBackPressed();
        } else if (view == binding.header.button) {
            AddAddressActivity.start(
                    this,
                    null,
                    UtilsList.isEmpty(addressModels) ? null : new ArrayList<>(addressModels)
            );
        }
    }

    private void notifyUpdate() {
        if (binding != null) {
            binding.contentEmpty.getRoot().setVisibility(
                    UtilsList.isEmpty(addressModels) ? View.VISIBLE : View.GONE);
        }
    }

    private void getData() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final boolean isEmptyData = UtilsList.isEmpty(addressModels);

            if (isEmptyData) {
                if (progressView == null) {
                    progressView = new ProgressView(this);
                }
                progressView.show(binding.getRoot());
                db.collection(Constants.DB_COLLECTION_CUSTOMERS)
                        .document(user.getUid())
                        .get().addOnSuccessListener(documentSnapshot -> {
                            final CustomerModel cm = documentSnapshot.toObject(CustomerModel.class);
                            if (cm != null) {
                                final List<AddressModel> addressModels = cm.getAddressModels();
                                if (addressModels != null) {
                                    addressAdapter.setAddressModels(addressModels);
                                    addressAdapter.notifyDataSetChanged();
                                }
                                notifyUpdate();
                            }
                            if (progressView != null) {
                                progressView.dismiss();
                            }
                        }).addOnFailureListener(e -> {
                            if (progressView != null) {
                                progressView.dismiss();
                            }
                            UtilsDialog.showToast(getBaseContext(), getString(R.string.message_error_see_data));
                        });
            }
        }
    }
}