package com.rikyahmadfathoni.test.opaku.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rikyahmadfathoni.test.opaku.Constants;
import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.adapter.PaymentAdapter;
import com.rikyahmadfathoni.test.opaku.adapter.SimpleProductAdapter;
import com.rikyahmadfathoni.test.opaku.databinding.ActivityCheckoutBinding;
import com.rikyahmadfathoni.test.opaku.databinding.MergeProductListBinding;
import com.rikyahmadfathoni.test.opaku.model.AddressModel;
import com.rikyahmadfathoni.test.opaku.model.BankModel;
import com.rikyahmadfathoni.test.opaku.model.CreditCardModel;
import com.rikyahmadfathoni.test.opaku.model.OrderModel;
import com.rikyahmadfathoni.test.opaku.model.PaymentModel;
import com.rikyahmadfathoni.test.opaku.model.ProductModel;
import com.rikyahmadfathoni.test.opaku.model.ProductOrderModel;
import com.rikyahmadfathoni.test.opaku.store.model.CartModel;
import com.rikyahmadfathoni.test.opaku.store.viewmodel.CartViewModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsDialog;
import com.rikyahmadfathoni.test.opaku.utils.UtilsList;
import com.rikyahmadfathoni.test.opaku.utils.UtilsMain;
import com.rikyahmadfathoni.test.opaku.utils.UtilsNumber;
import com.rikyahmadfathoni.test.opaku.utils.UtilsString;
import com.rikyahmadfathoni.test.opaku.utils.UtilsThread;
import com.rikyahmadfathoni.test.opaku.view.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RESULT_CODE_PAYMENT_BANK = 300;
    public static final int RESULT_CODE_PAYMENT_CARD = 400;
    public static final String KEY_MODEL_LIST = "KEY_MODEL_LIST";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final OrderModel orderModel = new OrderModel();
    private CartViewModel cartViewModel;
    private ActivityCheckoutBinding binding;
    private ArrayList<CartModel> cartModels;
    private PaymentAdapter paymentAdapter;
    private ProgressView progressView;
    private MergeProductListBinding productListBinding;
    private AddressModel addressModel;
    private BankModel bankModel;
    private FirebaseAnalytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        productListBinding = MergeProductListBinding.bind(binding.subTotal.getRoot());
        setContentView(binding.getRoot());

        bindObject();
        bindIntent(getIntent());
        bindHeader();
        bindAddress();
        bindPaymentMethod();
        bindProductPrice();
        bindEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_PAYMENT_BANK) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    bankModel = data.getParcelableExtra(BankSelectorActivity.KEY_BANK_MODEL);
                    if (bankModel != null) {
                        orderModel.setBankId(bankModel.getId());
                        orderModel.setPaymentId(bankModel.getPaymentId());
                        orderModel.setPaymentService(bankModel.getBankAccount() + " - " + bankModel.getBankAccountNumber());
                        notifyPaymentMethod(bankModel);
                        inValidateInput();

                        //log analytics
                        analytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO,
                                UtilsMain.getAnalyticsPayment(cartModels, orderModel.getPaymentService()));
                    }
                }
            }
        } else if (requestCode == RESULT_CODE_PAYMENT_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    CreditCardModel cardModel = data.getParcelableExtra(CardSelectorActivity.KEY_CARD_MODEL);
                    if (cardModel != null) {
                        orderModel.setPaymentId(cardModel.getPaymentId());
                        orderModel.setPaymentService("VISA" + " - " + cardModel.getCardNumber());
                        notifyPaymentMethod(cardModel);
                        inValidateInput();

                        //log analytics
                        analytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO,
                                UtilsMain.getAnalyticsPayment(cartModels, orderModel.getPaymentService()));
                    }
                }
            }
        } else if (requestCode == AddressActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    addressModel = data.getParcelableExtra(AddressActivity.KEY_ADDRESS_MODEL);
                    if (addressModel != null) {
                        if (binding != null) {
                            binding.address.textAddress.setText(
                                    String.format("%s\n%s", addressModel.getName(),
                                            addressModel.getFullAddress()));
                        }
                        inValidateInput();
                    }
                }
            }
        }
    }

    private void notifyPaymentMethod(@NonNull BankModel bankModel) {
        if (paymentAdapter == null) {
            return;
        }
        final int indexPayment = paymentAdapter.getIndex(Constants.ID_PAYMENT_BANK);
        if (indexPayment >= 0) {
            final PaymentModel pm = paymentAdapter.getItem(indexPayment);
            if (pm != null) {
                pm.setMethodId(bankModel.getId());
                pm.setPaymentDescription(bankModel.getBankAccount() + " - " + bankModel.getBankHolderName());
                paymentAdapter.notifyItemChanged(indexPayment);
            }
        }
    }

    private void notifyPaymentMethod(@NonNull CreditCardModel creditCardModel) {
        if (paymentAdapter == null) {
            return;
        }
        final int indexPayment = paymentAdapter.getIndex(Constants.ID_PAYMENT_CARD);
        if (indexPayment >= 0) {
            final PaymentModel pm = paymentAdapter.getItem(indexPayment);
            if (pm != null) {
                pm.setMethodId(creditCardModel.getId());
                pm.setPaymentDescription("VISA" + " - " + creditCardModel.getCardNumber());
                paymentAdapter.notifyItemChanged(indexPayment);
            }
        }
    }

    private final TextWatcher addressWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            final String value = editable.toString();
            if (!UtilsString.isEmpty(value)) {
                orderModel.setDestinationAddress(value);
                inValidateInput();
            }
        }
    };

    private void bindObject() {
        this.analytics = FirebaseAnalytics.getInstance(this);
        this.cartViewModel = CartViewModel.getInstance(getApplication());
        this.progressView = new ProgressView(getBaseContext());
        this.progressView.show(binding.getRoot());
    }

    private List<ProductOrderModel> getProducts() {
        final List<ProductOrderModel> products = new ArrayList<>();
        for (CartModel cartModel : cartModels) {
            products.add(new ProductOrderModel(cartModel));
        }
        return products;
    }

    private void bindIntent(Intent intent) {
        if (intent != null) {
            cartModels = intent.getParcelableArrayListExtra(KEY_MODEL_LIST);
        }
    }

    private void bindHeader() {
        binding.header.textTitle.setText(R.string.title_activity_checkout);
    }

    private void bindEvent() {
        orderModel.setCustomerId(getCustomerId());
        orderModel.setDestinationAddress(binding.address.textAddress.getText().toString());
        orderModel.setProducts(getProducts());

        binding.header.iconBack.setOnClickListener(this);
        binding.buttonAddCart.setOnClickListener(this);
        binding.address.textChangeAddress.setOnClickListener(this);
        binding.address.textAddress.addTextChangedListener(addressWatcher);

        //log analytics
        analytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT,
                UtilsMain.getAnalyticsCheckout(cartModels));
    }

    private String getCustomerId() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    private void bindAddress() {
        binding.address.title.text.setText(getString(R.string.title_address));
        binding.address.title.icon.setImageResource(R.drawable.ic_pin);
    }

    private void bindPaymentMethod() {
        binding.paymentMethod.title.text.setText(getString(R.string.title_payment_method));
        binding.paymentMethod.title.icon.setImageResource(R.drawable.ic_wallet);
        paymentAdapter = new PaymentAdapter();
        binding.paymentMethod.listPayment.setAdapter(paymentAdapter);
        binding.paymentMethod.listPayment.setItemAnimator(null);
        getPaymentMethod();
    }

    private void getPaymentMethod() {
        db.collection(Constants.DB_COLLECTION_PAYMENT).get().addOnSuccessListener(documentSnapshot -> {
            final List<DocumentSnapshot> dss = documentSnapshot.getDocuments();
            final List<PaymentModel> paymentModels = new ArrayList<>();
            for (DocumentSnapshot ds : dss) {
                final PaymentModel paymentModel = ds.toObject(PaymentModel.class);
                if (paymentModel != null) {
                    paymentModel.setId(ds.getId());
                    paymentModels.add(paymentModel);
                }
            }
            paymentAdapter.setPaymentModelList(paymentModels);
            paymentAdapter.notifyDataSetChanged();
            if (progressView != null) {
                progressView.dismiss();
            }
        }).addOnFailureListener(e -> {
            if (progressView != null) {
                progressView.dismiss();
            }
        });
    }

    private void bindProductPrice() {
        productListBinding.title.text.setText(R.string.info_detail_produk);
        productListBinding.title.icon.setImageResource(R.drawable.ic_sheets);
        productListBinding.message.textName.setText(getString(R.string.info_message));
        productListBinding.message.textValue.setHint(getString(R.string.hint_leave_message));
        productListBinding.productPrice.textName.setText(getString(R.string.info_sub_product_price));
        productListBinding.shipmentPrice.textName.setText(getString(R.string.info_sub_payment_price));
        productListBinding.totalPrice.textName.setText(getString(R.string.info_total_price));

        SimpleProductAdapter simpleProductAdapter = new SimpleProductAdapter(this);
        productListBinding.listItem.setAdapter(simpleProductAdapter);

        simpleProductAdapter.submitList(getProdukModels());

        getSubTotal();
    }

    private List<ProductOrderModel> getProdukModels() {
        final List<ProductOrderModel> productOrderModels = new ArrayList<>();
        for (CartModel cartModel : cartModels) {
            productOrderModels.add(new ProductOrderModel(cartModel));
        }
        return productOrderModels;
    }

    private void getSubTotal() {
        if (cartModels == null) {
            return;
        }
        UtilsThread.runOnThread(() -> {
            if (cartModels != null) {
                long productPrice = 0;
                long productWeight = 0;
                int totalItem = 0;
                for (CartModel cartModel : cartModels) {
                    final int selected = cartModel.getProductSelected();
                    productPrice += (selected * cartModel.getProductPrice());
                    totalItem += selected;
                    productWeight += (selected * cartModel.getProductWeight());
                }
                final long price = productPrice;
                final long weight = UtilsMain.getWeight(productWeight);
                final long shipmentPrice = weight * Constants.SHIPMENT_PRICE_IN_KG;
                final long totalPrice = price + shipmentPrice;

                if (binding != null) {
                    orderModel.setTotalItem(totalItem);
                    orderModel.setTotalWeight(productWeight);
                    orderModel.setProductPrice(productPrice);
                    orderModel.setShipmentPrice(shipmentPrice);
                    orderModel.setTotalPrice(totalPrice);
                    productListBinding.productPrice.textValue.post(() -> {
                        productListBinding.productPrice.textValue.setText(UtilsNumber.formatRupiah(price));
                        productListBinding.shipmentPrice.textValue.setText(UtilsNumber.formatRupiah(shipmentPrice));
                        productListBinding.totalPrice.textValue.setText(UtilsNumber.formatRupiah(totalPrice));
                    });
                }
            }
        });
    }

    private void inValidateInput() {
        if (binding == null) {
            return;
        }
        binding.buttonAddCart.setEnabled(isInputValid());
    }

    private boolean isInputValid() {
        if (orderModel != null) {
            return addressModel != null &&
                    orderModel.getCustomerId() != null
                    && orderModel.getDestinationAddress() != null
                    && orderModel.getPaymentId() != null
                    && orderModel.getPaymentService() != null
                    && !UtilsList.isEmpty(orderModel.getProducts());
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (binding == null) {
            return;
        }
        if (view == binding.buttonAddCart) {
            createOrder();
        } else if (view == binding.header.iconBack) {
            super.onBackPressed();
        } else if (view == binding.address.textChangeAddress) {
            AddressActivity.start(this);
        }
    }

    private void createOrder() {
        if (binding == null) {
            return;
        }
        if (addressModel != null) {
            orderModel.setCustomerName(addressModel.getName());
            orderModel.setCustomerPhone(addressModel.getPhoneNumber());
        }
        orderModel.setStatus(getStatus(orderModel.getPaymentId()));
        orderModel.setCreateDate(Timestamp.now());
        orderModel.setCustomerMessage(productListBinding.message.textValue.getText().toString());
        if (progressView != null) {
            progressView.show(binding.getRoot());
        }
        db.collection(Constants.DB_COLLECTION_ORDERS).add(orderModel).addOnSuccessListener(documentReference -> {
            orderModel.setId(documentReference.getId());
            cartViewModel.deleteAll();
            if (progressView != null) {
                progressView.dismiss();
            }
            updateProductStock();
            if (Constants.ID_PAYMENT_BANK.equals(orderModel.getPaymentId())) {
                BankOrderActivity.start(this, orderModel, bankModel);
            } else {
                UtilsDialog.showToast(CheckoutActivity.this,
                        getString(R.string.message_success_checkout), Gravity.CENTER);
            }

            //log analytics
            analytics.logEvent(FirebaseAnalytics.Event.PURCHASE,
                    UtilsMain.getAnalyticsPurchase(cartModels, orderModel.getPaymentService()));

            finish();
        }).addOnFailureListener(e -> {
            UtilsDialog.showToast(CheckoutActivity.this,
                    getString(R.string.message_failed_checkout), Gravity.CENTER);
            if (progressView != null) {
                progressView.dismiss();
            }
        });
    }

    private int getStatus(String paymentId) {
        return paymentId.equals(Constants.ID_PAYMENT_BANK)
                ? Constants.ORDER_STATUS_WAITING_FOR_PAYMENT : Constants.ORDER_STATUS_PAYMENT;
    }

    private void updateProductStock() {
        final List<ProductOrderModel> orderModels = orderModel.getProducts();
        for (ProductOrderModel pom : orderModels) {
            db.collection(Constants.DB_COLLECTION_PRODUCTS)
                    .document(pom.getProductId())
                    .get().addOnSuccessListener(documentSnapshot -> {
                final ProductModel pm = documentSnapshot.toObject(ProductModel.class);
                if (pm != null) {
                    updateProductStock(documentSnapshot.getId(), pm.getStock() - pom.getProductAmount());
                }
            }).addOnFailureListener(e -> {

            });
        }
    }

    private void updateProductStock(String poductId, int stock) {
        if (stock >= 0) {
            db.collection(Constants.DB_COLLECTION_PRODUCTS)
                    .document(poductId).update("stock", stock)
                    .addOnSuccessListener(aVoid -> {

                    }).addOnFailureListener(e -> {

            });
        } else {
            UtilsDialog.showToast(this, getString(R.string.message_update_stock_invalid));
        }
    }
}