package com.rikyahmadfathoni.test.opaku.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.model.BankModel;
import com.rikyahmadfathoni.test.opaku.model.PaymentModel;

import java.util.ArrayList;
import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.ViewHolder> {

    private List<BankModel> bankModels = new ArrayList<>();
    private int lastPositionSelected = -1;

    public void setBankModels(List<BankModel> bankModels) {
        this.bankModels = bankModels;
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
    public BankModel getItem(int position) {
        try {
            return bankModels.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return bankModels.size();
    }

    public int getIndex(String bankId) {
        if (bankId != null && bankModels != null) {
            for (int i=0; i<bankModels.size(); i++) {
                final BankModel bankModel = getItem(i);
                if (bankModel != null) {
                    if (bankId.equals(bankModel.getId())) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    public int getLastPositionSelected() {
        return lastPositionSelected;
    }

    public void setLastPositionBy(String bankId) {
        if (bankId == null) {
            return;
        }
        final int index = getIndex(bankId);
        System.err.println("Position : " + index);
        if (index >= 0) {
            lastPositionSelected = index;
        }
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
            textDescription.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(this);
        }

        public void onBindData(BankModel item) {
            if (item != null) {
                final int position = getBindingAdapterPosition();
                textTitle.setText(item.getBankAccount());
                textDescription.setText(item.getBankHolderName());

                radio.setChecked(position == lastPositionSelected);
            }
        }

        @Override
        public void onClick(View view) {
            final int position = getBindingAdapterPosition();
            final BankModel bankModel = getItem(position);
            if (bankModel != null) {
                radio.setChecked(true);
                if (eventListener != null) {
                    eventListener.onClick(bankModel, position);
                }
                lastPositionSelected = position;
            }
        }
    }

    public interface EventListener {
        void onClick(BankModel bankModel, int position);
    }

    private EventListener eventListener;

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }
}
