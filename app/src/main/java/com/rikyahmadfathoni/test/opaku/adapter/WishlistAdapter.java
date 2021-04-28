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
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.model.WishlistModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.WishlistViewModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsList;
import com.rikyahmadfathoni.test.opaku.utils.UtilsNumber;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;
import com.rikyahmadfathoni.test.opaku.view.SquareImageView;
import com.rikyahmadfathoni.test.opaku.viewholder.EmptyViewHolder;

import java.util.List;

public class WishlistAdapter extends ListAdapter<WishlistModel, RecyclerView.ViewHolder> {

    private final int viewTypeEmpty = -2;
    private final int viewTypePlaceholder = 0;
    private final int viewTypeProduct = 1;
    private List<WishlistModel> wishlistModels;
    private boolean enablePlaceholder;
    private boolean enableMessage;
    private final WishlistViewModel wishlistViewModel;

    public WishlistAdapter(Activity activity) {
        super(diffCallback);
        this.wishlistViewModel = WishlistViewModel.getInstance(activity.getApplication());
    }

    private static final DiffUtil.ItemCallback<WishlistModel> diffCallback = new DiffUtil.ItemCallback<WishlistModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull WishlistModel oldItem, @NonNull WishlistModel newItem) {
            //same item
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull WishlistModel oldItem, @NonNull WishlistModel newItem) {
            //same data
            return oldItem.getProductStock() == newItem.getProductStock()
                    && oldItem.getProductPrice() == newItem.getProductPrice()
                    && UtilsString.equals(oldItem.getImageUrl(), newItem.getImageUrl());
        }
    };

    public void setPlaceholderMode(boolean isEnabled) {
        this.enablePlaceholder = isEnabled;
        if (isEnabled) {
            this.wishlistModels = UtilsList.dummyList(6);
            this.submitList(wishlistModels);
        }
    }

    public void setEmptyMode(boolean isEnabled) {
        this.enableMessage = isEnabled;
        if (isEnabled) {
            this.wishlistModels = UtilsList.dummyList(1);
            this.submitList(wishlistModels);
        }
    }

    public void setModels(List<WishlistModel> discoverProductModels) {
        this.wishlistModels = discoverProductModels;
    }

    @Override
    public void submitList(@Nullable List<WishlistModel> list) {
        this.setModels(list);
        super.submitList(list);
    }

    public void removeModel(CartModel cartModel) {
        this.wishlistModels.remove(cartModel);
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
            final View view = li.inflate(R.layout.item_list_wishlist, parent, false);
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
    public WishlistModel getItem(int position) {
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

    public List<WishlistModel> getWishlistModels() {
        return wishlistModels;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageProduct;
        private final TextView textProductName;
        private final TextView textProductPrice;
        private final SquareImageView buttonRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            buttonRemove = itemView.findViewById(R.id.button_remove);

            if (buttonRemove != null) {
                buttonRemove.setOnClickListener(this);
            }

            itemView.setOnClickListener(this);
        }

        public void bindData(@Nullable WishlistModel item) {
            if (item != null) {
                textProductName.setText(item.getProductName());
                textProductPrice.setText(UtilsNumber.formatRupiah(item.getProductPrice()));

                Glide.with(imageProduct).load(item.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(imageProduct);
            }
        }

        @Override
        public void onClick(View view) {
            final int position = getBindingAdapterPosition();
            final WishlistModel wishlistModel = getItem(position);
            if (wishlistModel == null) {
                return;
            }
            if (view == buttonRemove) {
                wishlistViewModel.delete(wishlistModel);
            } else {
                final Context context = view.getContext();
                if (context instanceof Activity) {
                    final Activity activity = (Activity) context;
                    final Intent intent = new Intent(activity, ProductActivity.class);
                    intent.putExtra(ProductActivity.KEY_PRODUCT_ID, wishlistModel.getProductId());
                    activity.startActivity(intent);
                }
            }
        }
    }
}
