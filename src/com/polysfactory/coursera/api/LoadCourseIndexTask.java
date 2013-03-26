
package com.polysfactory.coursera.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

import com.polysfactory.coursera.model.AuthToken;
import com.polysfactory.coursera.model.Course;
import com.polysfactory.coursera.model.VideoLecture;

public class LoadCourseIndexTask extends AsyncTask<Void, Void, List<VideoLecture>> {

    private final AuthToken mAuthToken;
    
    private final Course mCourse;

    private final Callback mCallback;
    
    public LoadCourseIndexTask(AuthToken context, Course course, Callback callback) {
        this.mAuthToken = context;
        this.mCourse = course;
        this.mCallback = callback;
    }
    
    @Override
    protected List<VideoLecture> doInBackground(Void... arg0) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(mCourse.homeLink);
        httpGet.setHeader("Cookie", mAuthToken.getCookie());
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            String response = EntityUtils.toString(entity);
            Document doc = Jsoup.parse(response);
            Elements lectureLinkElements = doc.getElementsByClass("lecture-link");
            List<VideoLecture> lectures = new ArrayList<VideoLecture>();
            for (Element e : lectureLinkElements) {
                VideoLecture vl = new VideoLecture();
                vl.title = e.text();
                vl.url = e.attr("href");
                lectures.add(vl);
            }
            return lectures;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<VideoLecture> result) {
        if (mCallback != null) {
            mCallback.onFinish(result);
        }
    }
    
    public static interface Callback {
        public void onFinish(List<VideoLecture> results);
    }
}
