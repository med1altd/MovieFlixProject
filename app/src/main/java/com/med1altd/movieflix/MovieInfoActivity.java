package com.med1altd.movieflix;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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

public class MovieInfoActivity extends AppCompatActivity {

    ImageView imageBack, imageMain, BackButton;

    String JSON_URL;

    String Title, ImageBack, ImageMain, Video, Description, Year, Genre, Duration, Type;

    ArrayList<String> TitleList = new ArrayList<>();

    Integer Position;

    TextView TitleText, YearText, GenreText, DurationText, DescriptionText, DescriptionIndicatorText;

    RelativeLayout progressLayout;

    LinearLayout DescriptionLinearLayout, PlayButton;

    ProgressBar progressBarBack, progressBarMain;

    String type, startString, endString, Fetch;

    Integer start, end, posStart, posEnd;

    Boolean fetchVideo = false;

    Integer beforeEnd;

    Boolean loaded;

    ArrayList<String> TitleMethodsList;
    ArrayList<String> StartStringList;
    ArrayList<String> EndStringList;
    ArrayList<String> TypeList;
    ArrayList<Integer> BeforeList;
    ArrayList<Integer> StartList;
    ArrayList<Integer> EndList;

    Boolean isPremium = false;

    InterstitialAd
            mInterstitialAd;

    private String AD_INTERSTITIAL_ID;

    Integer randomPos;

    Boolean
            isTest = true,
            showedAd = false,
            isShowingInterstitialAds = false,
            isShowingBannerAds = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        JSON_URL = getResources().getString(R.string.main_json);

        isTest = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isTestMode", false);

        isPremium = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Premium", false);

        isShowingBannerAds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isShowingBannerAds", false);

        isShowingInterstitialAds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isShowingInterstitialAds", false);

