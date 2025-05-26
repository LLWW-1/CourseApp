package com.example.courseapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private ListView courseListView;
    private CourseAdapter courseAdapter;
    private List<Course> todayCourses = new ArrayList<>();
    private String currentUsername;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 先初始化DBHelper
        dbHelper = new DBHelper(this);

        // 2. 获取并检查用户名
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(this, "用户名无效，请重新登录", Toast.LENGTH_SHORT).show();
            finish(); // 返回登录页面
            return;
        }

        // 3. 获取用户ID
        currentUserId = getUserId(currentUsername);
        if (currentUserId == -1) {
            Toast.makeText(this, "用户不存在，请重新登录", Toast.LENGTH_SHORT).show();
            finish(); // 返回登录页面
            return;
        }

        // 4. 设置欢迎文本
        TextView welcomeTextView = findViewById(R.id.welcome_text_view);
        welcomeTextView.setText("欢迎回来，" + currentUsername);

        // 5. 初始化ListView
        courseListView = findViewById(R.id.course_list_view);

        // 6. 设置按钮事件
        setupButtons();

        // 7. 加载今日课程（添加异常处理）
        try {
            loadTodayCourses();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "加载课程失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtons() {
        Button viewCourseButton = findViewById(R.id.view_course_button);
        Button addCourseButton = findViewById(R.id.add_course_button);
        Button modifyCourseButton = findViewById(R.id.modify_course_button);
        Button deleteCourseButton = findViewById(R.id.delete_course_button);
        Button logoutButton = findViewById(R.id.logout_button);

        viewCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
        });

        addCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
        });

        modifyCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChangeActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
        });

        deleteCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DeleteActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    private int getUserId(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"id"};
        String selection = "username =?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query("User", columns, selection, selectionArgs, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    private void loadTodayCourses() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(calendar.getTime());

        // 注意：这里查询的是"正在进行"的课程，可能导致没有数据
        // 如果需要显示全天课程，应修改查询条件
        String[] columns = {"id", "course_name", "teacher_name", "start_time", "end_time", "location"};
        String selection = "user_id =? AND weekday =? AND start_time <=? AND end_time >=?";
        String[] selectionArgs = {String.valueOf(currentUserId), String.valueOf(today), currentTime, currentTime};
        Cursor cursor = db.query("Course", columns, selection, selectionArgs, null, null, "start_time");

        todayCourses.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String courseName = cursor.getString(1);
            String teacherName = cursor.getString(2);
            String startTime = cursor.getString(3);
            String endTime = cursor.getString(4);
            String location = cursor.getString(5);
            Course course = new Course(id, currentUserId, courseName, teacherName, startTime, endTime, location, today);
            todayCourses.add(course);
        }
        cursor.close();
        db.close();

        // 检查是否有课程数据
        if (todayCourses.isEmpty()) {
            Toast.makeText(this, "今日没有正在进行的课程", Toast.LENGTH_SHORT).show();
        }

        courseAdapter = new CourseAdapter(this, todayCourses);
        courseListView.setAdapter(courseAdapter);
    }
}