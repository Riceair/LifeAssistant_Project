package com.example.lifeassistant_project.activity_update;

import android.os.Build;

import androidx.annotation.RequiresApi;

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
        public static String address = "192.168.203.108";
        private final int CONNECTION_TIMEOUT = 5000;
        private String packageType = "";
        private SentenceHandler sendSentence;
        private SentenceHandler rcvSentence;
        private AccountPackage accountPackage;
        private SchedulePackage schedulePackage;
        private ArrayList<AccountPackage> rcvAccountData;
        private ArrayList<WeatherPackage> rcvWeatherData;
        private LoginPackage loginPackage;
        private String rcvUserKey;

        public ArrayList<WeatherPackage> getRcvWeatherData(){ return this.rcvWeatherData; }
        public ArrayList<AccountPackage> getRcvAccountData(){ return this.rcvAccountData; }
        public SentenceHandler getRcvSentence() { return this.rcvSentence; }
        public String getRcvUserKey() { return this.rcvUserKey; }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run()  {
            if(this.packageType.equals("bookkeeping"))
            {
                synchronized (this)
                {
                    try {
                        final int PACKAGE_SIZE = 167;

                        SocketAddress tempSocketAddress = new InetSocketAddress(this.address, this.port);
                        Socket client = SocketFactory.getDefault().createSocket();
                        client.connect(tempSocketAddress, CONNECTION_TIMEOUT);

                        OutputStream out = client.getOutputStream();

                        // send account package
                        out.write(PackageHandler.accountPackageEncode(this.accountPackage));
                        out.flush();
                        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

                        StringBuffer buf = new StringBuffer();        // 建立讀取字串。
                        ByteBuffer b_buf = getInputByteBuffer(in, 1024*16);
                        out.close();

                        byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), 0, b_buf.array().length);

                        if(this.accountPackage.getRequestAction() == 3)
                        {
                            System.out.println("Hello");
                            ArrayList<AccountPackage> rcvAccountData = new ArrayList<AccountPackage>();
                            for (int i = 0, currentLength = 0; i < b_buf.array().length / PACKAGE_SIZE; i++)
                            {
                                byte[] resultArray = Arrays.copyOfRange(b_buf.array(), currentLength+3, currentLength+PACKAGE_SIZE);
                                AccountPackage temp = PackageHandler.accountPackageDecode(resultArray);
                                if(temp.getID() == 0) break;
                                rcvAccountData.add(temp);
                                currentLength += PACKAGE_SIZE;
                            }
                            this.rcvAccountData = rcvAccountData;
//
//                        for(AccountPackage temp : this.rcvAccountData)
//                        {
//                            System.out.println(temp.getID());
//                        }
                        }
                        else
                        {
                            String rcvString = new String(rcvArray, StandardCharsets.UTF_8);
                            System.out.println(rcvString);
                        }

                        System.out.println("message send.");                    // 印出接收到的訊息。
                        client.close();                                // 關閉 TcpSocket.
                    }catch (Exception e){
                        System.out.println("exception");
                        System.out.println(e);
                        this.rcvAccountData = new ArrayList<AccountPackage>();
                    }finally {
                        if(this.accountPackage.getRequestAction() == 3)
                            notify();
                    }
                }
            }
            else if (this.packageType.equals("weather"))
            {
                synchronized (this)
                {
                    try {
                        System.out.println("wea");
                        final int PACKAGE_SIZE = 76, CITY_COUNT = 22, WEATHER_PER_CITY = 14;

                        SocketAddress temp = new InetSocketAddress(this.address, this.port);
                        Socket client = SocketFactory.getDefault().createSocket();
                        client.connect(temp, CONNECTION_TIMEOUT);

                        OutputStream out = client.getOutputStream();

                        // send account package
                        out.write(PackageHandler.weatherPackageEncode());
                        out.flush();
                        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

                        StringBuffer buf = new StringBuffer();        // 建立讀取字串。
                        ByteBuffer b_buf = getInputByteBuffer(in, PACKAGE_SIZE * CITY_COUNT * WEATHER_PER_CITY);
                        out.close();

                        byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), 0, b_buf.array().length);
                        ArrayList<WeatherPackage> weatherData = new ArrayList<WeatherPackage>();
                        for (int i = 0, currentLength = 0; i < b_buf.array().length / PACKAGE_SIZE; i++)
                        {
                            System.out.println(i);
                            byte[] resultArray = Arrays.copyOfRange(b_buf.array(), currentLength+3, currentLength+PACKAGE_SIZE);
                            weatherData.add(PackageHandler.weatherPackageDecode(resultArray));
                            currentLength += PACKAGE_SIZE;
                        }

                        this.rcvWeatherData = weatherData;

                        String rcvString = new String(rcvArray, StandardCharsets.UTF_8);
//                        System.out.println(rcvString);

                        System.out.println("message send.");                    // 印出接收到的訊息。
                        client.close();                                // 關閉 TcpSocket.
                    }catch (Exception e){
                        System.out.println(e);
                        this.rcvWeatherData = new ArrayList<WeatherPackage>();
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
                        final int PACKAGE_SIZE = 71;

                        Socket client = new Socket(this.address, this.port);

                        OutputStream out = client.getOutputStream();

                        // send account package
                        out.write(PackageHandler.sentencePackageEncode(this.sendSentence));
                        out.flush();
                        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

                        ByteBuffer b_buf = getInputByteBuffer(in, 1024);
                        out.close();

                        byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), 3, b_buf.array().length);

                        String rcvString = new String(rcvArray, StandardCharsets.UTF_8);
                        SentenceHandler rcvSentence = PackageHandler.sentencePackageDecode(rcvArray);

                        this.rcvSentence = rcvSentence;

                        System.out.println("message send.");                    // 印出接收到的訊息。
                        client.close();                                // 關閉 TcpSocket.
                    }catch (Exception e){
                        System.out.println("exception");
                        System.out.println(e);
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
                        System.out.println("log");
                        final int PACKAGE_SIZE = 69;
                        Socket client = new Socket(this.address, this.port);

                        OutputStream out = client.getOutputStream();

                        // send account package
                        out.write(PackageHandler.LoginPackageEncode(this.loginPackage));
                        out.flush();
                        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

                        StringBuffer buf = new StringBuffer();        // 建立讀取字串。

                        ByteBuffer b_buf = getInputByteBuffer(in, PACKAGE_SIZE);
                        out.close();

                        byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), 3, b_buf.array().length);

                        rcvUserKey = PackageHandler.LoginPackageDecode(rcvArray);
                        String rcvString = new String(rcvArray, StandardCharsets.UTF_8);
                        System.out.println(rcvString);

                        System.out.println("message send.");                    // 印出接收到的訊息。
                        client.close();                                // 關閉 TcpSocket.
                    }catch (Exception e){
                        System.out.println(e);
                        rcvUserKey = "FA"; //connection fail
                    }finally {
                        notify();
                    }
                }

            }
            else
            {
                System.out.println("TypeError");
            }

