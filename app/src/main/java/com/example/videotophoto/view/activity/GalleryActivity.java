package com.example.videotophoto.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.videotophoto.R;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ImageView imgBack = findViewById(R.id.img_back);
        CardView cvVideo = findViewById(R.id.cv_video_folder);
        CardView cvImage = findViewById(R.id.cv_image_folder);
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);

        imgBack.setOnClickListener(v -> {
            finish();
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        });

        cvVideo.setOnClickListener(v -> {
            Intent intent = new Intent(GalleryActivity.this, CustomVideoActivity.class);
            startActivity(intent);
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });

        cvImage.setOnClickListener(v -> {
            Intent intent = new Intent(GalleryActivity.this, ImageActivity.class);
            startActivity(intent);
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
    }
}