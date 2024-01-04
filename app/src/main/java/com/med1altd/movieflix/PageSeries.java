package com.med1altd.movieflix;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class PageSeries extends Fragment {

    String JSON_URL;

    ArrayList<Category> Categories = new ArrayList<>();

    RecyclerView recyclerView;

    RelativeLayout loadingLayout;

    JSONObject jsonObject;

    public PageSeries() {
        // required empty public constructor.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pageseries, container, false);

        JSON_URL = getResources().getString(R.string.main_json);

        loadingLayout = view.findViewById(R.id.ProgressLayout);

        recyclerView = view.findViewById(R.id.RecyclerView);

        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setHasFixedSize(false);

        success();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Calculate();

    }

    void Calculate() {

        int nOfColumns = calculateNoOfColumns(getContext(), 300);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), nOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapterCategories recyclerViewAdapterCategories = new RecyclerViewAdapterCategories(getContext(), Categories, "Series");
        recyclerView.setAdapter(recyclerViewAdapterCategories);

    }

    public static int calculateNoOfColumns(Context context, float columnDp) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnDp); // + 0.5
        return noOfColumns;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();



    }

    @Override
    public void onPause() {

        Categories = new ArrayList<>();

        super.onPause();
    }

    @Override
    public void onResume() {

        Categories = new ArrayList<>();

        super.onResume();

    }

    public void success() {

        String result;

        Ion.with(this).load(JSON_URL).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception exception, String result) {

                try {

                    jsonObject = new JSONObject(result);

                    JSONArray jsonArray = jsonObject.getJSONArray("Series");

                    String
                            Title,
                            Image,
                            JSON_URL;

                    Integer
                            Category;

                    Boolean
                            isUnlocked;

                    for ( int i = 0; i < jsonArray.length(); i++ ) {

                        Title = null;
                        Image = null;
                        JSON_URL = null;
                        Category = null;
                        isUnlocked = null;

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        Title = jsonObject.getString("Title");
                        Image = jsonObject.getString("Image");
                        JSON_URL = jsonObject.getString("JSON");
                        Category = i;
                        isUnlocked = jsonObject.getBoolean("isUnlocked");

                        Categories.add(new Category(Title, Image, JSON_URL, Category, isUnlocked));

                    }

                    Calculate();

                    loadingLayout.setVisibility(View.GONE);

                } catch (JSONException e) {

                }

            }

        });

    }

}