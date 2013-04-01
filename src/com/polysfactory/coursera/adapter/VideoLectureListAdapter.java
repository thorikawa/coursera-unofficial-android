
package com.polysfactory.coursera.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.polysfactory.coursera.R;
import com.polysfactory.coursera.model.VideoLecture;
import com.polysfactory.coursera.model.VideoLectureGroup;

public class VideoLectureListAdapter extends BaseExpandableListAdapter {

    final Context mContext;

    final List<VideoLectureGroup> mVideoLectureGroups;

    final LayoutInflater mInflater;

    public VideoLectureListAdapter(Context context, List<VideoLectureGroup> videoLectures) {
        mContext = context;
        mVideoLectureGroups = videoLectures;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        TextView videoLectureNameTextView;
    }

    @Override
    public int getGroupCount() {
        return mVideoLectureGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mVideoLectureGroups.get(groupPosition).getLectureList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mVideoLectureGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mVideoLectureGroups.get(groupPosition).getLectureList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 1000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        VideoLectureGroup groupItem = (VideoLectureGroup) this.getGroup(groupPosition);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.video_lecture_group, null);
            viewHolder = new ViewHolder();
            viewHolder.videoLectureNameTextView = (TextView) convertView
                    .findViewById(R.id.video_lecture_group_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.videoLectureNameTextView.setText(groupItem.groupTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        VideoLecture childItem = (VideoLecture) this.getChild(groupPosition, childPosition);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.video_lecture_item, null);
            viewHolder = new ViewHolder();
            viewHolder.videoLectureNameTextView = (TextView) convertView
                    .findViewById(R.id.video_lecture_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.videoLectureNameTextView.setText(childItem.getTitle());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
