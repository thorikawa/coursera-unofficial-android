
package com.polysfactory.coursera.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.polysfactory.coursera.Constants;
import com.polysfactory.coursera.R;
import com.polysfactory.coursera.api.ScoutUrlTask;
import com.polysfactory.coursera.api.ScoutUrlTask.Callback;
import com.polysfactory.coursera.model.VideoLecture;

public class VideoPlayerActivity extends Activity {

    private static final String TAG = "VideoPlayerActivity";

    VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_player);
        this.mVideoView = (VideoView) findViewById(R.id.video_view);
        Intent intent = getIntent();
        VideoLecture videoLecture = intent
                .getParcelableExtra(Constants.COURSERA_INTENT_KEY_VIDEO_LECTURE);
        Log.v(TAG, "play:" + videoLecture.url);
        Log.v(TAG, "subscript:" + videoLecture.scriptUrl);

        ScoutUrlTask videoUrlFindTask = new ScoutUrlTask(videoLecture.url, new Callback() {
            @Override
            public void onRedirected(final String url) {
                Log.v(TAG, "find URL:" + url);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoView.setVideoURI(Uri.parse(url));
                        mVideoView.start();
                    }
                });
            }
        });
        videoUrlFindTask.execute();
    }

}
