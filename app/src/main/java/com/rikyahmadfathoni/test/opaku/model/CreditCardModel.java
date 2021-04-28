package com.rikyahmadfathoni.test.opaku.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CreditCardModel implements Parcelable {

    private String id;
    private String paymentId;
    private String cardHolderName;
    private String cardNumber;
    private String cardExpired;
    private String cvv;

    public CreditCardModel() {
        super();
    }

    protected CreditCardModel(Parcel in) {
        id = in.readString();
        paymentId = in.readString();
        cardHolderName = in.readString();
        cardNumber = in.readString();
        cardExpired = in.readString();
        cvv = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(paymentId);
        dest.writeString(cardHolderName);
        dest.writeString(cardNumber);
        dest.writeString(cardExpired);
        dest.writeString(cvv);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreditCardModel> CREATOR = new Creator<CreditCardModel>() {
        @Override
        public CreditCardModel createFromParcel(Parcel in) {
            return new CreditCardModel(in);
        }

        @Override
        public CreditCardModel[] newArray(int size) {
            return new CreditCardModel[size];
        }
    };

    public String getId() {
        if (id == null) {
            return paymentId + cardNumber;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpired() {
        return cardExpired;
    }

    public void setCardExpired(String cardExpired) {
        this.cardExpired = cardExpired;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
