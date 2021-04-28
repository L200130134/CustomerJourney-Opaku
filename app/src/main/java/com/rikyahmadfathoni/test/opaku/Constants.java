package com.rikyahmadfathoni.test.opaku;

public class Constants {

    public static final long SHIPMENT_PRICE_IN_KG = 25000;
    public static final String ID_PAYMENT_BANK = "ss7ha4QfEgYFD4I5wpBf";
    public static final String ID_PAYMENT_CARD = "FeI5qezCGQfoQ9GyksrF";

    public static final long MAX_ON_PAYMENT_TIME = 12 * 3600 * 1000;

    public static final int ORDER_STATUS_REFUND = -2;
    public static final int ORDER_STATUS_EXPIRED = -1;
    public static final int ORDER_STATUS_WAITING_FOR_PAYMENT = 0;
    public static final int ORDER_STATUS_PAYMENT = 1;
    public static final int ORDER_STATUS_PROCESS = 2;
    public static final int ORDER_STATUS_SHIPMENT = 3;
    public static final int ORDER_STATUS_RECEIVED = 4;
    public static final int ORDER_STATUS_COMPLETED = 5;

    public static final String DB_COLLECTION_CATEGORIES = "categories";
    public static final String DB_COLLECTION_CUSTOMERS = "customers";
    public static final String DB_COLLECTION_PRODUCTS = "products";
    public static final String DB_COLLECTION_ORDERS = "orders";
    public static final String DB_COLLECTION_PAYMENT = "payment";
    public static final String DB_COLLECTION_REFUND = "refund";
}
