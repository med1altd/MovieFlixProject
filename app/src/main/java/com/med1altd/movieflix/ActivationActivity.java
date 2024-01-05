package com.med1altd.movieflix;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ActivationActivity extends AppCompatActivity {

    String
            JSON_URL;

    ImageView
            Ig,
            Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        new Help().showCustomDialogBox(this, "Download!");

        JSON_URL = getResources().getString(R.string.main_json);

    }

}