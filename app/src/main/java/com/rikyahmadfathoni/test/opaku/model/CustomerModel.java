package com.rikyahmadfathoni.test.opaku.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CustomerModel implements Parcelable {

    private String id;
    private String email;
    private String name;
    private String userName;
    private String phoneNumber;
    private String photoUrl;
    private String primaryAddressId;
    private List<AddressModel> addressModels;

    public CustomerModel() {
        super();
    }

    protected CustomerModel(Parcel in) {
        id = in.readString();
        email = in.readString();
        name = in.readString();
        userName = in.readString();
        phoneNumber = in.readString();
        photoUrl = in.readString();
        primaryAddressId = in.readString();
        addressModels = in.createTypedArrayList(AddressModel.CREATOR);
    }

    public static final Creator<CustomerModel> CREATOR = new Creator<CustomerModel>() {
        @Override
        public CustomerModel createFromParcel(Parcel in) {
            return new CustomerModel(in);
        }

        @Override
        public CustomerModel[] newArray(int size) {
            return new CustomerModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPrimaryAddressId() {
        return primaryAddressId;
    }

    public void setPrimaryAddressId(String primaryAddressId) {
        this.primaryAddressId = primaryAddressId;
    }

    public List<AddressModel> getAddressModels() {
        return addressModels;
    }

    public void setAddressModels(List<AddressModel> addressModels) {
        this.addressModels = addressModels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(userName);
        parcel.writeString(phoneNumber);
        parcel.writeString(photoUrl);
        parcel.writeString(primaryAddressId);
        parcel.writeTypedList(addressModels);
    }
}
