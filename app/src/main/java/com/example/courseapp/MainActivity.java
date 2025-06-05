package com.example.courseapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import java.util.Calendar;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
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
        setupCourseListView();
    }

    private void setupButtons() {
        Button viewCourseButton = findViewById(R.id.view_course_button);
        Button addCourseButton = findViewById(R.id.add_course_button);

        viewCourseButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewActivity.class));
        });

        addCourseButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AddActivity.class));
        });
    }

    private void loadTodayCourses() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        // 转换为1-7(周一至周日)
        today = today == Calendar.SUNDAY ? 7 : today - 1;

        String[] columns = {"course_name", "teacher_name", "start_time", "end_time", "location", "weekday"};
        String selection = "weekday =?";
        String[] selectionArgs = {String.valueOf(today)};

        Cursor cursor = db.query("Course", columns, selection, selectionArgs, null, null, "start_time");

        todayCourses.clear();
        while (cursor.moveToNext()) {
            todayCourses.add(new Course(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getInt(5)
            ));
        }

        cursor.close();
        db.close();

        if (todayCourses.isEmpty()) {
            Toast.makeText(this, "今日没有课程", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupCourseListView() {
        courseAdapter = new CourseAdapter(this, todayCourses);
        courseListView.setAdapter(courseAdapter);

        courseListView.setOnItemClickListener((parent, view, position, id) -> {
            Course course = todayCourses.get(position);
            Intent intent = new Intent(this, EditActivity.class);

            intent.putExtra("course_name", course.getCourseName());
            intent.putExtra("teacher_name", course.getTeacherName());
            intent.putExtra("start_time", course.getStartTime());
            intent.putExtra("end_time", course.getEndTime());
            intent.putExtra("location", course.getLocation());
            intent.putExtra("weekday", course.getWeekday());

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodayCourses();
        courseAdapter.notifyDataSetChanged();
    }
}