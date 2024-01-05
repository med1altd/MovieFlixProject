package com.med1altd.movieflix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;

public class LiveStreamingActivity extends AppCompatActivity {

    Channel
            channel;

    StyledPlayerView playerView;
    private ExoPlayer simpleExoPlayer;
    private DefaultTrackSelector trackSelector;
    private SensorManager sm;
    AspectRatioFrameLayout ratio;

    private String
            AD_INTERSTITIAL_ID;

    String
            RatingUrl,
            live,
            description;

    Integer
            posStart,
            posEnd,
            startDPos,
            delay;

    Boolean
            checkSensorFullscreen = false,
            checkSensorExitFullscreen = false,
            startup = true,
            first = false,
            isPremium = false,
            hasValidVideo = false,
            isShowingInterstitialAds = false,
            isShowingBannerAds = false;

    RelativeLayout
            ToolBar;

    LinearLayout
            PlayPause,
            aspectLinearLayout,
            descriptionLayout;

    ImageView
            btFullScreen,
            Back;

    TextView
            TitleTxt,
            textDescription;

    ImageView
            Rate,
            Info,
            imageChannel,
            Report;

    ProgressBar
            progressBar;

    private InterstitialAd
            mInterstitialAd;

    private AdView
            mAdView;

    Boolean
            isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_streaming);

        isRunning = true;

        channel = (Channel) getIntent().getSerializableExtra("channel");

        ToolBar = findViewById(R.id.toolbarstreaming);
        playerView = findViewById(R.id.video_playerview1);
        ratio = findViewById(R.id.video_ratio1);
        btFullScreen = playerView.findViewById(R.id.bt_fullscreen);
        aspectLinearLayout = findViewById(R.id.AspectLinearLayout);
        TitleTxt = findViewById(R.id.textTitle);
        PlayPause = playerView.findViewById(R.id.sec_controlvid1);
        Report = playerView.findViewById(R.id.bt_report);
        Back = findViewById(R.id.Back);
        imageChannel = findViewById(R.id.imageChannel);
        progressBar = findViewById(R.id.progressBar2);
        descriptionLayout = findViewById(R.id.DescriptionLinearLayout);
        textDescription = findViewById(R.id.textDescription);

        TitleTxt.setSelected(true);

        AD_INTERSTITIAL_ID = getResources().getString(R.string.Interstitial_Real_TV_Id);

        isPremium = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Premium", false);

        isShowingBannerAds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isShowingBannerAds", false);

        isShowingInterstitialAds = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isShowingInterstitialAds", false);

        delay = PreferenceManager.getDefaultSharedPreferences(this).getInt("TVDelay", 0);

        if (!isPremium && isShowingBannerAds) {

            mAdView = findViewById(R.id.adView);

            mAdView.setVisibility(View.GONE);

        }

        Rate = findViewById(R.id.Rate);
        Info = findViewById(R.id.info);

        new Help().manageReportButton(this, Report);

        if (channel.getAnotherVideo()) {

            TitleTxt.setText(channel.getStartString());

            textDescription.setText(channel.getEndString());

            imageChannel.setVisibility(View.GONE);

        } else {

            if (channel.getLive()) {

                new Help().urlToJson(this, channel.getLiveUrl(), new UrlToJSON() {
                    @Override
                    public String onSuccess(String result) {

                        try {

                            if (result.trim().isEmpty()) {

                                TitleTxt.setText("Μη διαθέσιμο");

                                Toast.makeText(LiveStreamingActivity.this, "Error 111: \n Live Content Error!", Toast.LENGTH_SHORT).show();

                            } else {

                                posStart = result.indexOf(channel.getStartString());

                                posEnd = result.indexOf(channel.getEndString(), posStart);

                                posStart = posStart + channel.getStart();

                                posEnd = posEnd - channel.getEnd();

                                live = result.substring(posStart, posEnd);

                                if (live.trim().isEmpty() || live.contains("https") || live.contains("href") || live.contains("\">")) {

                                    TitleTxt.setText("Μη διαθέσιμο");

                                } else {

                                    TitleTxt.setText(live);

                                }

                            }

                            if (channel.getDescription()) {

                                new Help().urlToJson(getApplicationContext(), channel.getLiveUrl(), new UrlToJSON() {
                                    @Override
                                    public String onSuccess(String result) {

                                        try {

                                            if (result.trim().isEmpty()) {

                                                descriptionLayout.setVisibility(View.GONE);

                                                Toast.makeText(LiveStreamingActivity.this, "Error 111: \n Live Content Error!", Toast.LENGTH_SHORT).show();

                                            } else {

                                                startDPos = result.indexOf(TitleTxt.getText().toString());

                                                Integer posStartD = result.indexOf(channel.getStartStringD(), channel.getStartD());

                                                posStartD = posStartD + channel.getStartD();

                                                Integer posEndD = result.indexOf(channel.getEndStringD(), posStartD);

                                                posEndD = posEndD - channel.getEndD();

                                                description = result.substring(posStartD, posEndD);

                                                if (description.trim().isEmpty()) {

                                                    descriptionLayout.setVisibility(View.GONE);

                                                } else {

                                                    textDescription.setText(description);

                                                }

                                            }

                                        } catch (Exception e) {

                                            descriptionLayout.setVisibility(View.GONE);

                                            Toast.makeText(LiveStreamingActivity.this, "Error 444: \n Live Content Error!", Toast.LENGTH_SHORT).show();

                                        }

                                        return null;

                                    }

                                });

                            } else {

                                descriptionLayout.setVisibility(View.GONE);

                            }

                        } catch (Exception e) {

                            TitleTxt.setText("Μη διαθέσιμο");

                            Toast.makeText(LiveStreamingActivity.this, "Error 444: \n Live Content Error!", Toast.LENGTH_SHORT).show();

                        }

                        return null;

                    }

                });

            } else {

                if (channel.getTitle().trim().isEmpty()) {

                    TitleTxt.setText("Μη διαθέσιμο");

                } else {

                    TitleTxt.setText(channel.getTitle());

                    TitleTxt.setSelected(true);

                }

                descriptionLayout.setVisibility(View.GONE);

            }

            new Help().showImage(channel.getImage(), imageChannel, progressBar, this);

        }

        RatingUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("RatingUrl", "");

        if (channel.getVideo().contains("m3u8")) {

            hasValidVideo = true;

            iniExoPlayer_m3u8();

        } else if (channel.getVideo().contains("mp4")) {

            hasValidVideo = true;

            iniExoPlayer_mp4();

        } else if (channel.getVideo().contains("mpd")) {

            hasValidVideo = true;

            iniExoPlayer_mpd();

        } else {

            Toast.makeText(this, "Μη Διαθέσιμο!", Toast.LENGTH_SHORT).show();

            finish();

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

        onBackClickListener();

        onRateClickListener();

        onInfoClickListener();

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

                                                Help.delayNextInterstitialRequest(context, "TVDelay");

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

                                                                        finish();

                                                                        simpleExoPlayer.release();

                                                                    }

                                                                    @Override
                                                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {

                                                                        super.onAdFailedToShowFullScreenContent(adError);

                                                                        new Help().addStatisticToTheCount(context, "FailedToShow");

                                                                        finish();

                                                                        simpleExoPlayer.release();

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
                .setAllowChunklessPreparation(true).createMediaSource((MediaItem.fromUri(channel.getVideo())));

        initializeStream(createMediaSource);

    }

    private void iniExoPlayer_mp4() {

        MediaSource createMediaSource = new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory())
                .createMediaSource((MediaItem.fromUri(channel.getVideo())));

        initializeStream(createMediaSource);

    }

    private void iniExoPlayer_mpd() {

        DashMediaSource createMediaSource = new DashMediaSource.Factory(new DefaultHttpDataSource.Factory())
                .createMediaSource((MediaItem.fromUri(channel.getVideo())));

        initializeStream(createMediaSource);

    }

    public void initializeStream(MediaSource mediaSource) {

        trackSelector = new DefaultTrackSelector(this);

        ExoPlayer build = new ExoPlayer.Builder(this).setTrackSelector(trackSelector).build();

        simpleExoPlayer = build;

        playerView.setKeepScreenOn(true);

        playerView.setPlayer(build);

        simpleExoPlayer.setMediaSource(mediaSource);

        simpleExoPlayer.prepare();

        simpleExoPlayer.setPlayWhenReady(true);

    }

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

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            Landscape();

        } else {

            Portrait();

        }

        simpleExoPlayer.setPlayWhenReady(true);

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

    public void onDestroy() {

        super.onDestroy();

        if (simpleExoPlayer != null) {

            simpleExoPlayer.release();

        }

        if (!isPremium && isShowingBannerAds) {

            mAdView.destroy();

        }

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

        ratio.setAspectRatio(16/9f);

        aspectLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        btFullScreen.setImageDrawable(getResources()
                .getDrawable(R.drawable.fullscreen_icon));

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

        aspectLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        btFullScreen.setImageDrawable(getResources()
                .getDrawable(R.drawable.fullscreen_exit_icon));

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

        aspectLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        btFullScreen.setImageDrawable(getResources()
                .getDrawable(R.drawable.fullscreen_exit_icon));

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

        ratio.setAspectRatio(16/9f);

        aspectLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        btFullScreen.setImageDrawable(getResources()
                .getDrawable(R.drawable.fullscreen_icon));

    }

    public void onBackClickListener() {

        new Help().setupAnimationOnClick(Back, 0.8f, new OnViewClickListener() {
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

                if (RatingUrl != null) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(RatingUrl));

                    startActivity(intent);

                }

            }

        });

    }

    public void onInfoClickListener() {

        new Help().setupAnimationOnClick(Info, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                Intent intent = new Intent(LiveStreamingActivity.this, InfoActivity.class);

                startActivity(intent);

            }

        });

    }

}