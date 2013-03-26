
package com.polysfactory.coursera.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {
    public int id;
    public String homeLink;
    public int startYear;
    public int startMonth;
    public int startDay;
    public String startDateString;
    public String durationString;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(homeLink);
        dest.writeInt(startYear);
        dest.writeInt(startMonth);
        dest.writeInt(startDay);
        dest.writeString(startDateString);
        dest.writeString(durationString);
    }

    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    private Course(Parcel in) {
        id = in.readInt();
        homeLink = in.readString();
        startYear = in.readInt();
        startMonth = in.readInt();
        startDay = in.readInt();
        startDateString = in.readString();
        durationString = in.readString();
    }
}