        new Help().urlToJson(this, JSON_URL, new UrlToJSON() {
            @Override
            public String onSuccess(String result) {

                try {

                    JSONObject jsonObject = new JSONObject(result);

                    TitleMethodsList = new ArrayList<>();
                    BeforeList = new ArrayList<>();
                    TypeList = new ArrayList<>();
                    StartStringList = new ArrayList<>();
                    EndStringList = new ArrayList<>();
                    StartList = new ArrayList<>();
                    EndList = new ArrayList<>();

                    JSONArray jsonArrayFetchMethods = jsonObject.getJSONArray("Fetch");

                    for ( int m = 0; m < jsonArrayFetchMethods.length(); m++ ) {

                        JSONObject jsonObjectMethod = jsonArrayFetchMethods.getJSONObject(m);

                        String TitleAdd = jsonObjectMethod.getString("Title");
                        Integer BeforeAdd = jsonObjectMethod.getInt("beforeEnd");
                        String TypeAdd = jsonObjectMethod.getString("Type");
                        String StartStringAdd = jsonObjectMethod.getString("StartString");
                        String EndStringAdd = jsonObjectMethod.getString("EndString");
                        Integer StartAdd = jsonObjectMethod.getInt("Start");
                        Integer EndAdd = jsonObjectMethod.getInt("End");

                        TitleMethodsList.add(TitleAdd);
                        BeforeList.add(BeforeAdd);
                        TypeList.add(TypeAdd);
                        StartStringList.add(StartStringAdd);
                        EndStringList.add(EndStringAdd);
                        StartList.add(StartAdd);
                        EndList.add(EndAdd);

                    }

                    loaded = false;

                    AD_INTERSTITIAL_ID = getResources().getString(R.string.Interstitial_Movies_Open_Id);

                    JSON_URL = getIntent().getStringExtra("JSON_URL");

                    Position = getIntent().getIntExtra("Position", -1);
                    //Title = getIntent().getStringExtra("Title");
                    Type = getIntent().getStringExtra("Type");
                    //TitleList = getIntent().getStringArrayListExtra("TitleList");

                    TitleText = findViewById(R.id.textTitle);
                    YearText = findViewById(R.id.textYear);
                    GenreText = findViewById(R.id.textGenre);
                    DurationText = findViewById(R.id.DurationText);
                    DescriptionText = findViewById(R.id.textDescription);
                    DescriptionIndicatorText = findViewById(R.id.textDescriptionIndicator);

                    BackButton = findViewById(R.id.imageBackButton);
                    PlayButton = findViewById(R.id.PlayBtn);

                    progressLayout = findViewById(R.id.ProgressBarLayout);

                    DescriptionIndicatorText.setText("Περιγραφή Ταινίας:");

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

                    new Help().urlToJson(getApplicationContext(), JSON_URL, new UrlToJSON() {
                        @Override
                        public String onSuccess(String result) {

                            try {

                                JSONObject jsonObject = new JSONObject(result);

                                JSONArray jsonArrayMovies = jsonObject.getJSONArray("Movies");

                                //Position = TitleList.indexOf(Title);

                                JSONObject jsonObjectMovie = jsonArrayMovies.getJSONObject(Position);

                                Title = jsonObjectMovie.getString("Title");

                                ImageBack = jsonObjectMovie.getString("ImageBack");

                                ImageMain = jsonObjectMovie.getString("ImageMain");

                                if (jsonObject.getJSONArray("Main").getJSONObject(0).getBoolean("Live")) {

                                    Video = jsonObjectMovie.getString("Video");

                                    Fetch = jsonObjectMovie.getString("Fetch");

                                } else {

                                    Video = jsonObject.getJSONArray("Main").getJSONObject(0).getString("Video");

                                    Fetch = jsonObject.getJSONArray("Main").getJSONObject(0).getString("Fetch");

                                }

                                Description = jsonObjectMovie.getString("Description");

                                Year = jsonObjectMovie.getString("Year");

                                Genre = jsonObjectMovie.getString("Genre");

                                Duration = jsonObjectMovie.getString("Duration");

                                Boolean found = false;

                                for ( int t = 0; t < TitleMethodsList.size(); t++ ) {

                                    String Check = TitleMethodsList.get(t);

                                    if (Fetch.contains(Check)) {

                                        found = true;

                                        beforeEnd = BeforeList.get(t);

                                        type = TypeList.get(t);

                                        startString = StartStringList.get(t);

                                        endString = EndStringList.get(t);

                                        start = StartList.get(t);

                                        end = EndList.get(t);

                                        Integer videoLength = Video.length();

                                        videoLength = videoLength - beforeEnd;

                                        Video = Video.substring(0, videoLength);

                                    }

                                }

                                if (found) {

                                    new Help().urlToJson(getApplicationContext(), Video, new UrlToJSON() {
                                        @Override
                                        public String onSuccess(String result) {

                                            try {

                                                if (result.trim().isEmpty()) {

                                                    Toast.makeText(MovieInfoActivity.this, "Error 111: \n Προέκυψε άγνωστο πρόβλημμα! Παρακαλούμε επανεκκινήστε την εφαρμογή!", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    posStart = result.indexOf(startString);

                                                    posEnd = result.indexOf(endString, posStart);

                                                    posStart = posStart + start;

                                                    posEnd = posEnd - end;

                                                    Video = result.substring(posStart, posEnd);

                                                }

                                                load();

                                            } catch (Exception e) {

                                                Toast.makeText(MovieInfoActivity.this, "Error 444: \n Προέκυψε άγνωστο πρόβλημμα! Παρακαλούμε επανεκκινήστε την εφαρμογή!", Toast.LENGTH_SHORT).show();

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

                if (progressLayout.getVisibility() == View.INVISIBLE
                        || progressLayout.getVisibility() == View.GONE) {

                    if (!isPremium && isShowingInterstitialAds) {

                        onPlay();

                    } else {

                        playMovie();

                    }

                }

            }

        });

    }

    void load() {

        if ( Title.isEmpty() ) {
            TitleText.setVisibility(View.GONE);
        } else {
            TitleText.setText(Title);
        }

        if ( Year.isEmpty() ) {
            YearText.setVisibility(View.GONE);
        } else {
            YearText.setText(Year);
        }

        if ( Genre.trim().isEmpty() ) {
            GenreText.setVisibility(View.GONE);
        } else {
            GenreText.setText(Genre);
        }

        if ( Duration.trim().isEmpty() ) {
            DurationText.setVisibility(View.GONE);
        } else {
            DurationText.setText(Duration);
        }

        if ( Description.trim().isEmpty() ) {
            DescriptionText.setVisibility(View.GONE);
            DescriptionIndicatorText.setText("");
            DescriptionLinearLayout.setVisibility(View.GONE);
        } else {
            DescriptionText.setText(Description);
        }

        new Help().showImage(ImageBack, imageBack, progressBarBack, this);

        new Help().showImage(ImageMain, imageMain, progressBarMain, this);

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

                                        Help.delayNextInterstitialRequest(context, "MovieInfoDelay");

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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

        new Help().pushStringToFirebase(this, "Movies", Title);

        Intent intent = new Intent(MovieInfoActivity.this, VideoStreamingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Title", TitleText.getText());
        intent.putExtra("Video", Video);
        intent.putExtra("Year", YearText.getText());
        intent.putExtra("Genre", GenreText.getText());
        intent.putExtra("Duration", DurationText.getText());
        intent.putExtra("DescriptionIndicator", DescriptionIndicatorText.getText());
        intent.putExtra("Description", DescriptionText.getText());
        intent.putExtra("Type", Type);
        startActivity(intent);

        progressLayout.setVisibility(View.GONE);

    }

}