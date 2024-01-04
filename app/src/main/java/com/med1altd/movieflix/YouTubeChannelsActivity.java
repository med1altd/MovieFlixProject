package com.med1altd.movieflix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class YouTubeChannelsActivity extends AppCompatActivity {

    String JSON_URL, SubmissionUrl;

    ArrayList<YouTubeChannel>
            YouTubeChannels = new ArrayList<>(),
            YouTubeChannelsFiltered = new ArrayList<>();

    RecyclerView recyclerView;

    RecyclerViewAdapterYouTubeChannels recyclerViewAdapterYouTubeChannels;

    RelativeLayout progressLayout, noResultLayout;

    TextView submissionButton;

    SearchView searchView;

    ImageView backButton;

    ArrayList<String>
            Urls = new ArrayList<>();

    Integer
            Category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_channels);

        JSON_URL = getIntent().getStringExtra("JSON");

        Category = getIntent().getIntExtra("Category", -1);

        SubmissionUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("SubmissionUrl", "");

        recyclerView = findViewById(R.id.RecyclerViewPageMovies);

        progressLayout = findViewById(R.id.ProgressLayout);

        noResultLayout = findViewById(R.id.NoResultLayout);

        submissionButton = findViewById(R.id.SubmissionButton);

        backButton = findViewById(R.id.imageBackButton);

        searchView = findViewById(R.id.SearchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filter(newText);

                return false;

            }

        });

        onBackClickListener();

        onSubmissionButtonClickListener();

        try {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            String jsonUrls = sharedPreferences.getString("JSON_Urls", "{\"TV\":[{\"Url\":\"\"},{\"Url\":\"\"}],\"Movies\":[{\"Url\":\"\"},{\"Url\":\"\"}],\"Series\":[{\"Url\":\"\"}],\"Kids\":[{\"Url\":\"\"},{\"Url\":\"\"}]}");

            JSONObject
                    jsonObject = new JSONObject(jsonUrls);

            JSONArray
                    jsonArray;

            jsonArray = jsonObject.getJSONArray("Kids");

            for (int i = 0; i < jsonArray.length(); i++) {

                if (jsonArray.getJSONObject(i).getInt("Category") == Category) {

                    Urls.add(jsonArray.getJSONObject(i).getString("Url"));

                }

            }

            new findMovies().execute(
                    Urls);

        } catch (JSONException e) {

            throw new RuntimeException(e);

        }

    }

    void Calculate() {

        int nOfColumns = calculateNoOfColumns(this, 170);

        GridLayoutManager layoutManager = new GridLayoutManager(this, nOfColumns);

        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapterYouTubeChannels = new RecyclerViewAdapterYouTubeChannels(this, YouTubeChannelsFiltered);

        recyclerView.setAdapter(recyclerViewAdapterYouTubeChannels);

        if (YouTubeChannelsFiltered.size() > 0) {

            recyclerView.setVisibility(View.VISIBLE);

            noResultLayout.setVisibility(View.GONE);

        } else {

            recyclerView.setVisibility(View.GONE);

            noResultLayout.setVisibility(View.VISIBLE);

        }

    }

    void CalculateFiltered() {

        recyclerViewAdapterYouTubeChannels = new RecyclerViewAdapterYouTubeChannels(this, YouTubeChannelsFiltered);

        recyclerView.setAdapter(recyclerViewAdapterYouTubeChannels);

        if (YouTubeChannelsFiltered.size() > 0) {

            recyclerView.setVisibility(View.VISIBLE);

            noResultLayout.setVisibility(View.GONE);

        } else {

            recyclerView.setVisibility(View.GONE);

            noResultLayout.setVisibility(View.VISIBLE);

        }

    }

    public static int calculateNoOfColumns(Context context, float columnDp) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnDp); // + 0.5
        return noOfColumns;

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Calculate();

    }

    void filter (String text) {

        Integer
                total = 0,
                checked = 0;

        YouTubeChannelsFiltered = new ArrayList<>();

        for ( int i = 0; i < YouTubeChannels.size(); i++ ) {

            checked++;

            String nameMovie = YouTubeChannels.get(i).getTitle();

            if ( nameMovie.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                    nameMovie.toLowerCase(Locale.ROOT).contains(text.toUpperCase(Locale.ROOT)) ||
                    nameMovie.toUpperCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                    nameMovie.toUpperCase(Locale.ROOT).contains(text.toUpperCase(Locale.ROOT))) {

                YouTubeChannelsFiltered.add(new YouTubeChannel(YouTubeChannels.get(i).getTitle(), YouTubeChannels.get(i).getImage(), YouTubeChannels.get(i).getUrl(), YouTubeChannels.get(i).getJSON(), YouTubeChannels.get(i).getUnlocked()));

                total = total + 1;

            }

        }

        if (YouTubeChannels.size() > 0) {

            if (YouTubeChannelsFiltered.size() > 0) {

                addNull();

                recyclerView.setVisibility(View.VISIBLE);

                noResultLayout.setVisibility(View.GONE);

            } else {

                recyclerView.setVisibility(View.GONE);

                noResultLayout.setVisibility(View.VISIBLE);

            }

        } else {

            if (YouTubeChannelsFiltered.size() > 0) {

                addNull();

                recyclerView.setVisibility(View.VISIBLE);

                noResultLayout.setVisibility(View.GONE);

            } else {

                if (!text.trim().isEmpty()) {

                    recyclerView.setVisibility(View.GONE);

                    noResultLayout.setVisibility(View.VISIBLE);

                } else {

                    addNull();

                    recyclerView.setVisibility(View.VISIBLE);

                    noResultLayout.setVisibility(View.GONE);

                }

            }

        }

        CalculateFiltered();

    }

    public void onBackClickListener() {

        new Help().setupAnimationOnClick(backButton, 0.9f, new OnViewClickListener() {
            @Override
            public void onClick() {

                finish();

            }

        });

    }

    public void onSubmissionButtonClickListener() {

        new Help().setupAnimationOnClick(submissionButton, 0.9f, new OnViewClickListener() {
            @Override
            public void onClick() {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SubmissionUrl));

                startActivity(intent);

            }

        });

    }

    private class findMovies extends AsyncTask<ArrayList<String>, Void, Boolean> {

        private int successCount = 0;

        private int expectedCount;

        private CountDownLatch countDownLatch;

        @Override
        protected Boolean doInBackground(ArrayList<String>... urls) {

            ArrayList<String>
                    UrlsPassing = new ArrayList<>();

            ArrayList<ArrayList<String>>
                    Results = new ArrayList<>();

            for (ArrayList<String> Urls : urls) {

                for (String url : Urls) {

                    UrlsPassing.add(url);

                }

            }

            expectedCount = UrlsPassing.size();

            countDownLatch = new CountDownLatch(expectedCount);

            for (ArrayList<String> Urls : urls) {

                for (String url : Urls) {

                    if (getApplicationContext() != null) {

                        new Help().urlToJson(getApplicationContext(), url, new UrlToJSON() {
                            @Override
                            public String onSuccess(String result) {

                                ArrayList<String> content = new ArrayList<>();

                                content.add(result);

                                content.add(url);

                                if (url.equals(UrlsPassing.get(UrlsPassing.size() - 1))) {

                                    content.add("true");

                                } else {

                                    content.add("false");

                                }

                                Results.add(content);

                                countDownLatch.countDown();

                                successCount++;  // Increment the success count

                                // Check if all listeners have completed

                                if (successCount == expectedCount) {

                                    // Sort the Results based on identifiers

                                    Results.sort((r1, r2) -> r1.get(1).compareTo(r2.get(1)));

                                    startFetchingData(Results);

                                }

                                return null;

                            }

                        });

                    }

                }

            }

            try {

                // Wait until all tasks are completed

                countDownLatch.await();

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean executed) {

            // Do something with the combined data

            shuffle();

        }

    }

    private class findMoviesOld extends AsyncTask<ArrayList<String>, Void, Boolean> {

        private int successCount = 0;

        private int expectedCount;

        @Override
        protected Boolean doInBackground(ArrayList<String>... urls) {

            ArrayList<String>
                    UrlsPassing = new ArrayList<>();

            ArrayList<ArrayList<String>>
                    Results = new ArrayList<>();

            for (ArrayList<String> Urls : urls) {

                for (String url : Urls) {

                    UrlsPassing.add(url);

                }

            }

            expectedCount = UrlsPassing.size();  // Set the expected count

            for (ArrayList<String> Urls : urls) {

                for (String url : Urls) {

                    if (getApplicationContext() != null) {

                        new Help().urlToJson(getApplicationContext(), url, new UrlToJSON() {
                            @Override
                            public String onSuccess(String result) {

                                ArrayList<String> content = new ArrayList<>();

                                content.add(result);

                                content.add(url);

                                if (url.equals(UrlsPassing.get(UrlsPassing.size() - 1))) {

                                    content.add("true");

                                } else {

                                    content.add("false");

                                }

                                Results.add(content);

                                successCount++;  // Increment the success count

                                // Check if all listeners have completed

                                if (successCount == expectedCount) {

                                    startFetchingData(Results);

                                }

                                return null;

                            }

                        });

                    }

                }

            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean executed) {

            // Do something with the combined data

            shuffle();

        }

    }

    public void startFetchingData(ArrayList<ArrayList<String>> results) {

        for (int i = 0; i < results.size(); i ++) {

            fetchDataFromUrl(results.get(i).get(0), results.get(i).get(1), Boolean.valueOf(results.get(i).get(2)));

        }

    }

    private void fetchDataFromUrl(String result, String Url, Boolean lastOne) {

        try {

            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonArrayYouTubeChannels = jsonObject.getJSONArray("YouTube Channels");

            String
                    Title,
                    Image,
                    url,
                    JSON;

            Boolean
                    isUnlocked;

            for ( int y = 0; y < jsonArrayYouTubeChannels.length(); y++ ) {

                Title = null;

                Image = null;

                url = null;

                JSON = null;

                isUnlocked = false;

                JSONObject jsonObjectSeasons = jsonArrayYouTubeChannels.getJSONObject(y);

                Title  = jsonObjectSeasons.getString("Title");
                Image = jsonObjectSeasons.getString("Image");
                url = jsonObjectSeasons.getString("Url");
                JSON = Url;
                isUnlocked = jsonObjectSeasons.getBoolean("isUnlocked");

                YouTubeChannels.add(new YouTubeChannel(Title, Image, url, JSON, isUnlocked));

                YouTubeChannelsFiltered.add(new YouTubeChannel(Title, Image, url, JSON, isUnlocked));

            }

        } catch (JSONException e) {

            throw new RuntimeException(e);

        }

    }

    void shuffle() {

        Collections.shuffle(YouTubeChannelsFiltered);

        addNull();

        noResultLayout.setVisibility(View.GONE);

        Calculate();

        progressLayout.setVisibility(View.GONE);

    }

    public void reloadActivity() {

        finish();

        startActivity(getIntent());

    }

    public void addNull() {

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("MoreContentOption", false)) {

            YouTubeChannelsFiltered.add(null);

        }

    }

}