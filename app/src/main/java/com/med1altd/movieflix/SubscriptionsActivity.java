package com.med1altd.movieflix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class SubscriptionsActivity extends AppCompatActivity {

    BillingClient billingClient;

    Button subscribeButton;

    TextView statusText, errorText;

    Boolean isPremium = false;

    Integer supported = 0;

    ImageView Back, Rate, Info;

    String RatingUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        isPremium = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Premium", false);

        RatingUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Rating", "");

        Back = findViewById(R.id.Back);
        Rate = findViewById(R.id.Rate);
        Info = findViewById(R.id.info);
        statusText = findViewById(R.id.StatusTxt);
        errorText = findViewById(R.id.ErrorText);
        subscribeButton = findViewById(R.id.SubscriptionBtn);

        if (isPremium) {
            statusText.setText("Κατάσταση Συνδρομής: Premium Έκδοση");
        } else {
            statusText.setText("Κατάσταση Συνδρομής: Δωρεάν Έκδοση");
        }

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        new PurchasesUpdatedListener() {
                            @Override
                            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                                if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && list !=null) {
                                    checkSubscription();
                                }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        //Toast.makeText(this, "Initialize", Toast.LENGTH_SHORT).show();
        establishConnection();

        onBackClickListener();

        onSubscribeButtonOnClickSetListener();

        onRateClickListener();

        onInfoClickListener();

    }

    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts();
                } else {
                    supported = 1;
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    void showProducts() {

        ImmutableList productList = ImmutableList.of(
                //Product 1 = index is 0
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("movieflix_premium")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()

        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, productDetailsList) -> {
                    // Process the result
                    for (ProductDetails productDetails : productDetailsList) {
                        if (productDetails.getProductId().equals("movieflix_premium")) {
                            List subDetails = productDetails.getSubscriptionOfferDetails();
                            assert subDetails != null;

                            subscribeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    supported = 2;

                                    launchPurchaseFlow(productDetails);

                                }
                            });

                        } else {

                            supported = 1;

                        }
                    }
                }
        );

    }

    void launchPurchaseFlow(ProductDetails productDetails) {

        assert productDetails.getSubscriptionOfferDetails() != null;
        ImmutableList productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();
        BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);

    }

    protected void onResume() {
        super.onResume();
        checkSubscription();
    }

    void checkSubscription(){

        BillingClient billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {}).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){
                                    if(list.size()>0){
                                        PreferenceManager.getDefaultSharedPreferences(SubscriptionsActivity.this).edit().putBoolean("Premium", true).apply();
                                        statusText.setText("Κατάσταση Συνδρομής: Premium Έκδοση");
                                        int i = 0;
                                        for (Purchase purchase: list){
                                            //Here you can manage each product, if you have multiple subscription
                                            handlePurchase(purchase);
                                            i++;
                                        }
                                    }else {
                                        PreferenceManager.getDefaultSharedPreferences(SubscriptionsActivity.this).edit().putBoolean("Premium", false).apply();
                                        statusText.setText("Κατάσταση Συνδρομής: Δωρεάν Έκδοση");
                                    }

                                }

                            });

                }

            }

        });

    }

    public void onBackClickListener() {

        new Help().setupAnimationOnClick(Back, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                finish();

            }

        });

    }

    public void onRateClickListener() {

        new Help().setupAnimationOnClick(Rate, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(RatingUrl));

                startActivity(intent);

            }

        });

    }

    public void onInfoClickListener() {

        new Help().setupAnimationOnClick(Info, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                Intent intent = new Intent(SubscriptionsActivity.this, InfoActivity.class);

                startActivity(intent);

            }

        });

    }

    public void onSubscribeButtonOnClickSetListener() {

        new Help().setupAnimationOnClick(subscribeButton, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                if (supported == 1) {

                    Toast.makeText(SubscriptionsActivity.this, "Η Συσκευή σας δεν υποστηρίζει Google Play Services!", Toast.LENGTH_SHORT).show();

                    subscribeButton.setVisibility(View.GONE);

                    errorText.setVisibility(View.VISIBLE);

                }

            }

        });

    }

    void handlePurchase(Purchase purchase) {

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }

    }

    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(@androidx.annotation.NonNull BillingResult billingResult) {

        }
    };

}