//            try {
//                Socket client = new Socket(address, port);
//                OutputStream out = client.getOutputStream();
//
//                // send account package
//                AccountPackage testAccount = new AccountPackage(258,200,20,5,16,"哈哈哈","是我啦",true);
//                testAccount.setRequestAction(0);
//                out.write(PackageHandler.accountPackageEncode(testAccount));
//                //
//                // send schedule package
//        //        SchedulePackage testSchedule = new SchedulePackage(5566, "對阿天氣真好", 30, 4, 21, 12, 248);
//        //        out.write(PackageHandler.schedulePackageEncode(testSchedule));
//                //
//                out.flush();
//                InputStream in = client.getInputStream();      // 取得輸入訊息的串流
//                BufferedReader s_in = new BufferedReader (new InputStreamReader(in, "UTF-8"));
//
//                StringBuffer buf = new StringBuffer();        // 建立讀取字串。
//                ByteBuffer b_buf = ByteBuffer.allocate(1024);
//                try {
//                    while (true) {            // 不斷讀取。
//                        int x = in.read();    // 讀取一個 byte。(read 傳回 -1 代表串流結束)
//                        if (x==-1) break;    // x = -1 代表串流結束，讀取完畢，用 break 跳開。
//                        byte b = (byte) x;    // 將 x 轉為 byte，放入變數 b.
//                        b_buf.put(b);
//                        buf.append((char) b);// 假設傳送ASCII字元都是 ASCII。
//                    }
//                } catch (Exception e) {
//                    in.close();                // 關閉輸入串流。
//                }
//                out.close();
//
//                // deal with first 3 char
//                byte[] resultArray = Arrays.copyOfRange(b_buf.array(), 3, b_buf.array().length);
//                byte[] typeArray = Arrays.copyOfRange(b_buf.array(), 0, 3);
//                String typeString = new String(typeArray, StandardCharsets.UTF_8);
//        //        System.out.println(typeString);
//                if(typeString.equals("acc"))
//                {
//                    AccountPackage rcvPkg = PackageHandler.accountPackageDecode(resultArray);
//                    System.out.println(rcvPkg.getMoney());
//                }
//                else if(typeString.equals("sch"))
//                {
//                    SchedulePackage rcvPkg = PackageHandler.schedulePackageDecode(resultArray);
//                    System.out.println(rcvPkg.getTodo());
//                }
//                //
//
//
//                System.out.println("message send.");                    // 印出接收到的訊息。
//                client.close();                                // 關閉 TcpSocket.
//            }catch (Exception e){
//
//            }
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
}

