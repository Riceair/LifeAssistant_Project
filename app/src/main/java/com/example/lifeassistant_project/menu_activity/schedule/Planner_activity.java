package com.example.lifeassistant_project.menu_activity.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    private ArrayList<Integer> stuffIDList = new ArrayList<>();
    public String datewasclicked;
    private String namewasfilledin;
    public Integer status=0,stuffcount=0;
    private String datewaslastclicked="";
    private ArrayList<String> EventList = new ArrayList<>();
    public Integer clicked_year=0,clicked_day=0,clicked_month=0,selectstatus=0;

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
        CompactCalendarView.CompactCalendarViewListener func = new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                ListView list = findViewById(R.id.list);
                list.setOnItemClickListener(null);
                RelativeLayout sec_tabs = findViewById(R.id.sec_tabs);
                RelativeLayout sec = findViewById(R.id.secondary);
                EventList.clear();
                datewasclicked = formatter.format(dateClicked);
                String[] Eventtimeparts = datewasclicked.split("-");
                clicked_year=Integer.parseInt(Eventtimeparts[0]);
                clicked_month=Integer.parseInt(Eventtimeparts[1]);
                clicked_day=Integer.parseInt(Eventtimeparts[2]);
                namewasfilledin = "";
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);

//

                for (int i=0 ; i<events.size();i++)
                {

                    String[] Eventparts = events.get(i).toString().split("=");

                    EventList.add(Eventparts[3]);


                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Planner_activity.this, android.R.layout.simple_list_item_1,EventList);
                list.setAdapter(arrayAdapter);
                if(EventList.isEmpty()==false)
                { sec_tabs.setVisibility(View.VISIBLE);
                    sec.setVisibility(View.VISIBLE);
                }
                else
                {sec_tabs.setVisibility(View.INVISIBLE);
                    sec.setVisibility(View.INVISIBLE);}
            }


            @Override
            public void onMonthScroll(Date firstDayOfNewMonth)
            {

                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                monthlayout.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

                onDayClick(firstDayOfNewMonth);
            }
        };
        //Query
        //List<Event> events = compactCalendarView.getEvents(1597320075000L); // can also take a Date object
        // events has size 2 with the 2 events inserted previously
        //Log.d(TAG, "Events: " + events);
        // define a listener to receive callbacks when certain events happen.

        compactCalendarView.setListener(func);
        func.onDayClick(datenow);

    }

    private void ReadDBRecord() {
        stuffList.clear();
        stuffEndingList.clear();
        stuffNameList.clear();
        stuffIDList.clear();
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try {
            cursor = myDB.rawQuery("select schedule_record.事情, schedule_record.年,schedule_record.月,schedule_record.日,schedule_record.開始時間,schedule_record.結束時間,schedule_record.id from schedule_record", null);
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (cursor != null) {
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for (int i = 0; i < iRow; i++) {
                    SchedulePackage temp = new SchedulePackage(
                            cursor.getInt(6),
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
                    stuffIDList.add(temp.getID());
                    stuffcount=stuffIDList.size();
                    cursor.moveToNext();
                }
                // 5. 關閉 DB
                myDB.close();
            } else {
                Toast.makeText(this, "Hint 1: 請將db準備好!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void initial(View view)
    {
        ReadDBRecord();
        //初始化日曆頭標與語系
        Button p1_button = (Button)findViewById(R.id.indicator);
        p1_button.setText(stuffcount.toString());
        if (stuffcount==0)
            p1_button.setBackgroundResource(R.drawable.stuff_indicator_greendot);
        else if (stuffcount>0 && stuffcount<=3)
            p1_button.setBackgroundResource(R.drawable.stuff_indicator_heavygreendot);
        else if (stuffcount>3 && stuffcount<=6)
            p1_button.setBackgroundResource(R.drawable.stuff_indicator_yellowdot);
        else if (stuffcount>6 && stuffcount<=10)
            p1_button.setBackgroundResource(R.drawable.stuff_indicator_orangedot);
        else
            p1_button.setBackgroundResource(R.drawable.stuff_indicator_reddot);
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
        intent.putExtra("selstatus", selectstatus);
        intent.putExtra("clicked_year", clicked_year);
        intent.putExtra("clicked_month", clicked_month);
        intent.putExtra("clicked_day", clicked_day);
        startActivityForResult(intent,0);
    }



    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ReadDBRecord();
        initial(null);

    }

    public void clickViewPlan(View view){
        selectstatus=0;
        Intent intent = new Intent(view.getContext(),ViewPlan_activity.class);
        view.getContext().startActivity(intent);
        intent.putExtra("lastclickeddate",datewaslastclicked);
        startActivityForResult(intent,0);
    }
    public void clickEdit(View view){
        selectstatus=1;
        Intent intent = new Intent(view.getContext(),ViewPlan_activity.class);
        view.getContext().startActivity(intent);
        intent.putExtra("clicked_year",clicked_year);
        intent.putExtra("clicked_month",clicked_month);
        intent.putExtra("clicked_day",clicked_day);
        intent.putExtra("selstatus",selectstatus);
        startActivityForResult(intent,0);

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