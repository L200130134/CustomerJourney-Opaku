package com.rikyahmadfathoni.test.opaku.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityCreditCardBinding;
import com.rikyahmadfathoni.test.opaku.model.CreditCardModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;

public class CardSelectorActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_CARD_MODEL = "KEY_CARD_MODEL";

    private ActivityCreditCardBinding binding;
    private final CreditCardModel creditCardModel = new CreditCardModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreditCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindData();
        bindEvent();
    }

    private void bindData() {
        binding.header.textTitle.setText(R.string.title_activity_credit_card);
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
            selectCard();
        }
    }

    private void selectCard() {
        final String cardName = binding.inputHolderName.getText().toString();
        final String cardNumber = binding.inputNumber.getText().toString();
        final String cardExpired = binding.inputExpired.getText().toString();
        final String cvv = binding.inputCvv.getText().toString();

        if (UtilsString.isEmpty(cardName)) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_card_name));
            return;
        }

        if (UtilsString.isEmpty(cardNumber)) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_card_number));
            return;
        }

        if (UtilsString.isEmpty(cardExpired)) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_card_expired));
            return;
        }

        if (UtilsString.isEmpty(cvv)) {
            UtilsDialog.showToast(this, getString(R.string.message_invalid_cvv));
            return;
        }

        creditCardModel.setCardHolderName(cardName);
        creditCardModel.setCardNumber(cardNumber);
        creditCardModel.setCardExpired(cardExpired);
        creditCardModel.setPaymentId(Constants.ID_PAYMENT_CARD);
        creditCardModel.setCvv(cvv);

        final Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_CARD_MODEL, creditCardModel);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}