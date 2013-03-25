
package com.polysfactory.coursera.api;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.polysfactory.coursera.model.AuthToken;
import com.polysfactory.coursera.model.Course;

public class LoadCourseListTask extends AsyncTask<Void, Void, Course[]> {

    private static final String TAG = "LoadCourseListTask";

    private static final String URL = "https://www.coursera.org/maestro/api/topic/list_my";

    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private AuthToken mAuthToken;

    private CallbackListner mCallback;

    public LoadCourseListTask(AuthToken authToken, CallbackListner callback) {
        this.mAuthToken = authToken;
        this.mCallback = callback;
    }

    @Override
    protected Course[] doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        httpGet.getParams().setParameter("user_id", String.valueOf(mAuthToken.getUser().id));
        httpGet.setHeader("Cookie", mAuthToken.getCookie());
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            String response = EntityUtils.toString(entity);
            Course[] courses = gson.fromJson(response, Course[].class);
            Log.v(TAG, response);
            return courses;
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
    protected void onPostExecute(Course[] courses) {
        if (mCallback != null) {
            mCallback.onFinish(courses);
        }
    };

    public static interface CallbackListner {
        public void onFinish(Course[] courses);
    }
};
