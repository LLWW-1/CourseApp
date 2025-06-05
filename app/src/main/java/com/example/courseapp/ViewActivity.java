package com.example.courseapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        dbHelper = new DBHelper(this);
        displayCourseTable();
    }

    private void displayCourseTable() {
        TableLayout tableLayout = findViewById(R.id.course_table);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 清空表格
        tableLayout.removeAllViews();

        // 创建表头行
        TableRow headerRow = new TableRow(this);
        addHeaderCell(headerRow, "节数/星期");

        String[] weekdays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (String weekday : weekdays) {
            addHeaderCell(headerRow, weekday);
        }
        tableLayout.addView(headerRow);

        // 每天12节课
        for (int timeSlot = 1; timeSlot <= 12; timeSlot++) {
            TableRow timeRow = new TableRow(this);

            // 添加时间单元格
            addTimeCell(timeRow, timeSlot + "节");

            for (String weekday : weekdays) {
                String[] columns = {"course_name", "teacher_name", "location", "start_time", "end_time"};
                String selection = "weekday = ? AND start_time <= ? AND end_time >= ?";
                String[] selectionArgs = {
                        String.valueOf(getWeekdayNumber(weekday)),
                        String.valueOf(timeSlot),
                        String.valueOf(timeSlot)
                };

                Cursor cursor = db.query("Course", columns, selection, selectionArgs, null, null, null);

                TextView courseCell = new TextView(this);
                if (cursor.moveToFirst()) {
                    String courseName = cursor.getString(0);
                    String teacherName = cursor.getString(1);
                    String location = cursor.getString(2);
                    int startTime = cursor.getInt(3);
                    int endTime = cursor.getInt(4);

                    String courseText = courseName + "\n" + teacherName + "\n" + location;
                    if (endTime > startTime) {
                        courseText += "\n(" + startTime + "-" + endTime + "节)";
                    }

                    courseCell.setText(courseText);
                    courseCell.setBackgroundColor(Color.parseColor("#E3F2FD")); // 浅蓝色背景
                } else {
                    courseCell.setText("");
                    courseCell.setBackgroundColor(Color.WHITE);
                }

                setCourseCellStyle(courseCell);
                timeRow.addView(courseCell);
                cursor.close();
            }
            tableLayout.addView(timeRow);
        }
        db.close();
    }

    private void addHeaderCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setPadding(16, 16, 16, 16);
        textView.setBackgroundColor(Color.parseColor("#3F51B5")); // 深蓝色背景
        textView.setTextColor(Color.WHITE);
        row.addView(textView);
    }

    private void addTimeCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setPadding(16, 8, 16, 8);
        textView.setBackgroundColor(Color.parseColor("#9E9E9E")); // 灰色背景
        textView.setTextColor(Color.WHITE);
        row.addView(textView);
    }

    private void setCourseCellStyle(TextView textView) {
        textView.setTextSize(14);
        textView.setPadding(16, 8, 16, 8);
        textView.setSingleLine(false);
        textView.setMaxLines(4);
    }

    private int getWeekdayNumber(String weekday) {
        switch (weekday) {
            case "周一": return 1;
            case "周二": return 2;
            case "周三": return 3;
            case "周四": return 4;
            case "周五": return 5;
            case "周六": return 6;
            case "周日": return 7;
            default: return 0;
        }
    }
}