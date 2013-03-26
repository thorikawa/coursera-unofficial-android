
package com.polysfactory.coursera.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.polysfactory.coursera.Constants;
import com.polysfactory.coursera.CourseraApplication;
import com.polysfactory.coursera.R;
import com.polysfactory.coursera.adapter.VideoLectureListAdapter;
import com.polysfactory.coursera.api.LoadCourseIndexTask;
import com.polysfactory.coursera.api.LoadCourseIndexTask.Callback;
import com.polysfactory.coursera.model.Course;
import com.polysfactory.coursera.model.VideoLecture;

public class LectureIndexActivity extends Activity {

    private ListView mVideoLectureListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_index);
        mVideoLectureListView = (ListView) findViewById(R.id.video_lecture_list);

        CourseraApplication application = (CourseraApplication) getApplication();
        Course course = getIntent().getParcelableExtra(Constants.COURSERA_INTENT_KEY_COURSE);
        LoadCourseIndexTask loadCourseIndexTask = new LoadCourseIndexTask(
                application.getAuthToken(), course, new Callback() {
                    @Override
                    public void onFinish(List<VideoLecture> results) {
                        mVideoLectureListView.setAdapter(new VideoLectureListAdapter(
                                LectureIndexActivity.this, results));
                    }
                });
        loadCourseIndexTask.execute();
    }
}
