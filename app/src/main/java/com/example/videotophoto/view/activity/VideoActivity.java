package com.example.videotophoto.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.videotophoto.R;
import com.example.videotophoto.controller.VideoAdapter;
import com.example.videotophoto.model.Video;

import java.util.ArrayList;
import java.util.Locale;

public class VideoActivity extends AppCompatActivity {
    private ArrayList<Video> videos = new ArrayList<>();
    private VideoAdapter videoAdapter;
    private ImageView imgBack;
    private RecyclerView recyclerView;
    private String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initView();
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);

        folderName = getIntent().getStringExtra("folder_name");
        if (folderName != null) {
            loadVideos();
        }

        imgBack.setOnClickListener(v -> {
            finish();
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        });

        videoAdapter.setCallBack(position -> {
            Intent intent = new Intent(VideoActivity.this, PlayerActivity.class);
//                intent.putExtra("videoId", videos.get(position).getId());
            intent.putExtra("videoData", videos.get(position).getUriData());
            intent.putExtra("videoName", videos.get(position).getStrTitle());
            startActivity(intent);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });
    }

    private void loadVideos() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String[] projection = {MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION};

                String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";
                String selection = MediaStore.Video.Media.DATA + " like?";
                String[] selectionArgs = new String[]{"%" + folderName + "%"};

                @SuppressLint("Recycle") Cursor cursor = getApplication().getContentResolver()
                        .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                                selection, selectionArgs, sortOrder);
                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        String title = cursor.getString(titleColumn);
                        int duration = cursor.getInt(durationColumn);

                        Uri data = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                        String duration_formatted;
                        int sec = (duration / 1000) % 60;
                        int min = (duration / (1000 * 60)) % 60;
                        int hour = duration / (1000 * 60 * 60);

                        if (hour == 0) {
                            duration_formatted = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                        } else {
                            duration_formatted = String.valueOf(hour).concat(":".concat(String.format(Locale.UK, "%02d", min).concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                        }

                        videos.add(new Video(id, data, title, duration_formatted));
                        runOnUiThread(() -> {
                            videoAdapter.setData(videos);
                            recyclerView.setAdapter(videoAdapter);
                            videoAdapter.notifyItemInserted(videos.size() - 1);
                        });
                    }
                }
            }
        }.start();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview_video);
        videos = new ArrayList<>();
        videoAdapter = new VideoAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        imgBack = findViewById(R.id.img_back);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
    }
}