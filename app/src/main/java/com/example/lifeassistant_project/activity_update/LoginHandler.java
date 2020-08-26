package com.example.lifeassistant_project.activity_update;

public class LoginHandler {
    static private String userName = "Null";
    static private String userKey = null;

    static private boolean isLogin = false;

    static public boolean Login(LoginPackage loginPackage)
    {
        String resultKey = LoginCertification(loginPackage);
        if(resultKey.equals("NO"))
        {
            System.out.println("Certification Fail."); //need to be conduct functionality.
            return false;
        }
        else if(resultKey.equals("FA"))
        {
            System.out.println("Connection Fail.");
            return false;
        }
        else {
            LoginHandler.userKey = resultKey;
            LoginHandler.userName = loginPackage.getName();
            LoginHandler.isLogin = true;
            DatabaseBehavior.synchronizeData();
            return true;
        }
    }

    static public boolean checkAuthorization()
    {
        if(LoginHandler.userKey == null)
            return false;

        ClientProgress client = new ClientProgress();
        LoginPackage checkPackage = new LoginPackage();
        checkPackage.setName("key");
        checkPackage.setPassword(LoginHandler.getUserKey());
        client.setLogin(checkPackage);
        Thread cThread = new Thread(client);
        cThread.start();

        synchronized (client)
        {
            try {
                client.wait(5000);
            }catch (Exception e)
            {
                System.out.println(e);
            }
        }

        String rcvMessage = client.getRcvUserKey();
        if(!rcvMessage.equals("OK"))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    static private String LoginCertification(LoginPackage loginPackage) {
        ClientProgress client = new ClientProgress();
        client.setLogin(loginPackage);
        Thread cThread = new Thread(client);
        cThread.start();

        synchronized (client) {
            try {
                client.wait(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        return client.getRcvUserKey();
    }

    static public void setUserName(String userName) { LoginHandler.userName = userName; }

    static public String getUserName() { return userName; }

    static public void setIsLogin(boolean t) {isLogin = t;}

    static public boolean getIsLogin() { return isLogin; }

    static public void setUserKey(String message){userKey = message;}

    static public String getUserKey(){ return userKey; }
}
