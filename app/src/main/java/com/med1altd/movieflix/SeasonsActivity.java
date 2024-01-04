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

public class SeasonsActivity extends AppCompatActivity {

    ArrayList<Season>
            Seasons = new ArrayList<>();

    RecyclerView recyclerView;

    TextView titleTxt;

    String JSON_URL, Title;

    Integer Position;

    RelativeLayout progressLayout;

    RecyclerViewAdapterSeasons recyclerViewAdapterSeasons;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons);

        Seasons = new ArrayList<>();

        JSON_URL = getIntent().getStringExtra("JSON_URL");
        Position = getIntent().getIntExtra("Position", -1);
        Title = getIntent().getStringExtra("Title");
        //TitleListIndex = getIntent().getStringArrayListExtra("TitleList");

        progressLayout = findViewById(R.id.progressLayout);

        backButton = findViewById(R.id.imageBackButton);

        recyclerView = findViewById(R.id.RecyclerView);

        titleTxt = findViewById(R.id.TitleText);

        titleTxt.setText(Title);

        onBackClickListener();

        new Help().urlToJson(this, JSON_URL, new UrlToJSON() {
            @Override
            public String onSuccess(String result) {

                try {

                    JSONObject jsonObject = new JSONObject(result);

                    JSONArray jsonArraySeries = jsonObject.getJSONArray("Series");

                    //Position = TitleListIndex.indexOf(Title);

                    JSONObject jsonObjectSeries = jsonArraySeries.getJSONObject(Position);

                    JSONArray jsonArraySeasons = jsonObjectSeries.getJSONArray("Seasons");

                    String
                            Title,
                            Image,
                            Year;

                    Boolean
                            isUnlocked;

                    for ( int s = 0; s < jsonArraySeasons.length(); s++ ) {

                        Title = null;

                        Image = null;

                        Year = null;

                        isUnlocked = false;

                        JSONObject jsonObjectSeasons = jsonArraySeasons.getJSONObject(s);

                        Title  = jsonObjectSeasons.getString("Title");
                        Image = jsonObjectSeasons.getString("Image");
                        Year = jsonObjectSeasons.getString("Year");
                        isUnlocked = jsonObjectSeasons.getBoolean("isUnlocked");

                        Seasons.add(new Season(Title, Image, Year, isUnlocked));

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

        int nOfColumns = calculateNoOfColumns(this, 260);
        GridLayoutManager layoutManager = new GridLayoutManager(this, nOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapterSeasons = new RecyclerViewAdapterSeasons(this, Seasons, JSON_URL, Position, Title);
        recyclerView.setAdapter(recyclerViewAdapterSeasons);

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

        new Help().setupAnimationOnClick(backButton, 0.9f, new OnViewClickListener() {
            @Override
            public void onClick() {

                finish();

            }

        });

    }

}