package com.example.lifeassistant_project.activity_update.static_handler;

import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.packages.DataPackage;
import com.example.lifeassistant_project.activity_update.packages.LoginPackage;

public class LoginHandler {
    static private String userName = "Null";
    static private String userKey = null;
    static private String errorMessage = "default error message.";
    static private String resKey = "FA";

    static private String storedUserName = "Null";

    static private boolean isLogin = false;

    static public boolean Login(LoginPackage loginPackage)
    {
        String resultKey = LoginCertification(loginPackage);
        if(resultKey.equals("NO"))
        {
            System.out.println("Certification Fail."); //need to be conduct functionality.
            resKey = resultKey;
            if(loginPackage.ifRegister())
                errorMessage = "帳號已存在。請重新輸入一個新的帳號名稱。";
            else
                errorMessage = "請輸入正確的帳號密碼";

            return false;
        }
        else if(resultKey.equals("FA"))
        {
            System.out.println("Connection Fail.");
            resKey = resultKey;
            errorMessage = "連線失敗，請確認網路連線狀態";
            return false;
        }
        else {
            resKey = "OK";
            LoginHandler.userKey = resultKey;
            LoginHandler.userName = loginPackage.getName();
            LoginHandler.isLogin = true;

//            if(storedUserName.equals(userName))
//                DatabaseBehavior.synchronizeDataFromClient();
//            else
                DatabaseBehavior.synchronizeData();

            storedUserName = userName;
            return true;
        }
    }

    static public boolean Logout()
    {
        LoginHandler.isLogin = false;
        LoginHandler.userName = "Null";
        LoginHandler.userKey = null;
        DatabaseBehavior.resetDatabase();
        return true;
    }

    static public boolean Register(LoginPackage loginPackage)
    {
        loginPackage.setRegister(true);
        return Login(loginPackage);
    }

    static public boolean checkAuthorization()
    {
        if(LoginHandler.userKey == null)
            return false;

        ClientProgress client = new ClientProgress();
        LoginPackage checkPackage = new LoginPackage();
        checkPackage.setName("key");
        checkPackage.setPassword(LoginHandler.getUserKey());
        client.setPackage(checkPackage);
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

//        String rcvMessage = client.getRcvUserKey();
        LoginPackage resultPackage = (LoginPackage) client.getRcvPackageList().get(0);
        String rcvMessage = resultPackage.getAuthorizationKey();
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
        client.setPackage(loginPackage);
        Thread cThread = new Thread(client);
        cThread.start();

        synchronized (client) {
            try {
                client.wait(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        LoginPackage resultPackage = (LoginPackage) client.getRcvPackageList().get(0);
        return resultPackage.getAuthorizationKey();
    }

    public static String getErrorMessage() { return errorMessage; }

    public static String getResKey() { return resKey; }

    static public void setUserName(String userName) { LoginHandler.userName = userName; }

    static public String getUserName() { return userName; }

    static public void setIsLogin(boolean t) {isLogin = t;}

    static public boolean getIsLogin() { return isLogin; }

    static public void setUserKey(String message){userKey = message;}

    static public String getUserKey(){ return userKey; }

    public static String getStoredUserName() { return storedUserName; }

    public static void setStoredUserName(String storedUserName) { LoginHandler.storedUserName = storedUserName; }
}
