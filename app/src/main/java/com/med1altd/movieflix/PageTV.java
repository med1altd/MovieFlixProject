package com.med1altd.movieflix;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageTV#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageTV extends Fragment {

    ArrayList<Channel>
            ChannelsList = new ArrayList<>();

    RecyclerView
            recyclerView;

    RelativeLayout
            progressLayout;

    public PageTV() {
        // Required empty public constructor
    }

    public static PageTV newInstance(String param1, String param2) {

        PageTV fragment = new PageTV();

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    MainActivity
            mainActivity;

    ArrayList<String>
            Urls = new ArrayList<>();

    private
    WeakReference<Context> contextReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_page_tv, container, false);

        contextReference = new WeakReference<>(getContext());

        mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {

            // Call the method on the activity

            mainActivity.setCanBeTransmited(false);

        }

        progressLayout = view.findViewById(R.id.ProgressLayout);

        recyclerView = view.findViewById(R.id.RecyclerView);

        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setHasFixedSize(false);

        try {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            String jsonUrls = sharedPreferences.getString("JSON_Urls", "{\"TV\":[{\"Url\":\"\"},{\"Url\":\"\"}],\"Movies\":[{\"Url\":\"\"},{\"Url\":\"\"}],\"Series\":[{\"Url\":\"\"}],\"Kids\":[{\"Url\":\"\"},{\"Url\":\"\"}]}");

            JSONObject
                    jsonObject = new JSONObject(jsonUrls);

            JSONArray
                    jsonArray = jsonObject.getJSONArray("TV");

            for (int i = 0; i < jsonArray.length(); i ++ ) {

                Urls.add(jsonArray.getJSONObject(i).getString("Url"));

            }

            new CombineDataAsyncTask().execute(
                    Urls);

        } catch (JSONException e) {

            throw new RuntimeException(e);

        }

        return view;

    }

    private class CombineDataAsyncTask extends AsyncTask<ArrayList<String>, Void, Boolean> {

        private int successCount = 0;
        private int expectedCount;

        private CountDownLatch countDownLatch;

        @Override
        protected Boolean doInBackground(ArrayList<String>... urls) {

            if (isCancelled()) {

                return false;

            }

            ArrayList<String> UrlsPassing = new ArrayList<>();

            ArrayList<ArrayList<String>> Results = new ArrayList<>();

            for (ArrayList<String> Urls : urls) {

                for (String url : Urls) {

                    UrlsPassing.add(url);

                }

            }

            expectedCount = UrlsPassing.size();  // Set the expected count

            countDownLatch = new CountDownLatch(expectedCount);

            for (ArrayList<String> Urls : urls) {

                for (String url : Urls) {

                    if (getContext().getApplicationContext() != null) {

                        new Help().urlToJson(getContext().getApplicationContext(), url, new UrlToJSON() {

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

            Calculate();

            mainActivity = (MainActivity) getActivity();

            if (mainActivity != null) {

                // Call the method on the activity

                mainActivity.setCanBeTransmited(true);

            }

        }

    }

    private class CombineDataAsyncTaskOld extends AsyncTask<ArrayList<String>, Void, Boolean> {

        private CountDownLatch countDownLatch;
        private int expectedCount;

        @Override
        protected Boolean doInBackground(ArrayList<String>... urls) {

            if (isCancelled()) {

                return false;

            }

            ArrayList<String> UrlsPassing = new ArrayList<>();

            ArrayList<ArrayList<String>> Results = new ArrayList<>();

            for (ArrayList<String> Urls : urls) {

                for (String url : Urls) {

                    UrlsPassing.add(url);

                }

            }

            expectedCount = UrlsPassing.size();  // Set the expected count

            countDownLatch = new CountDownLatch(expectedCount);

            for (ArrayList<String> Urls : urls) {

                for (String url : Urls) {

                    if (getContext().getApplicationContext() != null) {

                        new Help().urlToJson(getContext().getApplicationContext(), url, new UrlToJSON() {
                            @Override
                            public String onSuccess(String result) {

                                ArrayList<String> content = new ArrayList<>();

                                content.add(result);

                                if (url.equals(UrlsPassing.get(UrlsPassing.size() - 1))) {

                                    content.add("true");

                                } else {

                                    content.add("false");

                                }

                                Results.add(content);

                                countDownLatch.countDown();

                                // Check if all listeners have completed

                                if (url.equals(UrlsPassing.get(UrlsPassing.size() - 1))) {

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

            Calculate();

        }

    }

    public void startFetchingData(ArrayList<ArrayList<String>> results) {

        for (int i = 0; i < results.size(); i ++) {

            fetchDataFromUrl(results.get(i).get(0), Boolean.valueOf(results.get(i).get(1)));

        }

    }

    String
            Title = null,
            Image = null,
            Video = null,
            LiveUrl = null,
            StartString = null,
            EndString = null,
            StartStringD = null,
            EndStringD = null;

    Integer
            Start = null,
            End = null,
            StartD = null,
            EndD = null;

    Boolean
            isUnlocked = null,
            isLive = null,
            isDescription = null,
            AnotherVideo = null;

    private void fetchDataFromUrl(String result, Boolean lastOne) {

        try {

            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonArray = jsonObject.getJSONArray("TV");

            Channel
                    currentChannel;

            for (int a = 0; a < jsonArray.length(); a ++ ) {

                Title = null;
                Image = null;
                Video = null;
                LiveUrl = null;
                StartString = null;
                EndString = null;
                StartStringD = null;
                EndStringD = null;
                Start = 0;
                End = 0;
                StartD = 0;
                EndD = 0;
                isUnlocked = false;
                isLive = false;
                isDescription = false;
                AnotherVideo = false;

                JSONObject jsonObjectChannel = jsonArray.getJSONObject(a);

                if (!jsonObject.getJSONArray("Main").getJSONObject(0).getBoolean("Live")) {

                    AnotherVideo = true;

                    Video = jsonObject.getJSONArray("Main").getJSONObject(0).getString("Video");

                    StartString = jsonObject.getJSONArray("Main").getJSONObject(0).getString("Title");

                    EndString = jsonObject.getJSONArray("Main").getJSONObject(0).getString("Description");

                } else {

                    Title = jsonObjectChannel.getString("Title");
                    Video = jsonObjectChannel.getString("Video");
                    isLive = jsonObjectChannel.getBoolean("liveContent");
                    isDescription = jsonObjectChannel.getBoolean("liveDescription");

                    if (isLive
                            || isDescription) {

                        LiveUrl = jsonObjectChannel.getString("LiveUrl");

                    }

                    if (isLive) {

                        StartString = jsonObjectChannel.getString("StartString");

                        EndString = jsonObjectChannel.getString("EndString");

                        Start = jsonObjectChannel.getInt("Start");

                        End = jsonObjectChannel.getInt("End");

                    }

                    if (isDescription) {

                        StartStringD = jsonObjectChannel.getString("StartStringD");

                        EndStringD = jsonObjectChannel.getString("EndStringD");

                        StartD = jsonObjectChannel.getInt("StartD");

                        EndD = jsonObjectChannel.getInt("EndD");

                    }

                }

                Image = jsonObjectChannel.getString("Image");

                isUnlocked = jsonObjectChannel.getBoolean("isUnlocked");

                currentChannel = new Channel(
                        Title,
                        Image,
                        Video,
                        LiveUrl,
                        StartString,
                        EndString,
                        StartStringD,
                        EndStringD,
                        Start,
                        End,
                        StartD,
                        EndD,
                        isUnlocked,
                        isLive,
                        isDescription,
                        AnotherVideo
                );

                ChannelsList.add(currentChannel);

                //currentChannels.add(currentChannel);

            }

        } catch (JSONException e) {

            throw new RuntimeException(e);

        }

    }

    void Calculate() {

        int nOfColumns = 3;

        if (mainActivity != null) {

            nOfColumns = new Help().calculateNoOfColumns(mainActivity.displayMetrics(), 120);

        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), nOfColumns);

        recyclerView.setLayoutManager(layoutManager);

        RecyclerViewAdapterChannels recyclerViewAdapterChannels = new RecyclerViewAdapterChannels(getContext(), ChannelsList);

        recyclerView.setAdapter(recyclerViewAdapterChannels);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                progressLayout.setVisibility(View.GONE);

            }

        }, 500);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (ChannelsList != null) {

            Calculate();

        }

    }

    @Override
    public void onPause() {

        super.onPause();

        ChannelsList = new ArrayList<>();

    }

    @Override
    public void onResume() {

        super.onResume();

        ChannelsList = new ArrayList<>();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {

            // Call the method on the activity

            mainActivity.setCanBeTransmited(true);

        }

    }

    @Override
    public void onDestroyView() {

        ChannelsList = new ArrayList<>();

        contextReference.clear();

        super.onDestroyView();

    }

}