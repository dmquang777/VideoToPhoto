package com.example.videotophoto.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videotophoto.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.PhotoEditor;

public class EmojiFragment extends BottomSheetDialogFragment {

    public EmojiFragment() {
    }

    private EmojiListener emojiListener;

    public interface EmojiListener {
        void onEmojiClick(String emojiUnicode);
    }

    private final BottomSheetBehavior.BottomSheetCallback sheetCallback = new BottomSheetBehavior.BottomSheetCallback() {

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
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
//        CoordinatorLayout.Behavior behavior = params.getBehavior();
//
//        if (behavior instanceof BottomSheetBehavior) {
//            ((BottomSheetBehavior) behavior).setBottomSheetCallback(sheetCallback);
//        }
        RecyclerView recyclerViewEmoji = contentView.findViewById(R.id.recycler_view_emoji);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5);
        recyclerViewEmoji.setLayoutManager(gridLayoutManager);

        EmojiAdapter emojiAdapter = new EmojiAdapter();
        recyclerViewEmoji.setAdapter(emojiAdapter);
    }

    public void setEmojiListener(EmojiListener emojiListener) {
        this.emojiListener = emojiListener;
    }

    public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.MyViewHolder> {

        ArrayList<String> emojis = PhotoEditor.getEmojis(Objects.requireNonNull(getActivity()));

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_emoji, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.emoji.setText(emojis.get(position));
        }

        @Override
        public int getItemCount() {
            return emojis.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView emoji;

            MyViewHolder(View itemView) {
                super(itemView);
                emoji = itemView.findViewById(R.id.emoji);

                itemView.setOnClickListener(v -> {
                    if (emojiListener != null) {
                        emojiListener.onEmojiClick(emojis.get(getLayoutPosition()));
                    }
                    dismiss();
                });
            }
        }
    }
}