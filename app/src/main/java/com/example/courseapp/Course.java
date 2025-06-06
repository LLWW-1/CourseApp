package com.example.courseapp;

import java.io.Serializable;

public class Course implements Serializable {
    private String courseName;
    private String teacherName;
    private int startTime;  //整数表示节数
    private int endTime;
    private String location;
    private int weekday;

    public Course(String courseName, String teacherName, int startTime,
                  int endTime, String location, int weekday) {
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.weekday = weekday;
    }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public int getStartTime() { return startTime; }
    public void setStartTime(int startTime) { this.startTime = startTime; }
    public int getEndTime() { return endTime; }
    public void setEndTime(int endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getWeekday() { return weekday; }
    public void setWeekday(int weekday) { this.weekday = weekday; }
}