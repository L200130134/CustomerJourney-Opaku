package com.rikyahmadfathoni.test.opaku.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.model.CategoryModel;
import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsNumber;
import com.rikyahmadfathoni.test.opaku.viewholder.EmptyViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int viewTypeEmpty = -2;
    private final int viewTypeFilter = -1;
    private final int viewTypePlaceholder = 0;
    private final int viewTypeProduct = 1;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<CategoryModel> categoryModels;
    private List<ProductModel> productModels = new ArrayList<>();
    private boolean enableMessage;
    private boolean resetFilter;

    private int lastPosition = -1;

    public void setModels(List<ProductModel> discoverProductModels) {
        this.productModels = discoverProductModels;
    }

    public void setEnableMessage(boolean enableMessage) {
        this.enableMessage = enableMessage;
    }

    public boolean isEnableMessage() {
        return enableMessage;
    }

    public void setResetFilter(boolean resetFilter) {
        this.resetFilter = resetFilter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater li = LayoutInflater.from(parent.getContext());
        if (viewType == viewTypeEmpty) {
            final View view = li.inflate(R.layout.item_empty, parent, false);
            return new EmptyViewHolder(view);
        } else if (viewType == viewTypeFilter) {
            final View view = li.inflate(R.layout.item_product_filter, parent, false);
            return new ViewHolderFilter(view);
        } else if (viewType == viewTypePlaceholder) {
            final View view = li.inflate(R.layout.item_placeholder_product, parent, false);
            return new ViewHolder(view);
        } else {
            final View view = li.inflate(R.layout.item_list_product, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return viewTypeFilter;
        }
        if (productModels.isEmpty()) {
            if (enableMessage) {
                return viewTypeEmpty;
            }
            return viewTypePlaceholder;
        }
        return viewTypeProduct;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) holder).bindData();
        } else if (holder instanceof ViewHolderFilter) {
            ((ViewHolderFilter) holder).bindData();
        } else if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bindData(getItem(position));
        }
    }

    @Override
    public int getItemCount() {
        return (productModels.isEmpty() ? enableMessage ? 1 : 6 : productModels.size()) + 1;
    }

    @Nullable
    public ProductModel getItem(int position) {
        if (!productModels.isEmpty()) {
            return productModels.get(position - 1);
        }
        return null;
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

        public void bindData(@Nullable ProductModel item) {
            if (item != null) {
                textProductName.setText(item.getName());
                textProductPrice.setText(UtilsNumber.formatRupiah(item.getPrice()));

                Glide.with(itemView.getContext()).load(item.getThumbnail())
                        .placeholder(R.drawable.placeholder)
                        .into(imageProduct);
            }
        }

        @Override
        public void onClick(View view) {
            final int position = getBindingAdapterPosition();
            final ProductModel productModel = getItem(position);
            if (productModel != null) {
                if (eventListener != null) {
                    eventListener.onClick(productModel, position);
                }
            }
        }
    }

    class ViewHolderFilter extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final HorizontalScrollView scroller;
        private final LinearLayout container;

        public ViewHolderFilter(@NonNull View itemView) {
            super(itemView);

            scroller = itemView.findViewById(R.id.scroller);
            container = itemView.findViewById(R.id.container);

            //itemView.setVisibility(View.GONE);

            if (categoryModels == null) {
                db.collection(Constants.DB_COLLECTION_CATEGORIES).get()
                        .addOnSuccessListener(qds -> {
                            categoryModels = qds.toObjects(CategoryModel.class);
                            bindView();
                        });
            } else {
                bindView();
            }
        }

        public void bindData() {
            final int position = getBindingAdapterPosition();
            if (resetFilter) {
                if (position == 0) {
                    final View view = container.getChildAt(position);
                    if (view != null) {
                        view.setSelected(true);
                    }
                    resetFilter = false;
                }
                if (lastPosition > 0) {
                    final View view = container.getChildAt(lastPosition);
                    if (view != null) {
                        view.setSelected(false);
                    }
                }
            }
        }

        public void bindView() {
            for (int i = 0; i <= categoryModels.size(); i++) {
                final View view = getViewChild();
                if (view instanceof TextView) {
                    final TextView textView = (TextView) view;
                    if (i == 0) {
                        textView.setTag(null);
                        textView.setText(R.string.title_category_all);
                        textView.setSelected(true);
                    } else {
                        final CategoryModel categoryModel = categoryModels.get(i - 1);
                        textView.setTag(categoryModel.getId());
                        textView.setText(categoryModel.getName());
                        textView.setSelected(false);
                    }
                    textView.setOnClickListener(this);
                    container.addView(textView);
                }
            }
        }

        private View getViewChild() {
            return LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.item_text_filter, container, false);
        }

        @Override
        public void onClick(View view) {
            final ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) parent;
                final int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    final View childView = viewGroup.getChildAt(i);
                    if (childView != view) {
                        childView.setSelected(false);
                    } else {
                        lastPosition = i;
                    }
                }
            }
            view.setSelected(true);
            if (eventListener != null) {
                final Object tag = view.getTag();
                eventListener.onFilterClick(view, tag != null ? String.valueOf(tag) : null);
            }
        }
    }

    public interface EventListener {
        void onFilterClick(View view, String id);

        void onClick(ProductModel productModel, int position);
    }

    private EventListener eventListener;

    public void setCategoryModels(List<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
    }

    public void setProductModels(List<ProductModel> productModels) {
        this.productModels = productModels;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }
}
