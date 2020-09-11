package com.example.lifeassistant_project.activity_update.packages;

import android.widget.ImageView;
import android.widget.Toast;
import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.ClientProgress;
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
    private static ArrayList<DataPackage> rcvWeatherData = new ArrayList<>();
    private static String currentCity;

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
        this.connectionTimeout = 3000;
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

    private static ArrayList<DataPackage> getWeatherDataFromServer()
    {
        final int WEEK_SIZE = 14;

        ClientProgress client = new ClientProgress();
        client.setPackage(new WeatherPackage());
        Thread cThread = new Thread(client);
        cThread.start();
        synchronized (client)
        {
            try {
                client.wait();
            }
            catch (InterruptedException e)
            {
                System.out.println(e);
            }
        }

        ArrayList<DataPackage> weatherData = client.getRcvPackageList();
        if(weatherData.size() == 0)
        {
            System.out.println("connection failed. can't get weather's information from server");
            return null;
        }

        int pointer;
        for (pointer = 0;pointer < weatherData.size(); pointer += WEEK_SIZE)
        {
            WeatherPackage weatherPackage = (WeatherPackage) weatherData.get(pointer);
            if(weatherPackage.getCity().equals(currentCity)) break;
        }

        if(pointer == weatherData.size())
        {
            System.out.println("Location error! set current city to default.");
            currentCity = "基隆市";
            pointer = 0;
        }

        ArrayList<DataPackage> resultData = new ArrayList<>();
        for(int i = 0;i < WEEK_SIZE;i++)
            resultData.add(weatherData.get(pointer++));

        rcvWeatherData = resultData;
        return resultData;
    }

    public static ArrayList<DataPackage> getRcvWeatherData()
    {
        if((rcvWeatherData.size() == 0) && currentCity != null)
            return getWeatherDataFromServer();
        else if(rcvWeatherData.size() != 0)
            return rcvWeatherData;
        else if(currentCity == null)
            System.out.println("Get weather data error! current city has not been settled.");
        return null;
    }

    public static void assignCondition2Image(String current_weather, ImageView image)
    {
        switch(current_weather)
        {
            case "多雲短暫陣雨":
                image.setImageResource(R.drawable.weather_condition_rain);
                break;
            case "多雲時陰短暫陣雨":
                image.setImageResource(R.drawable.weather_condition_shower);
                break;
            case "多雲時陰短暫陣雨或雷雨":
                image.setImageResource(R.drawable.weather_condition_rainandsnowmixed);
                break;
            case "多雲午後短暫雷陣雨":
                image.setImageResource(R.drawable.weather_condition_thunderstorms);
                break;
            case "多雲":
                image.setImageResource(R.drawable.weather_condition_mostlycloudy);
                break;
            case "多雲時晴":
                image.setImageResource(R.drawable.weather_condition_partlysunny);
                break;
            case "晴時多雲":
                image.setImageResource(R.drawable.weather_condition_sunny);
                break;
            case "晴午後短暫雷陣雨":
                image.setImageResource(R.drawable.weather_condition_mostlyclear);
                break;
            default:
                image.setImageResource(R.drawable.weather_condition_rain);
                break;
        }
    }

    public static void setCurrentCity(String t) {currentCity = t;}

    public static String getCurrentCity() {return  currentCity;}

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
