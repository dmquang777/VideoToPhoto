package com.example.videotophoto.controller;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videotophoto.R;

import java.util.ArrayList;

public class ImageFolderAdapter extends RecyclerView.Adapter<ImageFolderAdapter.MyViewHolder> {
    private ArrayList<String> folderList;
    private OnFolderListener callBack;

    public void setCallBack(OnFolderListener callBack) {
        this.callBack = callBack;
    }

    public ImageFolderAdapter() {
    }

    public void setData(ArrayList<String> data) {
        if (folderList == null) {
            folderList = new ArrayList<>();
        }
        folderList.clear();
        folderList.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_image_folder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtFolderName.setText(folderList.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (callBack != null) {
                callBack.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (folderList != null) {
            return folderList.size();
        }
        return 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtFolderName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFolderName = itemView.findViewById(R.id.txt_folder_name);
        }
    }

    public interface OnFolderListener {
        void onItemClick(int position);
    }
}
