package com.rikyahmadfathoni.test.opaku.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.activity.OrderDetailsActivity;
import com.rikyahmadfathoni.test.opaku.model.OrderModel;
import com.rikyahmadfathoni.test.opaku.model.ProductOrderModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsMain;
import com.rikyahmadfathoni.test.opaku.utils.UtilsNumber;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<OrderModel> orderModels = new ArrayList<>();

    public void setOrderModels(List<OrderModel> orderModels) {
        this.orderModels = orderModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(getItem(position));
    }

    @Nullable
    public OrderModel getItem(int position) {
        try {
            return orderModels.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textId;
        private final TextView textDate;
        private final TextView textStatus;
        private final TextView textTitleTotal;
        private final TextView textValueTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textId = itemView.findViewById(R.id.text_id);
            textDate = itemView.findViewById(R.id.text_date);
            textStatus = itemView.findViewById(R.id.text_status);
            textTitleTotal = itemView.findViewById(R.id.text_title_total);
            textValueTotal = itemView.findViewById(R.id.text_value_total);

            textTitleTotal.setText(R.string.info_total);

            itemView.setOnClickListener(this);
        }

        public void onBindData(OrderModel item) {
            if (item != null) {
                textId.setText(String.format("#%s", item.getId()));
                textDate.setText(UtilsMain.getOrderTime(item.getStatus(), item.getCreateDate().toDate().getTime()));
                textStatus.setText(UtilsMain.getOrderStatus(item.getStatus()));
                textValueTotal.setText(UtilsNumber.formatRupiah(item.getTotalPrice()));
            }
        }

        @Override
        public void onClick(View view) {
            final int position = getAdapterPosition();
            final OrderModel orderModel = getItem(position);
            if (orderModel != null) {
                final Context context = view.getContext();
                if (context instanceof Activity) {
                    final Activity activity = (Activity) context;
                    OrderDetailsActivity.start(activity, orderModel);
                }
            }
        }
    }

    private String getProductName(List<ProductOrderModel> products) {
        if (products != null) {
            final StringBuilder sb = new StringBuilder();
            for (ProductOrderModel pom : products) {
                sb.append(pom.getProductName()).append("; ");
            }
            return sb.substring(0, sb.length()-2);
        }
        return null;
    }
}
