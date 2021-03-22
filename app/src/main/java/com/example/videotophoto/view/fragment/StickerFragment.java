package com.example.videotophoto.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.videotophoto.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class StickerFragment extends BottomSheetDialogFragment {

    public StickerFragment() {
    }

    private StickerListener stickerListener;

    public void setStickerListener(StickerListener stickerListener) {
        this.stickerListener = stickerListener;
    }

    public interface StickerListener {
        void onStickerClick(Bitmap bitmap);
    }

    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_emoji, null);
        dialog.setContentView(contentView);
        RecyclerView recyclerViewSticker = contentView.findViewById(R.id.recycler_view_emoji);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerViewSticker.setLayoutManager(gridLayoutManager);

        StickerAdapter stickerAdapter = new StickerAdapter();
        recyclerViewSticker.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

        int[] stickers = new int[]{R.drawable.meme1, R.drawable.meme2, R.drawable.meme3, R.drawable.meme4,
                R.drawable.meme5, R.drawable.meme10, R.drawable.meme13, R.drawable.meme14, R.drawable.meme15,
                R.drawable.meme16, R.drawable.meme17,R.drawable.meme18, R.drawable.meme19,R.drawable.meme20,
                R.drawable.meme21,R.drawable.meme22, R.drawable.meme23};

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sticker, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.sticker.setImageResource(stickers[position]);
        }

        @Override
        public int getItemCount() {
            return stickers.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView sticker;

            ViewHolder(View itemView) {
                super(itemView);
                sticker = itemView.findViewById(R.id.sticker);

                itemView.setOnClickListener(v -> {
                    if (stickerListener != null) {
                        stickerListener.onStickerClick(
                                BitmapFactory.decodeResource(getResources(),
                                        stickers[getLayoutPosition()]));
                    }
                    dismiss();
                });
            }
        }
    }
}