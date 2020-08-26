package com.example.lifeassistant_project.menu_activity.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.packages.SchedulePackage;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Planner_activity extends AppCompatActivity {
    private static final String TAG = "Planner_activity";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    public SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private boolean shouldShow = false;
    public CompactCalendarView compactCalendarView;
    private static final String DBNAME = "myDB.db";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private ArrayList<String> stuffList = new ArrayList<>();
    private ArrayList<String> stuffEndingList = new ArrayList<>();
    private ArrayList<String> stuffNameList = new ArrayList<>();
    public String datewasclicked;
    private String namewasfilledin;
    public Integer status=0;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("   排程");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.newstand);
        //初始化日曆頭標與語系
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setLocale(TimeZone.getTimeZone("GMT-8:00"),Locale.CHINESE);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        final TextView monthlayout = (TextView) findViewById(R.id.Month_layout);
        monthlayout.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        Date datenow =new Date();
        datewasclicked=formatter.format(datenow);
        ReadDBRecord();
        initial(null);
        //Query
        //List<Event> events = compactCalendarView.getEvents(1597320075000L); // can also take a Date object
        // events has size 2 with the 2 events inserted previously
        //Log.d(TAG, "Events: " + events);
        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked)
            {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                datewasclicked=formatter.format(dateClicked);

                namewasfilledin="";
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth)
            {
                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                monthlayout.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            }
        });

    }

    private void ReadDBRecord(){
        stuffList.clear();
        stuffEndingList.clear();
        stuffNameList.clear();
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try {
            cursor = myDB.rawQuery("select schedule_record.事情, schedule_record.年,schedule_record.月,schedule_record.日,schedule_record.開始時間,schedule_record.結束時間 from schedule_record",null);
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(cursor!=null) {
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for (int i=0;i<iRow;i++){
                    SchedulePackage temp = new SchedulePackage(
                            0,
                            cursor.getString(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getInt(5));
                    String tempString = new String(Integer.toString(temp.getStartDateInFormat().getYear()) + "-" +
                            String.format("%02d", temp.getStartDateInFormat().getMonth()) + "-" +
                            String.format("%02d", temp.getStartDateInFormat().getDay()) + " " +
                            String.format("%02d", temp.getStartDateInFormat().getHour()) + ":" +
                            String.format("%02d", temp.getStartDateInFormat().getMinute()) + ":00");
                    stuffList.add(tempString);
                    tempString = new String(Integer.toString(temp.getEndDateInFormat().getYear()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getMonth()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getDay()) + " " +
                            String.format("%02d", temp.getEndDateInFormat().getHour()) + ":" +
                            String.format("%02d", temp.getEndDateInFormat().getMinute()) + ":00");
                    stuffEndingList.add(tempString);
                    stuffNameList.add(temp.getTodo());

                    cursor.moveToNext();
                }
                // 5. 關閉 DB
                myDB.close();
            }
            else
                {
                Toast.makeText(this,"Hint 1: 請將db準備好!",Toast.LENGTH_SHORT).show();
                }
        }
        catch (Exception e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
    public void initial(View view)
    {
        ReadDBRecord();
        //初始化日曆頭標與語系
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setLocale(TimeZone.getTimeZone("GMT-8:00"),Locale.CHINESE);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        final TextView monthlayout = (TextView) findViewById(R.id.Month_layout);
        monthlayout.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        // 這是在初始化日期格式，轉成mileseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //測試用
        compactCalendarView.removeAllEvents();
        for (int i=0;i<stuffList.size();i++)
        {
            String myDate=stuffList.get(i);
            String[] myDateparts = myDate.split(" ");
            String myStartingDate  = myDateparts[0]+" 00:00:00";


            String myEndingDate=stuffEndingList.get(i);
            String mystuffName=stuffNameList.get(i);
            try {
                Date date = sdf.parse(myStartingDate);
                long millis = date.getTime();
                Date endingDate = sdf.parse(myEndingDate);
                long endingmillis=endingDate.getTime();
                // 這是在加事情
                long m=millis;
                while (m <= endingmillis) {
                    m=m+86400000;
                    Event ev1 = new Event(Color.RED, m, mystuffName);
                    compactCalendarView.addEvent(ev1);


                }
            } catch (ParseException e) {
                e.printStackTrace();

            }

        }

    }
    public void clickNewPlan(View view){
        Intent intent = new Intent(view.getContext(),NewPlan_activity.class);
        status=1;
        intent.putExtra("clickeddate",datewasclicked);
        intent.putExtra("clickedname",namewasfilledin);
        intent.putExtra("clickedstatus",status);
        startActivityForResult(intent,0);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ReadDBRecord();
        initial(null);

    }
    public void clickTodayPlan(View view){
        Intent intent = new Intent(view.getContext(),ViewPlan_activity.class);
        view.getContext().startActivity(intent);
    }

    public void clickViewPlan(View view){
        Intent intent = new Intent(view.getContext(),ViewPlan_activity.class);
        view.getContext().startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Planner_activity.this.finish();
        }
        return true;
    }

//    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}