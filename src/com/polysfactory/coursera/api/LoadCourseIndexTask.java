
package com.polysfactory.coursera.api;

import java.lang.ref.WeakReference;
import java.util.List;

import android.os.Handler;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.polysfactory.coursera.model.AuthToken;
import com.polysfactory.coursera.model.Course;
import com.polysfactory.coursera.model.VideoLecture;

public class LoadCourseIndexTask extends Handler {

    private static final String TAG = "LoadCourseraIndexTask";

    private static final int MSG_CODE_MONITOR_WEWBVIEW = 0;

    private static final String LECTURE_LINK_SELECTOR = ".lecture-link";

    private final AuthToken mAuthToken;

    private final Course mCourse;

    private final Callback mCallback;

    private final WeakReference<WebView> mWebViewRef;

    public LoadCourseIndexTask(AuthToken authToken, Course course, Callback callback,
            WebView webView) {
        this.mAuthToken = authToken;
        this.mCourse = course;
        this.mCallback = callback;
        this.mWebViewRef = new WeakReference<WebView>(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsObject(), "android");
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(TAG, cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie("https://class.coursera.org/", mAuthToken.getCookie());
    }

    public void execute() {
        WebView webView = mWebViewRef.get();
        if (webView != null) {
            webView.loadUrl(mCourse.getLectureIndexUrl());
            this.sendEmptyMessage(MSG_CODE_MONITOR_WEWBVIEW);
        }
    }

    public static interface Callback {
        public void onFinish(List<VideoLecture> results);
    }

    public void handleMessage(android.os.Message msg) {
        if (msg.what == MSG_CODE_MONITOR_WEWBVIEW) {
            WebView webView = mWebViewRef.get();
            if (webView != null) {
                final String url = webView.getUrl();
                Log.d(TAG, "url: " + url);
                if (url != null && url.equals(mCourse.getLectureIndexUrl())) {
                    Log.d(TAG, "fired!!");
                    webView.loadUrl("javascript:var links=document.querySelectorAll('"
                            + LECTURE_LINK_SELECTOR
                            + "'); for (var i=0; i<links.length; i++) { android.findLectureLink(links[i].innerText); }");
                    if (mCallback != null) {
                        // TODO
                        // mCallback.onFinish(result);
                    }
                } else {
                    // TODO set timeout
                    this.sendEmptyMessageDelayed(MSG_CODE_MONITOR_WEWBVIEW, 500);
                }
            }
        }
    }

    static class JsObject {
        @JavascriptInterface
        public void findLectureLink(String title) {
            // TODO
            Log.v(TAG, title);
        }
    }

}
