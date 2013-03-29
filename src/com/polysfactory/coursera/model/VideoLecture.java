
package com.polysfactory.coursera.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoLecture implements Parcelable {

    public String title;

    public String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
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
    }
}
