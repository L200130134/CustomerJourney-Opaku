package com.rikyahmadfathoni.test.opaku.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.List;

public class OrderModel implements Parcelable {

    private String id;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private int totalItem;
    private long totalWeight;
    private long productPrice;
    private long shipmentPrice;
    private long totalPrice;
    private String destinationAddress;
    private String customerMessage;
    private String paymentId;
    private String bankId;
    private String paymentService;
    private List<ProductOrderModel> products;
    private int status;
    private Timestamp createDate;

    public OrderModel() {
        super();
    }

    protected OrderModel(Parcel in) {
        id = in.readString();
        customerId = in.readString();
        customerName = in.readString();
        customerPhone = in.readString();
        totalItem = in.readInt();
        totalWeight = in.readLong();
        productPrice = in.readLong();
        shipmentPrice = in.readLong();
        totalPrice = in.readLong();
        destinationAddress = in.readString();
        customerMessage = in.readString();
        paymentId = in.readString();
        bankId = in.readString();
        paymentService = in.readString();
        products = in.createTypedArrayList(ProductOrderModel.CREATOR);
        status = in.readInt();
        createDate = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(customerId);
        dest.writeString(customerName);
        dest.writeString(customerPhone);
        dest.writeInt(totalItem);
        dest.writeLong(totalWeight);
        dest.writeLong(productPrice);
        dest.writeLong(shipmentPrice);
        dest.writeLong(totalPrice);
        dest.writeString(destinationAddress);
        dest.writeString(customerMessage);
        dest.writeString(paymentId);
        dest.writeString(bankId);
        dest.writeString(paymentService);
        dest.writeTypedList(products);
        dest.writeInt(status);
        dest.writeParcelable(createDate, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public long getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(long totalWeight) {
        this.totalWeight = totalWeight;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public long getShipmentPrice() {
        return shipmentPrice;
    }

    public void setShipmentPrice(long shipmentPrice) {
        this.shipmentPrice = shipmentPrice;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getCustomerMessage() {
        return customerMessage;
    }

    public void setCustomerMessage(String customerMessage) {
        this.customerMessage = customerMessage;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(String paymentService) {
        this.paymentService = paymentService;
    }

    public List<ProductOrderModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOrderModel> products) {
        this.products = products;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
