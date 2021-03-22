package com.example.videotophoto.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class MyAsyncLoader extends AsyncTaskLoader<String> {
    private final String path;
    private final String videoName;

    public MyAsyncLoader(@NonNull Context context, String path, String videoName) {
        super(context);
        this.path = path;
        this.videoName = videoName;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        int executionId = FFmpeg.execute("-f concat -safe 0 -i " + path + "/allPathImages.txt"
                + " -f concat -safe 0 -i " + path + "/selectedMusicPath.txt"
                + " -c:a aac -pix_fmt yuv420p -crf 23 -r 24 -shortest -y -vf scale=trunc(iw/2)*2:trunc(ih/2)*2 "
                + path + File.separator + videoName);

        if (executionId == RETURN_CODE_SUCCESS) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File(path)));
            getContext().getApplicationContext().sendBroadcast(intent);
            return "Create successfully";
        } else if (executionId == RETURN_CODE_CANCEL) {
            return "Create cancelled";
        } else {
            Config.printLastCommandOutput(Log.INFO);
            return "Create failed";
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }
}
