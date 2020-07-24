package com.example.lifeassistant_project.menu_activity.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.SchedulePackage;
import com.example.lifeassistant_project.menu_activity.finance.Bookkeeping_activity;
import com.example.lifeassistant_project.menu_activity.finance.Report_type_activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class NewPlan_activity extends AppCompatActivity {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static final String FILTER_TABLE = "filter";
    private static final String BK_TABLE = "record";

    private SQLiteDatabase myDB;
    private Cursor cursor;
    private int mYear, mMonth, mDay;
    private TimePickerDialog timePickerDialog;
    private TimePickerDialog endstimePickerDialog;
    private List<String> list = new ArrayList<>();
    private SchedulePackage sendPackage;
    private int Starting_hour;
    private int Starting_minute;
    private int Ending_hour;
    private int Ending_minute;
    ArrayList<String> stuffList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("   新增記事");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.newstand);
        GregorianCalendar calendar = new GregorianCalendar();

        File dbDir = new File(PATH, "databases");
        dbDir.mkdir();
        File FdbFile = new File(PATH+"/databases",DBNAME);
        if(!FdbFile.exists() || !FdbFile.isFile())
            copyAssets(PATH); //初始資料庫複製到路徑



        //這是開始日期
        final TextView dateText = (TextView) findViewById(R.id.dateinput);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        dateText.setText(formatter.format(new java.util.Date()));
        Button datePicker = (Button) findViewById(R.id.datepicker);
        datePicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final Calendar c=Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(NewPlan_activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String format = setDataFormat(year,month,day);
                        dateText.setText(format);
                    }
                },mYear,mMonth,mDay).show();
            }
        });
        final TextView endsdateText = (TextView) findViewById(R.id.endsdateinput);

        endsdateText.setText(formatter.format(new java.util.Date()));
        Button endsdatePicker = (Button) findViewById(R.id.endsdatepicker);
        endsdatePicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final Calendar c=Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(NewPlan_activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String format = setDataFormat(year,month,day);
                        endsdateText.setText(format);
                    }
                },mYear,mMonth,mDay).show();
            }
        });
        ///////////
        //這是結束日期
        Button cancel = (Button) findViewById(R.id.cancelbutton);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NewPlan_activity.this.finish();
            }
        });
        ////////////
        //這是開始時間
        final TextView timeText = (TextView) findViewById(R.id.timeinput);
        timePickerDialog=new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener(){
            @Override
        public void onTimeSet(TimePicker view,int hourOfDay,int minute){
                timeText.setText(hourOfDay+":"+minute);
                Starting_hour=hourOfDay;
                Starting_minute=minute;
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(calendar.MINUTE),false);
        //////////////////
        //這是結束時間
        final TextView endstimeText = (TextView) findViewById(R.id.endstimeinput);
        endstimePickerDialog=new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view,int hourOfDay,int minute){
                endstimeText.setText(hourOfDay+":"+minute);
                Ending_hour=hourOfDay;
                Ending_minute=minute;
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(calendar.MINUTE),false);
        //////////////////
    }

    private String setDataFormat(int year, int monthOfYear,int dayOfMonth){
        return String.valueOf(year)+"-"+String.valueOf(monthOfYear+1)+"-"+String.valueOf(dayOfMonth);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            NewPlan_activity.this.finish();
        }
        return true;
    }
    public void setStartTime(View v){timePickerDialog.show();}
    public void setEndsTime(View v){endstimePickerDialog.show();}
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }

    private void copyAssets(String path) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getAssets().open(DBNAME);
            out = new FileOutputStream(PATH + "/databases/" + DBNAME);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////////////////////////////////排程資料庫/////////////////////////////////////////////////////////
    private void writeToBKDB(){
        //將表單內容讀入
        int Starting_year = 0;
        int Starting_month = 0;
        int Starting_day = 0;
        int Ending_year = 0;
        int Ending_month = 0;
        int Ending_day = 0;

        String event="0";
        TextView textView = (TextView) findViewById(R.id.eventinput); //事項
        if (!textView.getText().toString().equals(""))
            event = (String) textView.getText();
        else {
            Toast.makeText(this,"請輸入事項",Toast.LENGTH_LONG).show();
            return;
        }

        textView = (TextView) findViewById(R.id.dateinput); //開始日期
        String startingDate = textView.getText().toString();
        if (!startingDate.equals("")) {
            Starting_year = Integer.parseInt(startingDate.substring(0, 4));
            Starting_month = Integer.parseInt(startingDate.substring(5,startingDate.indexOf('-',5)));
            Starting_day = Integer.parseInt(startingDate.substring(startingDate.indexOf('-',5)+1,startingDate.length()));
        }

        textView = (TextView) findViewById(R.id.timeinput); //開始時間
        String startingTime = textView.getText().toString();

        if(startingDate.equals("")) {
            Toast.makeText(this,"請選擇開始日期",Toast.LENGTH_SHORT).show();
            return;
        }else if(startingTime.equals("")){
            Toast.makeText(this,"請輸入開始時間",Toast.LENGTH_SHORT).show();
            return;
        }

        textView = (TextView) findViewById(R.id.endsdateinput); //結束日期
        String endingDate =textView.getText().toString();
        if (!startingDate.equals("")) {
            Ending_year = Integer.parseInt(startingDate.substring(0, 4));
            Ending_month = Integer.parseInt(startingDate.substring(5,startingDate.indexOf('-',5)));
            Ending_day = Integer.parseInt(startingDate.substring(startingDate.indexOf('-',5)+1,startingDate.length()));
        }

        textView = (TextView) findViewById(R.id.endstimeinput); //結束時間
        String endingTime = textView.getText().toString();

        if(endingDate.equals("")) {
            Toast.makeText(this,"請選擇結束日期",Toast.LENGTH_SHORT).show();
            return;
        }else if(endingTime.equals("")){
            Toast.makeText(this,"請輸入結束時間",Toast.LENGTH_SHORT).show();
            return;
        }

        list.clear();
        ContentValues values = new ContentValues();
        values.put("事項",event);
        values.put("開始年",Starting_hour);
        values.put("開始月",Starting_month);
        values.put("開始日",Starting_day);
        values.put("開始小時",Starting_hour);
        values.put("開始分",Starting_minute);
        values.put("結束年",Ending_year);
        values.put("結束月",Ending_month);
        values.put("結束日",Ending_day);
        values.put("結束小時",Ending_hour);
        values.put("結束分",Ending_minute);


        myDB = openOrCreateDatabase(DBNAME,MODE_PRIVATE,null);
        long result=-1L;
        int times=0;
        while (true) {
            times++;
            if(times>100000){
                Toast.makeText(this,"資料儲存過多，請刪除無用的資料",Toast.LENGTH_SHORT).show();
                break;
            }
            try {
                int id = (int) (Math.random() * 99999)+1;
                values.put("id", id);
                // get Account Class
                //WOWthis.sendPackage = new SchedulePackage(id, event, Starting_year, Starting_month, Starting_day, Ending_year, Ending_month, Ending_day);
                //WOWthis.sendPackage.setRequestAction(0);
                //
                result = myDB.insert(BK_TABLE, null, values);
                break;
            }catch (Exception e){
                continue;
            }
        }
        if(result!=-1L){
            myDB.close();
            //Toast.makeText(this,"新增成功",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,"新增失敗",Toast.LENGTH_SHORT).show();
        }

        NewPlan_activity.this.finish();
    }
    //////////
    /*
     * 一既有的工具程式，可將來源 InputStream 物件所指向的資料串流
     * 拷貝到OutputStream 物件所指向的資料串流去
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[in.available()];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

}
