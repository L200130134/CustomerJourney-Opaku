package com.rikyahmadfathoni.test.opaku.store.model;

import com.google.gson.annotations.SerializedName;

public class SubTotalModel {

    @SerializedName("productTotal")
    private int productTotal;
    @SerializedName("priceTotal")
    private long priceTotal;

    public SubTotalModel() {
        super();
    }

    public SubTotalModel(int productTotal, long priceTotal) {
        this.productTotal = productTotal;
        this.priceTotal = priceTotal;
    }

    public int getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(int productTotal) {
        this.productTotal = productTotal;
    }

    public long getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(long priceTotal) {
        this.priceTotal = priceTotal;
    }
}
