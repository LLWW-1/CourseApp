package com.example.courseapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private EditText courseNameEditText;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        dbHelper = new DBHelper(this);
        courseNameEditText = findViewById(R.id.course_name_edit_text);
        deleteButton = findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(v -> {
            String courseName = courseNameEditText.getText().toString();
            if (courseName.isEmpty()) {
                Toast.makeText(this, "请输入课程名称", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String whereClause = "course_name =?";
                String[] whereArgs = {courseName};

                int rowsDeleted = db.delete("Course", whereClause, whereArgs);
                db.close();

                if (rowsDeleted > 0) {
                    Toast.makeText(this, "课程删除成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(DeleteActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "未找到该课程或无权限删除", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "删除失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}