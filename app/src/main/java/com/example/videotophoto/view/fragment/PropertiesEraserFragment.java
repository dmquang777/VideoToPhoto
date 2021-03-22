package com.example.videotophoto.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.videotophoto.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PropertiesEraserFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {
    public PropertiesEraserFragment() {
    }

    private Properties properties;

    public interface Properties {
        void onEraserSizeChanged(float brushEraserSize);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eraser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SeekBar sbSize = view.findViewById(R.id.sb_eraser_size);
        sbSize.setOnSeekBarChangeListener(this);
    }

    public void setPropertiesChangeListener(Properties properties) {
        this.properties = properties;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.sb_eraser_size) {
            if (properties != null) {
                properties.onEraserSizeChanged(progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}