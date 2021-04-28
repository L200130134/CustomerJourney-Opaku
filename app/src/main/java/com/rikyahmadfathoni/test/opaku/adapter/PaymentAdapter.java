package com.rikyahmadfathoni.test.opaku.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.activity.CardSelectorActivity;
import com.rikyahmadfathoni.test.opaku.activity.CheckoutActivity;
import com.rikyahmadfathoni.test.opaku.activity.BankSelectorActivity;
import com.rikyahmadfathoni.test.opaku.model.PaymentModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;

import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private List<PaymentModel> paymentModelList = new ArrayList<>();

    public void setPaymentModelList(List<PaymentModel> paymentModelList) {
        this.paymentModelList = paymentModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(getItem(position));
    }

    @Nullable
    public PaymentModel getItem(int position) {
        try {
            return paymentModelList.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return paymentModelList.size();
    }

    public int getIndex(String paymentId) {
        if (paymentModelList != null) {
            for (int i=0; i<paymentModelList.size(); i++) {
                final PaymentModel paymentModel = getItem(i);
                if (paymentModel != null) {
                    if (paymentId.equals(paymentModel.getId())) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textTitle;
        private final TextView textDescription;
        private final RadioButton radio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
            radio = itemView.findViewById(R.id.radio);

            itemView.setOnClickListener(this);
        }

        public void onBindData(PaymentModel item) {
            if (item != null) {
                final boolean isEmptyDesc = UtilsString.isEmpty(item.getPaymentDescription());
                textTitle.setText(item.getPaymentName());
                textDescription.setText(item.getPaymentDescription());
                textDescription.setVisibility(
                        isEmptyDesc ? View.GONE : View.VISIBLE
                );
                radio.setChecked(!isEmptyDesc);
            }
        }

        @Override
        public void onClick(View view) {
            final int position = getBindingAdapterPosition();
            final PaymentModel paymentModel = getItem(position);
            if (paymentModel != null) {
                final Context context = view.getContext();
                if (context instanceof Activity) {
                    final Activity activity = (Activity) context;
                    if (Constants.ID_PAYMENT_BANK.equals(paymentModel.getId())) {
                        final Intent intent = new Intent(activity, BankSelectorActivity.class);
                        intent.putExtra(BankSelectorActivity.KEY_BANK_ID, paymentModel.getMethodId());
                        activity.startActivityForResult(intent, CheckoutActivity.RESULT_CODE_PAYMENT_BANK);
                    } else if (Constants.ID_PAYMENT_CARD.equals(paymentModel.getId())) {
                        final Intent intent = new Intent(activity, CardSelectorActivity.class);
                        activity.startActivityForResult(intent, CheckoutActivity.RESULT_CODE_PAYMENT_CARD);
                    }
                }
            }
        }
    }
}
