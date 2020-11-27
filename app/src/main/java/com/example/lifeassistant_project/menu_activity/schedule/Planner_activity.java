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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.MainActivity;
import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.packages.SchedulePackage;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
    private ArrayList<String> stuffListBackup = new ArrayList<>();
    private ArrayList<String> stuffEndingListBackup = new ArrayList<>();
    private ArrayList<String> stuffNameListBackup = new ArrayList<>();
    private ArrayList<Integer> stuffIDListBackup = new ArrayList<>();
    private ArrayList<Long> stuffListinDateFormatBackup = new ArrayList<>();
    private ArrayList<Long> stuffEndingListinDateFormatBackup = new ArrayList<>();
    private ArrayList<Long> stuffListinDateFormat = new ArrayList<>();
    private ArrayList<Long> stuffEndingListinDateFormat = new ArrayList<>();
    private ArrayList<String> stuffTitleList = new ArrayList<>();
    public String datewasclicked;
    private String namewasfilledin;
    public Integer status=0,stuffcount=0;
    public String datewaslastclicked="";
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
        getSupportActionBar().hide();
//        //暫時取消全螢幕模式
//        final View decorView = getWindow().getDecorView();
//        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//               if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                    decorView.setSystemUiVisibility(
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                }
//            }
//       });
        //初始化日曆頭標與語系
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        compactCalendarView.setLocale(TimeZone.getTimeZone("GMT+:8:00"),Locale.CHINESE);
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
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
                    selectstatus=1;
                    Intent intent = new Intent(view.getContext(),ViewPlan_activity.class);
                    view.getContext().startActivity(intent);
                    intent.putExtra("clicked_year",clicked_year);
                    intent.putExtra("clicked_month",clicked_month);
                    intent.putExtra("clicked_day",clicked_day);
                    intent.putExtra("selstatus",selectstatus);
                    startActivityForResult(intent,0);

                    }
                });
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
                for (int i=0 ; i<events.size();i++)
                {
                    String[] Eventparts = events.get(i).toString().split("=");
                    String tempString=Eventparts[3];

                    EventList.add(tempString.substring(0, tempString.length() - 1));
                }
                for(int i=0;i<EventList.size();i++)
                {
                    int length = EventList.get(i).length();
                    String ele = EventList.get(i);
                    if(length > 15)
                    {
                        EventList.set(i, ele.substring(0, 15)+"⋯");
                    }
                    else
                    {
                        while(length < 15)
                        {
                            ele = ele + "　";
                            length++;
                        }
                        EventList.set(i, ele);
                    }
                }



                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Planner_activity.this, android.R.layout.simple_list_item_1,EventList);
                list.setAdapter(arrayAdapter);
                if(!EventList.isEmpty())
                {
                    sec_tabs.setVisibility(View.VISIBLE);
                    sec.setBackgroundResource(R.drawable.planner_stuff_background);
                }
                else
                {
                    sec_tabs.setVisibility(View.INVISIBLE);
                    sec.setBackgroundResource(R.drawable.planner_stuff_empty);
                }
                Animation anim_transDown = AnimationUtils.loadAnimation(Planner_activity.this,R.anim.translate_up);
                anim_transDown.setDuration(500);
                sec.startAnimation(anim_transDown);
            }


            @Override
            public void onMonthScroll(Date firstDayOfNewMonth)
            {

                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                monthlayout.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

                onDayClick(firstDayOfNewMonth);
            }
        };
        compactCalendarView.setListener(func);
        func.onDayClick(datenow);
    }

    private void ReadDBRecord() {
        stuffList.clear();
        stuffEndingList.clear();
        stuffNameList.clear();
        stuffIDList.clear();
        stuffListinDateFormat.clear();
        stuffEndingListinDateFormat.clear();
        stuffListBackup.clear();
        stuffEndingListBackup.clear();
        stuffNameListBackup.clear();
        stuffIDListBackup.clear();
        stuffEndingListinDateFormatBackup.clear();
        stuffListinDateFormatBackup.clear();
        stuffTitleList.clear();
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try {
            cursor = myDB.rawQuery("select schedule_record.事情, schedule_record.年,schedule_record.月,schedule_record.日,schedule_record.開始時間,schedule_record.結束時間,schedule_record.id from schedule_record", null);
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
                    tempString = new String(Integer.toString(temp.getStartDateInFormat().getYear()) + "-" +
                            String.format("%02d", temp.getStartDateInFormat().getMonth()) + "-" +
                            String.format("%02d", temp.getStartDateInFormat().getDay()) + " 00:00:00");
                    Date tempdate = null;
                    tempdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempString);
                    long tempmilis = tempdate.getTime();
                    stuffListinDateFormat.add(tempmilis);
                    tempString = new String(Integer.toString(temp.getEndDateInFormat().getYear()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getMonth()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getDay()) + " " +
                            String.format("%02d", temp.getEndDateInFormat().getHour()) + ":" +
                            String.format("%02d", temp.getEndDateInFormat().getMinute()) + ":00");
                    stuffEndingList.add(tempString);
                    tempString = new String(Integer.toString(temp.getEndDateInFormat().getYear()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getMonth()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getDay()) + " 23:59:59");
                    tempdate = null;
                    tempdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempString);
                    tempmilis = tempdate.getTime();
                    stuffEndingListinDateFormat.add(tempmilis);
                    stuffNameList.add(temp.getTodo());
                    stuffIDList.add(temp.getID());
                    stuffcount=stuffIDList.size();
                    cursor.moveToNext();
                }




                for(int i=stuffList.size()-1;i>=0;i--)
                {
                    int minIndex = stuffListinDateFormat.indexOf(Collections.min(stuffListinDateFormat));
                    stuffListinDateFormatBackup.add(stuffListinDateFormat.get(minIndex));
                    stuffEndingListinDateFormatBackup.add(stuffEndingListinDateFormat.get(minIndex));
                    stuffListBackup.add(stuffList.get(minIndex));
                    stuffEndingListBackup.add(stuffEndingList.get(minIndex));
                    stuffIDListBackup.add(stuffIDList.get(minIndex));
                    stuffNameListBackup.add(stuffNameList.get(minIndex));

                    stuffList.set(minIndex,"");
                    stuffNameList.set(minIndex,"");
                    stuffEndingList.set(minIndex,"");
                    stuffListinDateFormat.set(minIndex,Long.MAX_VALUE);
                    stuffEndingListinDateFormat.set(minIndex,Long.MAX_VALUE);
                    stuffIDList.set(minIndex,Integer.MAX_VALUE);


                }

                stuffList.clear();
                stuffNameList.clear();
                stuffEndingList.clear();
                stuffListinDateFormat.clear();
                stuffEndingListinDateFormat.clear();
                stuffIDList.clear();

                for(int i=0;i<stuffListBackup.size();i++)
                {
                    stuffListinDateFormat.add(stuffListinDateFormatBackup.get(i));
                    stuffEndingListinDateFormat.add(stuffEndingListinDateFormatBackup.get(i));
                    stuffList.add(stuffListBackup.get(i));
                    stuffEndingList.add(stuffEndingListBackup.get(i));
                    stuffIDList.add(stuffIDListBackup.get(i));
                    stuffNameList.add(stuffNameListBackup.get(i));

                }


                for(int i=0;i<stuffList.size();i++)
                {
                    stuffTitleList.add(stuffNameList.get(i)+" ("+stuffList.get(i).split(" ")[0]+" 至 "+stuffEndingList.get(i).split(" ")[0]);

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

    private void ReadSpecifiedRecord() {
        stuffList.clear();
        stuffEndingList.clear();
        stuffNameList.clear();
        stuffIDList.clear();
        stuffEndingListinDateFormat.clear();
        stuffListinDateFormat.clear();
        stuffListBackup.clear();
        stuffEndingListBackup.clear();
        stuffNameListBackup.clear();
        stuffIDListBackup.clear();
        stuffEndingListinDateFormatBackup.clear();
        stuffListinDateFormatBackup.clear();
        stuffTitleList.clear();
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try {
            cursor = myDB.rawQuery("select schedule_record.事情, schedule_record.年,schedule_record.月,schedule_record.日,schedule_record.開始時間,schedule_record.結束時間,schedule_record.id from schedule_record", null);


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
                    tempString = new String(Integer.toString(temp.getStartDateInFormat().getYear()) + "-" +
                            String.format("%02d", temp.getStartDateInFormat().getMonth()) + "-" +
                            String.format("%02d", temp.getStartDateInFormat().getDay()) + " 00:00:00");
                    Date tempdate = null;
                    tempdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempString);
                    long tempmilis = tempdate.getTime();
                    stuffListinDateFormat.add(tempmilis);
                    tempString = new String(Integer.toString(temp.getEndDateInFormat().getYear()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getMonth()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getDay()) + " " +
                            String.format("%02d", temp.getEndDateInFormat().getHour()) + ":" +
                            String.format("%02d", temp.getEndDateInFormat().getMinute()) + ":00");
                    stuffEndingList.add(tempString);

                    tempString = new String(Integer.toString(temp.getEndDateInFormat().getYear()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getMonth()) + "-" +
                            String.format("%02d", temp.getEndDateInFormat().getDay()) + " 23:59:59");
                    tempdate = null;
                    tempdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempString);
                    tempmilis = tempdate.getTime();
                    stuffEndingListinDateFormat.add(tempmilis);
                    stuffNameList.add(temp.getTodo());
                    stuffIDList.add(temp.getID());
                    stuffcount=stuffIDList.size();
                    cursor.moveToNext();
                }
                // 5. 關閉 DB
                myDB.close();


                String tempString = new String(Integer.toString(clicked_year) + "-" +
                        String.format("%02d", clicked_month) + "-" +
                        String.format("%02d", clicked_day) + " 12:00:00");
                Date tempdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempString);
                long tempmilis = tempdate.getTime();

                for(int i=stuffList.size()-1;i>=0;i--)
                {
                    if (tempmilis>stuffListinDateFormat.get(i))
                    {
                        if (tempmilis<stuffEndingListinDateFormat.get(i))
                        {

                        }
                        else
                        {
                            stuffList.remove(i);
                            stuffEndingList.remove(i);
                            stuffIDList.remove(i);
                            stuffNameList.remove(i);
                            stuffListinDateFormat.remove(i);
                            stuffEndingListinDateFormat.remove(i);
                        }
                    }
                    else
                    { stuffList.remove(i);
                        stuffEndingList.remove(i);
                        stuffIDList.remove(i);
                        stuffNameList.remove(i);
                        stuffListinDateFormat.remove(i);
                        stuffEndingListinDateFormat.remove(i);
                    }

                }

                for(int i=stuffList.size()-1;i>=0;i--)
                {
                    int minIndex = stuffListinDateFormat.indexOf(Collections.min(stuffListinDateFormat));
                    stuffListinDateFormatBackup.add(stuffListinDateFormat.get(minIndex));
                    stuffEndingListinDateFormatBackup.add(stuffEndingListinDateFormat.get(minIndex));
                    stuffListBackup.add(stuffList.get(minIndex));
                    stuffEndingListBackup.add(stuffEndingList.get(minIndex));
                    stuffIDListBackup.add(stuffIDList.get(minIndex));
                    stuffNameListBackup.add(stuffNameList.get(minIndex));
                    stuffList.set(minIndex,"");
                    stuffNameList.set(minIndex,"");
                    stuffEndingList.set(minIndex,"");
                    stuffListinDateFormat.set(minIndex,Long.MAX_VALUE);
                    stuffEndingListinDateFormat.set(minIndex,Long.MAX_VALUE);
                    stuffIDList.set(minIndex,Integer.MAX_VALUE);


                }

                stuffList.clear();
                stuffNameList.clear();
                stuffEndingList.clear();
                stuffListinDateFormat.clear();
                stuffEndingListinDateFormat.clear();
                stuffIDList.clear();

                for(int i=0;i<stuffListBackup.size();i++)
                {
                    stuffListinDateFormat.add(stuffListinDateFormatBackup.get(i));
                    stuffEndingListinDateFormat.add(stuffEndingListinDateFormatBackup.get(i));
                    stuffList.add(stuffListBackup.get(i));
                    stuffEndingList.add(stuffEndingListBackup.get(i));
                    stuffIDList.add(stuffIDListBackup.get(i));
                    stuffNameList.add(stuffNameListBackup.get(i));

                }



                for(int i=0;i<stuffList.size();i++)
                {
                    stuffTitleList.add(stuffNameList.get(i)+" ("+stuffList.get(i).split(" ")[0]+" 至 "+stuffEndingList.get(i).split(" ")[0]);

                }






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
//    @Override
//    protected void onResume() {
//        super.onResume();
//        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        final View decorView = getWindow().getDecorView();
//       decorView.setSystemUiVisibility(uiOptions);
//    }
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
    public void clickBack(View view){
//        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    //    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}