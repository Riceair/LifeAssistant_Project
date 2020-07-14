package com.example.lifeassistant_project.bookkeeping_activity_update;

public class SchedulePackage {
    private int id, year, month, day, start_time, end_time;

    // start_time = hours of the date, and end_time is that the difference between the start_time
    public SchedulePackage()
    {

    }

    public  SchedulePackage(int id, String todo, int year, int month, int day, int start_time, int end_time)
    {
        this.id = id;
        this.todo = todo;
        this.year = year;
        this.month = month;
        this.day = day;
        this.start_time = start_time;
        this.end_time = end_time;
    }
    public int getID() {
        return id;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    private String todo;

}
