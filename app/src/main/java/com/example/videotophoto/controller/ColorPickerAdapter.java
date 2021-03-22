package com.example.videotophoto.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videotophoto.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private final List<Integer> colorPickerColors;
    private OnColorPickerClickListener callBack;

    ColorPickerAdapter(@NonNull Context context, @NonNull List<Integer> colorPickerColors) {
        this.inflater = LayoutInflater.from(context);
        this.colorPickerColors = colorPickerColors;
    }

    public ColorPickerAdapter(@NonNull Context context) {
        this(context, getDefaultColors(context));
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnColorPickerClickListener(OnColorPickerClickListener callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_color_picker, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.vColorPicker.setBackgroundColor(colorPickerColors.get(position));
    }

    @Override
    public int getItemCount() {
        return colorPickerColors.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        View vColorPicker;

        public MyViewHolder(View itemView) {
            super(itemView);
            vColorPicker = itemView.findViewById(R.id.v_color_picker);
            itemView.setOnClickListener(v -> {
                if (callBack != null)
                    callBack.onClickColor(colorPickerColors.get(getAdapterPosition()));
            });
        }
    }

    public interface OnColorPickerClickListener {
        void onClickColor(int colorCode);
    }

    public static List<Integer> getDefaultColors(Context context) {
        ArrayList<Integer> colorPickerColors = new ArrayList<>();
        colorPickerColors.add(ContextCompat.getColor(context, R.color.blue_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.brown_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.green_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.orange_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.black));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red_orange_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.sky_blue_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.violet_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.white));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_green_color_picker));
        return colorPickerColors;
    }
}