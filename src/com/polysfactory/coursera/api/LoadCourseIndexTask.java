
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

    private static final int MSG_CODE_LOGIN = 1;

    private static final int MSG_FINISH_GET_VIDEO_LECTURES = 100;

    private final AuthToken mAuthToken;

    private final Course mCourse;

    private final Callback mCallback;

    private final WeakReference<WebView> mWebViewRef;

    private final JsObject mJsObject = new JsObject();

    private final JsCallback mJsCallback;

    private int monitorCount = 0;

    private static final int MONITOR_INTERVAL_MSEC = 500;

    private static final int MAX_MONITOR_COUNT = 50;

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
                    if (mCallback != null) {
                        mCallback.onFinish(mJsObject.getVideoLectures());
                    }
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
            this.monitorCount = 0;
            this.sendEmptyMessage(MSG_CODE_MONITOR_WEWBVIEW);
        }
    }

    public static interface Callback {
        public void onFinish(List<VideoLecture> results);
    }

    public void handleMessage(android.os.Message msg) {
        if (msg.what == MSG_CODE_MONITOR_WEWBVIEW) {
            this.monitorCount++;
            WebView webView = mWebViewRef.get();
            if (webView != null) {
                final String url = webView.getUrl();
                Log.d(TAG, "url: " + url);
                if (url != null) {
                    if (url.equals(mCourse.getLectureIndexUrl())) {
                        Log.d(TAG, "fired!!");
                        webView.loadUrl(
                                "javascript:document.addEventListener('DOMContentLoaded', function(){"
                                        + "var parent=document.querySelectorAll('.course-item-list');"
                                        + "var items = parent[0].getElementsByTagName('li');"
                                        + "for (var i=0; i<items.length; i++) { "
                                        + " var lecture = items[i].querySelectorAll('.lecture-link');"
                                        + " var title = lecture[0].innerHTML;"
                                        + " var links = items[i].getElementsByTagName('a');"
                                        + " for (var j=0; j<links.length; j++) { "
                                        + "   var url = links[j].getAttribute('href');"
                                        + "   if (url.match(/download.mp4/)) {"
                                        + "     vurl = url;"
                                        + "   } else if (url.match(/format=srt/)) {"
                                        + "     suburl = url;"
                                        + "   }"
                                        + " }"
                                        + " android.findLectureLink(title, vurl, suburl);"
                                        + "}"
                                        + "android.trigger(" + MSG_FINISH_GET_VIDEO_LECTURES + ");"
                                        + "});");
                        return;
                    } else if (url.startsWith(mCourse.getAuthUrl())) {
                        this.sendEmptyMessageDelayed(MSG_CODE_LOGIN, MONITOR_INTERVAL_MSEC);
                        return;
                    }
                }
                if (this.monitorCount < MAX_MONITOR_COUNT) {
                    this.sendEmptyMessageDelayed(MSG_CODE_MONITOR_WEWBVIEW, MONITOR_INTERVAL_MSEC);
                }
            }
        } else if (msg.what == MSG_CODE_LOGIN) {
            WebView webView = mWebViewRef.get();
            if (webView != null) {
                webView.loadUrl("javascript:location.href=document.getElementById('login_normal').getAttribute('href');");
                monitorCount = 0;
                this.sendEmptyMessageDelayed(MSG_CODE_MONITOR_WEWBVIEW, MONITOR_INTERVAL_MSEC);
            }
        }
    }

    static class JsObject {
        private final List<VideoLecture> videoLectures = new ArrayList<VideoLecture>();

        private JsCallback mCallback;

        @JavascriptInterface
        public void findLectureLink(String title, String url, String scriptUrl) {
            Log.v(TAG, title + ":" + url);
            VideoLecture videoLecture = new VideoLecture();
            videoLecture.title = title;
            videoLecture.url = url;
            videoLecture.scriptUrl = scriptUrl;
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
