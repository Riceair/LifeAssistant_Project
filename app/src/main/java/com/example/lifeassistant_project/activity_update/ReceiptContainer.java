package com.example.lifeassistant_project.activity_update;

import java.util.ArrayList;

public class ReceiptContainer {
    public String year, month;
    public ArrayList<String> hitNumber;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public ArrayList<String> getHitNumber() {
        return hitNumber;
    }

    public void setHitNumber(ArrayList<String> hitNumber) {
        this.hitNumber = hitNumber;
    }


}
