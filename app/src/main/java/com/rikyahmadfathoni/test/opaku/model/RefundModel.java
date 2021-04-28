package com.rikyahmadfathoni.test.opaku.model;

import java.util.List;

public class RefundModel {

    private String id;
    private String paymentId;
    private String paymentName;
    private List<ProductOrderModel> models;
    private long productPrice;
    private long shippingPrice;
    private long created;

    public RefundModel() {
        super();
    }

    public RefundModel(OrderModel orderModel) {
        this.id = null;
        this.paymentId = orderModel.getPaymentId();
        this.paymentName = orderModel.getPaymentService();
        this.models = orderModel.getProducts();
        this.productPrice = orderModel.getProductPrice();
        this.shippingPrice = orderModel.getShipmentPrice();
        this.created = System.currentTimeMillis();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public void setModels(List<ProductOrderModel> models) {
        this.models = models;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public void setShippingPrice(long shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public List<ProductOrderModel> getModels() {
        return models;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public long getShippingPrice() {
        return shippingPrice;
    }

    public long getCreated() {
        return created;
    }
}
