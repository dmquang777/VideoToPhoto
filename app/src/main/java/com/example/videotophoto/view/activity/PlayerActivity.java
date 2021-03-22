package com.example.videotophoto.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videotophoto.R;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import wseemann.media.FFmpegMediaMetadataRetriever;

import static com.example.videotophoto.view.activity.SettingActivity.MY_PREFS_NAME;
import static com.example.videotophoto.view.activity.SettingActivity.MY_QUALITY_LEVEL;
import static com.example.videotophoto.view.activity.SettingActivity.MY_FORMAT_TYPE;

public class PlayerActivity extends AppCompatActivity implements Player.EventListener {
    private ImageView imgBack;
    private CardView cvCapture;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Uri uri;
    private ImageView imgForward;
    private ImageView imgRewind;

    private ProgressBar progressBar;
    private ImageView imgFullscreen;
    private Boolean isFullScreen = false;

    private ImageView imgCapture;
    private TextView txtName;
    private String format;
    private int quality;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initView();
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);

        uri = (Uri) getIntent().getExtras().get("videoData");

        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        format = sharedPreferences.getString(MY_FORMAT_TYPE, "JPEG");
        int level = sharedPreferences.getInt(MY_QUALITY_LEVEL, 80);
        switch (level) {
            case 0:
                quality = 100;
                break;
            case 1:
                quality = 90;
                break;
            case 2:
                quality = 80;
                break;
            case 3:
                quality = 50;
                break;
            case 4:
                quality = 30;
                break;
            case 5:
                quality = 0;
                break;
        }

        txtName.setText(getIntent().getStringExtra("videoName"));

        imgFullscreen.setOnClickListener(v -> {
            if (isFullScreen) {
                imgFullscreen.setImageResource(R.drawable.ic_full_screen);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                isFullScreen = false;
            } else {
                imgFullscreen.setImageResource(R.drawable.ic_full_screen_exit);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                isFullScreen = true;
            }
            v.startAnimation(animPress);
        });

        imgForward.setOnClickListener(v -> player.seekTo(player.getCurrentPosition() + 10000));

        imgRewind.setOnClickListener(v -> player.seekTo(player.getCurrentPosition() - 10000));

        imgBack.setOnClickListener(v -> {
            finish();
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        });

        cvCapture.setOnClickListener(v -> {
            try {
                getFrame(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            v.startAnimation(animPress);
        });
    }

    private void getFrame(Uri uri) throws IOException {
        player.pause();
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        retriever.setDataSource(getApplicationContext(), uri);
        Bitmap captureBitmap = retriever.getFrameAtTime(player.getCurrentPosition() * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);

        Animation animImgCapture = AnimationUtils.loadAnimation(this, R.anim.anim_capture);
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);
        imgCapture.startAnimation(animImgCapture);

        if (captureBitmap != null) {
            imgCapture.setImageBitmap(captureBitmap);
            imgCapture.setOnClickListener(v -> {
                startActivity(new Intent(PlayerActivity.this, ImageActivity.class));
                v.setAnimation(animPress);
                overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
            });
//            saveImage(captureBitmap, "img" + System.currentTimeMillis(),
//                    format, quality);
        }
    }

//    public Bitmap getVideoThumbnail(Uri path) {
//        Bitmap bitmap = null;
//        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
//        try {
//            retriever.setDataSource(PlayerActivity.this, path);
//            final byte[] data = retriever.getEmbeddedPicture();
//            if (data != null) {
//                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            }
//            if (bitmap == null) {
//                bitmap = retriever.getFrameAtTime(player.getCurrentPosition() * 1000000);
//            }
//        } catch (Exception e) {
//            bitmap = null;
//        } finally {
//            retriever.release();
//        }
//        return bitmap;
//    }

    private void saveImage(Bitmap bitmap, @NonNull String name, String format, int quality) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getApplication().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image" + File.separator + format);
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + "VideoToPhoto");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
                    + File.separator + "VideoToPhoto";
            File file = new File(imagesDir);
            if (!file.exists()) {
                file.mkdir();
            }
            File image = new File(imagesDir, name + "." + format.toLowerCase());
            fos = new FileOutputStream(image);
        }
        try {
            saved = bitmap.compress(Bitmap.CompressFormat.valueOf(format), quality, fos);
            if (saved) {
                Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed, something wrong", Toast.LENGTH_SHORT).show();
            }
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        imgBack = findViewById(R.id.img_back);
        cvCapture = findViewById(R.id.cv_capture);
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progress_bar);
        imgFullscreen = playerView.findViewById(R.id.img_fullscreen);
        imgRewind = playerView.findViewById(R.id.exo_replay);
        imgForward = playerView.findViewById(R.id.exo_forward);
        imgCapture = findViewById(R.id.img_capture);
        txtName = findViewById(R.id.txt_video_play_name);
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
}