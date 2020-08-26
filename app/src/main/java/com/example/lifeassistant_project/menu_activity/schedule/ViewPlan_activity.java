package com.example.lifeassistant_project.menu_activity.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.activity_update.packages.SchedulePackage;


import java.util.ArrayList;

public class ViewPlan_activity extends AppCompatActivity {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";

    private SQLiteDatabase myDB;
    private Cursor cursor;

    private ArrayList<String> stuffList = new ArrayList<>();
    private ArrayList<String> stuffEndingList = new ArrayList<>();
    private ArrayList<String> stuffNameList = new ArrayList<>();
    private ListView list;
    public View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plan_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("   檢視記事");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.newstand);

        list = findViewById(R.id.list);
        ReadDBRecord();
        setList();
    }

    private void setList(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, stuffNameList);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String clickedItem=(String) list.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(),NewPlan_activity.class);
                String[] parts = stuffList.get(position).split(" ");
                String[] endingparts = stuffEndingList.get(position).split(" ");
                String datewasclicked = parts[0];
                String timewasclicked = parts[1];
                String endingdatewasclicked = endingparts[0];
                String endingtimewasclicked = endingparts[1];
                String namewasfilledin=stuffNameList.get(position);
                intent.putExtra("clickeddate",datewasclicked);
                intent.putExtra("clickedtime",timewasclicked);
                intent.putExtra("clickedname",namewasfilledin);
                intent.putExtra("clickedendingdate",endingdatewasclicked);
                intent.putExtra("clickedendingtime",endingtimewasclicked);

                ViewPlan_activity.this.startActivityForResult(intent,0);

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
    @Override
        protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            ReadDBRecord();
            setList();


        }


    }
    public void clickBackPlanner(View view){
        Intent intent = new Intent(getApplicationContext(), Planner_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true); startActivity(intent);
        startActivityForResult(intent, 3);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            ViewPlan_activity.this.finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);

    }


}
