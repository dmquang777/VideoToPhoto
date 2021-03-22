package com.example.videotophoto.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.videotophoto.R;

public class SettingActivity extends AppCompatActivity {
    private ImageView imgBack;
    private TextView txtFormat;
    private TextView txtQuality;
    private RadioGroup rgFormat;
    private RadioGroup rgQuality;
    private Dialog formatDialog;
    private Dialog qualityDialog;
    public static final String MY_PREFS_NAME = "Setting";
    public static final String MY_FORMAT_TYPE = "format_type";
    public static final String MY_QUALITY_LEVEL = "quality_level";
    public final String MY_KEY_SAVE_RADIO_BUTTON_PREFS = "MY_KEY_SAVE_RADIO_BUTTON_PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);
        loadFormatPrefs();
        loadQualityPrefs();

        imgBack.setOnClickListener(v -> {
            finish();
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
        });

        txtFormat.setOnClickListener(v -> {
            showOptionFormatDialog();
        });

        txtQuality.setOnClickListener(v -> {
            showOptionQualityDialog();
        });
    }

    private void showOptionQualityDialog() {
        rgQuality.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = rgQuality.findViewById(checkedId);
            int checkedIndex = rgQuality.indexOfChild(checkedRadioButton);
            if (checkedId == R.id.rb_ultra) {
                txtQuality.setText(getResources().getString(R.string.ultra));
            } else if (checkedId == R.id.rb_very_high) {
                txtQuality.setText(getResources().getString(R.string.very_high));
            } else if (checkedId == R.id.rb_high) {
                txtQuality.setText(getResources().getString(R.string.high));
            } else if (checkedId == R.id.rb_medium) {
                txtQuality.setText(getResources().getString(R.string.medium));
            } else if (checkedId == R.id.rb_low) {
                txtQuality.setText(getResources().getString(R.string.low));
            } else {
                txtQuality.setText(getResources().getString(R.string.very_low));
            }
            saveQualityPrefs(checkedIndex, checkedIndex);
            qualityDialog.dismiss();
        });
        qualityDialog.show();
    }

    private void showOptionFormatDialog() {
        rgFormat.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = rgFormat.findViewById(checkedId);
            int checkedIndex = rgFormat.indexOfChild(checkedRadioButton);
            if (checkedId == R.id.rb_jpeg) {
                txtFormat.setText(getResources().getString(R.string.jpeg));
            } else {
                txtFormat.setText(getResources().getString(R.string.png));
            }
            saveFormatTypePrefs(checkedIndex, txtFormat.getText().toString().trim());
            formatDialog.dismiss();
        });
        formatDialog.show();
    }

    private void saveFormatTypePrefs(int value, String type) {
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MY_KEY_SAVE_RADIO_BUTTON_PREFS, value);
        editor.putString(MY_FORMAT_TYPE, type);
        editor.apply();
    }

    private void saveQualityPrefs(int value, int level) {
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MY_KEY_SAVE_RADIO_BUTTON_PREFS, value);
        editor.putInt(MY_QUALITY_LEVEL, level);
        editor.apply();
    }

    private void loadFormatPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int saveRadioIndex = sharedPreferences.getInt(MY_KEY_SAVE_RADIO_BUTTON_PREFS, 0);
        RadioButton savedCheckedRadioButton = (RadioButton) rgFormat.getChildAt(saveRadioIndex);
        if (saveRadioIndex == 0) {
            txtFormat.setText(getResources().getString(R.string.jpeg));
        } else if (saveRadioIndex == 1) {
            txtFormat.setText(getResources().getString(R.string.png));
        }
        if (savedCheckedRadioButton != null) {
            savedCheckedRadioButton.setChecked(true);
        }
    }

    private void loadQualityPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int saveRadioIndex = sharedPreferences.getInt(MY_KEY_SAVE_RADIO_BUTTON_PREFS, 0);
        RadioButton savedCheckedRadioButton = (RadioButton) rgQuality.getChildAt(saveRadioIndex);
        switch (saveRadioIndex) {
            case 0:
                txtQuality.setText(getResources().getString(R.string.ultra));
                break;
            case 1:
                txtQuality.setText(getResources().getString(R.string.very_high));
                break;
            case 2:
                txtQuality.setText(getResources().getString(R.string.high));
                break;
            case 3:
                txtQuality.setText(getResources().getString(R.string.medium));
                break;
            case 4:
                txtQuality.setText(getResources().getString(R.string.low));
                break;
            case 5:
                txtQuality.setText(getResources().getString(R.string.very_low));
                break;
        }
        if (savedCheckedRadioButton != null) {
            savedCheckedRadioButton.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_back_in, R.anim.anim_back_out);
    }

    private void initView() {
        imgBack = findViewById(R.id.img_back);
        txtFormat = findViewById(R.id.txt_format);
        txtQuality = findViewById(R.id.txt_quality);

        formatDialog = new Dialog(this);
        formatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        formatDialog.setContentView(R.layout.dialog_format);
        formatDialog.getWindow().getAttributes().windowAnimations = R.style.InfoImageDialogAnimation;
        rgFormat = formatDialog.findViewById(R.id.rb_group);

        qualityDialog = new Dialog(this);
        qualityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        qualityDialog.setContentView(R.layout.dialog_quality);
        qualityDialog.getWindow().getAttributes().windowAnimations = R.style.InfoImageDialogAnimation;
        rgQuality = qualityDialog.findViewById(R.id.rb_group);
    }
}