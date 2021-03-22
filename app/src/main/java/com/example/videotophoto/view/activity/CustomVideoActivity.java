package com.example.videotophoto.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videotophoto.R;
import com.example.videotophoto.controller.CustomVideoAdapter;
import com.example.videotophoto.model.CustomVideo;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CustomVideoActivity extends AppCompatActivity {
    private ArrayList<CustomVideo> videos = new ArrayList<>();
    private CustomVideoAdapter videoAdapter;
    private ImageView imgBack;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        checkPermission();
        initView();
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);

        imgBack.setOnClickListener(v -> {
            onBackPressed();
            v.startAnimation(animPress);
        });

        videoAdapter.setCallBack(new CustomVideoAdapter.OnVideoClick() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(CustomVideoActivity.this, PlayerCustomVideoActivity.class);
                intent.putExtra("videoData", videos.get(position).getUriData());
                intent.putExtra("videoName", videos.get(position).getStrTitle());
                startActivity(intent);
                overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
            }

            @Override
            public void onItemLongClick(int position) {
                onLongClick(position);
            }
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(CustomVideoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(CustomVideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomVideoActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7);
        } else {
            loadVideos();
        }
    }

    private void loadVideos() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String[] projection = {MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.DATE_ADDED,
                        MediaStore.Video.Media.SIZE};

                String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";
                String selection = MediaStore.Video.Media.DATA + " like?";
                String[] selectionArgs = new String[]{"%/FFmpeg/%"};

                @SuppressLint("Recycle") Cursor cursor = getApplication().getContentResolver()
                        .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                                selection, selectionArgs, sortOrder);
                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                    int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        String title = cursor.getString(titleColumn);
                        int duration = cursor.getInt(durationColumn);
                        long dateAdded = cursor.getLong(dateAddedColumn);
                        long size = cursor.getLong(sizeColumn) / 1000;

                        Uri data = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                        String durationFormatted;
                        int sec = (duration / 1000) % 60;
                        int min = (duration / (1000 * 60)) % 60;
                        int hour = duration / (1000 * 60 * 60);

                        if (hour == 0) {
                            durationFormatted = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                        } else {
                            durationFormatted = String.valueOf(hour).concat(":".concat(String.format(Locale.UK, "%02d", min).concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                        }

                        @SuppressLint("SimpleDateFormat") String normalDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date(dateAdded));

                        String strSize;
                        if (size >= 10000) {
                            strSize = new DecimalFormat("#,##").format(size / 1000) + "mb";
                        } else if (size >= 1000) {
                            strSize = new DecimalFormat("#,#").format(size / 100) + "mb";
                        } else {
                            strSize = size + "kb";
                        }

                        videos.add(new CustomVideo(id, data, title, durationFormatted, normalDate, strSize));
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
        videoAdapter = new CustomVideoAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imgBack = findViewById(R.id.img_back);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
    }

    private void onLongClick(int position) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_video_options);
        dialog.getWindow().getAttributes().windowAnimations = R.style.InfoImageDialogAnimation;

        CardView cvPlay = dialog.findViewById(R.id.cv_play);
        CardView cvRename = dialog.findViewById(R.id.cv_rename);
        CardView cvDelete = dialog.findViewById(R.id.cv_delete);
        CardView cvShare = dialog.findViewById(R.id.cv_share);
        CardView cvDetail = dialog.findViewById(R.id.cv_detail);
        TextView txtVideoName = dialog.findViewById(R.id.custom_dialog_video_name);

        @SuppressLint("Recycle") Cursor returnCursor = getApplicationContext().getContentResolver().query(videos.get(position).getUriData(),
                null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        txtVideoName.setText(returnCursor.getString(nameIndex));

        cvPlay.setOnClickListener(v1 -> {
            Intent intent = new Intent(CustomVideoActivity.this, PlayerCustomVideoActivity.class);
            intent.putExtra("videoData", videos.get(position).getUriData());
            startActivity(intent);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });

        cvRename.setOnClickListener(v -> {
            Log.d("jhuanq", "last path: " + videos.get(position).getUriData().getLastPathSegment());
            dialog.dismiss();
            Dialog dialog1 = new Dialog(this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.dialog_rename);
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.getWindow().getAttributes().windowAnimations = R.style.InfoImageDialogAnimation;

            EditText edtRename = dialog1.findViewById(R.id.edt_rename);
            TextView txtCancel = dialog1.findViewById(R.id.txt_cancel);
            TextView txtOk = dialog1.findViewById(R.id.txt_ok);

            edtRename.setText(returnCursor.getString(nameIndex));

            txtOk.setOnClickListener(v2 -> {
                String strRename = edtRename.getText().toString();
                String path = Environment.getExternalStorageDirectory() + "/FFmpeg";
//                Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                        videos.get(position).getId());
                File dir = new File(path);
                if (dir.exists()) {
                    File from = new File(dir, returnCursor.getString(nameIndex).trim());
                    File to = new File(dir, strRename.trim() + ".mp4");
                    if (from.exists()) {
                        if (from.renameTo(to)) {
//                            getApplicationContext().getContentResolver().delete(contentUri, null, null);
                            videos.get(position).setStrTitle(to.getName());
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(Uri.fromFile(dir));
                            getApplicationContext().sendBroadcast(intent);
                            videoAdapter.notifyDataSetChanged();

                            int max = 1;
                            int lastPathNumber;
                            for (int i = 0; i < videos.size(); i++) {
                                Log.d("allpath", "last path " + i + ": " + videos.get(i).getUriData().getLastPathSegment());
                                lastPathNumber = Integer.parseInt(videos.get(i).getUriData().getLastPathSegment());
                                if (lastPathNumber > max) {
                                    max = lastPathNumber;
                                }
                            }
                            Log.d("jhuanq", "max number last path: " + max);
                            max++;
                            String newFilePath = "content://media/external/video/media/" + max;
                            videos.get(position).setUriData(Uri.parse(newFilePath));
                        } else {
                            Toast.makeText(this, "Failed !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                dialog1.dismiss();
            });
            txtCancel.setOnClickListener(v2 -> dialog1.dismiss());
            dialog1.show();
        });

        cvDelete.setOnClickListener(v -> {
            dialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.delete_image))
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog2, which) -> {
                        String filePath = returnCursor.getString(returnCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                videos.get(position).getId());
                        File file = new File(filePath);
                        Log.d("checkFile", "check " + file.exists());
                        if (file.exists()) {
                            if (file.delete()) {
                                getApplicationContext().getContentResolver().delete(contentUri, null, null);
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                intent.setData(Uri.fromFile(file));
                                getApplicationContext().sendBroadcast(intent);
                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                                videos.remove(position);
                                videoAdapter.setData(videos);
                            } else {
                                Toast.makeText(this, "Failed !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog2, which) -> dialog2.cancel());
            AlertDialog dialog3 = builder.create();
            dialog3.getWindow().getAttributes().windowAnimations = R.style.ExitDialogAnimation;
            dialog3.show();
        });

        cvShare.setOnClickListener(v -> {
            dialog.dismiss();
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            String path = returnCursor.getString(returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            Log.d("path", "onLongClick: " + path);

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("video/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
            startActivity(Intent.createChooser(sharingIntent, "Share Video"));
        });

        cvDetail.setOnClickListener(v -> {
            dialog.dismiss();
            Dialog dialog1 = new Dialog(this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.dialog_detail_custom_video);
            dialog1.getWindow().getAttributes().windowAnimations = R.style.ExitDialogAnimation;

            String[] projection = {MediaStore.Video.VideoColumns.DATE_TAKEN};
            @SuppressLint("Recycle") Cursor cursor = getApplicationContext().getContentResolver()
                    .query(videos.get(position).getUriData(), projection, null, null, null);
            int dateTakenIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_TAKEN);
            cursor.moveToFirst();

            long dateTaken = cursor.getLong(dateTakenIndex);
            long size = returnCursor.getLong(sizeIndex) / 1000;

            TextView txtName = dialog1.findViewById(R.id.txt_video_name);
            TextView txtDate = dialog1.findViewById(R.id.txt_date);
            TextView txtPath = dialog1.findViewById(R.id.txt_path);
            TextView txtSize = dialog1.findViewById(R.id.txt_size_video);

            txtName.setText(returnCursor.getString(nameIndex));

            @SuppressLint("SimpleDateFormat") String normalDateTaken = new java.text.SimpleDateFormat("dd/MM/yyyy")
                    .format(new java.util.Date(dateTaken));
            if (dateTaken == 0) {
                txtDate.setText("");
            } else {
                txtDate.setText(normalDateTaken);
            }

            txtPath.setText(returnCursor.getString(returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

            if (size >= 10000) {
                txtSize.setText(String.format("%smb", new DecimalFormat("#,##").format(size / 1000)));
            } else if (size >= 1000) {
                txtSize.setText(String.format("%smb", new DecimalFormat("#,#").format(size / 100)));
            } else {
                txtSize.setText(String.format("%dkb", size));
            }

            dialog1.show();
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                loadVideos();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}