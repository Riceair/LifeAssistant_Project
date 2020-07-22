package com.example.lifeassistant_project.menu_activity.schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.example.lifeassistant_project.menu_activity.finance.Report_type_activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ViewPlan_activity extends AppCompatActivity {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static final String FILTER_TABLE = "filter";
    private static final String BK_TABLE = "record";

    private SQLiteDatabase myDB;
    private Cursor cursor;
    ArrayList<String> stuffList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<>();
    ArrayList<String> timeList = new ArrayList<>();
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
        final ListView list = findViewById(R.id.list);

        File dbDir = new File(PATH, "databases");
        dbDir.mkdir();
        File FdbFile = new File(PATH+"/databases",DBNAME);
        if(!FdbFile.exists() || !FdbFile.isFile())
            copyAssets(PATH); //初始資料庫複製到路徑

        ReadDBRecord();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, stuffList);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem=(String) list.getItemAtPosition(position);
                Toast.makeText(ViewPlan_activity.this,clickedItem,Toast.LENGTH_LONG).show();
            }
        });












    }



    private void ReadDBRecord(){
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try {
            cursor = myDB.rawQuery("select schedule_record.事情, schedule_record.年,schedule_record.月,schedule_record.日,schedule_record.開始時間,schedule_record.結束時間 from schedule_record",null);
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(cursor!=null) {
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for (int i=0;i<iRow;i++){
                    String stuffName = cursor.getString(0)+" ("+cursor.getString(1)+"年"+cursor.getString(2)+"月"+cursor.getString(3)+"日) （時間：Starts at"+cursor.getString(4)+"點/ Ends at: "+cursor.getString(5)+"點)";
                    stuffList.add(stuffName);
                    cursor.moveToNext();
                }
                Toast.makeText(this,stuffList.get(0),Toast.LENGTH_SHORT).show();
                // 5. 關閉 DB
                myDB.close();
            }
            else {
                Toast.makeText(this,"Hint 1: 請將db準備好!",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
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
