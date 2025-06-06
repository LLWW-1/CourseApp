package com.example.courseapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "course.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCourseTable = "CREATE TABLE Course (" +
                "course_name TEXT PRIMARY KEY NOT NULL," +
                "teacher_name TEXT," +
                "start_time INTEGER," +
                "end_time INTEGER," +
                "location TEXT," +
                "weekday INTEGER)";

        db.execSQL(createCourseTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Course");
        onCreate(db);
    }
}