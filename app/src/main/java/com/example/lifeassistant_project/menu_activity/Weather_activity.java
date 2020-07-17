package com.example.lifeassistant_project.menu_activity;

import android.icu.lang.UCharacter;
import android.media.Image;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.WeatherPackage;

import java.util.*;

public class Weather_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide(); //隱藏狀態列(綠色的那塊)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);  //全螢幕
        setContentView(R.layout.activity_weather_activity);

        this.showWeatherData();
    }

    private void showWeatherData()
    {
        final String CURRENT_CITY = "基隆市"; //this is for testing
        final int WEEK_SIZE = 14;

        ClientProgress client = new ClientProgress();
        client.setWeather();
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
        ArrayList<WeatherPackage> weatherData = client.getRcvWeatherData();

        int pointer;
        for (pointer = 0;pointer < weatherData.size(); pointer++)
        {
            if(weatherData.get(pointer).getCity().equals(CURRENT_CITY)) break;
        }

        if(pointer == weatherData.size() - 1)
            System.out.println("ERROR!");

        for(int i = 0;i < WEEK_SIZE; i++)
        {
            WeatherPackage currentWeather = weatherData.get(pointer + i);
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
                    assignbackground2Image(currentWeather.getSituation(),R.id.mainly);

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
}
