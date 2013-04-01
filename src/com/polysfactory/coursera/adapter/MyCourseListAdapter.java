
package com.polysfactory.coursera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.polysfactory.coursera.R;
import com.polysfactory.coursera.api.DownloadImagesTask;
import com.polysfactory.coursera.model.MyListItem;

public class MyCourseListAdapter extends BaseAdapter {

    final Context mContext;

    final MyListItem[] mMyCourses;

    final LayoutInflater mInflater;

    public MyCourseListAdapter(Context context, MyListItem[] myCourses) {
        mContext = context;
        mMyCourses = myCourses;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mMyCourses.length;
    }

    @Override
    public Object getItem(int position) {
        return mMyCourses[position];
    }

    @Override
    public long getItemId(int position) {
        return mMyCourses[position].id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.my_course_item, null);
            viewHolder = new ViewHolder();
            viewHolder.courseNameTextView = (TextView) convertView.findViewById(R.id.course_name);
            viewHolder.universityNameTextView = (TextView) convertView
                    .findViewById(R.id.course_university_name);
            viewHolder.courseIconImageView = (ImageView) convertView.findViewById(R.id.course_icon);
            viewHolder.loadingImageView = (ProgressBar) convertView
                    .findViewById(R.id.image_loading);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.courseNameTextView.setText(mMyCourses[position].name);
        viewHolder.universityNameTextView.setText(mMyCourses[position].getUniversityName());
        viewHolder.courseIconImageView.setTag(mMyCourses[position].smallIcon);
        DownloadImagesTask downloadImagesTask = new DownloadImagesTask(mContext,
                viewHolder.courseIconImageView, viewHolder.loadingImageView);
        downloadImagesTask.load(mMyCourses[position].smallIcon);
        return convertView;
    }

    static class ViewHolder {
        TextView courseNameTextView;
        TextView universityNameTextView;
        ImageView courseIconImageView;
        ProgressBar loadingImageView;
    }

}
