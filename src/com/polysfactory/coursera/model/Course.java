
package com.polysfactory.coursera.model;

import com.google.gson.annotations.SerializedName;

public class Course {
    @SerializedName("course-ids")
    public long[] courseIds;
    public String name;
    public long id;
    public String photo;
    public University[] universities;

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
