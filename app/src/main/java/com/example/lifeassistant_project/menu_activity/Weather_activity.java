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
import org.w3c.dom.Text;

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
            System.out.println(currentWeather.getSituation());

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
                    break;
                case 2:
                    assignTemperature2Text(currentWeather, R.id.onedaytemperature);
                    break;
                case 4:
                    assignTemperature2Text(currentWeather, R.id.twodaytemperature);
                    break;
                case 6:
                    assignTemperature2Text(currentWeather, R.id.threedaytemperature);
                    break;
                case 8:
                    assignTemperature2Text(currentWeather, R.id.fourdaytemperature);
                    break;
                case 10:
                    assignTemperature2Text(currentWeather, R.id.fivedaytemperature);
                    break;
                case 12:
                    assignTemperature2Text(currentWeather, R.id.sixdaytemperature);
                    break;
                default:
                    break;
            }
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
