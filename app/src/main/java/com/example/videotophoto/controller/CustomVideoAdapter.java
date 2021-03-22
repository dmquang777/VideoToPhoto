package com.example.videotophoto.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videotophoto.R;
import com.example.videotophoto.model.CustomVideo;

import java.util.ArrayList;

public class CustomVideoAdapter extends RecyclerView.Adapter<CustomVideoAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<CustomVideo> videos;
    private OnVideoClick callBack;

    public void setCallBack(OnVideoClick callBack) {
        this.callBack = callBack;
    }

    public CustomVideoAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<CustomVideo> data) {
        if (videos == null) {
            videos = new ArrayList<>();
        }
        videos.clear();
        videos.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_custom_video, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CustomVideo video = videos.get(position);
        holder.txtTitle.setText(video.getStrTitle());
        holder.txtDuration.setText(video.getStrDuration());
        holder.txtDateAdded.setText(video.getStrDateAdded());
        holder.txtSize.setText(video.getStrSize());
        Glide.with(holder.itemView.getContext())
                .load(video.getUriData())
                .into(holder.imgThumbnail);
        holder.itemView.setOnClickListener(v -> {
            if (callBack != null) {
                callBack.onItemClick(position);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (callBack != null) {
                callBack.onItemLongClick(position);
            }
            return true;
        });
        holder.rlVideo.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_recycler_fade_scale));
    }

    @Override
    public int getItemCount() {
        if (videos != null) {
            return videos.size();
        }
        return 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView txtTitle;
        TextView txtDuration;
        TextView txtDateAdded;
        TextView txtSize;
        RelativeLayout rlVideo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            txtDuration = itemView.findViewById(R.id.txt_duration);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtDateAdded = itemView.findViewById(R.id.txt_date_added);
            txtSize = itemView.findViewById(R.id.txt_size);
            rlVideo = itemView.findViewById(R.id.relative_layout_video);
        }
    }

    public interface OnVideoClick {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }
}
