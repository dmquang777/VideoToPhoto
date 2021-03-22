package com.example.videotophoto.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videotophoto.R;
import com.example.videotophoto.model.Image;

import java.util.ArrayList;

public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.myViewHolder> {
    private final Context context;
    private final ArrayList<Image> images;
    private OnImagesListener callBack;

    public ImagePickerAdapter(Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
    }

    public void setCallBack(OnImagesListener callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_image_picker, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Image image = images.get(position);
        Glide.with(holder.imageView.getContext())
                .load(image.getUriData())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(v -> callBack.onImageClick(image, position));
        holder.rlImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_recycler_view_down_to_up));
    }


    @Override
    public int getItemCount() {
        if (images != null) {
            return images.size();
        }
        return 0;
    }

    static class myViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        RelativeLayout rlImage;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_images);
            rlImage = itemView.findViewById(R.id.relative_layout_image);
        }
    }

    public interface OnImagesListener {
        void onImageClick(Image image, int position);
    }
}
