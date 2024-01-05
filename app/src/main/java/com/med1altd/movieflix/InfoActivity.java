package com.med1altd.movieflix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class InfoActivity extends AppCompatActivity {

    String
            InfoString,
            PrivacyPolicyUrl,
            contactName,
            contactEmail,
            contactSubject,
            contactMessage,
            DMCAUrl;

    RelativeLayout
            progressLayout;

    Button
            PrivacyPolicy,
            Email;

    TextView
            PrivacyPolicyTxt,
            contactNameTxt,
            contactEmailTxt,
            versionTxt;

    ImageView
            backButton,
            imageMain;

    ConstraintLayout
            dmca;

    ProgressBar
            progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        versionTxt = findViewById(R.id.versionText);

        versionTxt.setText(new Help().getCurrentVersion(this));

        progressLayout = findViewById(R.id.ProgressLayout);

        PrivacyPolicy = findViewById(R.id.PrivacyPolicy);

        Email = findViewById(R.id.Email);

        PrivacyPolicyTxt = findViewById(R.id.PrivacyPolicyTxt);

        contactNameTxt = findViewById(R.id.contactName);

        contactEmailTxt = findViewById(R.id.contactEmail);

        backButton = findViewById(R.id.Back);

        dmca = findViewById(R.id.imageDMCA);

        imageMain = findViewById(R.id.imageMain);

        progressBar = findViewById(R.id.progressBar);

        InfoString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("PrivacyPolicyMessage", "");

        PrivacyPolicyUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("PrivacyPolicyUrl", "");

        contactName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("contactName", "");

        contactEmail = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("contactEmail", "");

        contactSubject = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("contactSubject", "");

        contactMessage = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("contactMessage", "");

        DMCAUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("DMCAUrl", "");

        PrivacyPolicyTxt.setText(InfoString);

        contactNameTxt.setText(contactName);

        contactEmailTxt.setText(contactEmail);

        new Help().setupAnimationOnClick(PrivacyPolicy, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                if (PrivacyPolicyUrl != null && !PrivacyPolicyUrl.trim().isEmpty()) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PrivacyPolicyUrl));

                    startActivity(intent);

                }

            }

        });

        new Help().setupAnimationOnClick(Email, 0.8f, new OnViewClickListener() {
            @Override
            public void onClick() {

                String finalContactUrl = "mailto:" + contactEmail;

                if ((contactSubject != null && !contactSubject.trim().isEmpty()) || (contactMessage != null && !contactMessage.trim().isEmpty())) {

                    finalContactUrl = finalContactUrl + "?";

                    if (contactSubject != null && !contactSubject.trim().isEmpty()) {

                        if (finalContactUrl.charAt(finalContactUrl.length() - 1) == '?') {

                            finalContactUrl = finalContactUrl + "subject=" + contactSubject;

                        }

                    }

                    if (contactMessage != null && !contactMessage.trim().isEmpty()) {

                        if (finalContactUrl.charAt(finalContactUrl.length() - 1) == '?') {

                            finalContactUrl = finalContactUrl + "body=" + contactMessage;

                        } else {

                            finalContactUrl = finalContactUrl + "&body=" + contactMessage;

                        }

                    }

                }

                if (!finalContactUrl.trim().isEmpty()) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalContactUrl));

                    startActivity(intent);

                }

            }

        });

        new Help().setupAnimationOnClick(backButton, 0.9f, new OnViewClickListener() {
            @Override
            public void onClick() {

                finish();

            }

        });

        new Help().showImage(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("DMCAImageUrl", "https://i.ibb.co/6wdZZ5x/Untitled-3.png"), imageMain, progressBar, this);

        new Help().setupAnimationOnClick(dmca, 0.9f, new OnViewClickListener() {
            @Override
            public void onClick() {

                String DMCAUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("DMCAUrl", "");

                if (!DMCAUrl.trim().isEmpty()) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DMCAUrl));

                    startActivity(intent);

                }

            }

        });

        progressLayout.setVisibility(View.GONE);

    }

}