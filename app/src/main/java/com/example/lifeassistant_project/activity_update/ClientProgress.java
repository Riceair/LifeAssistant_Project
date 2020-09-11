package com.example.lifeassistant_project.activity_update;

import android.os.Build;

import androidx.annotation.RequiresApi;
import com.example.lifeassistant_project.activity_update.packages.SentenceHandler;
import com.example.lifeassistant_project.activity_update.packages.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ClientProgress implements Runnable {

    public static int port = 6666;
    public static String address = "172.20.10.8";

    public DataPackage sndPackage;
    public ArrayList<DataPackage> rcvPackageList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run()  {
        synchronized (this)
        {
            try{
                this.rcvPackageList = this.sndPackage.sendOperation(address, port);
            }catch (Exception e) {
                System.out.println("exception");
                System.out.println(e);
                this.rcvPackageList = new ArrayList<DataPackage>();
                if(this.sndPackage.getClass() == LoginPackage.class)
                {
                    this.rcvPackageList.add(new LoginPackage());
                }
                else if(this.sndPackage.getClass() == SentenceHandler.class)
                {
                    SentenceHandler errSen = new SentenceHandler();
                    errSen.setFulfillment("連線失敗，我必須要透過網路才能幫得到您！");
                    this.rcvPackageList.add(errSen);
                }
            }finally{
                notify();
            }

        }
    }

    public ArrayList<DataPackage> getRcvPackageList() { return this.rcvPackageList; }
    public void setPackage(DataPackage dataPackage) { this.sndPackage = dataPackage; }
}

