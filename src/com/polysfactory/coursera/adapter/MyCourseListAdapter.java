
package com.polysfactory.coursera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.polysfactory.coursera.R;
import com.polysfactory.coursera.model.Course;

public class MyCourseListAdapter extends BaseAdapter {

    final Context mContext;

    final Course[] mMyCourses;

    final LayoutInflater mInflater;

    public MyCourseListAdapter(Context context, Course[] myCourses) {
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.my_course_item, null);
        }
        TextView courseName = (TextView) convertView.findViewById(R.id.course_name);
        TextView universityName = (TextView) convertView.findViewById(R.id.course_university_name);
        courseName.setText(mMyCourses[position].name);
        universityName.setText(mMyCourses[position].getUniversityName());
        return convertView;
    }
}
