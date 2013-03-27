
package com.polysfactory.coursera;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Smoke;

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
        LoadCourseIndexTask loadCourseIndexTask = new LoadCourseIndexTask(authToken, course,
                callback);
        try {
            loadCourseIndexTask.execute();
            List<VideoLecture> list = loadCourseIndexTask.get(10000, TimeUnit.MILLISECONDS);
            Assert.assertEquals(true, list.size() > 0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
