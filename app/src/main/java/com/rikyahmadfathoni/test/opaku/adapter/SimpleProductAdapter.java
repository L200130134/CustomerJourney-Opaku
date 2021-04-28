package com.rikyahmadfathoni.test.opaku.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.activity.ProductActivity;
import com.rikyahmadfathoni.test.opaku.model.ProductOrderModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsList;
import com.rikyahmadfathoni.test.opaku.utils.UtilsNumber;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;
import com.rikyahmadfathoni.test.opaku.viewholder.EmptyViewHolder;

import java.util.List;

public class SimpleProductAdapter extends ListAdapter<ProductOrderModel, RecyclerView.ViewHolder> {

    private final int viewTypeEmpty = -2;
    private final int viewTypePlaceholder = 0;
    private final int viewTypeProduct = 1;
    private List<ProductOrderModel> productOrderModels;
    private boolean enablePlaceholder;
    private boolean enableMessage;

    public SimpleProductAdapter(Activity activity) {
        super(diffCallback);
    }

    private static final DiffUtil.ItemCallback<ProductOrderModel> diffCallback = new DiffUtil.ItemCallback<ProductOrderModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProductOrderModel oldItem, @NonNull ProductOrderModel newItem) {
            //same item
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductOrderModel oldItem, @NonNull ProductOrderModel newItem) {
            //same data
            return oldItem.getProductAmount() == newItem.getProductAmount()
                    && oldItem.getProductPrice() == newItem.getProductPrice()
                    && oldItem.getProductWeight() == newItem.getProductWeight()
                    && UtilsString.equals(oldItem.getImageUrl(), newItem.getImageUrl());
        }
    };

    public void setPlaceholderMode(boolean isEnabled) {
        this.enablePlaceholder = isEnabled;
        if (isEnabled) {
            this.productOrderModels = UtilsList.dummyList(6);
            this.submitList(productOrderModels);
        }
    }

    public void setEmptyMode(boolean isEnabled) {
        this.enableMessage = isEnabled;
        if (isEnabled) {
            this.productOrderModels = UtilsList.dummyList(1);
            this.submitList(productOrderModels);
        }
    }

    public void setModels(List<ProductOrderModel> productOrderModels) {
        this.productOrderModels = productOrderModels;
    }

    @Override
    public void submitList(@Nullable List<ProductOrderModel> list) {
        this.setModels(list);
        super.submitList(list);
    }

    public void removeModel(ProductOrderModel model) {
        this.productOrderModels.remove(model);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater li = LayoutInflater.from(parent.getContext());
        if (viewType == viewTypeEmpty) {
            final View view = li.inflate(R.layout.item_empty, parent, false);
            return new EmptyViewHolder(view);
        } else if (viewType == viewTypePlaceholder) {
            final View view = li.inflate(R.layout.item_placeholder_list, parent, false);
            return new ViewHolder(view);
        } else {
            final View view = li.inflate(R.layout.item_simple_cart, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (enablePlaceholder) {
            return viewTypePlaceholder;
        }
        if (enableMessage) {
            return viewTypeEmpty;
        }
        return viewTypeProduct;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) holder).bindData();
        } else if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bindData(getItem(position));
        }
    }

    @Override
    @Nullable
    public ProductOrderModel getItem(int position) {
        if (enablePlaceholder) {
            return null;
        }
        try {
            return super.getItem(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ProductOrderModel> getProductOrderModels() {
        return productOrderModels;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageProduct;
        private final TextView textProductName;
        private final TextView textProductPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);

            itemView.setOnClickListener(this);
        }

        public void bindData(@Nullable ProductOrderModel item) {
            if (item != null) {
                textProductName.setText(item.getProductName());
                textProductPrice.setText(String.format(
                        "%s x %s", UtilsNumber.formatRupiah(item.getProductPrice()), item.getProductAmount())
                );

                Glide.with(imageProduct).load(item.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(imageProduct);
            }
        }

        @Override
        public void onClick(View view) {
            final int position = getBindingAdapterPosition();
            final ProductOrderModel item = getItem(position);
            if (item == null) {
                return;
            }
            final Context context = view.getContext();
            if (context instanceof Activity) {
                final Activity activity = (Activity) context;
                final Intent intent = new Intent(activity, ProductActivity.class);
                intent.putExtra(ProductActivity.KEY_PRODUCT_ID, item.getProductId());
                intent.putExtra(ProductActivity.KEY_ALLOW_ADD_CART, false);
                activity.startActivity(intent);
            }
        }
    }
}
