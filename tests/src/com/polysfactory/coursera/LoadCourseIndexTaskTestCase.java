
package com.polysfactory.coursera;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Smoke;
import android.webkit.WebView;

import com.polysfactory.coursera.api.LoadCourseIndexTask;
import com.polysfactory.coursera.api.LoadCourseIndexTask.Callback;
import com.polysfactory.coursera.model.AuthToken;
import com.polysfactory.coursera.model.Course;
import com.polysfactory.coursera.model.VideoLecture;

public class LoadCourseIndexTaskTestCase extends AndroidTestCase {

    @Smoke
    public void test1() {
        AuthToken authToken = new AuthToken("");
        Course course = new Course();
        course.homeLink = "https://class.coursera.org/posa-001/";
        Callback callback = new Callback() {
            @Override
            public void onFinish(List<VideoLecture> results) {
                System.out.println("callback is called");
            }
        };

        System.out.println("mockito start");
        WebView webView = mock(WebView.class);
        System.out.println("mockito end");
        LoadCourseIndexTask loadCourseIndexTask = LoadCourseIndexTask.newInstance(authToken,
                course, callback, webView);
        loadCourseIndexTask.execute();
    }
}
