package com.rikyahmadfathoni.test.opaku.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.rikyahmadfathoni.test.opaku.utils.UtilsString;

import java.util.Objects;

public class AddressModel implements Parcelable {

    private String id;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private String additionalInfo;

    public AddressModel() {
        super();
    }

    protected AddressModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        postalCode = in.readString();
        phoneNumber = in.readString();
        additionalInfo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(postalCode);
        dest.writeString(phoneNumber);
        dest.writeString(additionalInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddressModel> CREATOR = new Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getFullAddress() {
        return String.format("%s (%s) - %s", address, postalCode, phoneNumber);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressModel that = (AddressModel) o;
        return UtilsString.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
