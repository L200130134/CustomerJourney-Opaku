package com.rikyahmadfathoni.test.opaku.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.rikyahmadfathoni.test.opaku.utils.UtilsString;

public class BankModel implements Parcelable {

    private String id;
    private String paymentId;
    private String bankAccount;
    private String bankHolderName;
    private String bankAccountNumber;

    public BankModel() {
        super();
        /*
        if (UtilsString.isEmpty(id)) {
            id = paymentId + bankAccount;
        }*/
    }

    public BankModel(String paymentId, String bankAccount, String bankHolderName,
                     String bankAccountNumber) {
        this.paymentId = paymentId;
        this.bankAccount = bankAccount;
        this.bankHolderName = bankHolderName;
        this.bankAccountNumber = bankAccountNumber;
    }

    protected BankModel(Parcel in) {
        id = in.readString();
        paymentId = in.readString();
        bankAccount = in.readString();
        bankHolderName = in.readString();
        bankAccountNumber = in.readString();
    }

    public static final Creator<BankModel> CREATOR = new Creator<BankModel>() {
        @Override
        public BankModel createFromParcel(Parcel in) {
            return new BankModel(in);
        }

        @Override
        public BankModel[] newArray(int size) {
            return new BankModel[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setBankHolderName(String bankHolderName) {
        this.bankHolderName = bankHolderName;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getId() {
        /*if (UtilsString.isEmpty(id)) {
            id = paymentId + bankAccount;
        }*/
        return id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public String getBankHolderName() {
        return bankHolderName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(paymentId);
        parcel.writeString(bankAccount);
        parcel.writeString(bankHolderName);
        parcel.writeString(bankAccountNumber);
    }
}
