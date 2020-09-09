package com.example.lifeassistant_project.menu_activity.weather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.packages.DataPackage;
import com.example.lifeassistant_project.activity_update.packages.WeatherPackage;

import java.text.SimpleDateFormat;
import java.util.*;

public class Weather_activity extends AppCompatActivity {
    private static final String DBNAME = "myDB.db";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private boolean isTurnOn = true;
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
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
        //取得Location
        Bundle bundle = getIntent().getExtras();
        currentCity = bundle.getString("AdminArea");

        this.showWeatherData();
        RelativeLayout main = findViewById(R.id.easylist);
        main.setVisibility(View.VISIBLE);
        Animation anim_trans = AnimationUtils.loadAnimation(Weather_activity.this,R.anim.translate_up);
        anim_trans.setDuration(500);
        main.startAnimation(anim_trans);
    }

    private void showWeatherData()
    {
        this.checkAndFixCurrentCity();
        if(WeatherPackage.getCurrentCity() == null)
            WeatherPackage.setCurrentCity(currentCity);

        ArrayList<DataPackage> weatherData = WeatherPackage.getRcvWeatherData();

        final int WEEK_SIZE = 14;

        this.assignWeek2Text();
        if(weatherData == null || weatherData.size() < WEEK_SIZE)
        {
            //無法連線時的狀況，需要對此部分進行處理
            this.setDataVisibility(View.INVISIBLE);
            return;
        }
        else
            this.setDataVisibility(View.VISIBLE);

        for(int i = 0;i < WEEK_SIZE; i++)
        {
            WeatherPackage currentWeather = (WeatherPackage) weatherData.get(i);
            ImageView tempImage;
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
                    tempImage = findViewById(R.id.Conditions);
                    WeatherPackage.assignCondition2Image(currentWeather.getSituation(), tempImage);
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
                    tempImage = findViewById(R.id.secconditions);
                    WeatherPackage.assignCondition2Image(currentWeather.getSituation(),tempImage);

                    break;
                case 2:
                    assignTemperature2Text(currentWeather, R.id.onedaytemperature);
                    tempImage = findViewById(R.id.onedaycondition);
                    WeatherPackage.assignCondition2Image(currentWeather.getSituation(), tempImage);
                    break;
                case 4:
                    assignTemperature2Text(currentWeather, R.id.twodaytemperature);
                    tempImage = findViewById(R.id.twodayscondition);
                    WeatherPackage.assignCondition2Image(currentWeather.getSituation(), tempImage);
                    break;
                case 6:
                    assignTemperature2Text(currentWeather, R.id.threedaytemperature);
                    tempImage = findViewById(R.id.threedayscondition);
                    WeatherPackage.assignCondition2Image(currentWeather.getSituation(), tempImage);
                    break;
                case 8:
                    assignTemperature2Text(currentWeather, R.id.fourdaytemperature);
                    tempImage = findViewById(R.id.fourdayscondition);
                    WeatherPackage.assignCondition2Image(currentWeather.getSituation(),tempImage);
                    break;
                case 10:
                    assignTemperature2Text(currentWeather, R.id.fivedaytemperature);
                    tempImage = findViewById(R.id.fivedayscondition);
                    WeatherPackage.assignCondition2Image(currentWeather.getSituation(), tempImage);
                    break;
                case 12:
                    assignTemperature2Text(currentWeather, R.id.sixdaytemperature);
                    tempImage = findViewById(R.id.sixodayscondition);
                    WeatherPackage.assignCondition2Image(currentWeather.getSituation(), tempImage);
                    break;
                default:
                    break;
            }
        }
    }

    private void checkAndFixCurrentCity()
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

    private void setDataVisibility(int VIS_ID)
    {
        if((VIS_ID == View.INVISIBLE && !this.isTurnOn) || (VIS_ID == View.VISIBLE && this.isTurnOn))
            return;

        TextView tempText;
        ImageView tempImage;
        int Rid = 0;

        for(int i = 0;i < 15; i++)
        {
            switch (i)
            {
                case 0:
                    Rid = R.id.secondview;
                    break;
                case 1:
                    Rid = R.id.seccoditions;
                    break;
                case 2:
                    Rid = R.id.onedayafter;
                    break;
                case 3:
                    Rid = R.id.twodaysafter;
                    break;
                case 4:
                    Rid = R.id.threedaysafter;
                    break;
                case 5:
                    Rid = R.id.threedaysafter;
                    break;
                case 6:
                    Rid = R.id.fourdaysafter;
                    break;
                case 7:
                    Rid = R.id.fivedaysafter;
                    break;
                case 8:
                    Rid = R.id.sixdaysafter;
                    break;
                case 9:
                    Rid = R.id.onedaytemperature;
                    break;
                case 10:
                    Rid = R.id.twodaytemperature;
                    break;
                case 11:
                    Rid = R.id.threedaytemperature;
                    break;
                case 12:
                    Rid = R.id.fourdaytemperature;
                    break;
                case 13:
                    Rid = R.id.fivedaytemperature;
                    break;
                case 14:
                    Rid = R.id.sixdaytemperature;
                    break;
                default:
                    Rid = R.id.onedayafter;
                    break;
            }
            tempText = findViewById(Rid);
            tempText.setVisibility(VIS_ID);
        }

        for(int i = 0;i < 18; i++)
        {
            Rid = 0;
            switch (i)
            {
                case 0:
                    Rid = R.id.secconditions;
                    break;
                case 1:
                    Rid = R.id.sec_higher_decimaldegree;
                    break;
                case 2:
                    Rid = R.id.sec_higher_digitsdegree;
                    break;
                case 3:
                    Rid = R.id.sec_lower_decimaldegree;
                    break;
                case 4:
                    Rid = R.id.sec_lower_digitsdegree;
                    break;
                case 5:
                    Rid = R.id.secidicators;
                    break;
                case 6:
                    Rid = R.id.oneidicators;
                    break;
                case 7:
                    Rid = R.id.twoidicators;
                    break;
                case 8:
                    Rid = R.id.threeidicators;
                    break;
                case 9:
                    Rid = R.id.fouridicators;
                    break;
                case 10:
                    Rid = R.id.fiveidicators;
                    break;
                case 11:
                    Rid = R.id.sixidicators;
                    break;
                case 12:
                    Rid = R.id.onedaycondition;
                    break;
                case 13:
                    Rid = R.id.twodayscondition;
                    break;
                case 14:
                    Rid = R.id.threedayscondition;
                    break;
                case 15:
                    Rid = R.id.fourdayscondition;
                    break;
                case 16:
                    Rid = R.id.fivedayscondition;
                    break;
                case 17:
                    Rid = R.id.sixodayscondition;
                    break;
                default:
                    Rid = R.id.secconditions;
                    break;
            }
            tempImage = findViewById(Rid);
            tempImage.setVisibility(VIS_ID);
        }

        if(VIS_ID == View.INVISIBLE)
        {
            tempText = findViewById(R.id.Location);
            tempText.setText("---");
            tempText = findViewById(R.id.conditions);
            tempText.setText("無法與伺服器連線");
            this.assignNumber2Image(0, R.id.higher_digitsdegree);
            this.assignNumber2Image(0, R.id.higher_decimaldegree);
            this.assignNumber2Image(0, R.id.lower_decimaldegree);
            this.assignNumber2Image(0, R.id.lower_digitsdegree);
        }

        this.isTurnOn = !this.isTurnOn;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Weather_activity.this.finish();
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}
