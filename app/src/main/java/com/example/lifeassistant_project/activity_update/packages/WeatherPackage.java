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

public class WeatherPackage extends DataPackage
{
    private int month;
    private int day;
    private int max_temperature;
    private int min_temperature;
    private String city;
    private String period;

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    private String situation;

    public WeatherPackage()
    {
        this.month = 0;
        this.day = 0;
        this.max_temperature = 0;
        this.min_temperature = 0;
        this.city = "";
        this.period = "";
    }

    @Override
    public ArrayList<DataPackage> sendOperation(String address, int port) throws IOException {
        super.sendOperation(address, port);
        System.out.println("wea");
        final int PACKAGE_SIZE = 76, CITY_COUNT = 22, WEATHER_PER_CITY = 14;

        SocketAddress temp = new InetSocketAddress(address, port);
        Socket client = SocketFactory.getDefault().createSocket();
        client.connect(temp, connectionTimeout);

        OutputStream out = client.getOutputStream();

        // send account package
        out.write(PackageHandler.weatherPackageEncode());
        out.flush();
        InputStream in = client.getInputStream();      // 取得輸入訊息的串流

        StringBuffer buf = new StringBuffer();        // 建立讀取字串。
        ByteBuffer b_buf = super.getInputByteBuffer(in, PACKAGE_SIZE * CITY_COUNT * WEATHER_PER_CITY);
        out.close();

        byte[] rcvArray = Arrays.copyOfRange(b_buf.array(), 0, b_buf.array().length);
        ArrayList<DataPackage> weatherData = new ArrayList<DataPackage>();
        for (int i = 0, currentLength = 0; i < b_buf.array().length / PACKAGE_SIZE; i++)
        {
//            System.out.println(i);
            byte[] resultArray = Arrays.copyOfRange(b_buf.array(), currentLength+3, currentLength+PACKAGE_SIZE);
            weatherData.add(PackageHandler.weatherPackageDecode(resultArray));
            currentLength += PACKAGE_SIZE;
        }

        String rcvString = new String(rcvArray, StandardCharsets.UTF_8);
//        System.out.println(rcvString);

        System.out.println("message send.");                    // 印出接收到的訊息。
        client.close();                                // 關閉 TcpSocket.
        return weatherData;
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

    public int getMax_temperature() {
        return max_temperature;
    }

    public void setMax_temperature(int max_temperature) {
        this.max_temperature = max_temperature;
    }

    public int getMin_temperature() {
        return min_temperature;
    }

    public void setMin_temperature(int min_temperature) {
        this.min_temperature = min_temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

}
