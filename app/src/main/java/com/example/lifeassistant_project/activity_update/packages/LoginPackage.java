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

public class LoginPackage extends DataPackage{
    private String name;
    private String password;
    private String authorizationKey;
    private boolean isRegister = false;

    public LoginPackage()
    {
        this.name = "Null";
        this.password = "Null";
        this.authorizationKey = "FA"; //default value: connection fail.
        this.connectionTimeout = 2000;
    }

    public LoginPackage(String name, String password)
    {
        this.name = name;
        this.password = password;
        this.authorizationKey = "FA"; //default value: connection fail.
        this.connectionTimeout = 2000;
    }

    @Override
    public ArrayList<DataPackage> sendOperation(String address, int port) throws IOException {
        super.sendOperation(address, port);

        System.out.println("log");
        final int RCV_PACKAGE_SIZE = 69;
        ArrayList<DataPackage> resultList = new ArrayList<>();

        SocketAddress temp = new InetSocketAddress(address, port);
        Socket client = SocketFactory.getDefault().createSocket();
        client.connect(temp, connectionTimeout);

        OutputStream out = client.getOutputStream();

        out.write(PackageHandler.LoginPackageEncode(this));
        out.flush();
        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

        StringBuffer buf = new StringBuffer();        // 建立讀取字串。

        ByteBuffer b_buf = super.getInputByteBuffer(in, RCV_PACKAGE_SIZE);
        out.close();

        byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), 3, b_buf.array().length);

        this.authorizationKey = PackageHandler.LoginPackageDecode(rcvArray);
        String rcvString = new String(rcvArray, StandardCharsets.UTF_8);
        System.out.println(rcvString);

        System.out.println("message send.");                   // 印出接收到的訊息。
        client.close();                                        // 關閉 TcpSocket.

        resultList.add(this);
        return resultList;
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

    public String getAuthorizationKey() { return authorizationKey; }

    public void setAuthorizationKey(String authorizationKey) { this.authorizationKey = authorizationKey; }

    public boolean ifRegister() { return isRegister; }

    public void setRegister(boolean register) { isRegister = register; }
}
