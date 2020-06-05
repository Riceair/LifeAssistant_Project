package com.example.lifeassistant_project.bookkeeping_activity_update;

public class AccountPackage
{
    private int ID, money, year, month, day;
    private String item, detail;
    private boolean type; // True for 收入

    public AccountPackage(int ID, int money, int year, int month, int day, String item, String detail, boolean type)
    {
        this.ID = ID;
        this.money = money;
        this.year = year;
        this.month = month;
        this.day = day;
        this.item = item;
        this.detail = detail;
        this.type = type;
    }
    AccountPackage()
    {
        this.ID = 0;
        this.money = 0;
        this.year = 0;
        this.month = 0;
        this.day = 0;
        this.item = "";
        this.detail = "";
        this.type = false;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
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

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
