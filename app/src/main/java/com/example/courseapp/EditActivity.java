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

public class EditActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private EditText courseNameEditText, teacherNameEditText, startTimeEditText, endTimeEditText, locationEditText;
    private Spinner weekdaySpinner;
    private Button modifyButton, deleteButton;
    private String originalCourseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        originalCourseName = getIntent().getStringExtra("course_name");
        if (originalCourseName == null) {
            Toast.makeText(this, "未获取到课程信息", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DBHelper(this);
        initViews();

        Intent intent = getIntent();
        if (intent != null) {
            // 获取原始课程名（用于标识要修改的课程）
            originalCourseName = intent.getStringExtra("course_name");

            if (originalCourseName == null) {
                Toast.makeText(this, "未获取到课程信息", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // 直接从Intent填充UI，而不是查询数据库
            courseNameEditText.setText(intent.getStringExtra("course_name"));
            teacherNameEditText.setText(intent.getStringExtra("teacher_name"));
            startTimeEditText.setText(intent.getStringExtra("start_time"));
            endTimeEditText.setText(intent.getStringExtra("end_time"));
            locationEditText.setText(intent.getStringExtra("location"));

            // 设置星期选择器
            int weekday = intent.getIntExtra("weekday", 1);
            weekdaySpinner.setSelection(weekday - 1);
        } else {
            Toast.makeText(this, "未获取到课程信息", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupButtonListeners();
    }

    private void initViews() {
        courseNameEditText = findViewById(R.id.course_name_edit_text);
        teacherNameEditText = findViewById(R.id.teacher_name_edit_text);
        startTimeEditText = findViewById(R.id.start_time_edit_text);
        endTimeEditText = findViewById(R.id.end_time_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        weekdaySpinner = findViewById(R.id.weekday_spinner);
        modifyButton = findViewById(R.id.modify_button);
        deleteButton = findViewById(R.id.delete_button);
    }

    private void setupButtonListeners() {
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCourse();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCourse();
            }
        });
    }

    private void modifyCourse() {
        String newCourseName = courseNameEditText.getText().toString();
        String teacherName = teacherNameEditText.getText().toString();
        String startTime = startTimeEditText.getText().toString();
        String endTime = endTimeEditText.getText().toString();
        String location = locationEditText.getText().toString();
        int weekday = weekdaySpinner.getSelectedItemPosition() + 1;

        if (newCourseName.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(EditActivity.this, "课程名称、开始时间和结束时间不能为空", Toast.LENGTH_SHORT).show();
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

        String whereClause = "course_name =?";
        String[] whereArgs = {originalCourseName};

        int rowsAffected = db.update("Course", values, whereClause, whereArgs);
        db.close();

        if (rowsAffected > 0) {
            Toast.makeText(EditActivity.this, "课程修改成功", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(EditActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(EditActivity.this, "修改失败，未找到该课程", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCourse() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "course_name =?";
        String[] whereArgs = {originalCourseName};

        int rowsAffected = db.delete("Course", whereClause, whereArgs);
        db.close();

        if (rowsAffected > 0) {
            Toast.makeText(EditActivity.this, "课程删除成功", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(EditActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(EditActivity.this, "删除失败，未找到该课程", Toast.LENGTH_SHORT).show();
        }
    }
}