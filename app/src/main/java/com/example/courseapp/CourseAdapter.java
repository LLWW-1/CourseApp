package com.example.courseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CourseAdapter extends ArrayAdapter<Course> {
    public CourseAdapter(Context context, List<Course> courses) {
        super(context, 0, courses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView textView1 = convertView.findViewById(android.R.id.text1);
        TextView textView2 = convertView.findViewById(android.R.id.text2);

        Course course = getItem(position);
        if (course != null) {
            textView1.setText(course.getCourseName());
            textView2.setText("教师: " + course.getTeacherName() + " 时间: " + course.getStartTime() + "-" + course.getEndTime() + " 地点: " + course.getLocation());
        }

        return convertView;
    }
}