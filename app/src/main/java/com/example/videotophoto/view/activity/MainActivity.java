package com.example.videotophoto.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videotophoto.BuildConfig;
import com.example.videotophoto.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private CardView cvVideo;
    private CardView cvGallery;
    private CardView cvSlideshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        Animation animPress = AnimationUtils.loadAnimation(this, R.anim.anim_press);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_setting);
        navigationView.setItemIconTintList(null);

        cvVideo.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FolderActivity.class));
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });

        cvGallery.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GalleryActivity.class));
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });

        cvSlideshow.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SlideShowActivity.class));
            v.startAnimation(animPress);
            overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_setting: {
                startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), 2);
                overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
                break;
            }
            case R.id.nav_about: {
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_about);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.AboutDialogAnimation;

                TextView txtName = dialog.findViewById(R.id.name);
                TextView txtTerm = dialog.findViewById(R.id.term);
                Button btnOk = dialog.findViewById(R.id.btn_ok);

                txtName.setOnClickListener(v -> {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.facebook.com/dmquang321/"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
                });

                txtTerm.setOnClickListener(v -> {
                    startActivity(new Intent(MainActivity.this, TermActivity.class));
                    overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
                });

                btnOk.setOnClickListener(v -> dialog.cancel());
                dialog.show();
                break;
            }
            case R.id.nav_rate: {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.nav_share: {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Sup bro ! Check out this cool app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                                + "\nIt's free, DOWNLOAD NOW !!!");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.exit))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.nah), (dialog, which) -> finishAffinity())
                .setNegativeButton(getResources().getString(R.string.sure), (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExitDialogAnimation;

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            dialog.show();
        }
    }

    private void initView() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        cvVideo = findViewById(R.id.cv_video);
        cvGallery = findViewById(R.id.cv_gallery);
        cvSlideshow = findViewById(R.id.cv_slideshow);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK && data != null) {

            }
        }
    }
}