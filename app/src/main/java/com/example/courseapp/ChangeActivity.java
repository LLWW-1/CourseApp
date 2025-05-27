package com.example.courseapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private EditText courseNameEditText, teacherNameEditText, startTimeEditText, endTimeEditText, locationEditText;
    private Spinner weekdaySpinner;
    private Button changeButton;
    private String currentUsername;
    private String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        currentUsername = getIntent().getStringExtra("username");
        courseName = getIntent().getStringExtra("course_name");

        dbHelper = new DBHelper(this);
        courseNameEditText = findViewById(R.id.course_name_edit_text);
        teacherNameEditText = findViewById(R.id.teacher_name_edit_text);
        startTimeEditText = findViewById(R.id.start_time_edit_text);
        endTimeEditText = findViewById(R.id.end_time_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        weekdaySpinner = findViewById(R.id.weekday_spinner);
        changeButton = findViewById(R.id.change_button);

        if (courseName != null) {
            loadCourseDetails();
        }

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = courseNameEditText.getText().toString();
                String teacherName = teacherNameEditText.getText().toString();
                String startTime = startTimeEditText.getText().toString();
                String endTime = endTimeEditText.getText().toString();
                String location = locationEditText.getText().toString();
                int weekday = weekdaySpinner.getSelectedItemPosition() + 1;

                if (courseName.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    Toast.makeText(ChangeActivity.this, "课程名称、开始时间和结束时间不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("course_name", courseName);
                values.put("teacher_name", teacherName);
                values.put("start_time", startTime);
                values.put("end_time", endTime);
                values.put("location", location);
                values.put("weekday", weekday);

                String whereClause = "course_name =? AND username =?";
                String[] whereArgs = {courseName, currentUsername};

                int rowsAffected = db.update("Course", values, whereClause, whereArgs);
                db.close();

                if (rowsAffected > 0) {
                    Toast.makeText(ChangeActivity.this, "课程修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ChangeActivity.this,MainActivity.class);
                    intent.putExtra("username",currentUsername);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ChangeActivity.this, "未找到该课程或无权限修改", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCourseDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"course_name", "teacher_name", "start_time", "end_time", "location", "weekday"};
        String selection = "course_name =? AND username =?";
        String[] selectionArgs = {courseName, currentUsername};
        Cursor cursor = db.query("Course", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String courseName = cursor.getString(0);
            String teacherName = cursor.getString(1);
            String startTime = cursor.getString(2);
            String endTime = cursor.getString(3);
            String location = cursor.getString(4);
            int weekday = cursor.getInt(5);

            courseNameEditText.setText(courseName);
            teacherNameEditText.setText(teacherName);
            startTimeEditText.setText(startTime);
            endTimeEditText.setText(endTime);
            locationEditText.setText(location);
            weekdaySpinner.setSelection(weekday - 1);
        } else {
            Toast.makeText(this, "未找到该课程或无权限查看", Toast.LENGTH_SHORT).show();
            finish();
        }

        cursor.close();
        db.close();
    }
}