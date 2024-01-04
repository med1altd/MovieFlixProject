package com.med1altd.movieflix;

import android.app.Activity;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import android.widget.ImageView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Help {

    static Boolean
            canShowInterstitial;

    public static void setCanShowInterstitial(Boolean newValue) {

        canShowInterstitial = newValue;

    }

    static Handler
            handler;

    static Runnable
            runnable;

    public static void notifyWhenNextInterstitial(UrlToJSON myReceiver) {

        if (canShowInterstitial != null && !canShowInterstitial) {

            handler = new Handler();

            runnable = new Runnable() {
                @Override
                public void run() {

                    if (!canShowInterstitial) {

                        handler.postDelayed(this, 1000);

                    } else {

                        myReceiver.onSuccess(null);

                    }

                }

            };

            handler.postDelayed(runnable, 1000);

        } else {

            myReceiver.onSuccess(null);

        }

    }

    public static void delayNextInterstitialRequest(Context context, String Key) {

        int delay = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getInt(Key, 0);

        canShowInterstitial = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                canShowInterstitial = true;

            }

        }, delay);

    }

    public static void destroyWaitTillNextInterstitial() {

        if (handler != null && runnable != null) {

            handler.removeCallbacks(runnable);

        }

    }

    Integer
            MaxDailyAdRequests,
            currentDailyAdRequests;

    public void canShowInterstitialBasedOnDailyLimits(Context context, ReturnMultipleResults returnMultipleResults) {

        getCurrentDateAndTime(context, new ReturnMultipleResults() {
            @Override
            public ArrayList<Object> onSuccessMultipleResults(ArrayList<Object> results) {

                if (results != null) {

                    ArrayList<Object> resultList = new ArrayList<>();

                    String
                            date = results.get(0).toString(),
                            time = results.get(1).toString();

                    resultList.add(date);

                    resultList.add(time);

                    ArrayList<String> dividedDate = getDividedDate(date);

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://movieflix-live-tv-default-rtdb.europe-west1.firebasedatabase.app");

                    DatabaseReference
                            databaseReferenceValue = firebaseDatabase.getReference().child(new Help().getCurrentBundleVersion(context)).child("AdMob").child("Statistics").child(dividedDate.get(0)).child(dividedDate.get(1)).child(dividedDate.get(2)).child("Requests");

                    // Add a listener to check if data exists at that location

                    databaseReferenceValue.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                // If data exists, retrieve the current value of "Tries" and increment it

                                //Integer currentTries = dataSnapshot.getValue(Integer.class);

                                resultList.add("hasValue");

                                resultList.add(dataSnapshot.getValue(Long.class));

                            } else {

                                // If data does not exist, generate a new unique key

                                resultList.add("hasValue");

                                resultList.add(0L);

                            }

                            returnMultipleResults.onSuccessMultipleResults(resultList);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            // Handle errors here

                            ArrayList<Object> resultList = new ArrayList<>();

                            resultList.add("errorInFirebase");

                            resultList.add(0L);

                            returnMultipleResults.onSuccessMultipleResults(resultList);

                        }

                    });

                } else {

                    returnMultipleResults.onSuccessMultipleResults(null);

                }

                return null;

            }

        });

    }

    public void canShowInterstitialBasedOnHourLimits(Context context, ReturnMultipleResults returnMultipleResults) {

        getCurrentDateAndTime(context, new ReturnMultipleResults() {
            @Override
            public ArrayList<Object> onSuccessMultipleResults(ArrayList<Object> results) {

                if (results != null) {

                    ArrayList<Object> resultList = new ArrayList<>();

                    String
                            date = results.get(0).toString(),
                            time = results.get(1).toString();

                    resultList.add(date);

                    resultList.add(time);

                    ArrayList<String> dividedDate = getDividedDate(date);

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://movieflix-live-tv-default-rtdb.europe-west1.firebasedatabase.app");

                    DatabaseReference
                            databaseReferenceValue = firebaseDatabase.getReference().child(new Help().getCurrentBundleVersion(context)).child("AdMob").child("Statistics").child(dividedDate.get(0)).child(dividedDate.get(1)).child(dividedDate.get(2)).child("Requests");

                    // Add a listener to check if data exists at that location

                    databaseReferenceValue.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                // If data exists, retrieve the current value of "Tries" and increment it

                                //Integer currentTries = dataSnapshot.getValue(Integer.class);

                                resultList.add("hasValue");

                                resultList.add(dataSnapshot.getValue(Long.class));

                                returnMultipleResults.onSuccessMultipleResults(resultList);

                            } else {

                                // If data does not exist, generate a new unique key

                                resultList.add("hasValue");

                                resultList.add(0L);

                                returnMultipleResults.onSuccessMultipleResults(resultList);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            // Handle errors here

                            ArrayList<Object> resultList = new ArrayList<>();

                            resultList.add("errorInFirebase");

                            resultList.add(0L);

                            returnMultipleResults.onSuccessMultipleResults(resultList);

                        }

                    });

                } else {

                    returnMultipleResults.onSuccessMultipleResults(null);

                }

                return null;

            }

        });

    }

    public Boolean compareCurrentAndLimitAdRequests(Long cur, Long Max) {

        if (cur != null && Max != null) {

            if (cur < Max) {

                return true;

            } else {

                return false;

            }

        } else {

            return false;

        }

    }

    public void addStatisticToTheCount(Context context, String Type) {

        getCurrentDateAndTime(context, new ReturnMultipleResults() {
            @Override
            public ArrayList<Object> onSuccessMultipleResults(ArrayList<Object> results) {

                if (results != null) {

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://movieflix-live-tv-default-rtdb.europe-west1.firebasedatabase.app");

                    ArrayList<String> dividedDate = getDividedDate(results.get(0).toString());

                    DatabaseReference
                            databaseReferenceDaily = firebaseDatabase.getReference().child(new Help().getCurrentBundleVersion(context)).child("AdMob").child("Statistics").child(dividedDate.get(0)).child(dividedDate.get(1)).child(dividedDate.get(2)),
                            databaseReferenceHour = firebaseDatabase.getReference().child(new Help().getCurrentBundleVersion(context)).child("AdMob").child("Statistics").child(dividedDate.get(0)).child(dividedDate.get(1)).child(dividedDate.get(2)).child(results.get(1).toString());

                    // Add a listener to check if data exists at that location

                    databaseReferenceDaily.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                // If data exists, retrieve the current value of "Tries" and increment it

                                //Integer currentTries = dataSnapshot.getValue(Integer.class);

                                Long dailyRequests = dataSnapshot.child(Type).getValue(Long.class);

                                if (dailyRequests != null) {

                                    databaseReferenceDaily.child(Type).setValue(dailyRequests + 1);

                                } else {

                                    // Handle the case where HourlyRequests is null

                                    databaseReferenceDaily.child(Type).setValue(1L);

                                }

                            } else {

                                // If data does not exist, generate a new unique key

                                databaseReferenceDaily.child(Type).setValue(1);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            // Handle errors here

                        }

                    });

                    databaseReferenceHour.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                // If data exists, retrieve the current value of "Tries" and increment it

                                //Integer currentTries = dataSnapshot.getValue(Integer.class);

                                Long HourlyRequests = dataSnapshot.child(Type).getValue(Long.class);

                                if (HourlyRequests != null) {

                                    databaseReferenceHour.child(Type).setValue(HourlyRequests + 1);

                                } else {

                                    // Handle the case where HourlyRequests is null

                                    databaseReferenceHour.child(Type).setValue(1L);

                                }

                            } else {

                                // If data does not exist, generate a new unique key

                                databaseReferenceHour.child(Type).setValue(1);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            // Handle errors here

                        }

                    });

                }

                return null;

            }

        });

    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {

            // connected to the internet

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                // connected to wifi

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                // connected to mobile data

            }

            return true;

        } else {

            return false;

            // not connected to the internet

        }

    }

    public void urlToJson(Context context, String Url, UrlToJSON myReceiver) {

        if (Url.contains("main")) {

            Ion
                    .with(context)
                    .load(Url)
                    .setHeader("Cache-Control", "no-cache") // Set cache control directive to "no-cache"
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception exception, String result) {

                            myReceiver.onSuccess(result);

                        }

                    });

        } else {

            Ion
                    .with(context)
                    .load(Url)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception exception, String result) {

                            myReceiver.onSuccess(result);

                        }

                    });

        }

    }

    public void setupAnimationOnClick(View itemView, float scale, OnViewClickListener myReceiver) {

        itemView.setFocusable(true);

        itemView.setClickable(true);

        itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    itemView.animate().scaleX(scale).scaleY(scale).setDuration(0);

                } else {

                    itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(0);

                }

            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemView.animate().scaleX(scale).scaleY(scale).setDuration(0);
                return false;
            }
        });

        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_PRESS ||
                        motionEvent.getAction() == MotionEvent.ACTION_SCROLL ||
                        motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    itemView.animate().scaleX(scale).scaleY(scale).setDuration(0);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE ||
                        motionEvent.getAction() == MotionEvent.ACTION_BUTTON_RELEASE ||
                        motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(0);

                }

                return false;

            }

        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myReceiver.onClick();

            }
        });

    }

    public static int calculateNoOfColumns(DisplayMetrics displayMetrics, float columnDp) {

        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnDp); // + 0.5
        return noOfColumns;

    }

    public final Boolean isAndroidTV(Context context) {

        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);

        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {

            return true;

        } else {

            return false;

        }

    }

    public Boolean isPremium(Context context) {

        //        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("Premium", false);

        return false;

    }

    public Boolean isPortrait(Context context) {

        int orientation = context.getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            return false;

        } else {

            return true;

        }

    }

    FirebaseDatabase
            firebaseDatabase;

    DatabaseReference
            databaseReference;

    public void initializeFirebaseDatabase() {

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child("Users");

    }

    String
            Key;

    SharedPreferences
            sharedPreferences;

    public void pushStringToFirebase(Context context, String category, String value) {

        ArrayList<String>
                banChars = new ArrayList<>();

        banChars.add(".");
        banChars.add("#");
        banChars.add("$");
        banChars.add("[");
        banChars.add("]");

        for (int i = 0; i < banChars.size(); i ++) {

            value = value.replace(banChars.get(i), "");

        }

        // Create a reference to a location in the database based on the provided category and value

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://movieflix-live-tv-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference
                databaseReferenceValue = firebaseDatabase.getReference().child(new Help().getCurrentBundleVersion(context)).child(category).child(value);

        // Add a listener to check if data exists at that location

        databaseReferenceValue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    // If data exists, retrieve the current value of "Tries" and increment it

                    //Integer currentTries = dataSnapshot.getValue(Integer.class);

                    Integer currentTries = dataSnapshot.child("Views").getValue(Integer.class);

                    databaseReferenceValue.child("Views").setValue(currentTries + 1);

                } else {

                    // If data does not exist, generate a new unique key

                    databaseReferenceValue.child("Views").setValue(1);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Handle errors here

            }

        });

        /*

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://movieflix-live-tv-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference databaseReference = firebaseDatabase.getReference().child(category).child(value);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    // Data already exists, update the "Tries" value
                    Integer tries = dataSnapshot.child("Tries").getValue(Integer.class);
                    if (tries != null) {
                        databaseReference.child("Tries").setValue(tries + 1);
                        Log.d("FirebaseUpdate", "Tries updated successfully");
                    } else {
                        Log.e("FirebaseUpdate", "Invalid data format for 'Tries'");
                    }
                } else {

                    databaseReference.push();

                    // Data does not exist, create a new entry
                    Code code = new Code(1);
                    databaseReference.setValue(code);
                    Log.d("FirebaseUpdate", "New entry created successfully");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("FirebaseUpdate", "Database operation canceled: " + databaseError.getMessage());
            }
        });

        */

    }

    String
            final_Key;

    public String hasRealUserKey(Context context, String key) {

        final_Key = null;

        databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    // Key exists in the database

                    final_Key = key;

                } else {

                    // Key does not exist in the database

                    final_Key = generateUserKey(context);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Handle errors

                final_Key = generateUserKey(context);

            }

        });

        return final_Key;

    }

    String
            generatedKey;

    public String generateUserKey(Context context) {

        generatedKey = null;

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                generatedKey = databaseReference.push().getKey();

                databaseReference.child(generatedKey).setValue(new User(generate4DigitNumber(), false));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Handle errors

            }

        });

        return generatedKey;

    }

    public String generate4DigitNumber() {

        String
                numS = "";

        Integer
                randomPos;

        ArrayList<Integer>
                a = new ArrayList<Integer>(),
                b = new ArrayList<Integer>(),
                c = new ArrayList<Integer>(),
                d = new ArrayList<Integer>();

        for ( int i = 0; i < 10; i ++ ) {

            a.add(i);

            b.add(i);

            c.add(i);

            d.add(i);

        }

        Collections.shuffle(a);

        Collections.shuffle(b);

        Collections.shuffle(c);

        Collections.shuffle(d);

        do {

            for ( int i = 0; i < 4; i ++ ) {

                randomPos = new Random().nextInt(10);

                if (i == 0) {

                    numS = numS + Integer.toString(a.get(randomPos));

                } else if (i == 1) {

                    numS = numS + Integer.toString(b.get(randomPos));

                } else if (i == 2) {

                    numS = numS + Integer.toString(c.get(randomPos));

                } else {

                    numS = numS + Integer.toString(d.get(randomPos));

                }

            }

        } while (sameDigits(numS));

        return numS;

    }

    public static boolean sameDigits(String numberString) {

        for (int i = 1; i <= 9; i++) {

            if (numberString.replaceAll(String.valueOf(i), "").length() == 0)

                return true;

        }

        return false;

    }

    public Boolean isActivated(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isActivated", false);

    }

    public void showCustomDialogBox(Activity activity, String message) {

        String UrlForm = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString("AddContentFormUrl", null);

        if (UrlForm != null && !UrlForm.trim().isEmpty()) {

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString("AddContentFormUrl", null)));

            activity.startActivity(intent);

        }

        /*

        // Create a dialog

        final Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);

        dialog.setContentView(R.layout.layout_custom_dialog); // Make sure you have the correct layout resource

        Window window = dialog.getWindow();

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set background to transparent

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize UI components

        Button
                unlockBtn = dialog.findViewById(R.id.unlockBtn),
                cancelBtn = dialog.findViewById(R.id.cancelBtn);

        TextView
                exampleTxt;

        TextInputEditText
                textInputEditText;

        exampleTxt = dialog.findViewById(R.id.exampleTxt);

        textInputEditText = dialog.findViewById(R.id.textInputEditText);

        String
                exampleString = PreferenceManager.getDefaultSharedPreferences(activity).getString("CodeExample", null);

        if (exampleString != null &&
                !exampleString.trim().isEmpty()) {

            exampleTxt.setText("Παράδειγμα: " + exampleString);

        } else {

            exampleTxt.setVisibility(View.GONE);

        }

        // Set click listeners

        setupAnimationOnClick(unlockBtn, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                String text = textInputEditText.getText().toString();

                if (text.trim().isEmpty()) {

                    text = "No Code!";

                }

                checkCodeAndUnlock(activity, text, false, dialog, new UrlToJSON() {
                    @Override
                    public String onSuccess(String result) {

                        if (result.equals("Valid and Activated. (Saved)")) {

                            checkActivatedJSONUrls(activity, new UrlToJSON() {
                                @Override
                                public String onSuccess(String result) {

                                    loadJSONUrls(activity, new UrlToJSON() {
                                        @Override
                                        public String onSuccess(String result) {

                                            if (activity instanceof MainActivity || activity instanceof ContextWrapper
                                                    && ((ContextWrapper) activity).getBaseContext() instanceof MainActivity) {

                                                MainActivity mainActivity = (MainActivity) activity;

                                                mainActivity.reloadPageTVAfterActivatedNewCode();

                                            } else if (activity instanceof MoviesActivity || activity instanceof ContextWrapper
                                                    && ((ContextWrapper) activity).getBaseContext() instanceof MoviesActivity) {

                                                MoviesActivity moviesActivity = (MoviesActivity) activity;

                                                moviesActivity.reloadActivity();

                                            } else if (activity instanceof SeriesActivity || activity instanceof ContextWrapper
                                                    && ((ContextWrapper) activity).getBaseContext() instanceof SeriesActivity) {

                                                SeriesActivity seriesActivity = (SeriesActivity) activity;

                                                seriesActivity.reloadActivity();

                                            } else if (activity instanceof YouTubeChannelsActivity || activity instanceof ContextWrapper
                                                    && ((ContextWrapper) activity).getBaseContext() instanceof YouTubeChannelsActivity) {

                                                YouTubeChannelsActivity youTubeChannelsActivity = (YouTubeChannelsActivity) activity;

                                                youTubeChannelsActivity.reloadActivity();

                                            }

                                            return null;

                                        }

                                    });

                                    return null;

                                }

                            });

                        }

                        return null;

                    }

                });

            }

        });

        setupAnimationOnClick(cancelBtn, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                dialog.dismiss();

            }

        });

        // Show the dialog

        dialog.show();

        */

    }

    private static boolean isTabletAspectRatio(double aspectRatio) {

        // Define a threshold value for tablet aspect ratio

        double tabletAspectRatioThreshold = 1.5;

        // Compare aspect ratio with the threshold

        return aspectRatio > tabletAspectRatioThreshold;

    }

    public void checkCodeAndUnlock(Context context, String Code, Boolean startup, Dialog dialog, UrlToJSON myReceiver) {

        String
                JSON_URL = context.getResources().getString(R.string.main_json);

        urlToJson(context, JSON_URL, new UrlToJSON() {
            @Override
            public String onSuccess(String result) {

                if (result != null) {

                    try {

                        if (!androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString("JSON_Activated_Codes", "No Code!").contains(Code)) {

                            JSONObject jsonObject = new JSONObject(result);

                            JSONArray jsonArrayAccess = jsonObject.getJSONArray("Access");

                            Integer
                                    pos = -1;

                            do {

                                pos++;

                            } while (!Code.equalsIgnoreCase(jsonArrayAccess.getJSONObject(pos).getString("Code"))
                                    && !jsonArrayAccess.getJSONObject(pos).getBoolean("Valid")
                                    && !jsonArrayAccess.getJSONObject(pos).getBoolean("Active")
                                    && pos < jsonArrayAccess.length());

                            if (Code.equalsIgnoreCase(jsonArrayAccess.getJSONObject(pos).getString("Code"))) {

                                if (jsonArrayAccess.getJSONObject(pos).getBoolean("Valid")
                                        && jsonArrayAccess.getJSONObject(pos).getBoolean("Active")) {

                                    try {

                                        String jsonCodes = PreferenceManager.getDefaultSharedPreferences(context).getString("JSON_Activated_Codes", "");

                                        JSONObject
                                                jsonObjectCodes,
                                                newJsonObject;

                                        JSONArray
                                                jsonArrayCodes;

                                        if (!jsonCodes.trim().isEmpty()) {

                                            jsonObjectCodes = new JSONObject(jsonCodes);

                                            jsonArrayCodes = jsonObjectCodes.getJSONArray("Codes");

                                        } else {

                                            jsonObjectCodes = new JSONObject();

                                            jsonArrayCodes = new JSONArray();

                                        }

                                        newJsonObject = new JSONObject();

                                        newJsonObject.put("Code", Code);

                                        jsonArrayCodes.put(newJsonObject);

                                        JSONObject newJsonObjectFinal = new JSONObject();

                                        newJsonObjectFinal.put("Codes", jsonArrayCodes);

                                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("JSON_Activated_Codes", newJsonObjectFinal.toString()).apply();

                                        myReceiver.onSuccess("Valid and Activated. (Saved)");

                                    }  catch (JSONException e) {

                                        throw new RuntimeException(e);

                                    }

                                } else {

                                    if (!startup) {

                                        Toast.makeText(context, "Μη έγκυρος κωδικός περιεχομένου!", Toast.LENGTH_SHORT).show();

                                    }

                                    myReceiver.onSuccess("Invalid Version Code!");

                                }

                            } else {

                                if (!startup) {

                                    Toast.makeText(context, "Ο κωδικός περιεχομένου δεν βρέθηκε!", Toast.LENGTH_SHORT).show();

                                    pushStringToFirebase(context, "Not Found Codes", Code);

                                }

                                myReceiver.onSuccess("Version Code Not Found!");

                            }

                        } else {

                            if (!startup) {

                                Toast.makeText(context, "Ο κωδικός περιεχομένου έχει ήδη ενεργοποιηθεί!", Toast.LENGTH_SHORT).show();

                            }

                            myReceiver.onSuccess("Version Code Already Activated!");

                        }

                        if (!startup) {

                            dialog.dismiss();

                        }

                    } catch (JSONException e) {



                    }

                }

                return null;

            }

        });

    }

    public void showImage(String ImageUrl, ImageView view, View progressBar, Context context) {

        if (ImageUrl != null && !ImageUrl.trim().isEmpty()) {

            Picasso.get().load(ImageUrl).centerCrop().fit().into(view, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onError(Exception e) {

                    Picasso.get().load("https://i.ibb.co/9yLBtnr/NO-IMAGE.png").centerCrop().fit().into(view, new com.squareup.picasso.Callback() {

                        @Override
                        public void onSuccess() {

                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Exception e) {



                        }

                    });

                }

            });

        } else {

            Picasso.get().load(androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString("NoImageUrl", "https://i.ibb.co/9yLBtnr/NO-IMAGE.png")).centerCrop().fit().into(view, new com.squareup.picasso.Callback() {

                @Override
                public void onSuccess() {

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onError(Exception e) {



                }

            });

        }

    }

    public void checkStandardJSONUrls(Context context, UrlToJSON myReceiver) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        try {

            JSONObject
                    jsonObject;

            JSONArray
                    jsonArrayTV = new JSONArray(),
                    jsonArrayMovies = new JSONArray(),
                    jsonArraySeries = new JSONArray(),
                    jsonArrayKids = new JSONArray();

            new Help().urlToJson(context, context.getResources().getString(R.string.main_json), new UrlToJSON() {
                @Override
                public String onSuccess(String result) {

                    try {

                        JSONObject
                                jsonObjectResult = new JSONObject(result),
                                newJsonObject;

                        JSONArray
                                jsonArray;

                        jsonArray = jsonObjectResult.getJSONArray("Standard").getJSONObject(0).getJSONArray("TV");

                        for (int i = 0; i < jsonArray.length(); i ++ ) {

                            newJsonObject = new JSONObject();

                            newJsonObject.put("Url", jsonArray.getJSONObject(i).getString("Url"));

                            jsonArrayTV.put(0, newJsonObject);

                        }

                        jsonArray = jsonObjectResult.getJSONArray("Standard").getJSONObject(0).getJSONArray("Movies");

                        for (int i = 0; i < jsonArray.length(); i ++ ) {

                            newJsonObject = new JSONObject();

                            newJsonObject.put("Url", jsonArray.getJSONObject(i).getString("Url"));

                            newJsonObject.put("Category", jsonArray.getJSONObject(i).getString("Category"));

                            jsonArrayMovies.put(i, newJsonObject);

                        }

                        jsonArray = jsonObjectResult.getJSONArray("Standard").getJSONObject(0).getJSONArray("Series");

                        for (int i = 0; i < jsonArray.length(); i ++ ) {

                            newJsonObject = new JSONObject();

                            newJsonObject.put("Url", jsonArray.getJSONObject(i).getString("Url"));

                            newJsonObject.put("Category", jsonArray.getJSONObject(i).getString("Category"));

                            jsonArraySeries.put(i, newJsonObject);

                        }

                        jsonArray = jsonObjectResult.getJSONArray("Standard").getJSONObject(0).getJSONArray("Kids");

                        for (int i = 0; i < jsonArray.length(); i ++ ) {

                            newJsonObject = new JSONObject();

                            newJsonObject.put("Url", jsonArray.getJSONObject(i).getString("Url"));

                            newJsonObject.put("Category", jsonArray.getJSONObject(i).getString("Category"));

                            jsonArrayKids.put(i, newJsonObject);

                        }

                        JSONObject finalJSONObject = new JSONObject();

                        finalJSONObject.put("TV", jsonArrayTV);

                        finalJSONObject.put("Movies", jsonArrayMovies);

                        finalJSONObject.put("Series", jsonArraySeries);

                        finalJSONObject.put("Kids", jsonArrayKids);

                        sharedPreferences.edit().putString("JSON_Urls", finalJSONObject.toString()).apply();

                        myReceiver.onSuccess(result);

                    } catch (JSONException e) {

                        throw new RuntimeException(e);

                    }

                    return null;

                }

            });

        } catch (Resources.NotFoundException e) {

            throw new RuntimeException(e);

        }

    }

    public void checkActivatedJSONUrls(Context context, UrlToJSON myReceiver) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String jsonCodes = sharedPreferences.getString("JSON_Activated_Codes", "");

        if (!jsonCodes.trim().isEmpty()) {

            try {

                JSONObject
                        jsonObject = new JSONObject(jsonCodes);

                JSONArray
                        jsonArrayCodes = jsonObject.getJSONArray("Codes");

                ArrayList<String> Codes = new ArrayList<>();

                for (int i = 0; i < jsonArrayCodes.length(); i ++ ) {

                    Codes.add(jsonArrayCodes.getJSONObject(i).getString("Code"));

                }

                new Help().urlToJson(context, context.getResources().getString(R.string.main_json), new UrlToJSON() {
                    @Override
                    public String onSuccess(String result) {

                        try {

                            JSONObject
                                    jsonObjectResult = new JSONObject(result),
                                    jsonObjectNewJSON = new JSONObject();

                            JSONArray
                                    jsonArrayAccess = jsonObjectResult.getJSONArray("Access"),
                                    jsonArrayNew = new JSONArray();

                            Integer
                                    a;

                            Boolean
                                    found;

                            for (int c = 0; c < Codes.size(); c ++ ) {

                                a = -1;

                                found = false;

                                do {

                                    a ++;

                                    if (jsonArrayAccess.getJSONObject(a).getString("Code").equals(Codes.get(c))
                                            && jsonArrayAccess.getJSONObject(a).getBoolean("Active")) {

                                        found = true;

                                    }

                                } while (!jsonArrayAccess.getJSONObject(a).getString("Code").equals(Codes.get(c))
                                        && !jsonArrayAccess.getJSONObject(a).getBoolean("Active")
                                        && a < Codes.size());

                                if (found) {

                                    jsonObjectNewJSON.put("Code", jsonArrayAccess.getJSONObject(a).getString("Code"));

                                    jsonObjectNewJSON.put("Index", a);

                                    jsonArrayNew.put(jsonObjectNewJSON);

                                }

                            }

                            if (Codes.size() != 0) {

                                JSONObject jsonObjectNew = new JSONObject().put("Codes", jsonArrayNew);

                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("JSON_Activated_Codes", jsonObjectNew.toString()).apply();

                            }

                            myReceiver.onSuccess(result);

                        } catch (JSONException e) {

                            throw new RuntimeException(e);

                        }

                        return null;

                    }

                });

            } catch (JSONException e) {

                throw new RuntimeException(e);

            }

        } else {

            myReceiver.onSuccess(null);

        }

    }

    public void loadJSONUrls(Context context, UrlToJSON myReceiver) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String jsonUrls = sharedPreferences.getString("JSON_Urls", "{" +
                "\"TV\":[{\"Url\":\"\"}]," +
                "\"Movies\":[{\"Url\":\"\"}]," +
                "\"Series\":[{\"Url\":\"\"}]," +
                "\"Kids\":[{\"Url\":\"\"}]}");

        String jsonCodes = sharedPreferences.getString("JSON_Activated_Codes", "{\"Codes\":[]}");

        ArrayList<String> Codes = new ArrayList<>();

        ArrayList<Integer> CodesIndexes = new ArrayList<>();

        if (!jsonCodes.trim().isEmpty()) {

            try {

                JSONObject
                        jsonObjectUrls = new JSONObject(jsonUrls),
                        jsonObjectCodes = new JSONObject(jsonCodes);

                JSONArray
                        jsonArrayTV = jsonObjectUrls.getJSONArray("TV"),
                        jsonArrayMovies = jsonObjectUrls.getJSONArray("Movies"),
                        jsonArraySeries = jsonObjectUrls.getJSONArray("Series"),
                        jsonArrayKids = jsonObjectUrls.getJSONArray("Kids"),
                        jsonArrayCodes = jsonObjectCodes.getJSONArray("Codes");

                for (int i = 0; i < jsonArrayCodes.length(); i ++ ) {

                    Codes.add(jsonArrayCodes.getJSONObject(i).getString("Code"));

                    CodesIndexes.add(jsonArrayCodes.getJSONObject(i).getInt("Index"));

                }

                new Help().urlToJson(context, context.getResources().getString(R.string.main_json), new UrlToJSON() {

                    @Override
                    public String onSuccess(String result) {

                        try {

                            JSONObject
                                    jsonObjectResult = new JSONObject(result),
                                    newJsonObject,
                                    jsonObjectJSONs;

                            newJsonObject = new JSONObject();

                            JSONArray
                                    jsonArrayAccess,
                                    jsonArrayCategory;

                            JSONObject
                                    jsonObjectCurrentCode;

                            jsonArrayAccess = jsonObjectResult.getJSONArray("Access");

                            for (int c = 0; c < Codes.size(); c ++ ) {

                                jsonObjectCurrentCode = jsonArrayAccess.getJSONObject(CodesIndexes.get(c));

                                jsonObjectJSONs = jsonObjectCurrentCode.getJSONArray("JSON_Urls").getJSONObject(0);

                                jsonArrayCategory = jsonObjectJSONs.getJSONArray("TV");

                                newJsonObject = new JSONObject();

                                for (int t = 0; t < jsonArrayCategory.length(); t ++ ) {

                                    newJsonObject.put("Url", jsonArrayCategory.getJSONObject(t).getString("Url"));

                                }

                                jsonArrayTV.put(newJsonObject);

                                jsonArrayCategory = jsonObjectJSONs.getJSONArray("Movies");

                                for (int t = 0; t < jsonArrayCategory.length(); t ++ ) {

                                    newJsonObject = new JSONObject();

                                    newJsonObject.put("Url", jsonArrayCategory.getJSONObject(t).getString("Url"));

                                    newJsonObject.put("Category", jsonArrayCategory.getJSONObject(t).getString("Category"));

                                    jsonArrayMovies.put(newJsonObject);

                                }

                                jsonArrayCategory = jsonObjectJSONs.getJSONArray("Series");

                                for (int t = 0; t < jsonArrayCategory.length(); t ++ ) {

                                    newJsonObject = new JSONObject();

                                    newJsonObject.put("Url", jsonArrayCategory.getJSONObject(t).getString("Url"));

                                    newJsonObject.put("Category", jsonArrayCategory.getJSONObject(t).getString("Category"));

                                    jsonArraySeries.put(newJsonObject);

                                }

                                jsonArrayCategory = jsonObjectJSONs.getJSONArray("Kids");

                                for (int t = 0; t < jsonArrayCategory.length(); t ++ ) {

                                    newJsonObject = new JSONObject();

                                    newJsonObject.put("Url", jsonArrayCategory.getJSONObject(t).getString("Url"));

                                    newJsonObject.put("Category", jsonArrayCategory.getJSONObject(t).getString("Category"));

                                    jsonArrayKids.put(newJsonObject);

                                }

                            }

                            JSONObject finalJSONObject = new JSONObject();

                            finalJSONObject.put("TV", jsonArrayTV);

                            finalJSONObject.put("Movies", jsonArrayMovies);

                            finalJSONObject.put("Series", jsonArraySeries);

                            finalJSONObject.put("Kids", jsonArrayKids);

                            sharedPreferences.edit().putString("JSON_Urls", finalJSONObject.toString()).apply();

                            myReceiver.onSuccess(result);

                        } catch (JSONException e) {

                            throw new RuntimeException(e);

                        }

                        return null;

                    }

                });

            } catch (JSONException e) {

                throw new RuntimeException(e);

            }

        } else {

            myReceiver.onSuccess(null);

        }

    }

    String
            DMCAUrl;

    public void manageReportButton(Context context, View view) {

        DMCAUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("DMCAUrl", null);

        setupAnimationOnClick(view, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                if (DMCAUrl != null && !DMCAUrl.trim().isEmpty()) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DMCAUrl));

                    context.startActivity(intent);

                }

            }

        });

    }

    String TimeAPIUrl = "https://worldtimeapi.org/api/timezone/Europe/Athens";

    public void getCurrentDateAndTime(Context context, ReturnMultipleResults returns) {

        urlToJson(context, TimeAPIUrl, new UrlToJSON() {
            @Override
            public String onSuccess(String result) {

                try {

                    ArrayList<Object> results = new ArrayList<>();

                    if (result != null) {

                        JSONObject jsonObject = new JSONObject(result);

                        results.add(getDate(jsonObject.getString("datetime")));

                        results.add(getTime(jsonObject.getString("datetime")));

                        if (results.get(0) != null &&
                                !results.get(0).toString().trim().isEmpty() &&
                                results.get(1) != null &&
                                !results.get(0).toString().trim().isEmpty()) {

                            returns.onSuccessMultipleResults(results);

                        } else {

                            returns.onSuccessMultipleResults(null);

                        }

                    } else {

                        returns.onSuccessMultipleResults(null);

                    }

                } catch (JSONException e) {

                    throw new RuntimeException(e);

                }

                return null;

            }

        });

    }

    public String getDate(String result) {

        return result.substring(8, 10) + "-" + result.substring(5, 7) + "-" + result.substring(0, 4);

    }

    public String getTime(String result) {

        return result.substring(11, 13) + ":00";

    }

    public long getAdMobLimit (Context context, String Type) {

        try {

            String jsonObjectString = PreferenceManager.getDefaultSharedPreferences(context).getString("AdMobLimits", null);

            JSONObject jsonObject = new JSONObject(jsonObjectString);

            if (!Type.equals("Daily")) {

                if (jsonObjectString.contains("All")) {

                    if (jsonObject.getLong("All") >= 0) {

                        return jsonObject.getLong("All");

                    } else {

                        return jsonObject.getLong(Type);

                    }

                } else {

                    return jsonObject.getLong(Type);

                }

            } else {

                return jsonObject.getLong(Type);

            }

        } catch (JSONException e) {

            return 0;

        }

    }

    public String getCurrentVersion(Context context) {

        PackageManager packageManager = context.getPackageManager();

        try {

            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            return String.valueOf(packageInfo.versionName);

        } catch (PackageManager.NameNotFoundException e) {

            return "1.0.4";

        }

    }

    public String getCurrentBundleVersion(Context context) {

        PackageManager packageManager = context.getPackageManager();

        try {

            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            return String.valueOf(packageInfo.versionCode);

        } catch (PackageManager.NameNotFoundException e) {

            return "5";

        }

    }

    public ArrayList<String> getDividedDate(String date) {

        ArrayList<String> result = new ArrayList<>();

        result.add(date.substring(6, 10));

        result.add(getMonthName(Integer.valueOf(date.substring(3, 5))));

        result.add(date.substring(0, 2));

        return result;

    }

    public static String getMonthName(int monthNumber) {

        if (monthNumber >= Calendar.JANUARY && monthNumber <= Calendar.DECEMBER) {

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.MONTH, monthNumber);

            return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        } else {

            return "Invalid month number";

        }

    }

    public void readAndSaveJSONAdMobLimits(Context context, UrlToJSON urlToJSON) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://movieflix-live-tv-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference
                databaseReferenceValue = firebaseDatabase.getReference().child(new Help().getCurrentBundleVersion(context)).child("AdMob").child("Limits");

        // Add a listener to check if data exists at that location

        databaseReferenceValue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    // If data exists, retrieve the current value of "Tries" and increment it

                    String result = dataSnapshotToJSONObject(dataSnapshot).toString();

                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("AdMobLimits", result).apply();

                }

                urlToJSON.onSuccess(null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Handle errors here

                urlToJSON.onSuccess(null);

            }

        });

    }

    public JSONObject dataSnapshotToJSONObject(DataSnapshot dataSnapshot) {

        try {

            JSONObject jsonObject = new JSONObject();

            // Iterate through DataSnapshot children

            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                String key = childSnapshot.getKey();

                Object value = childSnapshot.getValue();

                // Add key-value pair to JSONObject

                jsonObject.put(key, value);

            }

            return jsonObject;

        } catch (JSONException e) {

            // Handle JSON-related exceptions

            e.printStackTrace();

            return null;

        }

    }
}