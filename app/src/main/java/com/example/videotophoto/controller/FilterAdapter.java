package com.example.videotophoto.controller;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videotophoto.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {
    private final FilterListener filterListener;
    private final List<Pair<String, PhotoFilter>> pairList = new ArrayList<>();

    public FilterAdapter(FilterListener filterListener) {
        this.filterListener = filterListener;
        setupFilters();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_filter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pair<String, PhotoFilter> filterPair = pairList.get(position);
        Bitmap fromAsset = getBitmapFromAsset(holder.itemView.getContext(), filterPair.first);
        holder.imgFilterThumb.setImageBitmap(fromAsset);
        holder.txtFilterName.setText(filterPair.second.name().replace("_", " "));
    }

    @Override
    public int getItemCount() {
        return pairList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFilterThumb;
        TextView txtFilterName;

        MyViewHolder(View itemView) {
            super(itemView);
            imgFilterThumb = itemView.findViewById(R.id.img_filter_thumb);
            txtFilterName = itemView.findViewById(R.id.txt_filter_name);
            itemView.setOnClickListener(v -> filterListener.onFilterSelected(pairList.get(getLayoutPosition()).second));
        }
    }

    private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        try {
            inputStream = assetManager.open(strName);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupFilters() {
        pairList.add(new Pair<>("filters/original.jpg", PhotoFilter.NONE));
        pairList.add(new Pair<>("filters/auto_fix.png", PhotoFilter.AUTO_FIX));
        pairList.add(new Pair<>("filters/brightness.png", PhotoFilter.BRIGHTNESS));
        pairList.add(new Pair<>("filters/contrast.png", PhotoFilter.CONTRAST));
        pairList.add(new Pair<>("filters/documentary.png", PhotoFilter.DOCUMENTARY));
        pairList.add(new Pair<>("filters/dual_tone.png", PhotoFilter.DUE_TONE));
        pairList.add(new Pair<>("filters/fill_light.png", PhotoFilter.FILL_LIGHT));
        pairList.add(new Pair<>("filters/fish_eye.png", PhotoFilter.FISH_EYE));
        pairList.add(new Pair<>("filters/grain.png", PhotoFilter.GRAIN));
        pairList.add(new Pair<>("filters/gray_scale.png", PhotoFilter.GRAY_SCALE));
        pairList.add(new Pair<>("filters/lomish.png", PhotoFilter.LOMISH));
        pairList.add(new Pair<>("filters/negative.png", PhotoFilter.NEGATIVE));
        pairList.add(new Pair<>("filters/posterize.png", PhotoFilter.POSTERIZE));
        pairList.add(new Pair<>("filters/saturate.png", PhotoFilter.SATURATE));
        pairList.add(new Pair<>("filters/sepia.png", PhotoFilter.SEPIA));
        pairList.add(new Pair<>("filters/sharpen.png", PhotoFilter.SHARPEN));
        pairList.add(new Pair<>("filters/temprature.png", PhotoFilter.TEMPERATURE));
        pairList.add(new Pair<>("filters/tint.png", PhotoFilter.TINT));
        pairList.add(new Pair<>("filters/vignette.png", PhotoFilter.VIGNETTE));
        pairList.add(new Pair<>("filters/cross_process.png", PhotoFilter.CROSS_PROCESS));
        pairList.add(new Pair<>("filters/b_n_w.png", PhotoFilter.BLACK_WHITE));
    }
}
