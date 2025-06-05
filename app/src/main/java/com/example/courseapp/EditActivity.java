package com.example.courseapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private EditText courseNameEditText, teacherNameEditText, locationEditText;
    private Spinner weekdaySpinner, startTimeSpinner, endTimeSpinner;
    private Button modifyButton, deleteButton;
    private String originalCourseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dbHelper = new DBHelper(this);
        initViews();
        setupSpinners();
        loadCourseData();
        setupButtons();
    }

    private void initViews() {
        courseNameEditText = findViewById(R.id.course_name_edit_text);
        teacherNameEditText = findViewById(R.id.teacher_name_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        weekdaySpinner = findViewById(R.id.weekday_spinner);
        startTimeSpinner = findViewById(R.id.start_time_spinner);
        endTimeSpinner = findViewById(R.id.end_time_spinner);
        modifyButton = findViewById(R.id.modify_button);
        deleteButton = findViewById(R.id.delete_button);
    }

    private void setupSpinners() {
        // 星期选择器
        ArrayAdapter<CharSequence> weekdayAdapter = ArrayAdapter.createFromResource(this,
                R.array.weekdays, android.R.layout.simple_spinner_item);
        weekdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekdaySpinner.setAdapter(weekdayAdapter);

        // 节数选择器 (1-12节)
        List<Integer> timeSlots = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            timeSlots.add(i);
        }

        ArrayAdapter<Integer> timeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, timeSlots);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(timeAdapter);
        endTimeSpinner.setAdapter(timeAdapter);
    }

    private void loadCourseData() {
        Intent intent = getIntent();
        originalCourseName = intent.getStringExtra("course_name");

        courseNameEditText.setText(originalCourseName);
        teacherNameEditText.setText(intent.getStringExtra("teacher_name"));
        locationEditText.setText(intent.getStringExtra("location"));

        // 设置星期
        int weekday = intent.getIntExtra("weekday", 1);
        weekdaySpinner.setSelection(weekday - 1);

        // 设置节数
        int startTime = intent.getIntExtra("start_time", 1);
        int endTime = intent.getIntExtra("end_time", 1);
        startTimeSpinner.setSelection(startTime - 1);
        endTimeSpinner.setSelection(endTime - 1);
    }

    private void setupButtons() {
        modifyButton.setOnClickListener(v -> modifyCourse());
        deleteButton.setOnClickListener(v -> deleteCourse());
    }

    private void modifyCourse() {
        String newCourseName = courseNameEditText.getText().toString();
        String teacherName = teacherNameEditText.getText().toString();
        int startTime = (int) startTimeSpinner.getSelectedItem();
        int endTime = (int) endTimeSpinner.getSelectedItem();
        String location = locationEditText.getText().toString();
        int weekday = weekdaySpinner.getSelectedItemPosition() + 1;

        if (newCourseName.isEmpty()) {
            Toast.makeText(this, "课程名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endTime < startTime) {
            Toast.makeText(this, "结束节数不能小于开始节数", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("course_name", newCourseName);
        values.put("teacher_name", teacherName);
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("location", location);
        values.put("weekday", weekday);

        int rows = db.update("Course", values, "course_name=?", new String[]{originalCourseName});
        db.close();

        if (rows > 0) {
            Toast.makeText(this, "课程修改成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCourse() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Course", "course_name=?", new String[]{originalCourseName});
        db.close();

        if (rows > 0) {
            Toast.makeText(this, "课程删除成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }
}