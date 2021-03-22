package com.example.videotophoto.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.videotophoto.R;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class PlayerCustomVideoActivity extends AppCompatActivity implements Player.EventListener {
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Uri uri;
    private ImageView imgForward;
    private ImageView imgRewind;
    private ProgressBar progressBar;
    private ImageView imgFullscreen;
    private boolean isFullScreen = false;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_custom_video);

        initView();
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);
        uri = (Uri) getIntent().getExtras().get("videoData");

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            getSupportActionBar().setTitle(getIntent().getStringExtra("videoName"));
        }

        imgFullscreen.setOnClickListener(v -> {
            if (isFullScreen) {
                getSupportActionBar().show();
                imgFullscreen.setImageResource(R.drawable.ic_full_screen);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                isFullScreen = false;
            } else {
                getSupportActionBar().hide();
                imgFullscreen.setImageResource(R.drawable.ic_full_screen_exit);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                isFullScreen = true;
            }
            v.startAnimation(animPress);
        });

        imgForward.setOnClickListener(v -> player.seekTo(player.getCurrentPosition() + 10000));

        imgRewind.setOnClickListener(v -> player.seekTo(player.getCurrentPosition() - 10000));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        return true;
    }

    private void initPlayer() {
        player = new SimpleExoPlayer.Builder(this).build();

        DataSource.Factory factory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Jideo"));
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory)
                .createMediaSource(uri);
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player == null) {
            initPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
        super.onStop();
    }

    private void initView() {
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progress_bar);
        imgFullscreen = playerView.findViewById(R.id.img_fullscreen);
        imgRewind = playerView.findViewById(R.id.exo_replay);
        imgForward = playerView.findViewById(R.id.exo_forward);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            playerView.hideController();
            progressBar.setVisibility(View.VISIBLE);
        } else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED) {
            progressBar.setVisibility(View.INVISIBLE);
            playerView.showController();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
    }
}