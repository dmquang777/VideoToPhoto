package com.example.videotophoto.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videotophoto.controller.FilterListener;
import com.example.videotophoto.R;
import com.example.videotophoto.controller.FilterAdapter;
import com.example.videotophoto.view.fragment.EmojiFragment;
import com.example.videotophoto.view.fragment.PropertiesBrushFragment;
import com.example.videotophoto.view.fragment.PropertiesEraserFragment;
import com.example.videotophoto.view.fragment.StickerFragment;
import com.example.videotophoto.view.fragment.TextEditorFragment;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.ViewType;

public class EditImageActivity extends AppCompatActivity implements PropertiesBrushFragment.Properties,
        PropertiesEraserFragment.Properties,
        FilterListener,
        StickerFragment.StickerListener,
        EmojiFragment.EmojiListener,
        OnPhotoEditorListener {

    private PhotoEditorView editorView;
    private PhotoEditor editor;
    private TextView txtCurrentTool;
    private TextView txtCurrentToolLoading;
    private ImageView imgUndo;
    private ImageView imgRedo;
    private LinearLayout llCrop;
    private LinearLayout llBrush;
    private LinearLayout llText;
    private LinearLayout llEraser;
    private LinearLayout llFilter;
    private LinearLayout llSticker;
    private LinearLayout llStickerLoading;
    private LinearLayout llEmoji;
    private LinearLayout llEmojiLoading;

    private PropertiesBrushFragment propertiesBrush;
    private PropertiesEraserFragment propertiesEraser;

    private final FilterAdapter filterAdapter = new FilterAdapter(this);
    private RecyclerView recyclerViewFilter;
    private boolean isFilterVisible;
    private final ConstraintSet constraintSet = new ConstraintSet();
    private ConstraintLayout parentEdit;

    private StickerFragment stickerFragment;
    private EmojiFragment emojiFragment;

    private ProgressDialog progressDialog;
    private CardView cvSave;
    private CardView cvCancel;
    private boolean isEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        initView();
        onClickListener();

        propertiesBrush = new PropertiesBrushFragment();
        propertiesBrush.setPropertiesChangeListener(this);

        propertiesEraser = new PropertiesEraserFragment();
        propertiesEraser.setPropertiesChangeListener(this);

        recyclerViewFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFilter.setAdapter(filterAdapter);

        stickerFragment = new StickerFragment();
        stickerFragment.setStickerListener(this);

        emojiFragment = new EmojiFragment();
        emojiFragment.setEmojiListener(this);

        Uri uri = (Uri) getIntent().getExtras().get("imageData");

        editorView.getSource().setImageURI(uri);
        editor = new PhotoEditor.Builder(this, editorView)
                .setPinchTextScalable(true)
                .build();

        llCrop.setOnClickListener(v -> CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowCounterRotation(true)
                .setMultiTouchEnabled(true)
                .start(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                editorView.getSource().setImageURI(result.getUri());
                isEdited = true;
            }
        }
    }

    private void onClickListener() {

        llBrush.setOnClickListener(v -> {
            isEdited = true;
            txtCurrentTool.setText(getResources().getString(R.string.brush));
            editor.setBrushDrawingMode(true);
            propertiesBrush.show(getSupportFragmentManager(), propertiesBrush.getTag());
        });

        llText.setOnClickListener(v -> {
            isEdited = true;
            txtCurrentTool.setText(getResources().getString(R.string.text));
            TextEditorFragment textEditorDialogFragment = TextEditorFragment.show(EditImageActivity.this);
            textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> editor.addText(inputText, colorCode));
        });

        llEraser.setOnClickListener(v -> {
            txtCurrentTool.setText(getResources().getString(R.string.eraser));
            editor.brushEraser();
            propertiesEraser.show(getSupportFragmentManager(), propertiesEraser.getTag());
        });

        llFilter.setOnClickListener(v -> {
            isEdited = true;
            txtCurrentTool.setText(getResources().getString(R.string.filter));
            showFilter(true);
        });

        llSticker.setOnClickListener(v -> {
            isEdited = true;
            stickerFragment.show(getSupportFragmentManager(), stickerFragment.getTag());
            txtCurrentTool.setVisibility(View.GONE);
            txtCurrentToolLoading.setVisibility(View.VISIBLE);
            llSticker.setVisibility(View.GONE);
            llStickerLoading.setVisibility(View.VISIBLE);

            new CountDownTimer(5 * 1000, 1000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    txtCurrentTool.setVisibility(View.VISIBLE);
                    txtCurrentToolLoading.setVisibility(View.GONE);
                    llSticker.setVisibility(View.VISIBLE);
                    llStickerLoading.setVisibility(View.GONE);
                }
            }.start();
        });

        llEmoji.setOnClickListener(v -> {
            isEdited = true;
            emojiFragment.show(getSupportFragmentManager(), emojiFragment.getTag());
            txtCurrentTool.setVisibility(View.GONE);
            txtCurrentToolLoading.setVisibility(View.VISIBLE);
            llEmoji.setVisibility(View.GONE);
            llEmojiLoading.setVisibility(View.VISIBLE);

            new CountDownTimer(5 * 1000, 1000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    txtCurrentTool.setVisibility(View.VISIBLE);
                    txtCurrentToolLoading.setVisibility(View.GONE);
                    llEmoji.setVisibility(View.VISIBLE);
                    llEmojiLoading.setVisibility(View.GONE);
                }
            }.start();
        });

        imgUndo.setOnClickListener(v -> editor.undo());
        imgRedo.setOnClickListener(v -> editor.redo());

        cvSave.setOnClickListener(v -> saveImage());
        cvCancel.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        editorView = findViewById(R.id.photo_editor_view);
        txtCurrentTool = findViewById(R.id.txt_current_tool);
        txtCurrentToolLoading = findViewById(R.id.txt_current_tool_loading);
        llCrop = findViewById(R.id.ll_crop);
        llBrush = findViewById(R.id.ll_brush);
        llText = findViewById(R.id.ll_text);
        llEraser = findViewById(R.id.ll_eraser);
        llFilter = findViewById(R.id.ll_filters);
        llSticker = findViewById(R.id.ll_stickers);
        llStickerLoading = findViewById(R.id.ll_stickers_loading);
        llEmoji = findViewById(R.id.ll_emojis);
        llEmojiLoading = findViewById(R.id.ll_emojis_loading);
        imgUndo = findViewById(R.id.img_undo);
        imgRedo = findViewById(R.id.img_redo);

        recyclerViewFilter = findViewById(R.id.recycler_view_filter);
        parentEdit = findViewById(R.id.parent_edit);

        cvSave = findViewById(R.id.cv_save);
        cvCancel = findViewById(R.id.cv_cancel);
    }

    @Override
    public void onColorChanged(int colorCode) {
        editor.setBrushColor(colorCode);
        txtCurrentTool.setText(getResources().getString(R.string.brush));
    }

    @Override
    public void onOpacityChanged(int opacity) {
        editor.setOpacity(opacity);
        txtCurrentTool.setText(getResources().getString(R.string.brush));
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        editor.setBrushSize(brushSize);
        txtCurrentTool.setText(getResources().getString(R.string.brush));
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorFragment textEditor = TextEditorFragment.show(this, text, colorCode);
        textEditor.setOnTextEditorListener((inputText, colorCode1) -> {
            editor.editText(rootView, inputText, colorCode1);
            txtCurrentTool.setText(getResources().getString(R.string.text));
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onEraserSizeChanged(float brushEraserSize) {
        editor.setBrushEraserSize(brushEraserSize);
        editor.brushEraser();
        txtCurrentTool.setText(getResources().getString(R.string.eraser));
    }

    void showFilter(boolean isVisible) {
        isFilterVisible = isVisible;
        constraintSet.clone(parentEdit);

        if (isVisible) {
            constraintSet.clear(recyclerViewFilter.getId(), ConstraintSet.START);
            constraintSet.connect(recyclerViewFilter.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(recyclerViewFilter.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            constraintSet.connect(recyclerViewFilter.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.clear(recyclerViewFilter.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(parentEdit, changeBounds);

        constraintSet.applyTo(parentEdit);
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        editor.setFilterEffect(photoFilter);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        editor.addImage(bitmap);
        txtCurrentTool.setText(getResources().getString(R.string.sticker));
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        editor.addEmoji(emojiUnicode);
        txtCurrentTool.setText(getResources().getString(R.string.emoji));
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        showLoading("Saving...");
        String path = Environment.getExternalStorageDirectory().toString();
        File dir = new File(path + File.separator + "Jideo");
        dir.mkdir();
        File file = new File(dir, "img_" + System.currentTimeMillis() + ".jpg");

        try {
            file.createNewFile();

            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .setCompressQuality(100)
                    .build();

            editor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    hideLoading();
                    showSnackBar("Image Saved Successfully");
                    editorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(dir));
                    getApplicationContext().sendBroadcast(intent);
                    new Handler().postDelayed(() -> {
                        finish();
                        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
                    }, 2000);
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("fail", "onFailure: " + exception);
                    hideLoading();
                    showSnackBar("Failed to save Image");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            hideLoading();
            showSnackBar(Objects.requireNonNull(e.getMessage()));
        }
    }

    public void showLoading(@NonNull String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void showSnackBar(@NonNull String message) {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("Open", v -> {
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.setType("image/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    })
                    .setActionTextColor(getColor(R.color.teal_200))
                    .show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.cancel_edit))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.cancel_edit_yes), (dialog, which) -> {
                    finish();
                    overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
                })
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExitDialogAnimation;
        if (isFilterVisible) {
            showFilter(false);
            txtCurrentTool.setText(R.string.app_name);
        } else if (isEdited) {
            dialog.show();
        } else {
            finish();
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        }
    }
}