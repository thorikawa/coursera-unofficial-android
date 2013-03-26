
package com.polysfactory.coursera.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.polysfactory.coursera.R;
import com.polysfactory.coursera.model.VideoLecture;

public class VideoLectureListAdapter extends BaseAdapter {

    final Context mContext;

    final List<VideoLecture> mVideoLectures;

    final LayoutInflater mInflater;

    public VideoLectureListAdapter(Context context, List<VideoLecture> videoLectures) {
        mContext = context;
        mVideoLectures = videoLectures;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mVideoLectures.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoLectures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        viewHolder.videoLectureNameTextView.setText(mVideoLectures.get(position).title);
        return convertView;
    }

    static class ViewHolder {
        TextView videoLectureNameTextView;
    }

}
