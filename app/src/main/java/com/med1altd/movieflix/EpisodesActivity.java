package com.med1altd.movieflix;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class EpisodesActivity extends AppCompatActivity {

    private static ArrayList<Episode> Episodes = new ArrayList<>();

    RecyclerView recyclerView;

    String JSON_URL, Title;

    TextView textTitle;

    Integer PositionSeries, PositionSeasons;

    RelativeLayout progressLayout;

    RecyclerViewAdapterEpisodes recyclerViewAdapterEpisodes;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        Episodes = new ArrayList<>();

        Title = getIntent().getStringExtra("Title");
        JSON_URL = getIntent().getStringExtra("JSON_URL");
        PositionSeries = getIntent().getIntExtra("PositionSeries", -1);
        PositionSeasons = getIntent().getIntExtra("PositionSeasons", -1);

        progressLayout = findViewById(R.id.progressLayout);

        backButton = findViewById(R.id.imageBackButton);

        recyclerView = findViewById(R.id.RecyclerView);

        textTitle = findViewById(R.id.TitleText);

        textTitle.setText(Title);

        onBackClickListener();

        new Help().urlToJson(this, JSON_URL, new UrlToJSON() {
            @Override
            public String onSuccess(String result) {

                try {

                    JSONObject jsonObject = new JSONObject(result);

                    JSONArray jsonArraySeries = jsonObject.getJSONArray("Series");

                    JSONObject jsonObjectSeries = jsonArraySeries.getJSONObject(PositionSeries);

                    JSONArray jsonArraySeasons = jsonObjectSeries.getJSONArray("Seasons");

                    JSONObject jsonObjectEpisodes = jsonArraySeasons.getJSONObject(PositionSeasons);

                    JSONArray jsonArrayEpisodes = jsonObjectEpisodes.getJSONArray("Episodes");

                    String
                            Title,
                            Image;

                    Boolean
                            isUnlocked;

                    for ( int s = 0; s < jsonArrayEpisodes.length(); s++ ) {

                        Title = null;

                        Image = null;

                        isUnlocked = false;

                        JSONObject jsonObjectEpisode = jsonArrayEpisodes.getJSONObject(s);

                        Title  = jsonObjectEpisode.getString("Title");
                        Image = jsonObjectEpisode.getString("Image");
                        isUnlocked = jsonObjectEpisode.getBoolean("isUnlocked");

                        Episodes.add(new Episode(Title, Image, isUnlocked));

                    }

                    Calculate();

                    progressLayout.setVisibility(View.GONE);

                } catch (JSONException e) {

                }

                return null;

            }

        });

    }

    void Calculate() {

        int nOfColumns = calculateNoOfColumns(this, 170);
        GridLayoutManager layoutManager = new GridLayoutManager(this, nOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapterEpisodes = new RecyclerViewAdapterEpisodes(this, Episodes, JSON_URL, PositionSeries, PositionSeasons);
        recyclerView.setAdapter(recyclerViewAdapterEpisodes);

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

    public void onBackClickListener() {

        new Help().setupAnimationOnClick(backButton, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                finish();

            }

        });

    }

}