package com.example.videotophoto.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videotophoto.R;
import com.example.videotophoto.model.Music;

import java.util.ArrayList;

public class MusicPickerAdapter extends RecyclerView.Adapter<MusicPickerAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<Music> musicList;
    private OnMusicClick callBack;

    public MusicPickerAdapter(Context context) {
        this.context = context;
    }

    public void setCallBack(OnMusicClick callBack) {
        this.callBack = callBack;
    }

    public void setData(ArrayList<Music> data) {
        if (musicList == null) {
            musicList = new ArrayList<>();
        }
        musicList.clear();
        musicList.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_music_picker, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.txtMusicName.setText(music.getStrTitle());
        holder.txtMusicDuration.setText(music.getStrDuration());
        holder.txtMusicSize.setText(music.getStrSize());
        holder.txtMusicDateAdded.setText(music.getStrDateAdded());
        holder.itemView.setOnClickListener(v -> {
            if (callBack != null) {
                callBack.onItemClick(position);
            }
        });
        holder.rlMusic.setAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_recycler_fade_scale));
    }

    @Override
    public int getItemCount() {
        if (musicList != null) {
            return musicList.size();
        }
        return 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMusicName;
        TextView txtMusicDuration;
        TextView txtMusicSize;
        TextView txtMusicDateAdded;
        RelativeLayout rlMusic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMusicName = itemView.findViewById(R.id.txt_music_name);
            txtMusicDuration = itemView.findViewById(R.id.txt_music_duration);
            txtMusicSize = itemView.findViewById(R.id.txt_music_size);
            txtMusicDateAdded = itemView.findViewById(R.id.txt_music_modified);
            rlMusic = itemView.findViewById(R.id.relative_layout_music);
        }
    }

    public interface OnMusicClick {
        void onItemClick(int position);
    }
}
