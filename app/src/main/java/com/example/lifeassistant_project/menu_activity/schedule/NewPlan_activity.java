package com.example.lifeassistant_project.menu_activity.schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.lifeassistant_project.R;

import com.example.lifeassistant_project.activity_update.ClientProgress;

import com.example.lifeassistant_project.activity_update.static_handler.LoginHandler;
import com.example.lifeassistant_project.activity_update.packages.SchedulePackage;
import com.example.lifeassistant_project.menu_activity.finance.invoice.Invoice_activity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class NewPlan_activity extends AppCompatActivity {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static final String SC_TABLE = "schedule_record";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private int mYear, mMonth, mDay;
    private TimePickerDialog timePickerDialog;
    private TimePickerDialog endstimePickerDialog;
    private List<String> list = new ArrayList<>();
    private SchedulePackage sendPackage;
    private String datewasclicked;
    private String timewasclicked;
    private String endingdatewasclicked;
    private String endingtimewasclicked;
    private String namewasfilledin;
    private ArrayList<String> stuffList = new ArrayList<>();
    private ArrayList<String> stuffEndingList = new ArrayList<>();
    private ArrayList<String> stuffNameList = new ArrayList<>();
    private ArrayList<Integer> stuffIDList = new ArrayList<>();
    private ArrayList<Long> stuffListinDateFormat = new ArrayList<>();
    private ArrayList<Long> stuffEndingListinDateFormat = new ArrayList<>();
    private ArrayList<String> stuffListBackup = new ArrayList<>();
    private ArrayList<String> stuffEndingListBackup = new ArrayList<>();
    private ArrayList<String> stuffNameListBackup = new ArrayList<>();
    private ArrayList<Integer> stuffIDListBackup = new ArrayList<>();
    private ArrayList<Long> stuffListinDateFormatBackup = new ArrayList<>();
    private ArrayList<Long> stuffEndingListinDateFormatBackup = new ArrayList<>();


    private ArrayList<String> stuffTitleList = new ArrayList<>();
    private int year,month,day,start_time,end_time;
    private Integer status=0,selid=0,stuffcount=0,clicked_day=0,clicked_month=0,clicked_year=0,selectstatus=0;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("   新增記事");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.newstand);
//        getSupportActionBar().hide();
        GregorianCalendar calendar = new GregorianCalendar();
        Bundle bundle = getIntent().getExtras();
        datewasclicked = bundle.getString("clickeddate");
        timewasclicked = bundle.getString("clickedtime");
        namewasfilledin = bundle.getString("clickedname");
        endingtimewasclicked = bundle.getString("clickedendingtime");
        endingdatewasclicked = bundle.getString("clickedendingdate");
        clicked_day = bundle.getInt("clicked_day");
        clicked_month = bundle.getInt("clicked_month");
        clicked_year = bundle.getInt("clicked_year");
        selectstatus = bundle.getInt("selstatus");
        status=bundle.getInt("clickedstatus");
        selid=bundle.getInt("clickedID");
        RelativeLayout tabs = findViewById(R.id.tabs);
        Button addbutton = (Button) findViewById(R.id.savebutton);
        if (status==1)
        {
            tabs.setVisibility(View.GONE);
            addbutton.setText("新增");
        }
        if(status==0)
            ReadDBRecord();
        else
            ReadSpecifiedRecord();
        //這是承接事項
        final TextView nameText = (TextView) findViewById(R.id.eventinput);
        nameText.setText(namewasfilledin);
        //這是承接，填入開始日期
        final TextView dateText = (TextView) findViewById(R.id.dateinput);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        dateText.setText(datewasclicked);
        Button datePicker = (Button) findViewById(R.id.datepicker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(NewPlan_activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String format = setDataFormat(year, month, day);
                        dateText.setText(format);
                    }
                }, mYear, mMonth, mDay).show();
            }
        });
        //這是承接，填入結束日期
        final TextView endsdateText = (TextView) findViewById(R.id.endsdateinput);
        endsdateText.setText(endingdatewasclicked);
        Button endsdatePicker = (Button) findViewById(R.id.endsdatepicker);
        endsdatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(NewPlan_activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String format = setDataFormat(year, month, day);
                        endsdateText.setText(format);
                    }
                }, mYear, mMonth, mDay).show();
            }
        });
        ///////////

        Button cancel = (Button) findViewById(R.id.cancelbutton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewPlan_activity.this.finish();
            }
        });
        ////////////
        //這是承接，填入開始時間
        final TextView timeText = (TextView) findViewById(R.id.timeinput);
        timeText.setText(timewasclicked);
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":00");

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false);
        //////////////////
        //這是承接，填入結束時間
        final TextView endstimeText = (TextView) findViewById(R.id.endstimeinput);
        endstimeText.setText(endingtimewasclicked);
        endstimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endstimeText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":00");

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false);
        //////////////////
    }

    private String setDataFormat(int year, int monthOfYear, int dayOfMonth) {
        return String.valueOf(year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);
    }

