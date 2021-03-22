package com.example.videotophoto.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videotophoto.R;
import com.example.videotophoto.controller.ColorPickerAdapter;

import java.util.Objects;

public class TextEditorFragment extends DialogFragment {

    public static final String TAG = TextEditorFragment.class.getSimpleName();
    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_COLOR_CODE = "extra_color_code";
    private EditText edtText;
    private InputMethodManager inputMethodManager;
    private int colorCode;
    private TextEditor textEditor;

    public interface TextEditor {
        void onDone(String inputText, int colorCode);
    }

    public static TextEditorFragment show(@NonNull AppCompatActivity appCompatActivity, @NonNull String inputText, @ColorInt int colorCode) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        TextEditorFragment fragment = new TextEditorFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    public static TextEditorFragment show(@NonNull AppCompatActivity appCompatActivity) {
        return show(appCompatActivity, "", ContextCompat.getColor(appCompatActivity, R.color.white));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtText = view.findViewById(R.id.edt_add_text);
        inputMethodManager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        TextView txtDone = view.findViewById(R.id.txt_done);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_add_text_color);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getActivity());

        colorPickerAdapter.setOnColorPickerClickListener(colorCode -> {
            this.colorCode = colorCode;
            edtText.setTextColor(colorCode);
        });
        recyclerView.setAdapter(colorPickerAdapter);

        if (getArguments() != null) edtText.setText(getArguments().getString(EXTRA_INPUT_TEXT));
        colorCode = getArguments().getInt(EXTRA_COLOR_CODE);
        edtText.setTextColor(colorCode);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        edtText.requestFocus();

        txtDone.setOnClickListener(view1 -> {
            inputMethodManager.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            dismiss();
            String inputText = edtText.getText().toString();
            if (!TextUtils.isEmpty(inputText) && textEditor != null) {
                textEditor.onDone(inputText, colorCode);
            }
        });

    }

    public void setOnTextEditorListener(TextEditor textEditor) {
        this.textEditor = textEditor;
    }

    @Override
    public void onPause() {
        super.onPause();
        inputMethodManager.hideSoftInputFromWindow(edtText.getWindowToken(), 0);
    }
}