
package com.polysfactory.coursera.model;

import java.util.Collections;
import java.util.List;

public class VideoLectureGroup {
    public String groupTitle;

    private final List<VideoLecture> lectureList;

    public VideoLectureGroup(String title, List<VideoLecture> lectureList) {
        this.groupTitle = title;
        this.lectureList = lectureList;
    }

    public List<VideoLecture> getLectureList() {
        return Collections.unmodifiableList(lectureList);
    }
}
