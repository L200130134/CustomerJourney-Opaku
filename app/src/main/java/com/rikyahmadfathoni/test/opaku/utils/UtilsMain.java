package com.rikyahmadfathoni.test.opaku.utils;

import android.os.Bundle;
import android.os.Parcelable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.model.OrderModel;
import com.rikyahmadfathoni.test.opaku.model.PaymentModel;
import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.model.ProductOrderModel;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.model.WishlistModel;

import java.util.List;

public class UtilsMain {

    public static String getOrderStatus(int statusId) {
        if (statusId == Constants.ORDER_STATUS_WAITING_FOR_PAYMENT) {
            return "Waiting for payment";
        } else if (statusId == Constants.ORDER_STATUS_PAYMENT) {
            return "On payment";
        } else if (statusId == Constants.ORDER_STATUS_PROCESS) {
            return "On process";
        } else if (statusId == Constants.ORDER_STATUS_SHIPMENT) {
            return "On shipment";
        } else if (statusId == Constants.ORDER_STATUS_RECEIVED) {
            return "On received";
        } else if (statusId == Constants.ORDER_STATUS_COMPLETED) {
            return "Completed";
        } else if (statusId == Constants.ORDER_STATUS_EXPIRED) {
            return "Expired";
        } else if (statusId == Constants.ORDER_STATUS_REFUND) {
            return "Refund";
        }
        return "Unknown";
    }

    public static boolean isRefundable(int statusId) {
        return statusId == Constants.ORDER_STATUS_PAYMENT
                || statusId == Constants.ORDER_STATUS_PROCESS
                || statusId == Constants.ORDER_STATUS_SHIPMENT
                || statusId == Constants.ORDER_STATUS_RECEIVED;
    }

    public static String getOrderTime(int id, long dateCreated) {
        if (id == Constants.ORDER_STATUS_WAITING_FOR_PAYMENT) {
            final long maxDate = dateCreated + Constants.MAX_ON_PAYMENT_TIME;
            final String simpleDate = UtilsDate.getSimpleTanggal(dateCreated);

            final long hoursLeft = UtilsDate.getHoursLeft(System.currentTimeMillis(), maxDate);
            if (hoursLeft >= 0) {
                if (hoursLeft == 0) {
                    return String.format("%s (%s minutes left)", simpleDate,
                            UtilsDate.getMinutesLeft(System.currentTimeMillis(), maxDate));
                } else {
                    return String.format("%s (%s hours left)", simpleDate, hoursLeft);
                }
            } else {
                return String.format("%s (expired)", simpleDate);
            }
        }

        return UtilsDate.getStringDate(dateCreated);
    }

    public static int getTotalCartItem(List<CartModel> cartModels) {
        int totalItem = 0;
        for (CartModel cartModel : cartModels) {
            totalItem += cartModel.getProductSelected();
        }
        return totalItem;
    }

    public static long getWeight(long totalWeight) {
        return (long) Math.ceil(totalWeight/1000f);
    }

