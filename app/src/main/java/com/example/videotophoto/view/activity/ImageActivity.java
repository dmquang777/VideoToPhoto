package com.example.videotophoto.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.example.videotophoto.controller.ImageAdapter;
import com.example.videotophoto.model.Image;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 26;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private ImageView imgBack;
    private final ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        initView();
        checkPermission();

        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);
        imgBack.setOnClickListener(v -> {
            onBackPressed();
            v.startAnimation(animPress);
        });

        imageAdapter.setCallBack(position -> {
            Intent intent = new Intent(ImageActivity.this, DetailImageActivity.class);
            intent.putExtra("imageData", images.get(position).getUriData());
            intent.putExtra("imageId", images.get(position).getId());
            intent.putExtra("imagePos", position);
            startActivityForResult(intent, REQUEST_CODE);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                int pos = data.getIntExtra("delete", -1);
                images.remove(pos);
                imageAdapter.setData(images);
            }
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImageActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7);
        } else {
            loadImages();
        }
    }

    private void loadImages() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String[] projection = {MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_TAKEN};

                String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";
                String selection = MediaStore.Images.Media.DATA + " like?";
                String[] selectionArgs = new String[]{"%/DCIM/VideoToPhoto/%"};
                @SuppressLint("Recycle") Cursor cursor = getApplication().getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                projection, selection, selectionArgs, sortOrder);

                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        Uri data = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        images.add(new Image(id, data));
                        Log.d("uri_data", "data: " + data);
                    }
                    runOnUiThread(() -> {
                        imageAdapter.setData(images);
                        recyclerView.setAdapter(imageAdapter);
                        imageAdapter.notifyItemInserted(images.size() - 1);
                    });
                }
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                loadImages();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.image_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imageAdapter = new ImageAdapter(this);
        imgBack = findViewById(R.id.img_back);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
    }
}