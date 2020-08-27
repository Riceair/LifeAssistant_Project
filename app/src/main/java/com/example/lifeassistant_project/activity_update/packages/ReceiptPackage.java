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
import java.util.ArrayList;
import java.util.Arrays;

public class ReceiptPackage extends DataPackage{
    private String year, month;
    private ArrayList<String> hitNumber;

    public ReceiptPackage()
    {
        this.year = "2000";
        this.month = "01";
        this.hitNumber = new ArrayList<String>();
    }

    public ReceiptPackage(String year, String month, ArrayList<String> hitNumber)
    {
        this.year = year;
        this.month = month;
        this.hitNumber = hitNumber;
    }

    @Override
    public ArrayList<DataPackage> sendOperation(String address, int port) throws IOException {
        super.sendOperation(address, port);

        final int DATE_SIZE = 8, HIT_NUMBER_SIZE = 56, TOTAL_HIT_SIZE = DATE_SIZE + HIT_NUMBER_SIZE + 3;
        System.out.println("receiptQR");

        SocketAddress temp = new InetSocketAddress(address, port);
        Socket client = SocketFactory.getDefault().createSocket();
        client.connect(temp, connectionTimeout);

        OutputStream out = client.getOutputStream();

        out.write(PackageHandler.ReceiptQRPackageEncode());
        out.flush();
        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

        StringBuffer buf = new StringBuffer();        // 建立讀取字串。

        ByteBuffer b_buf = super.getInputByteBuffer(in, 1024);
        out.close();

        ArrayList<DataPackage> resultList = new ArrayList<DataPackage>();
        for(int i = 0 ;i < b_buf.array().length; i += TOTAL_HIT_SIZE)
        {
            int ptr = i;
            System.out.println(PackageHandler.TransByteArray2String(b_buf.array(), b_buf.array().length));
            if(!PackageHandler.TransByteArray2String(Arrays.copyOfRange(b_buf.array(), ptr, ptr + 3), 3).equals("rqr"))
                break;

            ptr += 3;
            byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), ptr, ptr + DATE_SIZE + HIT_NUMBER_SIZE);
            ReceiptPackage rcvReceipt = PackageHandler.ReceiptQRPackageDecode(rcvArray);
            resultList.add(rcvReceipt);
        }

        System.out.println("message send.");
        client.close();

        return resultList;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public ArrayList<String> getHitNumber() {
        return hitNumber;
    }

    public void setHitNumber(ArrayList<String> hitNumber) {
        this.hitNumber = hitNumber;
    }


}
