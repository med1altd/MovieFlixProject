package com.med1altd.movieflix;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import java.util.ArrayList;
import java.util.Random;

public class VideoStreamingActivity extends AppCompatActivity {

    SensorManager sm;

    StyledPlayerView playerView;

    ExoPlayer simpleExoPlayer;

    DefaultTrackSelector trackSelector;

    AspectRatioFrameLayout ratio;

    ImageView btFullScreen;

    Button Rewind, Forward;

    RelativeLayout RewindImg, ForwardImg, ToolBar, progressLayout;

    LinearLayout PlayPause, aspectLinearLayout, DescriptionIndicator;

    InterstitialAd
            mInterstitialAd;

    AdView
            mAdView;

    TextView
            TitleTxt,
            YearTxt,
            GenreTxt,
            DurationTxt,
            DescriptionIndicatorTxt,
            DescriptionTxt;

    String
            Title,
            Video,
            Year,
            Genre,
            Duration,
            DescriptionIndicatorTitle,
            Description,
            RatingUrl,
            Type;

    private String
            AD_INTERSTITIAL_ID;

    ImageView
            backButton,
            Rate,
            Info,
            Report;

    Boolean
            isPremium = false,
            checkSensorFullscreen = false,
            checkSensorExitFullscreen = false,
            startup = true,
            first = false,
            byPassDelay = false,
            playing,
            showedSubAlert,
            isShowingInterstitialAds = false,
            isShowingBannerAds = false,
            firstResume = true;

    Dialog
            dialog;

