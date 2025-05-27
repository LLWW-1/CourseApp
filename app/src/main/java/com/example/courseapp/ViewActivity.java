package com.example.courseapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        currentUsername = getIntent().getStringExtra("username");
        dbHelper = new DBHelper(this);
        displayCourseTable();
    }

    private void displayCourseTable() {
        TableLayout tableLayout = findViewById(R.id.course_table);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] weekdays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (String weekday : weekdays) {
            // 为每个星期创建一个表头行
            TableRow headerRow = new TableRow(this);
            TextView weekdayTextView = new TextView(this);
            weekdayTextView.setText(weekday);
            headerRow.addView(weekdayTextView);
            tableLayout.addView(headerRow);

            String[] columns = {"course_name", "teacher_name", "start_time", "end_time", "location"};
            String selection = "username =? AND weekday =?";
            String[] selectionArgs = {currentUsername, String.valueOf(getWeekdayNumber(weekday))};
            Cursor cursor = db.query("Course", columns, selection, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                // 为每门课程创建一个新行
                TableRow courseRow = new TableRow(this);
                String courseName = cursor.getString(0);
                String teacherName = cursor.getString(1);
                String startTime = cursor.getString(2);
                String endTime = cursor.getString(3);
                String location = cursor.getString(4);
                TextView courseTextView = new TextView(this);
                courseTextView.setText(courseName + "\n教师: " + teacherName + " 时间: " + startTime + "-" + endTime + " 地点: " + location);
                courseRow.addView(courseTextView);
                tableLayout.addView(courseRow);
            }
            cursor.close();
        }
        db.close();
    }

    private int getWeekdayNumber(String weekday) {
        switch (weekday) {
            case "周一":
                return 1;
            case "周二":
                return 2;
            case "周三":
                return 3;
            case "周四":
                return 4;
            case "周五":
                return 5;
            case "周六":
                return 6;
            case "周日":
                return 7;
            default:
                return 0;
        }
    }
}