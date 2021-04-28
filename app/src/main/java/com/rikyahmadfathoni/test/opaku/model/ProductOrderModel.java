package com.rikyahmadfathoni.test.opaku.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;

import java.util.Objects;

public class ProductOrderModel implements Parcelable {

    private String productId;
    private String productName;
    private String imageUrl;
    private long productPrice;
    private long productWeight;
    private int productAmount;

    public ProductOrderModel() {
        super();
    }

    public ProductOrderModel(CartModel cartModel) {
        this.productId = cartModel.getProductId();
        this.productName = cartModel.getProductName();
        this.imageUrl = cartModel.getImageUrl();
        this.productPrice = cartModel.getProductPrice();
        this.productWeight = cartModel.getProductWeight();
        this.productAmount = cartModel.getProductSelected();
    }

    protected ProductOrderModel(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        imageUrl = in.readString();
        productPrice = in.readLong();
        productWeight = in.readLong();
        productAmount = in.readInt();
    }

    public static final Creator<ProductOrderModel> CREATOR = new Creator<ProductOrderModel>() {
        @Override
        public ProductOrderModel createFromParcel(Parcel in) {
            return new ProductOrderModel(in);
        }

        @Override
        public ProductOrderModel[] newArray(int size) {
            return new ProductOrderModel[size];
        }
    };

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public long getProductWeight() {
        return productWeight;
    }

    public int getProductAmount() {
        return productAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductOrderModel that = (ProductOrderModel) o;
        return UtilsString.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(productId);
        parcel.writeString(productName);
        parcel.writeString(imageUrl);
        parcel.writeLong(productPrice);
        parcel.writeLong(productWeight);
        parcel.writeInt(productAmount);
    }
}