    Integer
            delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_streaming);

        isRunning = true;

        isPremium = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Premium", false);

        RatingUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("RatingUrl", "");

        isShowingBannerAds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isShowingBannerAds", false);

        isShowingInterstitialAds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isShowingInterstitialAds", false);

        delay = PreferenceManager.getDefaultSharedPreferences(this).getInt("VideoDelay", 0);

        progressLayout = findViewById(R.id.progressBarLayout);

        showedSubAlert = false;

        TitleTxt = findViewById(R.id.textTitle);
        DescriptionTxt = findViewById(R.id.textDescription);
        DescriptionIndicator = findViewById(R.id.DescriptionLinearLayout);
        DescriptionIndicatorTxt = findViewById(R.id.textDescriptionIndicator);
        YearTxt = findViewById(R.id.textYear);
        GenreTxt = findViewById(R.id.textGenre);
        DurationTxt = findViewById(R.id.DurationText);
        backButton = findViewById(R.id.Back);

        Rate = findViewById(R.id.Rate);
        Info = findViewById(R.id.info);

        ToolBar = findViewById(R.id.toolbarstreaming);
        playerView = findViewById(R.id.video_playerview1);
        ratio = findViewById(R.id.video_ratio1);
        btFullScreen = playerView.findViewById(R.id.bt_fullscreen);
        Report = playerView.findViewById(R.id.bt_report);
        aspectLinearLayout = findViewById(R.id.AspectLinearLayout);
        Rewind = playerView.findViewById(R.id.RewindButton);
        Forward = playerView.findViewById(R.id.ForwardButton);
        RewindImg = playerView.findViewById(R.id.LeftRewind);
        ForwardImg = playerView.findViewById(R.id.RightForward);
        PlayPause = playerView.findViewById(R.id.sec_controlvid1);
        RewindImg.setVisibility(View.INVISIBLE);
        ForwardImg.setVisibility(View.INVISIBLE);

        if (!isPremium && isShowingBannerAds) {

            mAdView = findViewById(R.id.adView);

            mAdView.setVisibility(View.GONE);

        }

        new Help().manageReportButton(this, Report);

        Type = getIntent().getStringExtra("Type");

        if ( Type.contains("Movies")) {

            AD_INTERSTITIAL_ID = getResources().getString(R.string.Interstitial_Real_Movies_Id);

        } else if ( Type.contains("Series")) {

            AD_INTERSTITIAL_ID = getResources().getString(R.string.Interstitial_Real_Series_Id);

        } else if ( Type.contains("Kids")) {

            AD_INTERSTITIAL_ID = getResources().getString(R.string.Interstitial_Real_Movies_Id);

        }

        Title = getIntent().getStringExtra("Title");
        Video = getIntent().getStringExtra("Video");
        Year = getIntent().getStringExtra("Year");
        Genre = getIntent().getStringExtra("Genre");
        Duration = getIntent().getStringExtra("Duration");
        DescriptionIndicatorTitle = getIntent().getStringExtra("DescriptionIndicator");
        Description = getIntent().getStringExtra("Description");

        if ( Title.trim().isEmpty() ) {

            TitleTxt.setVisibility(View.GONE);

        } else {

            TitleTxt.setText(Title);

        }

        if ( DescriptionIndicatorTitle.trim().isEmpty() ) {

            DescriptionIndicatorTxt.setVisibility(View.GONE);

        } else {

            DescriptionIndicatorTxt.setText(DescriptionIndicatorTitle);

        }

        if ( Description.trim().isEmpty() ) {

            DescriptionTxt.setVisibility(View.GONE);

        } else {

            DescriptionTxt.setText(Description);

        }

        if ( Year.trim().isEmpty() ) {

            YearTxt.setVisibility(View.GONE);

        } else {

            YearTxt.setText(Year);

        }

        if ( Genre.trim().isEmpty() ) {

            GenreTxt.setVisibility(View.GONE);

        } else {

            GenreTxt.setText(Genre);

        }

        if ( Duration.trim().isEmpty() ) {

            DurationTxt.setVisibility(View.GONE);

        } else {

            DurationTxt.setText(Duration);

        }

        if (Video.contains("m3u8")) {

            iniExoPlayer_m3u8();

        } else if (Video.contains("mpd")) {

            iniExoPlayer_mpd();

        } else {

            iniExoPlayer_mp4();

        }

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

        progressLayout.setVisibility(View.GONE);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Landscape();
        } else {
            first = true;
            Portrait();
        }

        btFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ExitFullscreen();
                    checkSensorExitFullscreen = true;
                } else {
                    Fullscreen();
                    checkSensorFullscreen = true;
                }
            }
        });

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        Rewind.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    playerView.hideController();
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    RewindImg.setVisibility(View.VISIBLE);
                    btFullScreen.setVisibility(View.INVISIBLE);
                    PlayPause.setVisibility(View.INVISIBLE);
                    simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RewindImg.setVisibility(View.INVISIBLE);
                            btFullScreen.setVisibility(View.VISIBLE);
                            PlayPause.setVisibility(View.VISIBLE);
                        }
                    }, 750);
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });

        Forward.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    playerView.hideController();
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    ForwardImg.setVisibility(View.VISIBLE);
                    btFullScreen.setVisibility(View.INVISIBLE);
                    PlayPause.setVisibility(View.INVISIBLE);
                    simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ForwardImg.setVisibility(View.INVISIBLE);
                            btFullScreen.setVisibility(View.VISIBLE);
                            PlayPause.setVisibility(View.VISIBLE);
                        }
                    }, 750);
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });

        onBackClickListener();

        onRateClickListener();

        onInfoClickListener();

    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];

            if (x < 0) {
                x = -x;
            }

            if (y < 0) {
                y = -y;
            }

            if (x > y) {
                if (checkSensorFullscreen) {
                    if (Settings.System.getInt(getContentResolver(),
                            Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
                        checkSensorFullscreen = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                            }
                        }, 1500);
                    }
                }
            }

            if (y > x) {
                if (checkSensorExitFullscreen) {
                    if (Settings.System.getInt(getContentResolver(),
                            Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
                        checkSensorExitFullscreen = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                            }
                        }, 1500);
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

    };

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Landscape();
        } else {
            first = true;
            Portrait();
        }

    }

    private void iniExoPlayer_m3u8() {
        HlsMediaSource createMediaSource = new HlsMediaSource.Factory(new DefaultHttpDataSource.Factory())
                .setAllowChunklessPreparation(true).createMediaSource((MediaItem.fromUri(Video)));
        trackSelector = new DefaultTrackSelector(this);
        ExoPlayer build = new ExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        simpleExoPlayer = build;
        playerView.setKeepScreenOn(true);
        playerView.setPlayer(build);
        simpleExoPlayer.setMediaSource(createMediaSource);
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(true);
        handleErrors();
    }

    private void iniExoPlayer_mp4() {
        MediaSource createMediaSource = new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory())
                .createMediaSource((MediaItem.fromUri(Video)));
        trackSelector = new DefaultTrackSelector(this);
        ExoPlayer build = new ExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        simpleExoPlayer = build;
        playerView.setKeepScreenOn(true);
        playerView.setPlayer(build);
        simpleExoPlayer.setMediaSource(createMediaSource);
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(true);
        handleErrors();
    }

    private void iniExoPlayer_mpd() {
        DashMediaSource createMediaSource = new DashMediaSource.Factory(new DefaultHttpDataSource.Factory())
                .createMediaSource((MediaItem.fromUri(Video)));
        trackSelector = new DefaultTrackSelector(this);
        ExoPlayer build = new ExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        simpleExoPlayer = build;
        playerView.setKeepScreenOn(true);
        playerView.setPlayer(build);
        simpleExoPlayer.setMediaSource(createMediaSource);
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(true);
        handleErrors();
    }

    Boolean
            isRunning = false;

    @Override
    protected void onPause() {

        super.onPause();

        isRunning = false;

        simpleExoPlayer.setPlayWhenReady(false);

    }

    @Override
    protected void onUserLeaveHint() {

        super.onUserLeaveHint();
        simpleExoPlayer.setPlayWhenReady(false);

    }

    @Override
    protected void onResume() {

        super.onResume();

        isRunning = true;

        int randomPos = -1111;

        if (firstResume) {

            firstResume = false;

        } else {

            if (!isPremium) {

                if (!showedSubAlert) {

                    Random random = new Random();
                    randomPos = random.nextInt(3 - 0) + 0;

                    if (randomPos == 2) {

                        //showDialog();

                    } else {

                        //if (dialog == null || !dianew Help().pushStringToFirebase(this, isShowing()) {

                        //    simpleExoPlayer.setPlayWhenReady(true);

                        //}

                    }

                }

            }

        }

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Landscape();
        } else {
            Portrait();
        }

    }

    public void onDestroy() {

        super.onDestroy();

        simpleExoPlayer.release();

        if (hasRequested) {

            Help.destroyWaitTillNextInterstitial();

        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        showAd();

    }

    private void Portrait() {
        if (startup) {
            startup = false;
        } else {
            if (first) {
                if (!isPremium && isShowingBannerAds) {
                    ShowBanner();
                }
            }
        }
        ToolBar.setVisibility(View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
        ratio.setAspectRatio(16/9f);
        aspectLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        btFullScreen.setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_baseline_fullscreen));
    }

    private void Landscape() {
        if (startup) {
            startup = false;
        } else {
            if (!isPremium && isShowingBannerAds) {
                HideBanner();
            }
        }
        ToolBar.setVisibility(View.GONE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ratio.setAspectRatio(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        aspectLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        btFullScreen.setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_baseline_fullscreen_exit));
    }

    private void Fullscreen() {
        if (first) {
            if (!isPremium && isShowingBannerAds) {
                HideBanner();
            }
        }
        ToolBar.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ratio.setAspectRatio(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        aspectLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        btFullScreen.setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_baseline_fullscreen_exit));
    }

    private void ExitFullscreen() {
        if (first) {
            if (!isPremium && isShowingBannerAds) {
                ShowBanner();
            }
        }
        ToolBar.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        ratio.setAspectRatio(16/9f);
        aspectLinearLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        btFullScreen.setImageDrawable(getResources()
                .getDrawable(R.drawable.ic_baseline_fullscreen));
    }

    Boolean
            hasRequested = false;

    private void loadAndManageInterstitialAd() {

        Context
                context = this;

        new Help().canShowInterstitialBasedOnDailyLimits(context, new ReturnMultipleResults() {
            @Override
            public ArrayList<Object> onSuccessMultipleResults(ArrayList<Object> results) {

                if (results != null && results.get(3) != null) {

                    Long curAdRequests = (Long) results.get(3);

                    new Help().readAndSaveJSONAdMobLimits(context, new UrlToJSON() {
                        @Override
                        public String onSuccess(String result) {

                            if (new Help().compareCurrentAndLimitAdRequests(curAdRequests, new Help().getAdMobLimit(context, "Daily"))) {

                                new Help().canShowInterstitialBasedOnHourLimits(context, new ReturnMultipleResults() {
                                    @Override
                                    public ArrayList<Object> onSuccessMultipleResults(ArrayList<Object> results2) {

                                        if (results2 != null && results2.get(3) != null) {

                                            Long curAdRequests = (Long) results2.get(3);

                                            if (new Help().compareCurrentAndLimitAdRequests(curAdRequests, new Help().getAdMobLimit(context, results2.get(1).toString()))) {

                                                AdRequest adRequest = new AdRequest.Builder().build();

                                                hasRequested = true;

                                                Help.delayNextInterstitialRequest(context, "VideoDelay");

                                                InterstitialAd.load(context, AD_INTERSTITIAL_ID, adRequest,
                                                        new InterstitialAdLoadCallback() {
                                                            @Override
                                                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                                                                super.onAdLoaded(interstitialAd);

                                                                mInterstitialAd = interstitialAd;

                                                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                                    @Override
                                                                    public void onAdDismissedFullScreenContent() {

                                                                        super.onAdDismissedFullScreenContent();

                                                                        simpleExoPlayer.release();

                                                                        finish();

                                                                    }

                                                                    @Override
                                                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {

                                                                        super.onAdFailedToShowFullScreenContent(adError);

                                                                        new Help().addStatisticToTheCount(context, "FailedToShow");

                                                                        simpleExoPlayer.release();

                                                                        finish();

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
                                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

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

                            return null;

                        }

                    });

                }

                return null;

            }

        });

    }

    private void ShowBanner() {

        if (!isPremium && isShowingBannerAds) {
            AdRequest adRequest = new AdRequest.Builder().build();

            mAdView.loadAd(adRequest);

            mAdView.setVisibility(View.VISIBLE);

        }

    }

    private void HideBanner() {

        if (!isPremium && isShowingBannerAds) {

            mAdView.destroy();

            mAdView.setVisibility(View.GONE);

        }

    }

    public void showAd() {

        if (!isPremium && isShowingInterstitialAds) {

            if (mInterstitialAd != null) {

                mInterstitialAd.show(this);

            } else {

                finish();

            }

        } else {

            finish();

        }

    }

    public void onBackClickListener() {

        new Help().setupAnimationOnClick(backButton, 0.9f, new OnViewClickListener() {

            @Override
            public void onClick() {

                showAd();

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

                Intent intent = new Intent(VideoStreamingActivity.this, InfoActivity.class);

                startActivity(intent);

            }

        });

    }

    public void handleErrors() {

        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);

                Toast.makeText(VideoStreamingActivity.this, "Προέκυψε σφάλμα μη ανταπόκρισης του Server!\n Ελέγξτε την ταχύτητα σύνδεσής σας!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void showDialog() {

        loadDialog();

        showedSubAlert = true;

        playing = simpleExoPlayer.getPlayWhenReady();

        simpleExoPlayer.setPlayWhenReady(false);

        dialog.show();

    }

    public void loadDialog() {

        //Create the Dialog here
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = dialog.findViewById(R.id.btn_okay);
        Button Cancel = dialog.findViewById(R.id.btn_cancel);

        new Help().setupAnimationOnClick(Okay, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                dialog.dismiss();

                simpleExoPlayer.setPlayWhenReady(false);

                Intent intent = new Intent(VideoStreamingActivity.this, SubscriptionsActivity.class);

                startActivity(intent);

            }

        });

        new Help().setupAnimationOnClick(Cancel, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                dialog.dismiss();

                if (playing) {

                    simpleExoPlayer.setPlayWhenReady(true);

                } else {

                    simpleExoPlayer.setPlayWhenReady(false);

                }

            }

        });

    }

}