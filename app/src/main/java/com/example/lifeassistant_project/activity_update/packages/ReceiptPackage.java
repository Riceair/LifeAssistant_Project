package com.example.lifeassistant_project.activity_update.packages;

import com.example.lifeassistant_project.activity_update.static_handler.PackageHandler;

import javax.net.SocketFactory;
import javax.xml.transform.Transformer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class ReceiptPackage extends DataPackage {
    private String user;

    public ReceiptPackage()
    {
        this.user = "Null";
    }
    public ReceiptPackage(String user)
    {
        this.user = user;
    }

    @Override
    public ArrayList<DataPackage> sendOperation(String address, int port) throws IOException {
        super.sendOperation(address, port);

        final int HIT_TYPE_SIZE = 3, ACCOUNT_SIZE = 36;
        System.out.println("receipt");

        SocketAddress temp = new InetSocketAddress(address, port);
        Socket client = SocketFactory.getDefault().createSocket();
        client.connect(temp, connectionTimeout);

        OutputStream out = client.getOutputStream();
        out.write(PackageHandler.ReceiptPackageEncode(this));
        out.flush();

        InputStream in = client.getInputStream();      // 取得輸入訊息的串流
        ByteBuffer b_buf = super.getInputByteBuffer(in, 1024);
        out.close();

        ArrayList<DataPackage> resultList = new ArrayList<DataPackage>();
        byte[] rcvBuf = Arrays.copyOfRange(b_buf.array(), 3, b_buf.array().length);
        String checkType;
        for(int i = 0;i < rcvBuf.length; i += ACCOUNT_SIZE + HIT_TYPE_SIZE)
        {
            int ptr = i;
            checkType = PackageHandler.TransByteArray2String(Arrays.copyOfRange(rcvBuf, ptr, ptr + HIT_TYPE_SIZE), HIT_TYPE_SIZE);
            if(checkType.charAt(0) != '#' || checkType.charAt(2) != '#') break;
            ptr += HIT_TYPE_SIZE;

            AccountPackage rcvPackage = PackageHandler.ReceiptPackageDecode(Arrays.copyOfRange(rcvBuf, ptr, ptr + ACCOUNT_SIZE));

            int typeCode = (int) checkType.charAt(1);
            if(typeCode == 0)
                rcvPackage.setItem("0");
            else if(typeCode == 1)
                rcvPackage.setItem("1");
            else if(typeCode < 5)
                rcvPackage.setItem("2");
            else
                rcvPackage.setItem("3");

            resultList.add(rcvPackage);
        }

        System.out.println("message send.");
        client.close();

        return resultList;
    }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }
}
