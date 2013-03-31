
package com.polysfactory.coursera.api;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.webkit.CookieManager;

public class ScoutUrlTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ScoutUrlTask";

    final private String mUrl;

    final private Callback mCallback;

    public ScoutUrlTask(String url, Callback callback) {
        this.mUrl = url;
        this.mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DefaultRedirectHandler drh = new DefaultRedirectHandler() {
            @Override
            public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                Header[] headers = response.getHeaders("Location");
                if (headers.length > 0) {
                    if (mCallback != null) {
                        String url = headers[0].getValue();
                        mCallback.onRedirected(url);
                    }
                }
                return false;
            }
        };
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectHandler(drh);
        HttpGet httpGet = new HttpGet(this.mUrl);
        String cookie = CookieManager.getInstance().getCookie(this.mUrl);
        httpGet.setHeader("Cookie", cookie);
        try {
            client.execute(httpGet);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public interface Callback {
        public void onRedirected(String url);
    }
}
