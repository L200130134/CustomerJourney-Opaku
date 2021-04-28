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

@Entity(tableName = "wishlist", indices = {@Index(value = {"productId"}, unique = true)})
public class WishlistModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    protected int id;
    @SerializedName("productId")
    protected String productId;
    @SerializedName("productName")
    protected String productName;
    @SerializedName("productPrice")
    protected long productPrice;
    @SerializedName("productWeight")
    protected long productWeight;
    @SerializedName("productStock")
    protected int productStock;
    @SerializedName("imageUrl")
    protected String imageUrl;
    @SerializedName("productCategory")
    protected String productCategory;
    @SerializedName("createdDate")
    protected long createdDate;

    @Ignore
    public WishlistModel() {
        super();
    }

    @Ignore
    public WishlistModel(ProductModel productModel) {
        if (productModel == null) {
            return;
        }
        this.productId = productModel.getId();
        this.productName = productModel.getName();
        this.productPrice = productModel.getPrice();
        this.productWeight = productModel.getWeight();
        this.productStock = productModel.getStock();
        this.imageUrl = productModel.getThumbnail();
        this.productCategory = productModel.getIdCategory();
        this.createdDate = System.currentTimeMillis();
    }

    public WishlistModel(int id, String productId, String productName, long productPrice, long productWeight,
                         int productStock, String imageUrl, String productCategory, long createdDate) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productWeight = productWeight;
        this.productStock = productStock;
        this.imageUrl = imageUrl;
        this.productCategory = productCategory;
        this.createdDate = createdDate;
    }

    protected WishlistModel(Parcel in) {
        id = in.readInt();
        productId = in.readString();
        productName = in.readString();
        productPrice = in.readLong();
        productWeight = in.readLong();
        productStock = in.readInt();
        imageUrl = in.readString();
        productCategory = in.readString();
        createdDate = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeLong(productPrice);
        dest.writeLong(productWeight);
        dest.writeInt(productStock);
        dest.writeString(imageUrl);
        dest.writeString(productCategory);
        dest.writeLong(createdDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WishlistModel> CREATOR = new Creator<WishlistModel>() {
        @Override
        public WishlistModel createFromParcel(Parcel in) {
            return new WishlistModel(in);
        }

        @Override
        public WishlistModel[] newArray(int size) {
            return new WishlistModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductWeight(long productWeight) {
        this.productWeight = productWeight;
    }

    public long getProductWeight() {
        return productWeight;
    }

    public int getProductStock() {
        return productStock;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final WishlistModel cartModel = (WishlistModel) o;
        return UtilsString.equals(productId, cartModel.productId);
    }
}
