package com.example.videotophoto.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;

import com.example.videotophoto.R;

public class TermActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        WebView webView = findViewById(R.id.web_view);
        ImageView imgBack = findViewById(R.id.img_back);
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);

        imgBack.setOnClickListener(v -> {
            finish();
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        });

        webView.loadUrl("file:///android_asset/term.html");
        webView.getSettings().setJavaScriptEnabled(true);
    }
}