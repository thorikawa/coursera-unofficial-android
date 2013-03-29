
package com.polysfactory.coursera.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.VideoView;

import com.polysfactory.coursera.R;

public class VideoPlayerActivity extends Activity {
    VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);
        this.mVideoView = (VideoView) findViewById(R.id.video_view);

        Intent intent = getIntent();
    }
}
