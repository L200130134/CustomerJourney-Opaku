package com.rikyahmadfathoni.test.opaku.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityCompleteOrderBinding;
import com.rikyahmadfathoni.test.opaku.model.BankModel;
import com.rikyahmadfathoni.test.opaku.model.OrderModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDate;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;

public class BankOrderActivity extends AppCompatActivity {

    public static final String KEY_ORDER_MODEL = "KEY_ORDER_MODEL";
    public static final String KEY_BANK_MODEL = "KEY_BANK_MODEL";

    public static void start(Activity activity, OrderModel orderModel, BankModel bankModel) {
        final Intent intent = new Intent(activity, BankOrderActivity.class);
        intent.putExtra(KEY_ORDER_MODEL, orderModel);
        intent.putExtra(KEY_BANK_MODEL, bankModel);
        activity.startActivity(intent);
    }

    private ActivityCompleteOrderBinding binding;
    private OrderModel orderModel;
    private BankModel bankModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindIntent(getIntent());
        bindData();
    }

    private void bindIntent(Intent intent) {
        if (intent != null) {
            orderModel = intent.getParcelableExtra(KEY_ORDER_MODEL);
            bankModel = intent.getParcelableExtra(KEY_BANK_MODEL);
            if (orderModel == null) {
                UtilsDialog.showToast(this, getString(R.string.message_invalid_order_model));
                finish();
            }
        }
    }

    private void bindData() {
        if (binding == null) {
            return;
        }
        if (orderModel == null) {
            return;
        }
        if (bankModel == null) {
            return;
        }

        binding.textInfo.setText(R.string.title_order_info);
        binding.textDate.setText(R.string.info_expired_order_time);

        binding.orderId.textName.setText(R.string.info_order_id);
        binding.bankHolderName.textName.setText(R.string.info_bank_holder_name);
        binding.bankName.textName.setText(R.string.info_bank_name);
        binding.bankNumber.textName.setText(R.string.info_bank_account_number);

        binding.orderId.textValue.setText(orderModel.getId());
        binding.bankHolderName.textValue.setText(bankModel.getBankHolderName());
        binding.bankName.textValue.setText(bankModel.getBankAccount());
        binding.bankNumber.textValue.setText(bankModel.getBankAccountNumber());

        countdownTime();
    }

    private void countdownTime() {
        final long time = orderModel.getCreateDate().toDate().getTime() + Constants.MAX_ON_PAYMENT_TIME;
        final long counterTime = UtilsDate.getTimeLeft(time);

        if (counterTime >= 0) {
            new CountDownTimer(counterTime, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (binding == null) {
                        return;
                    }
                    binding.textCounter.setText(UtilsDate.parseTime(millisUntilFinished));
                }

                public void onFinish() {
                    binding.textCounter.setText(R.string.info_expired);
                }

            }.start();
        } else {
            binding.textCounter.setText(R.string.info_expired);
        }
    }
}