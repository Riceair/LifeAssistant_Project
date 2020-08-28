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

public class AccountPackage extends DataPackage
{
    private int ID, money, year, month, day;
    private String item, detail, receipt, note, user;
    private boolean type; // True for 支出
    private int requestAction; // request 需要執行的行為
    // 0 = 新增, 1 = 刪除, 2 = 修改, 3 = 查詢, 4 = Debug.

    public int getRequestAction() {
        return requestAction;
    }

    public void setRequestAction(int requestAction) {
        this.requestAction = requestAction;
    }

    public AccountPackage(int ID, int money, int year, int month, int day, String item, String detail, boolean type, String receipt, String note)
    {
        this.ID = ID;
        this.money = money;
        this.year = year;
        this.month = month;
        this.day = day;
        this.item = item;
        this.detail = detail;
        this.type = type;
        this.receipt = receipt;
        this.note = note;
        this.requestAction = 4;
        this.user = "Null";
    }
    public AccountPackage()
    {
        this.ID = 0;
        this.money = 0;
        this.year = 0;
        this.month = 0;
        this.day = 0;
        this.item = "";
        this.detail = "";
        this.type = false;
        this.receipt = "";
        this.note = "";
        this.requestAction = 4;
        this.user = "Null";
    }

    @Override
    public ArrayList<DataPackage> sendOperation(String address, int port) throws IOException {
        super.sendOperation(address, port);
        final int PACKAGE_SIZE = 168;
        ArrayList<DataPackage> resultPackageList = null;

        SocketAddress tempSocketAddress = new InetSocketAddress(address, port);
        Socket client = SocketFactory.getDefault().createSocket();
        client.connect(tempSocketAddress, connectionTimeout);

        OutputStream out = client.getOutputStream();

        // send account package
        out.write(PackageHandler.accountPackageEncode(this));
        out.flush();
        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

        StringBuffer buf = new StringBuffer();        // 建立讀取字串。
        //need to be remade.
        ByteBuffer b_buf = super.getInputByteBuffer(in, 1024 * 16);
        out.close();

        byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), 0, b_buf.array().length);

        if (this.getRequestAction() == 3) {
            ArrayList<DataPackage> rcvAccountData = new ArrayList<DataPackage>();
            for (int i = 0, currentLength = 0; i < b_buf.array().length / PACKAGE_SIZE; i++) {
                byte[] resultArray = Arrays.copyOfRange(b_buf.array(), currentLength + 3, currentLength + PACKAGE_SIZE);
                AccountPackage temp = PackageHandler.accountPackageDecode(resultArray);
                if (temp.getID() == 0) break;
                rcvAccountData.add(temp);
                currentLength += PACKAGE_SIZE;
            }
            resultPackageList = rcvAccountData;
        } else {
            String rcvString = new String(rcvArray, StandardCharsets.UTF_8);
            System.out.println(rcvString);
        }

        System.out.println("message send.");                    // 印出接收到的訊息。
        client.close();                                         // 關閉 TcpSocket.
        return resultPackageList;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getReceipt() { return receipt; }

    public void setReceipt(String receipt) { this.receipt = receipt; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }

}
