package com.example.lifeassistant_project.activity_update.packages;

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

public class VersionPackage extends DataPackage{
    private String name;
    private int versionCode;

    public static int PACKAGE_SIZE = 27;

    public VersionPackage()
    {
        this.name = "null";
        this.versionCode = -1;
    }

    public VersionPackage(int code, String name)
    {
        this.versionCode = code;
        this.name = name;
    }

    @Override
    public ArrayList<DataPackage> sendOperation(String address, int port) throws IOException {
        super.sendOperation(address, port);
        System.out.println("ver");

        ArrayList<DataPackage> resultList = new ArrayList<>();

        SocketAddress temp = new InetSocketAddress(address, port);
        Socket client = SocketFactory.getDefault().createSocket();
        client.connect(temp, connectionTimeout);

        OutputStream out = client.getOutputStream();

        out.write(PackageHandler.VersionPackageEncode(this));
        out.flush();
        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

        StringBuffer buf = new StringBuffer();        // 建立讀取字串。

        ByteBuffer b_buf = super.getInputByteBuffer(in, PACKAGE_SIZE);
        out.close();

        byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), 3, b_buf.array().length);

        VersionPackage rcvPkg = PackageHandler.VersionPackageDecode(rcvArray);
        String rcvString = new String(rcvArray, StandardCharsets.UTF_8);
        System.out.println(rcvString);

        System.out.println("message send.");
        client.close();

        resultList.add(rcvPkg);
        return resultList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
