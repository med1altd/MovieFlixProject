package com.med1altd.movieflix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class SeriesInfoActivity extends AppCompatActivity {

    RelativeLayout progressLayout;

    LinearLayout DescriptionLinearLayout, PlayButton;

    ImageView imageBack, imageMain, BackButton;

    TextView TitleText, YearText, GenreText, DurationText, DescriptionIndicatorText, DescriptionText;

    ProgressBar progressBarBack, progressBarMain;

    String JSON_URL, TitleSeries, TitleEpisodes, Title, ImageBack, ImageMain, Video, DescriptionSeries, DescriptionEpisodes, YearSeries, YearSeasons, DateEpisodes, Genre, DurationSeries, DurationEpisodes;

    Integer posSeries, posSeasons, posEpisodes;

    String type, startString, endString;

    Integer start, end, posStart, posEnd;

    Boolean fetchVideo = false;

    Integer beforeEnd;

    InterstitialAd
            mInterstitialAd;

    private String AD_INTERSTITIAL_ID;

    Integer randomPos;

    Boolean
            isTest = true,
            isPremium = false,
            showedAd = false,
            isShowingInterstitialAds = false,
            isShowingBannerAds = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_info);

        isRunning = true;

        isTest = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isTestMode", false);

        isPremium = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Premium", false);

        isShowingBannerAds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isShowingBannerAds", false);

        isShowingInterstitialAds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isShowingInterstitialAds", false);

        AD_INTERSTITIAL_ID = getResources().getString(R.string.Interstitial_Series_Open_Id);

        JSON_URL = getIntent().getStringExtra("JSON_URL");
        posSeries = getIntent().getIntExtra("posSeries", -1);
        posSeasons = getIntent().getIntExtra("posSeasons", -1);
        posEpisodes = getIntent().getIntExtra("posEpisodes", -1);

        JSON_URL = getIntent().getStringExtra("JSON_URL");
        //Position = getIntent().getIntExtra("Position", -1);
        //Title = getIntent().getStringExtra("Title");
        //Type = getIntent().getStringExtra("Type");
        //TitleList = getIntent().getStringArrayListExtra("TitleList");

        TitleText = findViewById(R.id.textTitle);
        YearText = findViewById(R.id.textYear);
        GenreText = findViewById(R.id.textGenre);
        DurationText = findViewById(R.id.DurationText);
        DescriptionIndicatorText = findViewById(R.id.textDescriptionIndicator);
        DescriptionText = findViewById(R.id.textDescription);

        BackButton = findViewById(R.id.imageBackButton);
        PlayButton = findViewById(R.id.PlayBtn);

        progressLayout = findViewById(R.id.ProgressBarLayout);

        DescriptionLinearLayout = findViewById(R.id.DescriptionLinearLayout);

        imageBack = findViewById(R.id.imageBack);
        imageMain = findViewById(R.id.imageMain);

        progressBarBack = findViewById(R.id.progressBarBack);
        progressBarMain = findViewById(R.id.progressBarMain);

        Random random = new Random();
        randomPos = random.nextInt(4 - 0) + 0;

        if (!isPremium && isShowingInterstitialAds) {

            Help.notifyWhenNextInterstitial(new UrlToJSON() {
                @Override
                public String onSuccess(String result) {

                    if (isRunning) {

                        loadAndManageInterstitialAd();

                    }

                    return null;

                }

            });

        }

        onBackButtonClickListener();

        if (!isTest) {

            onPlayButtonClickListener();

        } else {

            PlayButton.setVisibility(View.GONE);

        }

        new Help().urlToJson(this, JSON_URL, new UrlToJSON() {
            @Override
            public String onSuccess(String result) {

                try {

                    JSONObject jsonObject = new JSONObject(result);

                    JSONArray jsonArraySeries = jsonObject.getJSONArray("Series");

                    JSONObject jsonObjectSeries = jsonArraySeries.getJSONObject(posSeries);

                    JSONArray jsonArraySeasons = jsonObjectSeries.getJSONArray("Seasons");

                    JSONObject jsonObjectSeasons = jsonArraySeasons.getJSONObject(posSeasons);

                    JSONArray jsonArrayEpisodes = jsonObjectSeasons.getJSONArray("Episodes");

                    JSONObject jsonObjectEpisodes = jsonArrayEpisodes.getJSONObject(posEpisodes);

                    TitleSeries = jsonObjectSeries.getString("Title");

                    TitleEpisodes = jsonObjectEpisodes.getString("Title");

                    ImageBack = jsonObjectSeries.getString("ImageMain");

                    ImageMain = jsonObjectEpisodes.getString("Image");

                    if (jsonObject.getJSONArray("Main").getJSONObject(0).getBoolean("Live")) {

                        Video = jsonObjectEpisodes.getString("Video");

                        fetchVideo = jsonObjectEpisodes.getBoolean("fetchVideo");

                    } else {

                        Video = jsonObject.getJSONArray("Main").getJSONObject(0).getString("Video");

                        fetchVideo = Boolean.valueOf(jsonObject.getJSONArray("Main").getJSONObject(0).getString("Fetch"));

                    }

                    DescriptionSeries = jsonObjectSeries.getString("Description");

                    DescriptionEpisodes = jsonObjectEpisodes.getString("Description");

                    YearSeries = jsonObjectSeries.getString("Year");

                    YearSeasons = jsonObjectSeasons.getString("Year");

                    DateEpisodes = jsonObjectEpisodes.getString("Date");

                    Genre = jsonObjectSeries.getString("Genre");

                    DurationSeries = jsonObjectSeries.getString("Duration");

                    DurationEpisodes = jsonObjectEpisodes.getString("Duration");

                    beforeEnd = jsonObjectEpisodes.getInt("beforeEnd");

                    if (fetchVideo) {

                        Integer videoLength = Video.length();

                        videoLength = videoLength - beforeEnd;

                        Video = Video.substring(0, videoLength);

                        startString = jsonObjectEpisodes.getString("StartString");

                        endString = jsonObjectEpisodes.getString("EndString");

                        start = jsonObjectEpisodes.getInt("Start");

                        end = jsonObjectEpisodes.getInt("End");

                        new Help().urlToJson(getApplicationContext(), Video, new UrlToJSON() {
                            @Override
                            public String onSuccess(String result) {

                                try {

                                    if (result.trim().isEmpty()) {

                                        Toast.makeText(SeriesInfoActivity.this, "Error 111: \n Προέκυψε άγνωστο πρόβλημμα! Παρακαλούμε επανεκκινήστε την εφαρμογή!", Toast.LENGTH_SHORT).show();

                                    } else {

                                        //posType = result.indexOf(type);

                                        posStart = result.indexOf(startString);

                                        posEnd = result.indexOf(endString, posStart);

                                        posStart = posStart + start;

                                        posEnd = posEnd - end;

                                        Video = result.substring(posStart, posEnd);

                                    }

                                    load();

                                } catch (Exception e) {

                                    Toast.makeText(SeriesInfoActivity.this, "Error 444: \n Προέκυψε άγνωστο πρόβλημμα! Παρακαλούμε επανεκκινήστε την εφαρμογή!", Toast.LENGTH_SHORT).show();

                                }

                                return null;

                            }

                        });

                    } else {

                        load();

                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;

            }

        });

    }

    public void onBackButtonClickListener() {

        new Help().setupAnimationOnClick(BackButton, 0.9f, new OnViewClickListener() {
            @Override
            public void onClick() {

                onBack();

            }

        });

    }

    public void onPlayButtonClickListener() {

        new Help().setupAnimationOnClick(PlayButton, 0.9f, new OnViewClickListener() {
            @Override
            public void onClick() {

                onPlay();

            }

        });

    }

    void load() {

        Title = TitleSeries + ": " + TitleEpisodes;

        if (Title.trim().isEmpty()) {
            TitleText.setVisibility(View.GONE);
        } else {
            TitleText.setText(Title);
        }

        if (ImageBack.contains("ht")) {

            new Help().showImage(ImageBack, imageBack, progressBarBack, this);

        }

        if (ImageMain.contains("ht")) {

            new Help().showImage(ImageMain, imageMain, progressBarMain, this);

        }

        if (DescriptionEpisodes.trim().isEmpty()) {

            if (DescriptionSeries.trim().isEmpty()) {

                DescriptionLinearLayout.setVisibility(View.GONE);

            } else {

                DescriptionIndicatorText.setText("Περιγραφή Σειράς:");
                DescriptionText.setText(DescriptionSeries);

            }

        } else {

            DescriptionIndicatorText.setText("Περιγραφή Επεισοδίου:");
            DescriptionText.setText(DescriptionEpisodes);

        }

        if (DateEpisodes.trim().isEmpty()) {

            if (YearSeasons.trim().isEmpty()) {

                if (YearSeries.trim().isEmpty()) {

                    YearText.setVisibility(View.GONE);

                } else {

                    YearText.setText(YearSeries);

                }

            } else {

                YearText.setText(YearSeasons);

            }

        } else {

            YearText.setText(DateEpisodes);

        }

        if (Genre.isEmpty()) {

            GenreText.setVisibility(View.GONE);

        } else {

            GenreText.setText(Genre);

        }

        if (DurationEpisodes.trim().isEmpty()) {

            if (DurationSeries.trim().isEmpty()) {

                DurationText.setVisibility(View.GONE);

            } else {

                DurationText.setText(DurationSeries);

            }

        } else {

            DurationText.setText(DurationEpisodes);

        }

        progressLayout.setVisibility(View.GONE);

    }

    Boolean
            hasRequested = false;

    private void loadAndManageInterstitialAd() {

        Context
                context = this;

        new Help().canShowInterstitialBasedOnDailyLimits(context, new ReturnMultipleResults() {
            @Override
            public ArrayList<Object> onSuccessMultipleResults(ArrayList<Object> results) {

                if (results != null) {

                    Long curAdRequests = (Long) results.get(3);

                    if (new Help().compareCurrentAndLimitAdRequests(curAdRequests, new Help().getAdMobLimit(context, "Daily"))) {

                        new Help().canShowInterstitialBasedOnHourLimits(context, new ReturnMultipleResults() {
                            @Override
                            public ArrayList<Object> onSuccessMultipleResults(ArrayList<Object> results) {

                                if (results != null) {

                                    Long curAdRequests = (Long) results.get(3);

                                    if (new Help().compareCurrentAndLimitAdRequests(curAdRequests, new Help().getAdMobLimit(context, results.get(1).toString()))) {

                                        AdRequest adRequest = new AdRequest.Builder().build();

                                        hasRequested = true;

                                        Help.delayNextInterstitialRequest(context, "SeriesInfoDelay");

                                        InterstitialAd.load(context, AD_INTERSTITIAL_ID, adRequest,
                                                new InterstitialAdLoadCallback() {
                                                    @Override
                                                    public void onAdLoaded(@androidx.annotation.NonNull InterstitialAd interstitialAd) {

                                                        super.onAdLoaded(interstitialAd);

                                                        mInterstitialAd = interstitialAd;

                                                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                            @Override
                                                            public void onAdDismissedFullScreenContent() {

                                                                super.onAdDismissedFullScreenContent();

                                                                showedAd = true;

                                                                if (back) {

                                                                    finish();

                                                                } else {

                                                                    progressLayout.setVisibility(View.VISIBLE);

                                                                    playMovie();

                                                                }

                                                            }

                                                            @Override
                                                            public void onAdFailedToShowFullScreenContent(@androidx.annotation.NonNull AdError adError) {

                                                                super.onAdFailedToShowFullScreenContent(adError);

                                                                new Help().addStatisticToTheCount(context, "FailedToShow");

                                                                if (back) {

                                                                    finish();

                                                                } else {

                                                                    playMovie();

                                                                }

                                                            }

                                                            @Override
                                                            public void onAdShowedFullScreenContent() {

                                                                super.onAdShowedFullScreenContent();

                                                                new Help().addStatisticToTheCount(context, "Impressions");

                                                            }

                                                        });

                                                        new Help().addStatisticToTheCount(context, "Requests");

                                                    }

                                                    @Override
                                                    public void onAdFailedToLoad(@androidx.annotation.NonNull LoadAdError loadAdError) {

                                                        super.onAdFailedToLoad(loadAdError);

                                                        mInterstitialAd = null;

                                                        new Help().addStatisticToTheCount(context, "FailedToLoad");

                                                    }

                                                });

                                    }

                                }

                                return null;

                            }

                        });

                    }

                }

                return null;

            }

        });

    }

    Boolean
            isRunning = false;

    @Override
    protected void onPause() {

        super.onPause();

        isRunning = false;

    }

    @Override
    protected void onResume() {

        super.onResume();

        isRunning = true;

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        onBack();

    }

    public void onDestroy() {

        super.onDestroy();

        if (hasRequested) {

            Help.destroyWaitTillNextInterstitial();

        }

    }

    Boolean
            hasShownInterstitial = false,
            back = false;

    public void onBack() {

        if (mInterstitialAd != null &&
                !hasShownInterstitial) {

            back = true;

            mInterstitialAd.show(this);

        } else {

            finish();

        }

    }

    public void onPlay() {

        if (mInterstitialAd != null &&
                !hasShownInterstitial) {

            back = false;

            mInterstitialAd.show(this);

        } else {

            playMovie();

        }

    }

    void playMovie() {

        new Help().pushStringToFirebase(this, "Series", Title);

        Intent intent = new Intent(SeriesInfoActivity.this, VideoStreamingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Title", TitleText.getText());
        intent.putExtra("Video", Video);
        intent.putExtra("Year", YearText.getText());
        intent.putExtra("Genre", GenreText.getText());
        intent.putExtra("Duration", DurationText.getText());
        intent.putExtra("DescriptionIndicator", DescriptionIndicatorText.getText());
        intent.putExtra("Description", DescriptionText.getText());
        intent.putExtra("Type", "Series");
        startActivity(intent);

        progressLayout.setVisibility(View.GONE);

    }

}