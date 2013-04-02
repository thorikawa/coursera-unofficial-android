
package com.polysfactory.coursera.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

public class VideoLecture implements Parcelable {

    private String title;

    public String url;

    public String subUrl;

    public boolean viewed;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = Html.fromHtml(title).toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(subUrl);
    }

    public static final Parcelable.Creator<VideoLecture> CREATOR = new Parcelable.Creator<VideoLecture>() {
        public VideoLecture createFromParcel(Parcel in) {
            return new VideoLecture(in);
        }

        public VideoLecture[] newArray(int size) {
            return new VideoLecture[size];
        }
    };

    public VideoLecture() {
    }

    private VideoLecture(Parcel in) {
        title = in.readString();
        url = in.readString();
        subUrl = in.readString();
    }
}
