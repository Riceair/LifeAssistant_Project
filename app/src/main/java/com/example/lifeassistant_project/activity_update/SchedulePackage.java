package com.example.lifeassistant_project.activity_update;

public class SchedulePackage {
    private int id, year, month, day, start_time, end_time;
    private String todo;
    private int requestAction; // request 需要執行的行為
    // 0 = 新增, 1 = 刪除, 2 = 修改, 3 = 查詢, 4 = Debug.

    // start_time = hours of the date, and end_time is that the difference between the start_time
    public SchedulePackage(int id, String todo, int requestAction, int s_year, int s_month, int s_day, int s_hour, int s_minute, int e_year , int e_month, int e_day, int e_hour, int e_minute)
    {
        this.id = id;
        this.todo = todo;
        this.requestAction = requestAction;
        this.setStartDateInFormat(s_year, s_month, s_day, s_hour, s_minute);
        this.setEndDateInFormat(e_year, e_month, e_day, e_hour, e_minute);
    }

    public SchedulePackage(int id, String todo, int year, int month, int day, int start_time, int end_time)
    {
        this.id = id;
        this.todo = todo;
        this.year = year;
        this.month = month;
        this.day = day;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public void setStartDateInFormat(int year, int month, int day, int hour, int minute)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.start_time = hour * 100 + minute;
    }

    public void setEndDateInFormat(int year, int month, int day, int hour, int minute)
    {
        int detHour = 0;
        int[] monthDay = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


        detHour += hour - (this.start_time / 100);
        detHour += (day - (this.day)) * 24;
        if(month != this.month)
        {
            if(month > this.month)
            {
                if((this.year % 4 == 0 && this.year % 4 != 0) || (this.year % 400 == 0))
                    monthDay[1] += 1;
                for(int ptr = this.month - 1; ptr < (month - 1); ptr++)
                {
                    detHour += (monthDay[ptr] * 24);
                }
            }
            else
            {
                if((year % 4 == 0 && year % 4 != 0) || (year % 400 == 0))
                    monthDay[1] += 1;
                for(int ptr = month - 1 ; ptr < (this.month - 1); ptr++)
                {
                    detHour -= (monthDay[ptr] * 24);
                }
            }
        }

        if(year > this.year)
        {
            for(int ptr = this.year ; ptr < year ; ptr++)
            {
                if((ptr % 4 == 0 && ptr % 4 != 0) || (ptr % 400 == 0))
                    detHour += 366 * 24;
                else
                    detHour += 365 * 24;
            }
        }
        else
        {
            this.end_time = -1;
            System.out.println("Set Time Error! You need to set StartDate before set EndDate.");
            return;
        }

        detHour = detHour * 100 + minute;

        this.end_time = detHour;
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

    public int getRequestAction() { return requestAction; }

    public void setRequestAction(int requestAction) { this.requestAction = requestAction; }

}