//q
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            NewPlan_activity.this.finish();
//        }
//        return true;
//    }

    public void setStartTime(View v) {
        timePickerDialog.show();
    }

    public void setEndsTime(View v) {
        endstimePickerDialog.show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.translate_out);
    }

    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId()==android.R.id.home){
           NewPlan_activity.this.finish();
        }
        return true;
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
                    {
                        stuffList.remove(i);
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
    /////////////////////////////////////////////////////////排程資料庫/////////////////////////////////////////////////////////
    private void writeToSCDB() {
        //將表單內容讀入
        int Starting_year = 0;
        int Starting_month = 0;
        int Starting_day = 0;
        int Starting_hour = 0;
        int Starting_minute = 0;
        int Ending_hour = 0;
        int Ending_minute = 0;
        int Ending_year = 0;
        int Ending_month = 0;
        int Ending_day = 0;
        String event = "0";
        TextView textView = (TextView) findViewById(R.id.eventinput); //事項
        event = textView.getText().toString();
        if (textView.getText().toString().equals("")) {
            Toast.makeText(this,"請輸入事項",Toast.LENGTH_LONG).show();
            return;
        }
        textView = (TextView) findViewById(R.id.dateinput); //開始日期
        if (textView.getText().toString().equals("")) {
            Toast.makeText(this,"請輸入日期",Toast.LENGTH_LONG).show();
            return;
        }
        String startingDate = textView.getText().toString();
        String[] startingparts = startingDate.split("-");
        Starting_year = Integer.parseInt(startingparts[0]);
        Starting_month = Integer.parseInt(startingparts[1]);
        Starting_day = Integer.parseInt(startingparts[2]);

        textView = (TextView) findViewById(R.id.timeinput); //開始時間
        if (textView.getText().toString().equals("")) {
            Toast.makeText(this,"請輸入時間",Toast.LENGTH_LONG).show();
            return;
        }
        String startingTime = textView.getText().toString();
        String[] startingTimeparts = startingTime.split(":");
        Starting_hour = Integer.parseInt(startingTimeparts[0]);
        Starting_minute = Integer.parseInt(startingTimeparts[1]);
        setStartDateInFormat(Starting_year,Starting_month,Starting_day,Starting_hour,Starting_minute);
        textView = (TextView) findViewById(R.id.endsdateinput); //結束日期
        if (textView.getText().toString().equals("")) {
            Toast.makeText(this,"請輸入結束日期",Toast.LENGTH_LONG).show();
            return;
        }
        String endingDate = textView.getText().toString();
        String[] endingparts = endingDate.split("-");
        Ending_year = Integer.parseInt(endingparts[0]);
        Ending_month = Integer.parseInt(endingparts[1]);
        Ending_day = Integer.parseInt(endingparts[2]);
        textView = (TextView) findViewById(R.id.endstimeinput); //結束時間
        if (textView.getText().toString().equals("")) {
            Toast.makeText(this,"請輸入結束時間",Toast.LENGTH_LONG).show();
            return;
        }
        String endingTime = textView.getText().toString();
        String[] endingTimeparts = endingTime.split(":");
        Ending_hour = Integer.parseInt(endingTimeparts[0]);
        Ending_minute = Integer.parseInt(endingTimeparts[1]);
        setEndDateInFormat(Ending_year,Ending_month,Ending_day,Ending_hour,Ending_minute);
        list.clear();
        ContentValues values = new ContentValues();
        values.put("事情", event);
        values.put("年", Starting_year);
        values.put("月", Starting_month);
        values.put("日", Starting_day);
        values.put("開始時間", start_time);
        values.put("結束時間", end_time);
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        myDB.execSQL("DELETE FROM " + SC_TABLE + " WHERE " + "id" + "='" + selid + "'");
        long result = -1L;
        int times = 0;
        while (true) {
            times++;
            if (times > 100000) {
                Toast.makeText(this, "資料儲存過多，請刪除無用的資料", Toast.LENGTH_SHORT).show();
                break;
            }
            try {
                int requestActionCode = 0, id = (int) (Math.random() * 99999) + 1;
                if(selid != 0)
                {
                    requestActionCode = 2;
                    id = selid;
                }
                values.put("id", id);
                this.sendPackage = new SchedulePackage(id, event, requestActionCode, Starting_year, Starting_month, Starting_day,
                        Starting_hour, Starting_minute, Ending_year, Ending_month, Ending_day, Ending_hour, Ending_minute);
                result = myDB.insert(SC_TABLE, null, values);
                break;
            } catch (Exception e) {
                continue;
            }
        }
        if (result != -1L) {
            myDB.close();
            } else {
            Toast.makeText(this, "新增失敗", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra("selstatus", selectstatus);
        intent.putExtra("clicked_year", clicked_year);
        intent.putExtra("clicked_month", clicked_month);
        intent.putExtra("clicked_day", clicked_day);
        finish();
    }
    public void clickBackPlanner(View view){
        Intent intent = new Intent(getApplicationContext(), Planner_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true); startActivity(intent);
        startActivityForResult(intent, 3);

    }
    public void clickToUpdateSC(View view) {
        writeToSCDB();
        this.sendSchedulePackage();
    }

    public void clickToDel(final View view) {
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
//       final EditText eventbox = (EditText) findViewById(R.id.eventinput);
        final TextView eventbox = (TextView) findViewById(R.id.eventinput);
        final String eventname = eventbox.getText().toString();
        new AlertDialog.Builder(NewPlan_activity.this)
                .setTitle("確定刪除" + eventname + "?").setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
                myDB.execSQL("DELETE FROM " + SC_TABLE + " WHERE " + "id" + "='" + selid + "'");
                myDB.close();
                //Intent intent = new Intent();
                //setResult(2, intent);

                sendPackage = new SchedulePackage();
                sendPackage.setID(selid);
                sendPackage.setRequestAction(1);
                sendSchedulePackage();

                finish();

                Intent intent = new Intent(getApplicationContext(), Planner_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                intent.putExtra("selstatus", selectstatus);
                intent.putExtra("clicked_year", clicked_year);
                intent.putExtra("clicked_month", clicked_month);
                intent.putExtra("clicked_day", clicked_day);
                startActivity(intent);
                startActivityForResult(intent, 3);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).show();
    }

    public void sendSchedulePackage()
    {
        ClientProgress client = new ClientProgress();
        this.sendPackage.setUser(LoginHandler.getUserName());
        client.setPackage(this.sendPackage);
        Thread conn = new Thread(client);
        conn.start();
    }

    public void setStartDateInFormat(int year, int month, int day, int hour, int minute)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.start_time = hour * 100 + minute;
    }

    public void setEndDateInFormat(int year, int month, int day, int hour, int minute)
    {
        int detTime = 0;
        int[] monthDay = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


        detTime += hour;
        detTime += (day - (this.day)) * 24;
        if(month != this.month)
        {
            if(month > this.month)
            {
                if((this.year % 4 == 0 && this.year % 100 != 0) || (this.year % 400 == 0))
                    monthDay[1] += 1;
                for(int ptr = this.month - 1; ptr < (month - 1); ptr++)
                {
                    detTime += (monthDay[ptr] * 24);
                }
            }
            else
            {
                if(((this.year + 1) % 4 == 0 && (this.year + 1) % 100 != 0) || ((this.year + 1) % 400 == 0))
                    monthDay[1] += 1;
                for(int ptr = month - 1 ; ptr < (this.month - 1); ptr++)
                {
                    detTime -= (monthDay[ptr] * 24);
                }
            }
        }

        if(year > this.year)
        {
            for(int ptr = this.year ; ptr < year ; ptr++)
            {
                if((ptr % 4 == 0 && ptr % 100 != 0) || (ptr % 400 == 0))
                {
                    if((ptr == this.year && this.month <= 2) || (ptr == year && month > 2))
                        detTime += 366 * 24;
                    else if(ptr != this.year && ptr != year)
                        detTime += 366 * 24;
                    else
                        detTime += 365 * 24;
                }

                else
                    detTime += 365 * 24;
            }
        }
        else if(year < this.year)
        {
            this.end_time = -1;
            System.out.println("Set Time Error! You need to set StartDate before set EndDate.");
            return;
        }

        detTime = detTime * 100 + minute;

        this.end_time = detTime;
    }
}