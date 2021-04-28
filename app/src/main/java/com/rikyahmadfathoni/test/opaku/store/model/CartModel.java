package com.rikyahmadfathoni.test.opaku.store.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;

import java.util.Objects;

@Entity(tableName = "cart", indices = {@Index(value = {"productId"}, unique = true)})
public class CartModel extends WishlistModel implements Parcelable {

    @SerializedName("productSelected")
    private int productSelected;

    @Ignore
    public CartModel() {
        super();
    }

    @Ignore
    public CartModel(ProductModel productModel) {
        if (productModel == null) {
            return;
        }
        this.productId = productModel.getId();
        this.productName = productModel.getName();
        this.productPrice = productModel.getPrice();
        this.productWeight = productModel.getWeight();
        this.productStock = productModel.getStock();
        this.productSelected = 1;
        this.imageUrl = productModel.getThumbnail();
        this.productCategory = productModel.getIdCategory();
        this.createdDate = System.currentTimeMillis();
    }

    public CartModel(ProductModel productModel, int productSelected) {
        super(productModel);
        this.productSelected = productSelected;
    }

    public CartModel(int id, String productId, String productName, long productPrice, long productWeight,
                     int productStock, String imageUrl, String productCategory, long createdDate,
                     int productSelected) {
        super(id, productId, productName, productPrice, productWeight, productStock,
                imageUrl, productCategory, createdDate);
        this.productSelected = productSelected;
    }

    public CartModel(Parcel in, int productSelected) {
        super(in);
        this.productSelected = productSelected;
    }

    public int getProductSelected() {
        return productSelected;
    }

    public void setProductSelected(int productSelected) {
        this.productSelected = productSelected;
    }

    protected CartModel(Parcel in) {
        super(in);
        productSelected = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(productSelected);
    }

    public static final Creator<CartModel> CREATOR = new Creator<CartModel>() {
        @Override
        public CartModel createFromParcel(Parcel in) {
            return new CartModel(in);
        }

        @Override
        public CartModel[] newArray(int size) {
            return new CartModel[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CartModel cartModel = (CartModel) o;
        return UtilsString.equals(productId, cartModel.productId);
    }
}
