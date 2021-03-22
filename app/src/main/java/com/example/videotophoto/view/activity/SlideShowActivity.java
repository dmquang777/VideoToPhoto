package com.example.videotophoto.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videotophoto.R;
import com.example.videotophoto.controller.ImageAdapter;
import com.example.videotophoto.controller.ImageFolderAdapter;
import com.example.videotophoto.controller.ImagePickerAdapter;
import com.example.videotophoto.model.Image;
import com.example.videotophoto.controller.MyAsyncLoader;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class SlideShowActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private ImageFolderAdapter folderAdapter;
    private ImageAdapter imageAdapter;
    private ImagePickerAdapter imagePickerAdapter;
    private ImageView imgBack;
    private ImageView imgCancel;
    private TextView txtFolderName;
    private TextView txtCapacity;
    private ImageView imgSpeed;
    private ImageView imgMusic;
    private CardView cvCreate;
    private final int REQUEST_PERMISSION = 512;
    static ArrayList<String> folderList = new ArrayList<>();
    private final ArrayList<Image> images = new ArrayList<>();
    private final ArrayList<Image> pickerImages = new ArrayList<>();
    private RecyclerView recyclerViewFolder;
    private RecyclerView recyclerViewImage;
    private RecyclerView recyclerViewImagePicker;
    private String strCapacity = "";

    private ProgressDialog progressDialog;
    private String path = Environment.getExternalStorageDirectory() + "/FFmpeg";
    private String videoName = "vid_" + System.currentTimeMillis() + ".mp4";
    private LoaderManager manager;
    private String selectedMusicPath = "";
    private String duration = "2";
    private boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        checkPermission();
        initView();
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);
        manager = LoaderManager.getInstance(this);
        progressDialog = new ProgressDialog(SlideShowActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        imgBack.setOnClickListener(v -> {
            finish();
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        });

        imgCancel.setOnClickListener(v -> {
            pickerImages.clear();
            imagePickerAdapter.notifyDataSetChanged();
            if (pickerImages.size() == 0) {
                strCapacity = getResources().getString(R.string.selected) + " " + pickerImages.size();
                txtCapacity.setText(strCapacity);
            }
        });

        folderAdapter.setCallBack(position -> {
            txtFolderName.setText(folderList.get(position));
            images.clear();
            loadImages(folderList.get(position));
        });

        imageAdapter.setCallBack(position -> {
            strCapacity = getResources().getString(R.string.selected) + " " + (pickerImages.size() + 1);
            txtCapacity.setText(strCapacity);
            pickerImages.add(images.get(position));
            imagePickerAdapter.notifyItemChanged(position);
            recyclerViewImagePicker.scrollToPosition(pickerImages.size() - 1);
        });

        imagePickerAdapter.setCallBack((image, position) -> {
            pickerImages.remove(image);
            imagePickerAdapter.notifyItemRemoved(position);
            imagePickerAdapter.notifyItemRangeChanged(position, images.size());
            strCapacity = getResources().getString(R.string.selected) + " " + pickerImages.size();
            txtCapacity.setText(strCapacity);
        });

        imgMusic.setOnClickListener(v -> {
            startActivityForResult(new Intent(SlideShowActivity.this, MusicActivity.class), 26);
        });

        imgSpeed.setOnClickListener(v -> {
            enterDuration(v);
        });

        cvCreate.setOnClickListener(v -> {
            if (!pickerImages.isEmpty()) {
                if (isSelected) {
                    progressDialog.show();
                    List<String> listAllPath = new ArrayList<>();
                    getPath(listAllPath);
                    startMyAsyncTask(v);
                } else {
                    Snackbar.make(v, "Please choose music", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(v, "Please choose image", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void enterDuration(View view) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_speed);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.InfoImageDialogAnimation;

        EditText edtDuration = dialog.findViewById(R.id.edt_duration);
        TextView txtCancel = dialog.findViewById(R.id.txt_cancel);
        TextView txtOk = dialog.findViewById(R.id.txt_ok);

        edtDuration.setText("2");
        txtOk.setOnClickListener(v -> {
            int n = Integer.parseInt(edtDuration.getText().toString().trim());
            if (n < 1 || n > 100) {
                Snackbar.make(view, "Unacceptable value ! Please re-enter", Snackbar.LENGTH_LONG).show();
            } else {
                duration = edtDuration.getText().toString();
            }
            dialog.dismiss();
        });
        txtCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void getPath(List<String> listAllPath) {
        for (int i = 0; i < pickerImages.size(); i++) {
            Cursor returnCursor = getContentResolver().query(pickerImages.get(i).getUriData(), null, null,
                    null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();

            String filePath = returnCursor.getString(returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            listAllPath.add(filePath);
        }
        try {
            createImagesTextFile(listAllPath);
            createMusicTextFile(selectedMusicPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createImagesTextFile(List<String> list) throws IOException {
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == (list.size() - 1)) {
                data.append("file ").append(list.get(i)).append("\nduration " + duration + "\n")
                        .append("file ").append((list.get(i)));
            } else {
                data.append("file ").append((list.get(i))).append("\nduration " + duration + "\n");
            }
        }
        writeToImagesFile(data.toString());
    }

    private void writeToImagesFile(String data) throws IOException {
        Writer writer = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File dir = new File(root + File.separator + "FFmpeg");
            dir.mkdir();
            writer = new OutputStreamWriter(new FileOutputStream(new File(dir, "allPathImages.txt")));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
        }
    }

    private void createMusicTextFile(String path) throws IOException {
        StringBuilder data = new StringBuilder();
        data.append("file ").append((path));
//        String data = "file " + path;
        writeToMusicFile(data.toString());
    }

    private void writeToMusicFile(String data) throws IOException {
        Writer writer = null;
        try {
            final File configDir = new File(Environment.getExternalStorageDirectory(), "FFmpeg");
            configDir.mkdir();
            writer = new OutputStreamWriter(new FileOutputStream(new File(configDir, "selectedMusicPath.txt")));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) writer.close();
        }
    }

//    private void writeToMusicFile(String data) throws IOException {
//        FileWriter writer = null;
//        try {
//            File configDir = new File(Environment.getExternalStorageDirectory(), "FFmpeg");
////            File test = new File(configDir, "selectedMusicPath.txt");
////            if (test.exists()) {
////                boolean b = test.delete();
////                Log.d("ngoai", "writeToMusicFile: " + b);
////            }
//            configDir.mkdir();
//            File child = new File(configDir, "selectedMusicPath.txt");
//            writer = new FileWriter(child);
//            writer.write(data);
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (writer != null) writer.close();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 26) {
            if (resultCode == RESULT_OK && data != null) {
                selectedMusicPath = data.getStringExtra("music_path");
//                selectedMusicPath = selectedMusicPath.trim();
//                selectedMusicPath = selectedMusicPath.replace(" ", "");
                isSelected = true;
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            } else {
                loadFolders();
            }
        }
    }

    private void loadImages(String folderName) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String[] projection = {MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_TAKEN};

                String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";
                String selection = MediaStore.Images.Media.DATA + " like?";
                String[] selectionArgs = new String[]{"%" + folderName + "%"};
                @SuppressLint("Recycle") Cursor cursor = getApplication().getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                projection, selection, selectionArgs, sortOrder);

                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        Uri data = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        images.add(new Image(id, data));
                    }
                    runOnUiThread(() -> {
                        imageAdapter.setData(images);
                        recyclerViewImage.setAdapter(imageAdapter);
                        imageAdapter.notifyItemInserted(images.size() - 1);
                    });
                }
            }
        }.start();
    }

    private void loadFolders() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String[] projection = {MediaStore.Images.Media.DATA};
                String sortOrder = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC";

                @SuppressLint("Recycle") Cursor cursor = getApplication().getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                null, sortOrder);
                if (cursor != null) {
                    int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    while (cursor.moveToNext()) {
                        String path = cursor.getString(pathColumn);

                        int slashFirstIndex = path.lastIndexOf("/");
                        String subString = path.substring(0, slashFirstIndex);
                        int index = subString.lastIndexOf("/");
                        String folderName = subString.substring(index + 1, slashFirstIndex);

                        if (!folderList.contains(folderName)) {
                            folderList.add(folderName);
                        }

                        runOnUiThread(() -> {
                            folderAdapter.setData(folderList);
                            recyclerViewFolder.setAdapter(folderAdapter);
                            folderAdapter.notifyItemInserted(folderList.size() - 1);
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
        imgBack = findViewById(R.id.img_back);
        txtFolderName = findViewById(R.id.folder_name);
        imgCancel = findViewById(R.id.img_cancel);
        txtCapacity = findViewById(R.id.capacity);
        recyclerViewFolder = findViewById(R.id.recycler_view_folder);
        folderAdapter = new ImageFolderAdapter();
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(this);
        recyclerViewImage = findViewById(R.id.recycler_view_image);
        recyclerViewImage.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewImagePicker = findViewById(R.id.recycler_view_image_picker);
        recyclerViewImagePicker.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagePickerAdapter = new ImagePickerAdapter(this, pickerImages);
        recyclerViewImagePicker.setAdapter(imagePickerAdapter);
        cvCreate = findViewById(R.id.cv_create);
        imgSpeed = findViewById(R.id.img_speed);
        imgMusic = findViewById(R.id.img_music);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new MyAsyncLoader(this, path, videoName);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        progressDialog.dismiss();
        View view = findViewById(android.R.id.content);
        if (data.equals("Create successfully")) {
            if (view != null) {
                Snackbar.make(view, data, Snackbar.LENGTH_LONG)
                        .setAction("Open", v -> startActivity(new Intent(SlideShowActivity.this, CustomVideoActivity.class))
                        )
                        .setActionTextColor(getColor(R.color.teal_200))
                        .show();
            } else {
                Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (view != null) {
                Snackbar.make(view, data, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        progressDialog.dismiss();
    }

    public void startMyAsyncTask(View view) {
        manager.initLoader(1, null, this);
    }
}