package com.rikyahmadfathoni.test.opaku;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

public class BaseProductModel implements Parcelable {

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

    protected BaseProductModel(Parcel in) {
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

    public static final Creator<BaseProductModel> CREATOR = new Creator<BaseProductModel>() {
        @Override
        public BaseProductModel createFromParcel(Parcel in) {
            return new BaseProductModel(in);
        }

        @Override
        public BaseProductModel[] newArray(int size) {
            return new BaseProductModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(productId);
        parcel.writeString(productName);
        parcel.writeLong(productPrice);
        parcel.writeLong(productWeight);
        parcel.writeInt(productStock);
        parcel.writeString(imageUrl);
        parcel.writeString(productCategory);
        parcel.writeLong(createdDate);
    }
}
