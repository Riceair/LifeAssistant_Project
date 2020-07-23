package com.example.lifeassistant_project.menu_activity.finance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Report_ultimate_activity extends AppCompatActivity {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static final String FILTER_TABLE = "filter";
    private static final String BK_TABLE = "record";
    private SQLiteDatabase myDB;
    private Cursor cursor;

    private int recordID;
    private TextView costinput,dateinput,filterinput,itemsinput,receiptinput,quotesinput;
    private RadioButton outlay,income;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_ultimate_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("  記帳");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_bookkeeping);

        //取得傳遞過來的記帳ID
        Bundle bundle=getIntent().getExtras();
        recordID=bundle.getInt("RECORDID");

        costinput=findViewById(R.id.costinput);
        dateinput=findViewById(R.id.dateinput);
        filterinput=findViewById(R.id.filterinput);
        itemsinput=findViewById(R.id.itemsinput);
        receiptinput=findViewById(R.id.receiptinput);
        quotesinput=findViewById(R.id.quotesinput);
        outlay=findViewById(R.id.outlay);
        income=findViewById(R.id.income);

        ReadDBRecord();
    }

    //讀取類別DB
    public void clickToReadFDB(View view){
        toModDB();
    }

    public void clickToUpdate(View view){

    }

    public void clickToDelete(View view){

    }

    public void toModDB(){

    }

    //讀資料庫
    private void ReadDBRecord(){
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try{
            cursor=myDB.rawQuery("select * from record where record.id=?",new String[]{String.valueOf(recordID)});
            if(cursor!=null){
                cursor.moveToFirst();
                setDBInf();
            }else{
                Toast.makeText(this,"資料庫無資料",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    //依照資料庫讀進的資料填入
    private void setDBInf(){
        costinput.setText(String.valueOf(cursor.getInt(0)));
        dateinput.setText(String.valueOf(cursor.getInt(1))+"-"+cursor.getInt(2)+"-"+cursor.getInt(3));
        filterinput.setText(cursor.getString(4));
        itemsinput.setText(cursor.getString(5));
        if(cursor.getString(6)!=null)
            receiptinput.setText(cursor.getString(6));
        if(cursor.getString(7)!=null)
            quotesinput.setText(cursor.getString(7));
        if(cursor.getInt(8)==1){
            outlay.setChecked(true);
        }else{
            income.setChecked(true);
        }
    }

    public void ToCancel(View view){
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Report_ultimate_activity.this.finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }

    //第一次開啟App才會啟用
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
}