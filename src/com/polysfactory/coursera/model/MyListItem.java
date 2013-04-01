
package com.polysfactory.coursera.model;

import com.google.gson.annotations.SerializedName;

public class MyListItem {
    @SerializedName("course-ids")
    public long[] courseIds;
    public String name;
    public long id;
    public String photo;
    public Course[] courses;
    public University[] universities;
    public String smallIcon;

    public String getUniversityName() {
        if (universities == null || universities.length == 0) {
            return null;
        }
        return universities[0].name;
    }

    public static class University {
        String name;
    }
}
