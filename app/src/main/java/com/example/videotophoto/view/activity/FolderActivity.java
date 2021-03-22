package com.example.videotophoto.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.videotophoto.R;
import com.example.videotophoto.controller.VideoFolderAdapter;

import java.util.ArrayList;

public class FolderActivity extends AppCompatActivity {
    private VideoFolderAdapter videoFolderAdapter;
    private ImageView imgBack;
    private final int REQUEST_PERMISSION = 96;
    static ArrayList<String> folderList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        initView();
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);
        checkPermission();

        imgBack.setOnClickListener(v -> {
            finish();
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        });

        videoFolderAdapter.setCallBack(position -> {
            Intent intent = new Intent(FolderActivity.this, VideoActivity.class);
            intent.putExtra("folder_name", folderList.get(position));
            startActivity(intent);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            loadFolders();
        }
    }

    private void loadFolders() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String[] projection = {MediaStore.Video.Media.DATA};
                String sortOrder = MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " ASC";

                @SuppressLint("Recycle") Cursor cursor = getApplication().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);
                if (cursor != null) {
                    int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

                    while (cursor.moveToNext()) {
                        String path = cursor.getString(pathColumn);

                        int slashFirstIndex = path.lastIndexOf("/");
                        String subString = path.substring(0, slashFirstIndex);
                        int index = subString.lastIndexOf("/");
                        String folderName = subString.substring(index + 1, slashFirstIndex);

                        Log.d("path", "run: " + path);
                        Log.d("folder_name", "run: " + folderName);

                        if (!folderList.contains(folderName)) {
                            folderList.add(folderName);
                        }

                        runOnUiThread(() -> {
                            videoFolderAdapter.setData(folderList);
                            recyclerView.setAdapter(videoFolderAdapter);
                            videoFolderAdapter.notifyItemInserted(folderList.size() - 1);
                        });
                    }
                }
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                loadFolders();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.folder_recyclerview);
        videoFolderAdapter = new VideoFolderAdapter();
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