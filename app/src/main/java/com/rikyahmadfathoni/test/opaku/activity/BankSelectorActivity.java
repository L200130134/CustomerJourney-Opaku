package com.rikyahmadfathoni.test.opaku.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.adapter.BankAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityBankSelectorBinding;
import com.rikyahmadfathoni.test.opaku.model.BankModel;
import com.rikyahmadfathoni.test.opaku.model.PaymentModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;

import java.util.List;

public class BankSelectorActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_BANK_MODEL = "KEY_BANK_MODEL";
    public static final String KEY_BANK_ID = "KEY_BANK_ID";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityBankSelectorBinding binding;
    private BankAdapter bankAdapter;
    private String bankId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBankSelectorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindIntent(getIntent());
        bindData();
        getData();
    }

    private void bindIntent(Intent intent) {
        if (intent != null) {
            bankId = intent.getStringExtra(KEY_BANK_ID);
        }
    }

    private void bindData() {
        bankAdapter = new BankAdapter();
        bankAdapter.setEventListener(eventListener);
        binding.list.setItemAnimator(null);
        binding.list.setAdapter(bankAdapter);
        binding.buttonSelect.setEnabled(!UtilsString.isEmpty(bankId));

        binding.header.textTitle.setText(R.string.title_activity_bank_account);
        binding.header.iconBack.setOnClickListener(this);
        binding.buttonSelect.setOnClickListener(this);
    }

    private void getData() {
        db.collection(Constants.DB_COLLECTION_PAYMENT)
                .document(Constants.ID_PAYMENT_BANK)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    final PaymentModel paymentModel = documentSnapshot.toObject(PaymentModel.class);
                    if (paymentModel == null) {
                        return;
                    }
                    final List<BankModel> bankModels = paymentModel.getBankList();
                    if (bankModels != null) {
                        bankAdapter.setBankModels(bankModels);
                        bankAdapter.setLastPositionBy(bankId);
                        bankAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (bankAdapter == null) {
            return;
        }
        if (view == binding.header.iconBack) {
            super.onBackPressed();
        }
        if (view == binding.buttonSelect) {
            final BankModel bankModel = bankAdapter.getItem(bankAdapter.getLastPositionSelected());
            if (bankModel != null) {
                final Intent returnIntent = new Intent();
                returnIntent.putExtra(KEY_BANK_MODEL, bankModel);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    private final BankAdapter.EventListener eventListener = new BankAdapter.EventListener() {
        @Override
        public void onClick(BankModel bankModel, int position) {
            if (binding == null) {
                return;
            }
            if (bankAdapter == null) {
                return;
            }
            final int lastPosition = bankAdapter.getLastPositionSelected();
            if (lastPosition >= 0) {
                bankAdapter.notifyItemChanged(lastPosition);
            }
            binding.buttonSelect.setEnabled(true);
        }
    };
}