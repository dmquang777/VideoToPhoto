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
import com.example.videotophoto.model.Video;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.myViewHolder> {
    private final Context context;
    private ArrayList<Video> videos;
    private onVideoClick callBack;

    public void setCallBack(onVideoClick callBack) {
        this.callBack = callBack;
    }

    public VideoAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Video> data) {
        if (videos == null) {
            videos = new ArrayList<>();
        }
        videos.clear();
        videos.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_video, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.txtDuration.setText(video.getStrDuration());
        Glide.with(holder.itemView.getContext())
                .load(video.getUriData())
                .into(holder.imgThumbnail);
        holder.itemView.setOnClickListener(v -> {
            if (callBack != null) {
                callBack.onItemClick(position);
            }
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

    static class myViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView txtDuration;
        RelativeLayout rlVideo;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            txtDuration = itemView.findViewById(R.id.txt_duration);
            rlVideo = itemView.findViewById(R.id.relative_layout_video);
        }
    }

    public interface onVideoClick {
        void onItemClick(int position);
    }
}