    /*
    * Log Event {FirebaseAnalytics.Event.VIEW_ITEM_LIST}
    * */
    public static Bundle getAnalyticsProducts(List<ProductModel> productModels) {
        final int length = productModels.size();
        final Parcelable[] parcelables = new Parcelable[length];

        for (int i=0; i<length; i++) {
            final ProductModel productModel = productModels.get(i);
            if (productModel != null) {
                parcelables[i] = getProductData(productModel,i+1);
            }
        }

        final Bundle viewItemListParams = new Bundle();
        viewItemListParams.putString(FirebaseAnalytics.Param.ITEM_LIST_ID, "L001");
        viewItemListParams.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "Related products");
        viewItemListParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS, parcelables);

        return viewItemListParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.SELECT_ITEM}
     * */
    public static Bundle getAnalyticsSelect(ProductModel productModel) {
        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.ITEM_LIST_ID, "L002");
        viewItemParams.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "Related products");
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS,
                new Parcelable[]{getProductData(productModel, 1)});
        return viewItemParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.VIEW_ITEM}
     * */
    public static Bundle getAnalyticsView(ProductModel productModel) {
        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.CURRENCY, "RP");
        viewItemParams.putLong(FirebaseAnalytics.Param.VALUE, productModel.getPrice());
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS,
                new Parcelable[]{getProductData(productModel, 1)});
        return viewItemParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.ADD_TO_CART}
     * */
    public static Bundle getAnalyticsCart(CartModel cartModel, boolean isAdded) {
        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.METHOD, isAdded ? "added" : "removed");
        viewItemParams.putString(FirebaseAnalytics.Param.CURRENCY, "RP");
        viewItemParams.putLong(FirebaseAnalytics.Param.QUANTITY, cartModel.getProductSelected());
        viewItemParams.putLong(FirebaseAnalytics.Param.VALUE,
                cartModel.getProductSelected() * cartModel.getProductPrice());
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS,
                new Parcelable[]{getCartData(cartModel, 1)});
        return viewItemParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.ADD_TO_WISHLIST}
     * */
    public static Bundle getAnalyticsWishlist(WishlistModel model) {
        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.CURRENCY, "RP");
        viewItemParams.putLong(FirebaseAnalytics.Param.VALUE, model.getProductPrice());
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS,
                new Parcelable[]{getWishlistData(model, 1)});
        return viewItemParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.VIEW_CART}
     * */
    public static Bundle getAnalyticsCarts(List<CartModel> models) {
        final int length = models.size();
        final Parcelable[] parcelables = new Parcelable[length];
        int productPrice = 0;

        for (int i=0; i<length; i++) {
            final CartModel cartModel = models.get(i);
            if (cartModel != null) {
                parcelables[i] = getCartData(cartModel,i+1);
                productPrice += (cartModel.getProductSelected() * cartModel.getProductPrice());
            }
        }

        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.CURRENCY, "RP");
        viewItemParams.putLong(FirebaseAnalytics.Param.VALUE, productPrice);
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS, parcelables);
        return viewItemParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.BEGIN_CHECKOUT}
     * */
    public static Bundle getAnalyticsCheckout(List<CartModel> models) {
        final int length = models.size();
        final Parcelable[] parcelables = new Parcelable[length];
        int productPrice = 0;

        for (int i=0; i<length; i++) {
            final CartModel cartModel = models.get(i);
            if (cartModel != null) {
                parcelables[i] = getCartData(cartModel,i+1);
                productPrice += (cartModel.getProductSelected() * cartModel.getProductPrice());
            }
        }

        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.CURRENCY, "RP");
        viewItemParams.putLong(FirebaseAnalytics.Param.VALUE, productPrice);
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS, parcelables);
        return viewItemParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.ADD_SHIPPING_INFO}
     * */
    public static Bundle getAnalyticsShipping(List<CartModel> models) {
        final int length = models.size();
        final Parcelable[] parcelables = new Parcelable[length];

        int productPrice = 0;
        long productWeight = 0;

        for (int i=0; i<length; i++) {
            final CartModel cartModel = models.get(i);
            if (cartModel != null) {
                parcelables[i] = getCartData(cartModel,i+1);

                final int selected = cartModel.getProductSelected();
                productPrice += (selected * cartModel.getProductPrice());
                productWeight += (selected * cartModel.getProductWeight());
            }
        }

        final long shipmentPrice = UtilsMain.getWeight(productWeight) * Constants.SHIPMENT_PRICE_IN_KG;
        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.CURRENCY, "RP");
        viewItemParams.putLong(FirebaseAnalytics.Param.VALUE, productPrice);
        viewItemParams.putLong(FirebaseAnalytics.Param.SHIPPING, shipmentPrice);
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS, parcelables);
        return viewItemParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.ADD_PAYMENT_INFO}
     * */
    public static Bundle getAnalyticsPayment(List<CartModel> models, String paymentService) {
        final int length = models.size();
        final Parcelable[] parcelables = new Parcelable[length];

        int productPrice = 0;

        for (int i=0; i<length; i++) {
            final CartModel cartModel = models.get(i);
            if (cartModel != null) {
                parcelables[i] = getCartData(cartModel,i+1);
                productPrice += (cartModel.getProductSelected() * cartModel.getProductPrice());
            }
        }

        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.CURRENCY, "RP");
        viewItemParams.putLong(FirebaseAnalytics.Param.VALUE, productPrice);
        viewItemParams.putString(FirebaseAnalytics.Param.PAYMENT_TYPE, paymentService);
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS, parcelables);
        return viewItemParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.PURCHASE}
     * */
    public static Bundle getAnalyticsPurchase(List<CartModel> models, String paymentService) {
        final int length = models.size();
        final Parcelable[] parcelables = new Parcelable[length];

        int productPrice = 0;
        long productWeight = 0;

        for (int i=0; i<length; i++) {
            final CartModel cartModel = models.get(i);
            if (cartModel != null) {
                parcelables[i] = getCartData(cartModel,i+1);

                final int selected = cartModel.getProductSelected();
                productPrice += (selected * cartModel.getProductPrice());
                productWeight += (selected * cartModel.getProductWeight());
            }
        }

        final long shipmentPrice = UtilsMain.getWeight(productWeight) * Constants.SHIPMENT_PRICE_IN_KG;
        final long tax = productPrice + shipmentPrice;

        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.CURRENCY, "RP");
        viewItemParams.putLong(FirebaseAnalytics.Param.VALUE, productPrice);
        viewItemParams.putLong(FirebaseAnalytics.Param.SHIPPING, shipmentPrice);
        viewItemParams.putLong(FirebaseAnalytics.Param.TAX, tax);
        viewItemParams.putString(FirebaseAnalytics.Param.PAYMENT_TYPE, paymentService);
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS, parcelables);
        return viewItemParams;
    }

    /*
     * Log Event {FirebaseAnalytics.Event.REFUND}
     * */
    public static Bundle getAnalyticsRefund(OrderModel model) {
        final List<ProductOrderModel> products = model.getProducts();
        final int length = products.size();
        final Parcelable[] parcelables = new Parcelable[length];

        int productPrice = 0;
        long productWeight = 0;
        int totalItem = 0;

        for (int i=0; i<length; i++) {
            final ProductOrderModel pom = products.get(i);
            if (pom != null) {
                parcelables[i] = getOrderProductData(pom,i+1);

                final int selected = pom.getProductAmount();
                productPrice += (selected * pom.getProductPrice());
                productWeight += (selected * pom.getProductWeight());
                totalItem += pom.getProductAmount();
            }
        }

        final long shipmentPrice = UtilsMain.getWeight(productWeight) * Constants.SHIPMENT_PRICE_IN_KG;
        final long tax = productPrice + shipmentPrice;

        final Bundle viewItemParams = new Bundle();
        viewItemParams.putString(FirebaseAnalytics.Param.CURRENCY, "RP");
        viewItemParams.putString(FirebaseAnalytics.Param.TRANSACTION_ID, model.getId());
        viewItemParams.putLong(FirebaseAnalytics.Param.VALUE, productPrice);
        viewItemParams.putLong(FirebaseAnalytics.Param.SHIPPING, shipmentPrice);
        viewItemParams.putLong(FirebaseAnalytics.Param.TAX, tax);
        viewItemParams.putLong(FirebaseAnalytics.Param.QUANTITY, totalItem);
        viewItemParams.putParcelableArray(FirebaseAnalytics.Param.ITEMS, parcelables);
        return viewItemParams;
    }

    private static Bundle getProductData(ProductModel productModel, int index) {
        final Bundle product = new Bundle();
        product.putString(FirebaseAnalytics.Param.ITEM_ID, productModel.getId());
        product.putString(FirebaseAnalytics.Param.ITEM_NAME, productModel.getName());
        product.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, productModel.getIdCategory());
        product.putString(FirebaseAnalytics.Param.ITEM_VARIANT, productModel.getMaterial());
        product.putString(FirebaseAnalytics.Param.ITEM_BRAND, productModel.getBrand());
        product.putLong(FirebaseAnalytics.Param.PRICE, productModel.getPrice());

        final Bundle itemsWithIndex = new Bundle(product);
        itemsWithIndex.putLong(FirebaseAnalytics.Param.INDEX, index);
        return itemsWithIndex;
    }

    private static Bundle getWishlistData(WishlistModel wishlistModel, int index) {
        final Bundle product = new Bundle();
        product.putString(FirebaseAnalytics.Param.ITEM_ID, wishlistModel.getProductId());
        product.putString(FirebaseAnalytics.Param.ITEM_NAME, wishlistModel.getProductName());
        product.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, wishlistModel.getProductCategory());
        product.putLong(FirebaseAnalytics.Param.PRICE, wishlistModel.getProductPrice());

        final Bundle itemsWithIndex = new Bundle(product);
        itemsWithIndex.putLong(FirebaseAnalytics.Param.INDEX, index);
        return itemsWithIndex;
    }

    private static Bundle getCartData(CartModel cartModel, int index) {
        final Bundle product = new Bundle();
        product.putString(FirebaseAnalytics.Param.ITEM_ID, cartModel.getProductId());
        product.putString(FirebaseAnalytics.Param.ITEM_NAME, cartModel.getProductName());
        product.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, cartModel.getProductCategory());
        product.putInt(FirebaseAnalytics.Param.QUANTITY, cartModel.getProductSelected());
        product.putLong(FirebaseAnalytics.Param.PRICE, cartModel.getProductPrice());

        final Bundle itemsWithIndex = new Bundle(product);
        itemsWithIndex.putLong(FirebaseAnalytics.Param.INDEX, index);
        return itemsWithIndex;
    }

    private static Bundle getOrderProductData(ProductOrderModel pom, int index) {
        final Bundle product = new Bundle();
        product.putString(FirebaseAnalytics.Param.ITEM_ID, pom.getProductId());
        product.putString(FirebaseAnalytics.Param.ITEM_NAME, pom.getProductName());
        product.putInt(FirebaseAnalytics.Param.QUANTITY, pom.getProductAmount());
        product.putLong(FirebaseAnalytics.Param.PRICE, pom.getProductPrice());

        final Bundle itemsWithIndex = new Bundle(product);
        itemsWithIndex.putLong(FirebaseAnalytics.Param.INDEX, index);
        return itemsWithIndex;
    }
}
