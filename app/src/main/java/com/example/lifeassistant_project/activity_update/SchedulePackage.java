package com.example.lifeassistant_project.activity_update;

public class SchedulePackage {
    private int id, year, month, day, start_time, end_time;
    private String todo, user;
    private int requestAction; // request 需要執行的行為
    // 0 = 新增, 1 = 刪除, 2 = 修改, 3 = 查詢, 4 = Debug.

    private ScheduleDate startDate, endDate;

    // start_time = hours of the date, and end_time is that the difference between the start_time
    public SchedulePackage()
    {
        this.id = 0;
        this.todo = "";
        this.requestAction = 4;
        this.year = 1970;
        this.month = 1;
        this.day = 1;
        this.start_time = 0;
        this.end_time = 0;
        this.user = "Null";
    }

    public SchedulePackage(int id, String todo, int requestAction, int s_year, int s_month, int s_day, int s_hour, int s_minute, int e_year , int e_month, int e_day, int e_hour, int e_minute)
    {
        this.id = id;
        this.todo = todo;
        this.requestAction = requestAction;
        this.setStartDateInFormat(s_year, s_month, s_day, s_hour, s_minute);
        this.setEndDateInFormat(e_year, e_month, e_day, e_hour, e_minute);
        this.user = "Null";
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
        this.user = "Null";
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
        int detTime = 0;
        int[] monthDay = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


        detTime += hour;
        detTime += (day - (this.day)) * 24;
        if(month != this.month)
        {
            if(month > this.month)
            {
                if((this.year % 4 == 0 && this.year % 100 != 0) || (this.year % 400 == 0))
                    monthDay[1] += 1;
                for(int ptr = this.month - 1; ptr < (month - 1); ptr++)
                {
                    detTime += (monthDay[ptr] * 24);
                }
            }
            else
            {
                if(((this.year + 1) % 4 == 0 && (this.year + 1) % 100 != 0) || ((this.year + 1) % 400 == 0))
                    monthDay[1] += 1;
                for(int ptr = month - 1 ; ptr < (this.month - 1); ptr++)
                {
                    detTime -= (monthDay[ptr] * 24);
                }
            }
        }

        if(year > this.year)
        {
            for(int ptr = this.year ; ptr < year ; ptr++)
            {
                if((ptr % 4 == 0 && ptr % 100 != 0) || (ptr % 400 == 0))
                {
                    if((ptr == this.year && this.month <= 2) || (ptr == year && month > 2))
                        detTime += 366 * 24;
                    else if(ptr != this.year && ptr != year)
                        detTime += 366 * 24;
                    else
                        detTime += 365 * 24;
                }
                else
                    detTime += 365 * 24;
            }
        }
        else if(year < this.year)
        {
            this.end_time = -1;
            System.out.println("Set Time Error! You need to set StartDate before set EndDate.");
            return;
        }

        detTime = detTime * 100 + minute;

        this.end_time = detTime;
    }

    public ScheduleDate getStartDateInFormat()
    {
        if(this.startDate == null)
        {
            ScheduleDate result = new ScheduleDate();

            result.setYear(this.year);
            result.setMonth(this.month);
            result.setDay(this.day);
            result.setHour(this.start_time / 100);
            result.setMinute(this.start_time % 100);

            this.startDate = result;
        }
        return this.startDate;
    }

    public ScheduleDate getEndDateInFormat()
    {
        if(this.endDate == null)
        {
            ScheduleDate result = new ScheduleDate();

            result.setMinute(this.end_time % 100);
            int tempHour = this.end_time / 100;
            result.setHour(tempHour % 24);

            result.setDay(this.day);
            result.setMonth(this.month);
            result.setYear(this.year);

            int[] monthDay = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            if((result.getYear() % 4 == 0 && result.getYear() % 100 != 0) || (result.getYear() % 400 == 0))
                monthDay[1] = 29;

            result.setDay(result.getDay() + (tempHour / 24));
            while(result.getDay() > monthDay[result.getMonth() - 1])
            {
                result.setDay(result.getDay() - monthDay[result.getMonth() - 1]);
                result.setMonth(result.getMonth() + 1);
                if(result.getMonth() > 12)
                {
                    result.setYear(result.getYear() + 1);
                    if((result.getYear() % 4 == 0 && result.getYear() % 100 != 0) || (result.getYear() % 400 == 0))
                        monthDay[1] = 29;
                    else
                        monthDay[1] = 28;
                    result.setMonth(1);
                }
            }

            this.endDate = result;
        }
        return this.endDate;
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

    public String getUser(){ return this.user;}

    public void setUser(String user){this.user = user;}

}

