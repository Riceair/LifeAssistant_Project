package com.example.lifeassistant_project.activity_update;

import android.os.Build;

import androidx.annotation.RequiresApi;
import com.example.lifeassistant_project.activity_update.packages.SentenceHandler;
import com.example.lifeassistant_project.activity_update.packages.*;
import com.example.lifeassistant_project.activity_update.static_handler.PackageHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientProgress implements Runnable {

    public static int port = 6666;
    public static String address = "192.168.203.115";
    private final int CONNECTION_TIMEOUT = 5000;
    private String packageType = "";
    private SentenceHandler sendSentence;
    private SentenceHandler rcvSentence;
    private AccountPackage accountPackage;
    private SchedulePackage schedulePackage;
    private ArrayList<AccountPackage> rcvAccountData;
    private ArrayList<SchedulePackage> rcvScheduleData;
    private ArrayList<WeatherPackage> rcvWeatherData;
    private ArrayList<ReceiptContainer> rcvReceiptContainer;
    private LoginPackage loginPackage;
    private String rcvUserKey;

    public ArrayList<WeatherPackage> getRcvWeatherData(){ return this.rcvWeatherData; }
    public ArrayList<AccountPackage> getRcvAccountData(){ return this.rcvAccountData; }
    public ArrayList<SchedulePackage> getRcvScheduleData() { return this.rcvScheduleData; }
    public ArrayList<ReceiptContainer> getRcvReceiptContainer() { return rcvReceiptContainer; }
    public SentenceHandler getRcvSentence() { return this.rcvSentence; }
    public String getRcvUserKey() { return this.rcvUserKey; }

    //DEBUG MEMBER
    public DataPackage sndPackage;
    public ArrayList<DataPackage> rcvPackageList;
    public ArrayList<DataPackage> getRcvPackageList() { return this.rcvPackageList; }
    public void setPackage(DataPackage dataPackage) { this.sndPackage = dataPackage; }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run()  {
        if(this.packageType.equals("bookkeeping"))
        {
            synchronized (this)
            {
                try {
                    this.rcvPackageList = this.sndPackage.sendOperation(address, port);
                }catch (Exception e)
                {
                    System.out.println("exception");
                    System.out.println(e);
                    this.rcvPackageList = new ArrayList<DataPackage>();
                }finally {
//                    if(this.sndPackage.getRequestAction() == 3)
                    notify();
                }

            }
        }
        else if (this.packageType.equals("plan"))
        {
            synchronized (this)
            {
                try {
                    this.rcvPackageList = this.sndPackage.sendOperation(address, port);
                }catch (Exception e){
                    System.out.println("exception");
                    System.out.println(e);
                    this.rcvPackageList = new ArrayList<DataPackage>();
                }finally {
//                    if(this.schedulePackage.getRequestAction() == 3)
                    notify();
                }
            }
        }
        else if (this.packageType.equals("weather"))
        {
            synchronized (this)
            {
                try {
                    this.rcvPackageList = this.sndPackage.sendOperation(address, port);
                }catch (Exception e){
                    System.out.println(e);
                    this.rcvPackageList = new ArrayList<DataPackage>();
                }finally {
                    notify();
                }
            }

        }
        else if(this.packageType.equals("chatBot"))
        {
            synchronized (this)
            {
                try {
                    this.rcvPackageList = this.sndPackage.sendOperation(address, port);
                }catch (Exception e){
                    System.out.println("chatbot Exception.");
                    System.out.println(e);
                    this.rcvPackageList = new ArrayList<DataPackage>();
                }finally {
                    notify();
                }
            }
        }
        else if(this.packageType.equals("login"))
        {
            synchronized (this)
            {
                try {
                    this.rcvPackageList = this.sndPackage.sendOperation(address, port);
                }catch (Exception e){
                    System.out.println("Login exception");
                    System.out.println(e);
                    // connection Fail
                    this.rcvPackageList = new ArrayList<DataPackage>();
                    this.rcvPackageList.add(new LoginPackage());
//                    rcvUserKey = "FA"; //connection fail
                }finally {
                    notify();
                }
            }

        }
        else if(this.packageType.equals("receiptQR"))
        {
            synchronized (this)
            {
                try
                {
                    this.rcvPackageList = this.sndPackage.sendOperation(address, port);
                }catch (Exception e)
                {
                    System.out.println("receiptQR Exception.");
                    System.out.println(e);
                    this.rcvPackageList = new ArrayList<DataPackage>();
                }finally {
                    notify();
                }
            }
        }
        else
        {
            System.out.println("TypeError");
        }
    }

    private ByteBuffer getInputByteBuffer(InputStream in, int allocateSize) throws IOException {
        StringBuffer buf = new StringBuffer();        // 建立讀取字串。
        ByteBuffer b_buf = ByteBuffer.allocate(allocateSize);
        try {
            while (true) {            // 不斷讀取。
                int x = in.read();    // 讀取一個 byte。(read 傳回 -1 代表串流結束)
                if (x==-1) break;     // x = -1 代表串流結束，讀取完畢，用 break 跳開。
                byte b = (byte) x;    // 將 x 轉為 byte，放入變數 b.
                b_buf.put(b);
                buf.append((char) b); // 假設傳送ASCII字元都是 ASCII。
            }
        } catch (Exception e) {
            in.close();               // 關閉輸入串流。
        }

        return b_buf;
    }

    public void setBookkeeping(AccountPackage sendPackage)
    {
        this.packageType = "bookkeeping";
        this.accountPackage = sendPackage;
    }
    public void setPlan(SchedulePackage sendPackage)
    {
        this.packageType = "plan";
        this.schedulePackage = sendPackage;
    }
    public void setWeather()
    {
        this.packageType = "weather";
    }
    public void setChatBot(SentenceHandler sentence)
    {
        this.packageType = "chatBot";
        this.sendSentence = sentence;
    }
    public void setLogin(LoginPackage loginPackage)
    {
        this.packageType = "login";
        this.loginPackage = loginPackage;
    }
    public void setReceiptQR() { this.packageType = "receiptQR"; }
}

