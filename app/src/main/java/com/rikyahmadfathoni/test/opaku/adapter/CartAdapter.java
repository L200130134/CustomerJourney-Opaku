package com.rikyahmadfathoni.test.opaku.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsInteger;
import com.rikyahmadfathoni.test.opaku.utils.UtilsNumber;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;
import com.rikyahmadfathoni.test.opaku.viewholder.EmptyViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends ListAdapter<CartModel, RecyclerView.ViewHolder> {

    private final int viewTypeEmpty = -2;
    private final int viewTypePlaceholder = 0;
    private final int viewTypeProduct = 1;
    private List<CartModel> cartModels;
    private boolean enablePlaceholder;
    private boolean enableMessage;

    public CartAdapter() {
        super(diffCallback);
    }

    private static final DiffUtil.ItemCallback<CartModel> diffCallback = new DiffUtil.ItemCallback<CartModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull CartModel oldItem, @NonNull CartModel newItem) {
            //same item
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull CartModel oldItem, @NonNull CartModel newItem) {
            //same data
            return oldItem.getProductStock() == newItem.getProductStock()
                    && oldItem.getProductPrice() == newItem.getProductPrice()
                    && oldItem.getProductSelected() == newItem.getProductSelected()
                    && UtilsString.equals(oldItem.getImageUrl(), newItem.getImageUrl());
        }
    };

    public void setPlaceholderMode(boolean isEnabled) {
        this.enablePlaceholder = isEnabled;
        if (isEnabled) {
            this.cartModels = getDummyModel(6);
            this.submitList(cartModels);
        }
    }

    public void setEmptyMode(boolean isEnabled) {
        this.enableMessage = isEnabled;
        if (isEnabled) {
            this.cartModels = getDummyModel(1);
            this.submitList(cartModels);
        }
    }

    private List<CartModel> getDummyModel(int length) {
        final List<CartModel> cartModels = new ArrayList<>();
        for (int i=0; i<length; i++) {
            cartModels.add(new CartModel());
        }
        return cartModels;
    }

    public void setModels(List<CartModel> discoverProductModels) {
        this.cartModels = discoverProductModels;
    }

    @Override
    public void submitList(@Nullable List<CartModel> list) {
        this.setModels(list);
        super.submitList(list);
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
            final View view = li.inflate(R.layout.item_list_cart, parent, false);
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
            ((EmptyViewHolder)holder).bindData();
        } else if (holder instanceof ViewHolder) {
            ((ViewHolder)holder).bindData(getItem(position));
        }
    }

    @Override
    @Nullable
    public CartModel getItem(int position) {
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

    public List<CartModel> getCartModels() {
        return cartModels;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {

        private final ImageView imageProduct;
        private final ImageView buttonDecrease;
        private final ImageView buttonIncrease;
        private final ImageView buttonRemove;
        private final TextView textProductName;
        private final TextView textProductPrice;
        private final EditText inputStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            inputStock = itemView.findViewById(R.id.input_stock);
            buttonDecrease = itemView.findViewById(R.id.button_decrease);
            buttonIncrease = itemView.findViewById(R.id.button_increase);
            buttonRemove = itemView.findViewById(R.id.button_remove);

            if (buttonRemove != null) {
                buttonDecrease.setOnClickListener(this);
                buttonIncrease.setOnClickListener(this);
                buttonRemove.setOnClickListener(this);
                inputStock.addTextChangedListener(this);
            }

            itemView.setOnClickListener(this);
        }

        public void bindData(@Nullable CartModel item) {
            if (item != null) {
                final String productId = item.getProductId();
                if (productId != null) {
                    textProductName.setText(item.getProductName());
                    textProductPrice.setText(UtilsNumber.formatRupiah(item.getProductPrice()));
                    inputStock.setText(String.valueOf(item.getProductSelected()));

                    Glide.with(imageProduct).load(item.getImageUrl())
                            .placeholder(R.drawable.placeholder)
                            .into(imageProduct);
                }
            }
        }

        @Override
        public void onClick(View view) {
            final int position = getBindingAdapterPosition();
            final CartModel cartModel = getItem(position);
            if (cartModel == null) {
                return;
            }
            if (view == buttonDecrease) {
                final Integer value = UtilsInteger.parseInt(inputStock.getText().toString());
                if (value != null) {
                    if (value > 1) {
                        final int selectedTotal = value - 1;
                        if (eventListener != null) {
                            eventListener.onUpdateStock(cartModel.getProductId(), selectedTotal);
                        }
                    }
                }
            } else if (view == buttonIncrease) {
                final Integer value = UtilsInteger.parseInt(inputStock.getText().toString());
                if (value != null) {
                    if (value < cartModel.getProductStock()) {
                        final int selectedTotal = value + 1;
                        if (eventListener != null) {
                            eventListener.onUpdateStock(cartModel.getProductId(), selectedTotal);
                        }
                    }
                }
            } else if (view == buttonRemove) {
                if (eventListener != null) {
                    eventListener.onRemoveItem(cartModel);
                }
            } else {
                if (eventListener != null) {
                    eventListener.onClick(cartModel);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            final String valueString = editable.toString();
            if (!UtilsString.isEmpty(valueString)) {
                final int position = getBindingAdapterPosition();
                final CartModel cartModel = getItem(position);
                if (cartModel != null) {
                    final Integer value = UtilsInteger.parseInt(valueString);
                    if (value != null) {
                        if (value <= cartModel.getProductStock()) {
                            if (eventListener != null) {
                                eventListener.onUpdateStock(cartModel.getProductId(), value);
                            }
                        } else {
                            UtilsDialog.showToast(itemView.getContext(),
                                    "Jumlah stok produk saat ini : " + cartModel.getProductStock());
                        }
                    }
                }
            }
        }
    }

    public interface EventListener {
        void onClick(CartModel cartModel);
        void onUpdateStock(String productId, int stock);
        void onRemoveItem(CartModel cartModel);
    }

    private EventListener eventListener;

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }
}
