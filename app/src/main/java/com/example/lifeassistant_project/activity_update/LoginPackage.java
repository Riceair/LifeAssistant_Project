package com.example.lifeassistant_project.activity_update;

public class LoginPackage {
    private String name;
    private String password;
    static private String userKey = null;
    static private boolean isLogin = false;

    static public void setIsLogin(boolean t) {isLogin = t;}

    static public boolean getIsLogin() { return isLogin; }

    static public void setUserKey(String message){userKey = message;}

    static public String getUserKey(){ return userKey; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
