package com.rikyahmadfathoni.test.opaku.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class ProductModel implements Parcelable {

    private String id;
    private String idCategory;
    private String name;
    private long price;
    private int weight;
    private int stock;
    private String brand;
    private String material;
    private String description;
    private String thumbnail;
    private String image;
    private Timestamp createDate;

    public ProductModel() {
        super();
    }

    protected ProductModel(Parcel in) {
        id = in.readString();
        idCategory = in.readString();
        name = in.readString();
        price = in.readLong();
        weight = in.readInt();
        stock = in.readInt();
        brand = in.readString();
        material = in.readString();
        description = in.readString();
        thumbnail = in.readString();
        image = in.readString();
        createDate = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(idCategory);
        dest.writeString(name);
        dest.writeLong(price);
        dest.writeInt(weight);
        dest.writeInt(stock);
        dest.writeString(brand);
        dest.writeString(material);
        dest.writeString(description);
        dest.writeString(thumbnail);
        dest.writeString(image);
        dest.writeParcelable(createDate, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public static Creator<ProductModel> getCREATOR() {
        return CREATOR;
    }
}
