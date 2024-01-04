package com.med1altd.movieflix;

import static android.view.View.GONE;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private String
            JSON_URL;

    RelativeLayout
            mainLayout,
            progressLayout,
            notLiveLayout,
            noInternetLayout;

    Fragment
            selectedFragment;

    ImageView
            Rate,
            Info,
            Ig,
            Subscribe;

    TextView
            notLiveMessageTxt;

    Button
            tryAgain;

    String
            versionMinimum,
            versionMaximum,
            currentVersionName,
            message,
            ratingUrl,
            submissionUrl,
            infoMessage,
            contactName,
            contactEmail,
            contactSubject,
            contactMessage,
            PrivacyPolicyUrl,
            DMCAUrl,
            MoreImageUrl,
            NoImageUrl,
            DMCAImageUrl,
            CodeExample,
            AddContentFormUrl;

    Boolean
            isLive = false,
            isTest = false,
            isWorking = false,
            isShowingBannerAds = false,
            isShowingInterstitialAds = false,
            isPremium = false,
            isRatingVisible = false,
            isInfoVisible = false,
            isSubscriptionLive = false,
            hasInternet = false,
            promote = false,
            MoreContentOption = false;

    Integer
            TVSec,
            VideoSec,
            MoviesInfoSec,
            SeriesInfoSec,
            dailyInterstitialAdRequestsLimit;

    private ArrayList<String>
            subscriptionProductsList = new ArrayList<>(),
            pre_unlocked_codes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MovieFlix);
        setContentView(R.layout.activity_main);

        Help.setCanShowInterstitial(true);

        // Get package manager
        PackageManager packageManager = getPackageManager();

        try {

            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

            currentVersionName = packageInfo.versionName;

            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            FirebaseApp.initializeApp(this);

            initialize();

            findViewsById();

            checkInternetConnectionAndGenerateUserKey();

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();

        }

    }

    public void initialize() {

        subscriptionProductsList.add("movieflix_premium");

        //checkSubscription();

        JSON_URL = getResources().getString(R.string.main_json);

    }

    public void findViewsById() {

        mainLayout = findViewById(R.id.mainLayout);
        progressLayout = findViewById(R.id.ProgressLayout);
        notLiveLayout = findViewById(R.id.notLiveLayout);
        notLiveMessageTxt = findViewById(R.id.notLiveText);
        noInternetLayout = findViewById(R.id.no_internet_layout);
        tryAgain = findViewById(R.id.try_again);
        Rate = findViewById(R.id.Rate);
        Info = findViewById(R.id.info);
        Ig = findViewById(R.id.Ig);
        Subscribe = findViewById(R.id.NoAds);

        Rate.setVisibility(GONE);
        Info.setVisibility(GONE);

    }

    public void checkInternetConnectionAndGenerateUserKey() {

        hasInternet = new Help().isConnected(this);

        if (hasInternet) {

            //User_Key = new Help().checkIfUserKeyHasBeenCreated(this);

            new Help().readAndSaveJSONAdMobLimits(this, new UrlToJSON() {
                @Override
                public String onSuccess(String result) {

                    checkInternetConnection();

                    return null;
                }

            });

        } else {

            noInternetLayout.setVisibility(View.VISIBLE);

            onRetryClickListener(true);

            progressLayout.setVisibility(View.INVISIBLE);

        }

    }

    public void checkInternetConnection() {

        hasInternet = new Help().isConnected(this);

        if (hasInternet) {

            progressLayout.setVisibility(View.VISIBLE);
            noInternetLayout.setVisibility(View.GONE);

            setupClickListeners();

            new Help().urlToJson(this, JSON_URL, new UrlToJSON() {
                @Override
                public String onSuccess(String result) {

                    if (result != null) {

                        try {

                            JSONObject jsonObject = new JSONObject(result);

                            JSONArray jsonArrayRating = jsonObject.getJSONArray("Rating");

                            JSONArray jsonArrayStatus = jsonObject.getJSONArray("Status");

                            JSONObject jsonObjectStatus = jsonArrayStatus.getJSONObject(0);
                            JSONObject jsonObjectRating = jsonArrayRating.getJSONObject(0);

                            isLive = jsonObjectStatus.getBoolean("isLive");
                            isTest = jsonObjectStatus.getBoolean("isTest");
                            promote = jsonObjectStatus.getBoolean("promote");
                            isWorking = jsonObjectStatus.getBoolean("isWorking");
                            isShowingBannerAds = jsonObjectStatus.getBoolean("showBannerAds");
                            isShowingInterstitialAds = jsonObjectStatus.getBoolean("showInterstitialAds");
                            MoreContentOption = jsonObjectStatus.getBoolean("AddContentOption");

                            MoreImageUrl = jsonObjectStatus.getString("MoreImageUrl");

                            NoImageUrl = jsonObjectStatus.getString("NoImageUrl");

                            contactName = jsonObjectStatus.getString("contactName");

                            contactEmail = jsonObjectStatus.getString("contactEmail");

                            contactSubject = jsonObjectStatus.getString("contactSubject");

                            contactMessage = jsonObjectStatus.getString("contactMessage");

                            PrivacyPolicyUrl = jsonObjectStatus.getString("PrivacyPolicyUrl");

                            AddContentFormUrl = jsonObjectStatus.getString("AddContentFormUrl");

                            DMCAUrl = jsonObjectStatus.getString("DMCAUrl");

                            DMCAImageUrl = jsonObjectStatus.getString("DMCAImageUrl");

                            CodeExample = jsonObjectStatus.getString("CodeExample");

                            message = jsonObjectStatus.getString("Message");

                            TVSec = jsonObjectStatus.getInt("TVDelay");

                            VideoSec = jsonObjectStatus.getInt("VideoDelay");

                            MoviesInfoSec = jsonObjectStatus.getInt("MoviesInfoDelay");

                            SeriesInfoSec = jsonObjectStatus.getInt("SeriesInfoDelay");

                            dailyInterstitialAdRequestsLimit = jsonObjectStatus.getInt("MaxDailyAdRequests");

                            versionMinimum = jsonObjectStatus.getString("minVersion");

                            versionMaximum = jsonObjectStatus.getString("maxVersion");

                            versionMinimum = versionMinimum.replace(".", "");

                            currentVersionName = currentVersionName.replace(".", "");

                            versionMaximum = versionMaximum.replace(".", "");

                            Integer minVersion = Integer.valueOf(versionMinimum),
                                    curVersion = Integer.valueOf(currentVersionName),
                                    maxVersion = Integer.valueOf(versionMaximum);

                            if (curVersion >= minVersion && curVersion <= maxVersion) {

                                if (promote) {

                                    Intent intent = new Intent(MainActivity.this, PromoteActivity.class);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    intent.putExtra("promoteTitle", jsonObjectStatus.getString("promoteTitle"));

                                    intent.putExtra("promoteUrl", jsonObjectStatus.getString("promoteUrl"));

                                    intent.putExtra("promoteImageUrl", jsonObjectStatus.getString("promoteImageUrl"));

                                    intent.putExtra("promoteButtonTitle", jsonObjectStatus.getString("promoteButtonTitle"));

                                    intent.putExtra("promoteNotes", jsonObjectStatus.getString("promoteNotes"));

                                    startActivity(intent);

                                } else {

                                    if (isLive) {

                                        isRatingVisible = jsonObjectRating.getBoolean("isRatingVisible");
                                        ratingUrl = jsonObjectRating.getString("RatingUrl");

                                        isInfoVisible = jsonObjectRating.getBoolean("isInfoVisible");
                                        infoMessage = jsonObjectRating.getString("InfoText");
                                        isSubscriptionLive = jsonObjectRating.getBoolean("isSubscriptionVisible");

                                        submissionUrl = jsonObjectRating.getString("SubmissionUrl");

                                        JSONArray jsonArrayPreUnlocked = jsonObject.getJSONArray("Pre-Unlocked");

                                        for ( int i = 0; i < jsonArrayPreUnlocked.length(); i ++ ) {

                                            pre_unlocked_codes.add(jsonArrayPreUnlocked.getJSONObject(i).getString("Code"));

                                        }

                                        for (int i = 0; i < pre_unlocked_codes.size(); i++ ) {

                                            new Help().checkCodeAndUnlock(getApplicationContext(), pre_unlocked_codes.get(i), true, null, new UrlToJSON() {
                                                @Override
                                                public String onSuccess(String result) {

                                                    return null;

                                                }

                                            });

                                        }

                                        if (isRatingVisible) {
                                            Rate.setVisibility(View.VISIBLE);
                                        } else {
                                            Rate.setVisibility(View.GONE);
                                        }

                                        if (isInfoVisible) {
                                            Info.setVisibility(View.VISIBLE);
                                        } else {
                                            Info.setVisibility(View.GONE);
                                        }

                                        if (isSubscriptionLive) {
                                            Subscribe.setVisibility(View.VISIBLE);
                                        } else {
                                            Subscribe.setVisibility(View.GONE);
                                        }

                                        mainLayout.setVisibility(View.VISIBLE);

                                        bottomNavigationView();

                                    } else {

                                        notLiveMessageTxt.setText(message);

                                        notLiveLayout.setVisibility(View.VISIBLE);

                                        progressLayout.setVisibility(View.GONE);

                                    }

                                }

                            } else {

                                notLiveMessageTxt.setText(message);

                                notLiveLayout.setVisibility(View.VISIBLE);

                                mainLayout.setVisibility(GONE);

                                progressLayout.setVisibility(View.GONE);

                            }

                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("PrivacyPolicyMessage", infoMessage).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("RatingUrl", ratingUrl).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("SubmissionUrl", submissionUrl).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isWorking", isWorking).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isTestMode", isTest).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isShowingBannerAds", isShowingBannerAds).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isShowingInterstitialAds", isShowingInterstitialAds).apply();

                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("DMCAImageUrl", DMCAImageUrl).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("MoreImageUrl", MoreImageUrl).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("NoImageUrl", NoImageUrl).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("contactName", contactName).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("contactEmail", contactEmail).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("contactSubject", contactSubject).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("contactMessage", contactMessage).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("PrivacyPolicyUrl", PrivacyPolicyUrl).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("DMCAUrl", DMCAUrl).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("CodeExample", CodeExample).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("MoreContentOption", MoreContentOption).apply();
                            //PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("MoreContentOption", false).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("Premium", false).apply();

                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("TVDelay", TVSec).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("VideoDelay", VideoSec).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("MoviesInfoDelay", MoviesInfoSec).apply();
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("SeriesInfoDelay", SeriesInfoSec).apply();

                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("MaxDailyAdRequests", dailyInterstitialAdRequestsLimit).apply();

                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("AddContentFormUrl", AddContentFormUrl).apply();

                        } catch (JSONException e) {

                            throw new RuntimeException(e);

                        }

                    } else {

                        Toast.makeText(MainActivity.this, "Error 711\nΠαρακαλούμε επανεκκινήστε την εφαρμογή!", Toast.LENGTH_SHORT).show();

                    }

                    return null;

                }

            });

        } else {

            noInternetLayout.setVisibility(View.VISIBLE);

            onRetryClickListener(false);

            progressLayout.setVisibility(View.INVISIBLE);

        }

    }

    BottomNavigationView
            bottomNavigationView;

    Boolean
            canBeTransmited = true;

    public void bottomNavigationView() {

        new Help().checkStandardJSONUrls(this, new UrlToJSON() {
            @Override
            public String onSuccess(String result) {

                new Help().checkActivatedJSONUrls(MainActivity.this, new UrlToJSON() {

                    @Override
                    public String onSuccess(String result) {

                        new Help().loadJSONUrls(MainActivity.this, new UrlToJSON() {

                            @Override
                            public String onSuccess(String result) {

                                try {

                                    bottomNavigationView = findViewById(R.id.bottomNavigationView);

                                    bottomNavigationView.setVisibility(View.VISIBLE);

                                    selectedFragment = new PageTV();

                                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                                    transaction.replace(R.id.fragment_container_main, selectedFragment);

                                    if (!getSupportFragmentManager().isDestroyed()) {

                                        transaction.commit();

                                    }

                                    bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                                        @Override
                                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                                            if (item.getItemId() == R.id.tv) {

                                                selectedFragment = new PageTV();

                                            } else if (item.getItemId() == R.id.tainies) {

                                                selectedFragment = new PageMovies();

                                            } else if (item.getItemId() == R.id.seires) {

                                                selectedFragment = new PageSeries();

                                            } else if (item.getItemId() == R.id.paidika) {

                                                selectedFragment = new PageKids();

                                            }

                                            if (!isFinishing()
                                                    && !isDestroyed()
                                                    && !getSupportFragmentManager().isStateSaved()
                                                    && !getSupportFragmentManager().isDestroyed()
                                                    && selectedFragment != null
                                                    && canBeTransmited) {

                                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                                                transaction.replace(R.id.fragment_container_main, selectedFragment);

                                                transaction.commit();

                                            }

                                            return true;

                                        }

                                    });

                                    progressLayout.setVisibility(View.GONE);

                                    if (isShowingBannerAds || isShowingInterstitialAds) {

                                        chat();

                                    }

                                } catch (Exception e) {

                                    throw new RuntimeException(e);

                                }

                                return null;

                            }

                        });

                        return null;

                    }

                });

                return null;

            }

        });

    }

    public void setupClickListeners() {

        onRateClickListener();

        onInfoClickListener();

        onIgClickListener();

        onSubscribeClickListener();

    }

    private ConsentInformation
            consentInformation;

    public void chat() {

        // Set tag for under age of consent. false means users are not under age of consent.

        ConsentRequestParameters params = new ConsentRequestParameters.Builder()

                .setTagForUnderAgeOfConsent(false)

                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            loadAndShowError -> {

                                if (loadAndShowError != null) {

                                    // Consent gathering failed.

                                }

                                // Consent has been gathered.

                                if (consentInformation.canRequestAds()) {

                                    initializeAdmob();

                                }

                            }

                    );

                },

                requestConsentError -> {

                    // Consent gathering failed

                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel

        // while checking for new consent information. Consent obtained in

        // the previous session can be used to request ads.

        if (consentInformation.canRequestAds()) {

            initializeAdmob();

        }

    }

    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    public void initializeAdmob() {

        if (isMobileAdsInitializeCalled.getAndSet(true)) {

            // Initialize the AdMob SDK here

            MobileAds.initialize(this, initializationStatus -> {

                // Add the code to handle the initialization completion

                // (e.g., check if initialization was successful)

            });

        }

    }

    public void checkSubscription() {

        BillingClient billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {
        }).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    if (list.size() > 0) {

                                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("Premium", true).apply();

                                        int i = 0;

                                        for (Purchase purchase : list) {

                                            //Here you can manage each product, if you have multiple subscription
                                            i++;

                                        }

                                    } else {

                                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("Premium", false).apply();

                                    }

                                }

                            });

                }

            }

        });

    }

    public void onRateClickListener() {

        new Help().setupAnimationOnClick(Rate, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ratingUrl));

                startActivity(intent);

            }

        });

    }

    public void onInfoClickListener() {

        new Help().setupAnimationOnClick(Info, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                Intent intent = new Intent(MainActivity.this, InfoActivity.class);

                startActivity(intent);

            }

        });

    }

    public void onRetryClickListener(boolean isGenerateUserKeyState) {

        new Help().setupAnimationOnClick(tryAgain, 0.85f, new OnViewClickListener() {
            @Override
            public void onClick() {

                progressLayout.setVisibility(View.VISIBLE);

                if (isGenerateUserKeyState) {

                    checkInternetConnectionAndGenerateUserKey();

                } else {

                    checkInternetConnection();

                }

            }

        });

    }

    public void onIgClickListener() {

        new Help().setupAnimationOnClick(Ig, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/movieflixgr"));

                startActivity(intent);

            }

        });

    }

    public void onSubscribeClickListener() {

        new Help().setupAnimationOnClick(Subscribe, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                Intent intent = new Intent(MainActivity.this, SubscriptionsActivity.class);

                startActivity(intent);

            }

        });

    }

    public DisplayMetrics displayMetrics() {

        return this.getResources().getDisplayMetrics();

    }

    public void setCanBeTransmited(Boolean can) {

        canBeTransmited = can;

    }

    public void reloadPageTVAfterActivatedNewCode() {

        selectedFragment = new PageTV();

        if (selectedFragment != null && canBeTransmited) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_main, selectedFragment);

            if (!getSupportFragmentManager().isDestroyed()) {

                transaction.commitAllowingStateLoss();

                Toast.makeText(this, "New Version Unlocked!", Toast.LENGTH_SHORT).show();

            }

        }

    }

}