package com.example.lifeassistant_project.activity_update.packages;

public class LoginPackage {
    private String name;
    private String password;

    public LoginPackage()
    {
        this.name = "Null";
        this.password = "Null";
    }

    public LoginPackage(String name, String password)
    {
        this.name = name;
        this.password = password;
    }

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
