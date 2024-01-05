package com.med1altd.movieflix;

import static android.view.View.GONE;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PromoteActivity extends AppCompatActivity {

    RelativeLayout
            progressLayout;

    ConstraintLayout
            promoteImageLayout;

    LinearLayout
            promoteLayout;

    ImageView
            promoteImage;

    TextView
            promoteTitleTxt,
            promoteButtonText,
            promoteNotesTxt;

    CardView
            promoteButton;

    ProgressBar
            progressBarPromoteImage;

    String
            promoteTitle,
            promoteUrl,
            promoteImageUrl,
            promoteButtonTitle,
            promoteNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote);

        getIntentStrings();

        findViewsById();

        loadPromoteCampaign();

    }

    public void getIntentStrings() {

        promoteTitle = getIntent().getStringExtra("promoteTitle");

        promoteUrl = getIntent().getStringExtra("promoteUrl");

        promoteImageUrl = getIntent().getStringExtra("promoteImageUrl");

        promoteButtonTitle = getIntent().getStringExtra("promoteButtonTitle");

        promoteNotes = getIntent().getStringExtra("promoteNotes");

    }

    public void findViewsById() {

        progressLayout = findViewById(R.id.ProgressLayout);
        promoteLayout = findViewById(R.id.promoteLayout);
        promoteTitleTxt = findViewById(R.id.promoteTitleText);
        promoteImageLayout = findViewById(R.id.GreekVersionLayout);
        promoteImage = findViewById(R.id.imageMain);
        progressBarPromoteImage = findViewById(R.id.progressBarPromoteImage);
        promoteButton = findViewById(R.id.promoteButton);
        promoteButtonText = findViewById(R.id.promoteButtonText);
        promoteNotesTxt = findViewById(R.id.promoteNotesText);

        promoteTitleTxt.setSelected(true);

    }

    public void loadPromoteCampaign() {

        if (!promoteTitle.trim().isEmpty()) {

            promoteTitleTxt.setText(promoteTitle);

        }

        if (!promoteImageUrl.trim().isEmpty()) {

            new Help().showImage(promoteImageUrl, promoteImage, progressBarPromoteImage, this);

        } else {

            progressBarPromoteImage.setVisibility(GONE);

            promoteImageLayout.setVisibility(GONE);

        }

        if (!promoteButtonTitle.trim().isEmpty()) {

            promoteButtonText.setText(promoteButtonTitle);

        }

        if (!promoteNotes.trim().isEmpty()) {

            promoteNotesTxt.setText(promoteNotes);

        }

        new Help().setupAnimationOnClick(promoteButton, 0.85f, new OnViewClickListener() {
            @Override
            public void onClick() {

                Uri uri = Uri.parse(promoteUrl);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(intent);

            }

        });

        progressLayout.setVisibility(GONE);

    }

}