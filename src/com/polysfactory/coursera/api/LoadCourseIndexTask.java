
package com.polysfactory.coursera.api;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Handler;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.polysfactory.coursera.api.LoadCourseIndexTask.JsObject.JsCallback;
import com.polysfactory.coursera.model.AuthToken;
import com.polysfactory.coursera.model.Course;
import com.polysfactory.coursera.model.VideoLecture;

public class LoadCourseIndexTask extends Handler {

    private static final String TAG = "LoadCourseraIndexTask";

    private static final int MSG_CODE_MONITOR_WEWBVIEW = 0;

    private static final int MSG_FINISH_GET_VIDEO_LECTURES = 100;

    private static final String LECTURE_LINK_SELECTOR = ".lecture-link";

    private final AuthToken mAuthToken;

    private final Course mCourse;

    private final Callback mCallback;

    private final WeakReference<WebView> mWebViewRef;

    private final JsObject mJsObject = new JsObject();

    private final JsCallback mJsCallback;

    /**
     * We use Factory method to retrieve instance since we need to register the
     * event listener to the new instance.
     * 
     * @param authToken
     * @param course
     * @param callback
     * @param webView
     * @return new instance
     */
    public static LoadCourseIndexTask newInstance(AuthToken authToken, Course course,
            Callback callback,
            WebView webView) {
        LoadCourseIndexTask instance = new LoadCourseIndexTask(authToken, course, callback, webView);
        instance.mJsObject.registerCallback(instance.mJsCallback);
        return instance;
    }

    private LoadCourseIndexTask(AuthToken authToken, Course course, Callback callback,
            WebView webView) {
        this.mAuthToken = authToken;
        this.mCourse = course;
        this.mCallback = callback;
        this.mWebViewRef = new WeakReference<WebView>(webView);
        this.mJsCallback = new JsCallback() {
            @Override
            public void handleEvent(final int eventCode) {
                if (eventCode == MSG_FINISH_GET_VIDEO_LECTURES) {
                    Log.v(TAG, "video lecture result");
                    mCallback.onFinish(mJsObject.getVideoLectures());
                }
            }
        };
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(mJsObject, "android");
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
                            + LECTURE_LINK_SELECTOR + "');"
                            + "for (var i=0; i<links.length; i++) { "
                            + " var t = links[i].innerText;"
                            + " var u = links[i].getAttribute('href');"
                            + " android.findLectureLink(t, u);"
                            + "}"
                            + "android.trigger(" + MSG_FINISH_GET_VIDEO_LECTURES + ");");
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
        private final List<VideoLecture> videoLectures = new ArrayList<VideoLecture>();

        private JsCallback mCallback;

        @JavascriptInterface
        public void findLectureLink(String title, String url) {
            VideoLecture videoLecture = new VideoLecture();
            videoLecture.title = title;
            videoLecture.url = url;
            videoLectures.add(videoLecture);
        }

        @JavascriptInterface
        public void trigger(int eventCode) {
            if (mCallback != null) {
                mCallback.handleEvent(eventCode);
            }
        }

        public List<VideoLecture> getVideoLectures() {
            return Collections.unmodifiableList(videoLectures);
        }

        public void registerCallback(JsCallback callback) {
            this.mCallback = callback;
        }

        public interface JsCallback {
            public void handleEvent(int eventCode);
        }
    }

}
