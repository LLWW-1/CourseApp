package com.example.courseapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Collections;
import java.util.Comparator;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private ListView courseListView;
    private CourseAdapter courseAdapter;
    private List<Course> todayCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        courseListView = findViewById(R.id.course_list_view);
        setupButtons();
        loadTodayCourses();
    }

    private void setupButtons() {
        Button viewCourseButton = findViewById(R.id.view_course_button);
        Button addCourseButton = findViewById(R.id.add_course_button);
        Button modifyCourseButton = findViewById(R.id.modify_course_button);
        Button deleteCourseButton = findViewById(R.id.delete_course_button);

        viewCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewActivity.class);
            startActivity(intent);
        });

        addCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        });

        modifyCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChangeActivity.class);
            startActivity(intent);
        });

        deleteCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DeleteActivity.class);
            startActivity(intent);
        });
    }

    private void loadTodayCourses() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        if (today == Calendar.SUNDAY) {
            today = 7;
        } else {
            today = today - 1;
        }

        String[] columns = {"course_name", "teacher_name", "start_time", "end_time", "location", "weekday"};
        String selection = "weekday =?";
        String[] selectionArgs = {String.valueOf(today)};
        Cursor cursor = db.query("Course", columns, selection, selectionArgs, null, null, "start_time");

        todayCourses.clear();
        while (cursor.moveToNext()) {
            String courseName = cursor.getString(0);
            String teacherName = cursor.getString(1);
            String startTime = cursor.getString(2);
            String endTime = cursor.getString(3);
            String location = cursor.getString(4);
            int weekday = cursor.getInt(5);
            Course course = new Course("", courseName, teacherName, startTime, endTime, location, weekday); // 第一个参数设为空
            todayCourses.add(course);
        }
        cursor.close();
        db.close();

        // 对todayCourses列表按照课程开始时间进行排序
        Collections.sort(todayCourses, new Comparator<Course>() {
            @Override
            public int compare(Course course1, Course course2) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                try {
                    return sdf.parse(course1.getStartTime()).compareTo(sdf.parse(course2.getStartTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        if (todayCourses.isEmpty()) {
            Toast.makeText(this, "今日没有课程", Toast.LENGTH_SHORT).show();
        }

        courseAdapter = new CourseAdapter(this, todayCourses);
        courseListView.setAdapter(courseAdapter);
    }
}