package com.example.courseapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private EditText courseNameEditText, teacherNameEditText, startTimeEditText, endTimeEditText, locationEditText;
    private Spinner weekdaySpinner;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(this);
        courseNameEditText = findViewById(R.id.course_name_edit_text);
        teacherNameEditText = findViewById(R.id.teacher_name_edit_text);
        startTimeEditText = findViewById(R.id.start_time_edit_text);
        endTimeEditText = findViewById(R.id.end_time_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        weekdaySpinner = findViewById(R.id.weekday_spinner);
        addButton = findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = courseNameEditText.getText().toString();
                String teacherName = teacherNameEditText.getText().toString();
                String startTime = startTimeEditText.getText().toString();
                String endTime = endTimeEditText.getText().toString();
                String location = locationEditText.getText().toString();
                int weekday = weekdaySpinner.getSelectedItemPosition() + 1;

                if (courseName.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    Toast.makeText(AddActivity.this, "课程名称、开始时间和结束时间不能为空", Toast.LENGTH_SHORT).show();
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
                db.insert("Course", null, values);
                db.close();

                Toast.makeText(AddActivity.this, "课程添加成功", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AddActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}