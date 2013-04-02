
package com.polysfactory.coursera.api;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.polysfactory.coursera.api.LoadCourseIndexTask.JsObject.JsCallback;
import com.polysfactory.coursera.model.AuthToken;
import com.polysfactory.coursera.model.Course;
import com.polysfactory.coursera.model.VideoLecture;
import com.polysfactory.coursera.model.VideoLectureGroup;

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
                        mCallback.onFinish(mJsObject.getVideoLectureGroups());
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
        public void onFinish(List<VideoLectureGroup> results);
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
                                        + "var headers = document.querySelectorAll('.course-item-list-header');"
                                        + "var containers = document.querySelectorAll('.course-item-list-section-list');"
                                        + "for (var i=0; i<headers.length; i++) { "
                                        + " var header = headers[i];"
                                        + " console.log(header);"
                                        + " console.log(header.getElementsByTagName('h3'));"
                                        + " console.log(header.getElementsByTagName('h3')[0]);"
                                        + " var groupTitle = header.getElementsByTagName('h3')[0].innerHTML;"
                                        + " console.log(groupTitle);"
                                        + " var items = containers[i].getElementsByTagName('li');"
                                        + " var lectures = [];"
                                        + " for (var k=0; k<items.length; k++) { "
                                        + "  var lecture = items[k].querySelectorAll('.lecture-link');"
                                        + "  var title = lecture[0].innerHTML;"
                                        + "  var links = items[k].getElementsByTagName('a');"
                                        + "  var viewed = (items[k].className != 'unviewed');"
                                        + "  for (var j=0; j<links.length; j++) { "
                                        + "    var url = links[j].getAttribute('href');"
                                        + "    if (url.match(/download.mp4/)) {"
                                        + "      vurl = url;"
                                        + "    } else if (url.match(/format=srt/)) {"
                                        + "      suburl = url;"
                                        + "    }"
                                        + "  }"
                                        + "  lectures.push({'title':title, 'url':vurl, 'sub_url':suburl, 'viewed':viewed});"
                                        + " }/* end of lecture item loop */"
                                        + " android.addLectureGroup(groupTitle, JSON.stringify(lectures));"
                                        + "}/* end of lecture group loop */"
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
        private final List<VideoLectureGroup> videoLectureGroups = new ArrayList<VideoLectureGroup>();

        private JsCallback mCallback;

        private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(
                FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        @JavascriptInterface
        public void addLectureGroup(String groupTitleHTML, String jsonVideoLectureItemArray) {
            String groupTitle = Html.fromHtml(groupTitleHTML).toString();
            Log.v(TAG, groupTitle + ":" + jsonVideoLectureItemArray);
            VideoLecture[] videoLectureArray = GSON.fromJson(jsonVideoLectureItemArray,
                    VideoLecture[].class);
            VideoLectureGroup videoLectureGroup = new VideoLectureGroup(groupTitle,
                    Arrays.asList(videoLectureArray));
            videoLectureGroups.add(videoLectureGroup);
        }

        @JavascriptInterface
        public void trigger(int eventCode) {
            if (mCallback != null) {
                mCallback.handleEvent(eventCode);
            }
        }

        public List<VideoLectureGroup> getVideoLectureGroups() {
            return Collections.unmodifiableList(videoLectureGroups);
        }

        public void registerCallback(JsCallback callback) {
            this.mCallback = callback;
        }

        public interface JsCallback {
            public void handleEvent(int eventCode);
        }
    }

}
