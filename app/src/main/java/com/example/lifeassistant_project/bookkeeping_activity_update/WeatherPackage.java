package com.example.lifeassistant_project.bookkeeping_activity_update;

public class WeatherPackage
{
    private int month;
    private int day;
    private int max_temperature;
    private int min_temperature;
    private String city, period;

    WeatherPackage()
    {
        this.month = 0;
        this.day = 0;
        this.max_temperature = 0;
        this.min_temperature = 0;
        this.city = "";
        this.period = "";
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

    public int getMax_temperature() {
        return max_temperature;
    }

    public void setMax_temperature(int max_temperature) {
        this.max_temperature = max_temperature;
    }

    public int getMin_temperature() {
        return min_temperature;
    }

    public void setMin_temperature(int min_temperature) {
        this.min_temperature = min_temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

}
