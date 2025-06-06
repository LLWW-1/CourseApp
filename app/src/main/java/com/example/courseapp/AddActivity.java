package com.example.courseapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private EditText courseNameEditText, teacherNameEditText, locationEditText;
    private Spinner weekdaySpinner, startTimeSpinner, endTimeSpinner;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(this);
        initViews();
        setupSpinners();
        setupButton();
    }

    private void initViews() {
        courseNameEditText = findViewById(R.id.course_name_edit_text);
        teacherNameEditText = findViewById(R.id.teacher_name_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        weekdaySpinner = findViewById(R.id.weekday_spinner);
        startTimeSpinner = findViewById(R.id.start_time_spinner);
        endTimeSpinner = findViewById(R.id.end_time_spinner);
        addButton = findViewById(R.id.add_button);
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

    private void setupButton() {
        addButton.setOnClickListener(v -> {
            String courseName = courseNameEditText.getText().toString();
            String teacherName = teacherNameEditText.getText().toString();
            int startTime = (int) startTimeSpinner.getSelectedItem();
            int endTime = (int) endTimeSpinner.getSelectedItem();
            String location = locationEditText.getText().toString();
            int weekday = weekdaySpinner.getSelectedItemPosition() + 1;

            if (courseName.isEmpty()) {
                Toast.makeText(AddActivity.this, "课程名称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (endTime < startTime) {
                Toast.makeText(AddActivity.this, "结束节数不能小于开始节数", Toast.LENGTH_SHORT).show();
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

            try {
                db.insert("Course", null, values);
                Toast.makeText(AddActivity.this, "课程添加成功", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(AddActivity.this, "添加失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                db.close();
            }
        });
    }
}