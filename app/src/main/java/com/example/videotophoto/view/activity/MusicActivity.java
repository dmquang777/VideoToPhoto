package com.example.videotophoto.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.videotophoto.R;
import com.example.videotophoto.controller.MusicPickerAdapter;
import com.example.videotophoto.model.Music;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MusicActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Music> musicList;
    private ImageView imgBack;
    private MusicPickerAdapter musicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initView();
        loadMusic();
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);

        imgBack.setOnClickListener(v -> {
            finish();
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        });

        musicAdapter.setCallBack(position -> {
            @SuppressLint("Recycle") Cursor returnCursor = getContentResolver().query(musicList.get(position).getUriData(),
                    null, null, null, null);
            returnCursor.moveToFirst();
            String filePath = returnCursor.getString(returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            Intent returnIntent = new Intent();
            returnIntent.putExtra("music_path", filePath);
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }

    private void loadMusic() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String[] projection = {MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATE_ADDED};

                String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

                @SuppressLint("Recycle") Cursor cursor = getApplication().getContentResolver()
                        .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
                                null, null, sortOrder);
                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                    int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                    int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        String title = cursor.getString(titleColumn);
                        int duration = cursor.getInt(durationColumn);
                        long size = cursor.getLong(sizeColumn) / 1000;
                        long dateAdded = cursor.getLong(dateAddedColumn);

                        Uri data = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                        String durationFormatted;
                        int sec = (duration / 1000) % 60;
                        int min = (duration / (1000 * 60)) % 60;
                        int hour = duration / (1000 * 60 * 60);

                        if (hour == 0) {
                            durationFormatted = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                        } else {
                            durationFormatted = String.valueOf(hour).concat(":".concat(String.format(Locale.UK, "%02d", min).concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                        }

                        String normalDate = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA).format(new Date(dateAdded));

                        String strSize;
                        if (size >= 1000) {
                            strSize = new DecimalFormat("#,#").format(size / 100) + "mb";
                        } else {
                            strSize = size + "kb";
                        }

                        musicList.add(new Music(id, data, title, durationFormatted, strSize, normalDate));
                        runOnUiThread(() -> {
                            musicAdapter.setData(musicList);
                            recyclerView.setAdapter(musicAdapter);
                            musicAdapter.notifyItemInserted(musicList.size() - 1);
                        });
                    }
                }
            }
        }.start();
    }

    private void initView() {
        imgBack = findViewById(R.id.img_back);
        recyclerView = findViewById(R.id.recyclerview_music);
        musicList = new ArrayList<>();
        musicAdapter = new MusicPickerAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
    }
}