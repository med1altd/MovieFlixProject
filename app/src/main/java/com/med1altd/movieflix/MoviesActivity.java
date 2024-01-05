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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MoviesActivity extends AppCompatActivity{

    String JSON_URL, SubmissionUrl, Type;

    ArrayList<Movie>
            Movies = new ArrayList<>(),
            FilteredMovies = new ArrayList<>();

    RecyclerView recyclerView;

    RecyclerViewAdapterMovies recyclerViewAdapterMovies;

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
        setContentView(R.layout.activity_movies);

        Category = getIntent().getIntExtra("Category", -1);

        JSON_URL = getIntent().getStringExtra("JSON");

        Type = getIntent().getStringExtra("Type");

        SubmissionUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("SubmissionUrl", "");

        recyclerView = findViewById(R.id.RecyclerViewPageMovies);

        progressLayout = findViewById(R.id.ProgressLayout);

        backButton = findViewById(R.id.Back);

        noResultLayout = findViewById(R.id.NoResultLayout);

        submissionButton = findViewById(R.id.SubmissionButton);

        searchView = findViewById(R.id.SearchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                filter(query);

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

            if (Category < 3) {

                jsonArray = jsonObject.getJSONArray("Movies");

                for (int i = 0; i < jsonArray.length(); i ++ ) {

                    if (jsonArray.getJSONObject(i).getInt("Category") == Category) {

                        Urls.add(jsonArray.getJSONObject(i).getString("Url"));

                    }

                }

            } else {

                jsonArray = jsonObject.getJSONArray("Kids");

                for (int i = 0; i < jsonArray.length(); i ++ ) {

                    if (jsonArray.getJSONObject(i).getInt("Category") == (Category - 3)) {

                        Urls.add(jsonArray.getJSONObject(i).getString("Url"));

                    }

                }

            }

            new findMovies().execute(
                    Urls);

        } catch (JSONException e) {

            throw new RuntimeException(e);

        }

    }

    void shuffle() {

        Collections.shuffle(FilteredMovies);

        addNullMovie();

        noResultLayout.setVisibility(View.GONE);

        Calculate();

        progressLayout.setVisibility(View.GONE);

    }

    void Calculate() {

        int nOfColumns = calculateNoOfColumns(this, 120);

        GridLayoutManager layoutManager = new GridLayoutManager(this, nOfColumns);

        recyclerView.setLayoutManager(layoutManager);

        //recyclerViewAdapterMovies = new RecyclerViewAdapterMovies(this, TitleList, ImageList, PosList, isUnlockedList, JSON_URL, Type, TitleList);

        recyclerViewAdapterMovies = new RecyclerViewAdapterMovies(this, FilteredMovies, Type);

        recyclerView.setAdapter(recyclerViewAdapterMovies);

        if (FilteredMovies.size() > 0) {

            recyclerView.setVisibility(View.VISIBLE);

            noResultLayout.setVisibility(View.GONE);

        } else {

            recyclerView.setVisibility(View.GONE);

            noResultLayout.setVisibility(View.VISIBLE);

        }

    }

    void CalculateFiltered() {

        recyclerViewAdapterMovies = new RecyclerViewAdapterMovies(this, FilteredMovies, Type);

        recyclerView.setAdapter(recyclerViewAdapterMovies);

        if (FilteredMovies.size() > 0) {

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

    Integer
            total,
            checked;

    void filter (String text) {

        total = 0;

        checked = 0;

        FilteredMovies = new ArrayList<>();

        String
                Title,
                Image;

        Integer
                Position;

        Boolean
                isUnlocked;

        for ( int i = 0; i < Movies.size(); i++ ) {

            checked++;

            String nameMovie = Movies.get(i).getTitle();

            if ( nameMovie.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                    nameMovie.toLowerCase(Locale.ROOT).contains(text.toUpperCase(Locale.ROOT)) ||
                    nameMovie.toUpperCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                    nameMovie.toUpperCase(Locale.ROOT).contains(text.toUpperCase(Locale.ROOT))) {

                Title = Movies.get(i).getTitle();
                Image = Movies.get(i).getImage();
                Position = Movies.get(i).getPosition();
                JSON_URL = Movies.get(i).getJSON();
                isUnlocked = Movies.get(i).getUnlocked();

                FilteredMovies.add(new Movie(Title, Image, null, null, null, null, null, JSON_URL, null, Position, isUnlocked));

                total = total + 1;

            }

        }

        if (Movies.size() > 0) {

            if (FilteredMovies.size() > 0) {

                addNullMovie();

                recyclerView.setVisibility(View.VISIBLE);

                noResultLayout.setVisibility(View.GONE);

            } else {

                recyclerView.setVisibility(View.GONE);

                noResultLayout.setVisibility(View.VISIBLE);

            }

        } else {

            if (FilteredMovies.size() > 0) {

                addNullMovie();

                recyclerView.setVisibility(View.VISIBLE);

                noResultLayout.setVisibility(View.GONE);

            } else {

                if (!text.trim().isEmpty()) {

                    recyclerView.setVisibility(View.GONE);

                    noResultLayout.setVisibility(View.VISIBLE);

                } else {

                    addNullMovie();

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

        private int expectedCount;
        private CountDownLatch countDownLatch;

        @Override
        protected Boolean doInBackground(ArrayList<String>... urls) {
            ArrayList<String> urlsPassing = new ArrayList<>();

            for (ArrayList<String> urlList : urls) {
                urlsPassing.addAll(urlList);
            }

            expectedCount = urlsPassing.size();
            countDownLatch = new CountDownLatch(expectedCount);

            for (String url : urlsPassing) {
                if (getApplicationContext() != null) {
                    new Help().urlToJson(getApplicationContext(), url, new UrlToJSON() {
                        @Override
                        public String onSuccess(String result) {
                            ArrayList<String> content = new ArrayList<>();
                            content.add(result);
                            content.add(url);
                            content.add(url.equals(urlsPassing.get(urlsPassing.size() - 1)) ? "true" : "false");
                            //Results.add(content);
                            countDownLatch.countDown();
                            return null;
                        }
                    });
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

            JSONArray jsonArray = jsonObject.getJSONArray("Movies");

            Movie currentMovie;

            String
                    Title,
                    Image,
                    Video,
                    Genre,
                    Duration,
                    Description,
                    DescriptionIndicatorTitle,
                    JSON_Url;

            Integer
                    Year,
                    Position;

            Boolean
                    isUnlocked;

            for ( int m = 0; m < jsonArray.length(); m++ ) {

                Title = null;
                Image = null;
                Video = null;
                Genre = null;
                Duration = null;
                Description = null;
                DescriptionIndicatorTitle = null;
                JSON_Url = null;
                Year = null;
                Position = null;
                isUnlocked = false;

                JSONObject jsonObjectMovie = jsonArray.getJSONObject(m);

                Title = jsonObjectMovie.getString("Title");
                Image = jsonObjectMovie.getString("ImageMain");
                Position = m;
                JSON_Url = Url;
                isUnlocked = jsonObjectMovie.getBoolean("isUnlocked");

                currentMovie = new Movie(Title, Image, Video, Genre, Duration, Description, DescriptionIndicatorTitle, JSON_Url, Year, Position, isUnlocked);

                Movies.add(currentMovie);

                FilteredMovies.add(currentMovie);

            }

        } catch (JSONException e) {

            throw new RuntimeException(e);

        }

    }

    public void reloadActivity() {

        finish();

        startActivity(getIntent());

    }

    public void addNullMovie() {

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("MoreContentOption", false)) {

            FilteredMovies.add(null);

        }

    }

}