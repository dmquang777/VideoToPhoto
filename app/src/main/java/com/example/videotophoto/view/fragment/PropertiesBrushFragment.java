package com.example.videotophoto.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.videotophoto.R;
import com.example.videotophoto.controller.ColorPickerAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class PropertiesBrushFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    public PropertiesBrushFragment() {
    }

    private Properties properties;

    public interface Properties {
        void onColorChanged(int colorCode);

        void onOpacityChanged(int opacity);

        void onBrushSizeChanged(int brushSize);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_properties_brush, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SeekBar sbOpacity = view.findViewById(R.id.sb_opacity);
        SeekBar sbSize = view.findViewById(R.id.sb_size);

        sbOpacity.setOnSeekBarChangeListener(this);
        sbSize.setOnSeekBarChangeListener(this);

        RecyclerView rvColor = view.findViewById(R.id.recycler_view_colors);
        rvColor.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(Objects.requireNonNull(getActivity()));

        colorPickerAdapter.setOnColorPickerClickListener(colorCode -> {
            if (properties != null) {
                dismiss();
                properties.onColorChanged(colorCode);
            }
        });
        rvColor.setAdapter(colorPickerAdapter);
    }

    public void setPropertiesChangeListener(Properties properties) {
        this.properties = properties;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_opacity:
                if (properties != null) {
                    properties.onOpacityChanged(progress);
                }
                break;
            case R.id.sb_size:
                if (properties != null) {
                    properties.onBrushSizeChanged(progress);
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}