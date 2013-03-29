
package com.polysfactory.coursera.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import com.polysfactory.coursera.Constants;
import com.polysfactory.coursera.R;
import com.polysfactory.coursera.model.VideoLecture;

public class VideoPlayerActivity extends Activity {

    private static final String TAG = "VideoPlayerActivity";

    VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);
        this.mVideoView = (VideoView) findViewById(R.id.video_view);

        Intent intent = getIntent();
        VideoLecture videoLecture = intent
                .getParcelableExtra(Constants.COURSERA_INTENT_KEY_VIDEO_LECTURE);
        Log.v(TAG, "play:" + videoLecture.url);
        mVideoView.setVideoURI(Uri.parse(videoLecture.url));
    }
}
