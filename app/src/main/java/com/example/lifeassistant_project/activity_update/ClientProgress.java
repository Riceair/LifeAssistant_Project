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
    public static String address = "192.168.203.115";

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
                    this.rcvPackageList = new ArrayList<DataPackage>();
                    this.rcvPackageList.add(new LoginPackage());
                }
            }finally{
                notify();
            }

        }
    }

    public ArrayList<DataPackage> getRcvPackageList() { return this.rcvPackageList; }
    public void setPackage(DataPackage dataPackage) { this.sndPackage = dataPackage; }
}

