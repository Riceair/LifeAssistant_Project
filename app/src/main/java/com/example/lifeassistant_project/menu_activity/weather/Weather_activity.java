package com.example.lifeassistant_project.menu_activity.weather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.packages.DataPackage;
import com.example.lifeassistant_project.activity_update.packages.WeatherPackage;

import java.text.SimpleDateFormat;
import java.util.*;

public class Weather_activity extends AppCompatActivity {
    private static final String DBNAME = "myDB.db";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    String currentCity ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("  天氣");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.weather);

        //取得Location
        Bundle bundle = getIntent().getExtras();
        currentCity = bundle.getString("AdminArea");

        this.showWeatherData();
    }

    private void showWeatherData()
    {
        if(currentCity.equals("")){ //定位失敗
            Toast.makeText(this,"請開啟定位以取得定位資訊",Toast.LENGTH_SHORT).show();
            myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null); //取得資料庫過去取得的位置資料
            try{
                cursor = myDB.rawQuery("select Location from history_weather",null);
                if(cursor!=null){
                    cursor.moveToFirst();
                    currentCity =cursor.getString(0);
                }
                myDB.close();
            }catch (Exception e){}
        }else if(currentCity.startsWith("台")){
            currentCity ="臺"+ currentCity.substring(1);
        }
        //將現在的位置設為過去的位置資料
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        ContentValues values=new ContentValues();
        values.put("Location", currentCity);
        myDB.update("history_weather",values,"id=1",null);
        myDB.close();

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
            Toast.makeText(this,"無網路連線",Toast.LENGTH_SHORT).show();
            return;
        }

        int pointer;
        for (pointer = 0;pointer < weatherData.size(); pointer++)
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

        for(int i = 0;i < WEEK_SIZE; i++)
        {
            WeatherPackage currentWeather = (WeatherPackage) weatherData.get(pointer + i);
            switch (i)
            {
                case 0:
                    assignNumber2Image(currentWeather.getMax_temperature() / 10, R.id.higher_decimaldegree);
                    assignNumber2Image(currentWeather.getMax_temperature() % 10, R.id.higher_digitsdegree);
                    assignNumber2Image(currentWeather.getMin_temperature() / 10, R.id.lower_decimaldegree);
                    assignNumber2Image(currentWeather.getMin_temperature() % 10, R.id.lower_digitsdegree);
                    TextView displayText = (TextView) findViewById(R.id.conditions);
                    displayText.setText(currentWeather.getSituation());
                    displayText = (TextView) findViewById(R.id.Location);
                    displayText.setText(currentWeather.getCity());
                    assignCondition2Image(currentWeather.getSituation(),R.id.Conditions);
                    assignbackground2Image(currentWeather.getSituation(),R.id.easylist);

                    break;
                case 1:
                    assignNumber2Image(currentWeather.getMax_temperature() / 10, R.id.sec_higher_decimaldegree);
                    assignNumber2Image(currentWeather.getMax_temperature() % 10, R.id.sec_higher_digitsdegree);
                    assignNumber2Image(currentWeather.getMin_temperature() / 10, R.id.sec_lower_decimaldegree);
                    assignNumber2Image(currentWeather.getMin_temperature() % 10, R.id.sec_lower_digitsdegree);
                    displayText = (TextView) findViewById(R.id.secondview);
                    displayText.setText(currentWeather.getPeriod());
                    displayText = (TextView) findViewById(R.id.seccoditions);
                    displayText.setText(currentWeather.getSituation());
                    assignCondition2Image(currentWeather.getSituation(),R.id.secconditions);

                    break;
                case 2:
                    assignTemperature2Text(currentWeather, R.id.onedaytemperature);
                    assignCondition2Image(currentWeather.getSituation(),R.id.onedaycondition);
                    break;
                case 4:
                    assignTemperature2Text(currentWeather, R.id.twodaytemperature);
                    assignCondition2Image(currentWeather.getSituation(),R.id.twodayscondition);
                    break;
                case 6:
                    assignTemperature2Text(currentWeather, R.id.threedaytemperature);
                    assignCondition2Image(currentWeather.getSituation(),R.id.threedayscondition);
                    break;
                case 8:
                    assignTemperature2Text(currentWeather, R.id.fourdaytemperature);
                    assignCondition2Image(currentWeather.getSituation(),R.id.fourdayscondition);
                    break;
                case 10:
                    assignTemperature2Text(currentWeather, R.id.fivedaytemperature);
                    assignCondition2Image(currentWeather.getSituation(),R.id.fivedayscondition);
                    break;
                case 12:
                    assignTemperature2Text(currentWeather, R.id.sixdaytemperature);
                    assignCondition2Image(currentWeather.getSituation(),R.id.sixodayscondition);
                    break;
                default:
                    break;
            }
        }
        this.assignWeek2Text();
    }

    private void assignWeek2Text()
    {
        final String[] weekList = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四" , "星期五", "星期六"};

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E");
        String currentWeek = ft.format(dNow);

        int ptr = 0;
        if(currentWeek.equals("Sun")) ptr = 0;
        else if(currentWeek.equals("Mon")) ptr = 1;
        else if(currentWeek.equals("Tus")) ptr = 2;
        else if(currentWeek.equals("Wed")) ptr = 3;
        else if(currentWeek.equals("Thu")) ptr = 4;
        else if(currentWeek.equals("Fri")) ptr = 5;
        else if(currentWeek.equals("Sat")) ptr = 6;
        else ptr = 0;

        for(int i = 0; i < 7; i++)
        {
            if (ptr >= 7) ptr = 0;
            TextView target;
            int RID = 0;
            if(i == 0) System.out.println("today");
            else if(i == 1)RID = R.id.onedayafter;
            else if(i == 2)RID = R.id.twodaysafter;
            else if(i == 3)RID = R.id.threedaysafter;
            else if(i == 4)RID = R.id.fourdaysafter;
            else if(i == 5)RID = R.id.fivedaysafter;
            else if(i == 6)RID = R.id.sixdaysafter;
            else RID = R.id.onedayafter;

            target = (TextView) findViewById(RID);
            if(target != null) target.setText(weekList[ptr++]);
            else ptr++;

        }
    }

    private void assignCondition2Image(String current_weather,int RID)
    {
        ImageView image = (ImageView)findViewById(RID);
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
                image.setImageResource(R.drawable.weather_condition_windy);
                break;
        }
    }

    private void assignbackground2Image(String current_weather,int RID)
    {
        RelativeLayout relativeLayout = findViewById(RID);
        switch(current_weather)
        {
            case "多雲短暫陣雨":
                relativeLayout.setBackgroundResource(R.drawable.weather_bg_rain);
                break;
            case "多雲時陰短暫陣雨":
                relativeLayout.setBackgroundResource(R.drawable.weather_bg_fog);
                break;
            case "多雲時陰短暫陣雨或雷雨":
                relativeLayout.setBackgroundResource(R.drawable.weather_bg_snow);
                break;
            case "多雲午後短暫雷陣雨":
                relativeLayout.setBackgroundResource(R.drawable.weather_bg_thunderstorms);
                break;
            case "多雲":
                relativeLayout.setBackgroundResource(R.drawable.weather_bg_cloudy);
                break;
            case "多雲時晴":
                relativeLayout.setBackgroundResource(R.drawable.weather_bg_parly_sunny);
                break;
            case "晴時多雲":
                relativeLayout.setBackgroundResource(R.drawable.weather_bg_clear);
                break;
            case "晴午後短暫雷陣雨":
                relativeLayout.setBackgroundResource(R.drawable.weather_bg_mostly_clear);
                break;
            default:
                relativeLayout.setBackgroundResource(R.drawable.weather_bg_sunny);
                break;
        }
    }



    private void assignNumber2Image(int number, int RID)
    {
        ImageView image = (ImageView) findViewById(RID);
        switch (number)
        {
            case 0:
                image.setImageResource(R.drawable.digits_0);
                break;
            case 1:
                image.setImageResource(R.drawable.digits_1);
                break;
            case 2:
                image.setImageResource(R.drawable.digits_2);
                break;
            case 3:
                image.setImageResource(R.drawable.digits_3);
                break;
            case 4:
                image.setImageResource(R.drawable.digits_4);
                break;
            case 5:
                image.setImageResource(R.drawable.digits_5);
                break;
            case 6:
                image.setImageResource(R.drawable.digits_6);
                break;
            case 7:
                image.setImageResource(R.drawable.digits_7);
                break;
            case 8:
                image.setImageResource(R.drawable.digits_8);
                break;
            case 9:
                image.setImageResource(R.drawable.digits_9);
                break;
            default:
                image.setImageResource(R.drawable.digits_0);
                break;
        }
    }
    private void assignTemperature2Text(WeatherPackage weatherPackage, int RID)
    {
        TextView textView = (TextView) findViewById(RID);
        String temp = new String(Integer.toString(weatherPackage.getMax_temperature()) + "-" + Integer.toString(weatherPackage.getMin_temperature()));
        textView.setText(temp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Weather_activity.this.finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}
