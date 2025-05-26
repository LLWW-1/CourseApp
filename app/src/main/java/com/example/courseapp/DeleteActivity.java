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
    private EditText courseIdEditText;
    private Button deleteButton;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        currentUserId = getIntent().getIntExtra("user_id", -1);
        dbHelper = new DBHelper(this);
        courseIdEditText = findViewById(R.id.course_id_edit_text);
        deleteButton = findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseIdStr = courseIdEditText.getText().toString();
                if (courseIdStr.isEmpty()) {
                    Toast.makeText(DeleteActivity.this, "请输入课程ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int courseId = Integer.parseInt(courseIdStr);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    String whereClause = "id =? AND user_id =?";
                    String[] whereArgs = {String.valueOf(courseId), String.valueOf(currentUserId)};

                    int rowsDeleted = db.delete("Course", whereClause, whereArgs);
                    db.close();

                    if (rowsDeleted > 0) {
                        Toast.makeText(DeleteActivity.this, "课程删除成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DeleteActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(DeleteActivity.this, "未找到该课程ID或无权限删除", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(DeleteActivity.this, "请输入有效的课程ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}