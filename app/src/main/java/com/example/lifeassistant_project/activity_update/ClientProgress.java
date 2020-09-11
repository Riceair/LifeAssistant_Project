package com.example.lifeassistant_project.activity_update;

import android.os.Build;

import androidx.annotation.RequiresApi;
import com.example.lifeassistant_project.activity_update.packages.SentenceHandler;
import com.example.lifeassistant_project.activity_update.packages.*;
import com.example.lifeassistant_project.activity_update.static_handler.PackageHandler;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientProgress implements Runnable {

    public static int port = 6666;
    public static String address = "172.20.10.8";

    public DataPackage sndPackage;
    public ArrayList<DataPackage> rcvPackageList;
    public byte[] sndBytePackage;
    public boolean sndMode = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run()  {
        synchronized (this)
        {
            try{
                if(this.sndMode)
                    this.rcvPackageList = this.sendoutBytePackage();
                else
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
    public void setPackage(DataPackage dataPackage) {
        this.sndPackage = dataPackage;
        this.sndMode = false;
    }
    public void setBytePackage(byte[] bytePackage) {
        this.sndBytePackage = bytePackage;
        this.sndMode = true;
    }

    private ArrayList<DataPackage> sendoutBytePackage() throws IOException {
        final int TIME_OUT = 2000;
        ArrayList<DataPackage> resultPackageList = null;

        SocketAddress tempSocketAddress = new InetSocketAddress(address, port);
        Socket client = SocketFactory.getDefault().createSocket();
        client.connect(tempSocketAddress, TIME_OUT);

        OutputStream out = client.getOutputStream();

        out.write(this.sndBytePackage);
        out.flush();

        System.out.println("message send.");                    // 印出接收到的訊息。
        client.close();                                         // 關閉 TcpSocket.
        return resultPackageList;
    }
}

