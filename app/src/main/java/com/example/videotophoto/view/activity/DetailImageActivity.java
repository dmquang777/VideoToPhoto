package com.example.videotophoto.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videotophoto.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

public class DetailImageActivity extends AppCompatActivity {
    private ImageView imgBack;
    private ImageView imgEdit;
    private ImageView imgDetailImage;
    private ImageView imgShare;
    private ImageView imgInfo;
    private ImageView imgDelete;
    private TextView txtName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);

        initView();
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);
        Uri uri = (Uri) getIntent().getExtras().get("imageData");
        long id = getIntent().getLongExtra("imageId", -1);

        imgDetailImage.setImageURI(uri);

        @SuppressLint("Recycle") Cursor returnCursor = getContentResolver().query(uri, null, null,
                null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        txtName.setText(returnCursor.getString(nameIndex));

        imgBack.setOnClickListener(v -> {
            v.startAnimation(animPress);
            finish();
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        });

        imgEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailImageActivity.this, EditImageActivity.class);
            intent.putExtra("imageData", uri);
            startActivity(intent);
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });

        imgShare.setOnClickListener(v -> {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Drawable drawable = imgDetailImage.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            File file = new File(getExternalCacheDir() + "/" + returnCursor.getString(nameIndex));
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpg");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(Intent.createChooser(intent, "Share image via"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            v.startAnimation(animPress);
        });

        imgInfo.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_info_img);
            dialog.getWindow().getAttributes().windowAnimations = R.style.InfoImageDialogAnimation;

            TextView txtDate = dialog.findViewById(R.id.txt_date);
            TextView txtTime = dialog.findViewById(R.id.txt_time);
            TextView txtName = dialog.findViewById(R.id.txt_name);
            TextView txtSize = dialog.findViewById(R.id.txt_size_image);
            TextView txtWAH = dialog.findViewById(R.id.txt_w_n_h_image);

            String[] projection = {MediaStore.Images.ImageColumns.DATE_TAKEN};
            Cursor cursor = getApplication().getContentResolver().query(uri, projection, null,
                    null, null);
            int dateTakenIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);

            cursor.moveToFirst();
            long dateTaken = cursor.getLong(dateTakenIndex);
            long size = returnCursor.getLong(sizeIndex) / 1000;

            @SuppressLint("SimpleDateFormat") String normalDateTaken = new java.text.SimpleDateFormat("dd/MM/yyyy")
                    .format(new java.util.Date(dateTaken));
            if (dateTaken == 0) {
                txtDate.setText("");
            } else {
                txtDate.setText(normalDateTaken);
            }
            @SuppressLint("SimpleDateFormat") String normalDateTime = new java.text.SimpleDateFormat("E HH:mm:ss")
                    .format(new java.util.Date(dateTaken));
            if (dateTaken == 0) {
                txtTime.setText("");
            } else {
                txtTime.setText(normalDateTime);
            }

            txtName.setText(returnCursor.getString(returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

            if (size >= 1000) {
                txtSize.setText(new DecimalFormat("#,#").format(size / 100) + "mb");
            } else {
                txtSize.setText(size + "kb");
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeStream(getApplication().getContentResolver()
                        .openInputStream(uri), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            txtWAH.setText(imageWidth + "x" + imageHeight);

            dialog.show();
            v.startAnimation(animPress);
        });

        imgDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.delete_image))
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                        String filePath = returnCursor.getString(returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        File file = new File(filePath);
                        if (file.exists()) {
                            if (file.delete()) {
                                getApplicationContext().getContentResolver().delete(contentUri, null, null);
                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();

                                int pos = getIntent().getIntExtra("imagePos", -1);
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("delete", pos);
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            } else {
                                Toast.makeText(this, "Failed !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.ExitDialogAnimation;
            dialog.show();
            v.startAnimation(animPress);
        });
    }

    private void initView() {
        imgBack = findViewById(R.id.img_back);
        imgEdit = findViewById(R.id.img_edit);
        imgDetailImage = findViewById(R.id.img_detail_image);
        imgShare = findViewById(R.id.img_share);
        imgInfo = findViewById(R.id.img_info);
        imgDelete = findViewById(R.id.img_delete);
        txtName = findViewById(R.id.txt_image_name);
    }
}