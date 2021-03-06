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

public class SentenceHandler extends DataPackage{
    private int intent, operation;
    private String fulfillment;

    private ArrayList<DataPackage> rcvSelectedList;
    private String calculateType;

    public SentenceHandler()
    {
        this.intent = 0;
        this.operation = 0;
        this.fulfillment = "null";
        this.rcvSelectedList = new ArrayList<>();
        this.calculateType = "def";
        this.connectionTimeout = 2000;
    }

    public SentenceHandler(int intent, int operation, String fulfillment)
    {
        this.intent = intent;
        this.operation = operation;
        this.fulfillment = fulfillment;
        this.rcvSelectedList = new ArrayList<>();
        this.calculateType = "def";
    }

    @Override
    public ArrayList<DataPackage> sendOperation(String address, int port) throws IOException {
        super.sendOperation(address, port);

        final int PACKAGE_SIZE = 71;

        SocketAddress temp = new InetSocketAddress(address, port);
        Socket client = SocketFactory.getDefault().createSocket();
        client.connect(temp, connectionTimeout);

        OutputStream out = client.getOutputStream();

        // send account package
        out.write(PackageHandler.sentencePackageEncode(this));
        out.flush();
        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

        ByteBuffer b_buf = super.getInputByteBuffer(in, 1024 * 16);
        out.close();

        byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), 3, b_buf.array().length);

        String rcvString = new String(rcvArray, StandardCharsets.UTF_8);
        System.out.println("Sentence package:");
        System.out.println(rcvString);
        SentenceHandler rcvSentence = PackageHandler.sentencePackageDecode(rcvArray);

        System.out.println("message send.");                    // 印出接收到的訊息。
        client.close();                                         // 關閉 TcpSocket.

        ArrayList<DataPackage> resultList = new ArrayList<DataPackage>();
        resultList.add(rcvSentence);
        return resultList;
    }

    public int getOperation() { return operation; }

    public void setOperation(int operation) { this.operation = operation; }

    public int getIntent() {
        return intent;
    }

    public void setIntent(int intent) {
        this.intent = intent;
    }

    public String getFulfillment() {
        return fulfillment;
    }

    public void setFulfillment(String fulfillment) {
        this.fulfillment = fulfillment;
    }

    public ArrayList<DataPackage> getRcvSelectedList() { return rcvSelectedList; }

    public void setRcvSelectedList(ArrayList<DataPackage> rcvSelectedList) { this.rcvSelectedList = rcvSelectedList; }

    public String getCalculateType() { return calculateType; }

    public void setCalculateType(String calculateType) { this.calculateType = calculateType; }
}
