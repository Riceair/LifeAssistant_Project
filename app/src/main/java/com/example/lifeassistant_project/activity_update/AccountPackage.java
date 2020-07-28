package com.example.lifeassistant_project.activity_update;

public class AccountPackage
{
    private int ID, money, year, month, day;
    private String item, detail, receipt, note, user;
    private boolean type; // True for 支出
    private int requestAction; // request 需要執行的行為
    // 0 = 新增, 1 = 刪除, 2 = 修改, 3 = 查詢, 4 = Debug.

    public int getRequestAction() {
        return requestAction;
    }

    public void setRequestAction(int requestAction) {
        this.requestAction = requestAction;
    }

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
        this.receipt = "";
        this.note = "";
        this.requestAction = 4;
        this.user = "Null";
    }
    public AccountPackage()
    {
        this.ID = 0;
        this.money = 0;
        this.year = 0;
        this.month = 0;
        this.day = 0;
        this.item = "";
        this.detail = "";
        this.type = false;
        this.receipt = "";
        this.note = "";
        this.requestAction = 4;
        this.user = "Null";
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

    public String getReceipt() { return receipt; }

    public void setReceipt(String receipt) { this.receipt = receipt; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }

}
