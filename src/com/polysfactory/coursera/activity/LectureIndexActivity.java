
package com.polysfactory.coursera.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;

import com.polysfactory.coursera.Constants;
import com.polysfactory.coursera.CourseraApplication;
import com.polysfactory.coursera.R;
import com.polysfactory.coursera.adapter.VideoLectureListAdapter;
import com.polysfactory.coursera.api.LoadCourseIndexTask;
import com.polysfactory.coursera.api.LoadCourseIndexTask.Callback;
import com.polysfactory.coursera.model.Course;
import com.polysfactory.coursera.model.VideoLecture;
import com.polysfactory.coursera.model.VideoLectureGroup;

public class LectureIndexActivity extends Activity implements OnChildClickListener {

    private ExpandableListView mVideoLectureListView;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_index);
        mVideoLectureListView = (ExpandableListView) findViewById(R.id.video_lecture_list);
        mVideoLectureListView.setOnChildClickListener(this);
        mWebView = new WebView(this);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        addContentView(mWebView, new LinearLayout.LayoutParams(0, 0));

        CourseraApplication application = (CourseraApplication) getApplication();
        Course course = getIntent().getParcelableExtra(Constants.COURSERA_INTENT_KEY_COURSE);
        LoadCourseIndexTask loadCourseIndexTask = LoadCourseIndexTask.newInstance(
                application.getAuthToken(), course, new Callback() {
                    @Override
                    public void onFinish(final List<VideoLectureGroup> results) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mVideoLectureListView.setAdapter(new VideoLectureListAdapter(
                                        LectureIndexActivity.this, results));
                            }
                        });
                    }
                }, mWebView);
        loadCourseIndexTask.execute();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        VideoLecture item = (VideoLecture) parent.getExpandableListAdapter().getChild(
                groupPosition, childPosition);
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(Constants.COURSERA_INTENT_KEY_VIDEO_LECTURE, item);
        this.startActivity(intent);
        return true;
    }
}
