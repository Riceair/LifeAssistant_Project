package com.example.lifeassistant_project.menu_activity.finance.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.menu_activity.finance.Bookkeeping_activity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.lifeassistant_project.features_class.AndroidCommonFunction.*;

public class Report_type_detail_activity extends AppCompatActivity {
    private static final String DBNAME = "myDB.db";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private int inOutAttribute; //收入或支出(資料庫Query)
    private String type,detail;
    private int year,month,day;
    List<Integer> amount_list=new ArrayList<>();
    List<Integer> year_list=new ArrayList<>();
    List<Integer> month_list=new ArrayList<>();
    List<Integer> day_list=new ArrayList<>();
    List<Integer> recordID_list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_type_detail_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("  報表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_report);

        //取得傳遞過來要Query的訊息
        Bundle bundle = getIntent().getExtras();
        type=bundle.getString("TYPE");
        detail=bundle.getString("DETAIL");
        inOutAttribute=bundle.getInt("inOutAttribute");
        year=bundle.getInt("YEAR");
        month=bundle.getInt("MONTH");
        day=bundle.getInt("DAY");

        ReadDBRecord(year,month,day);
        setInf();
        setLayoutSize();
    }

    private void setLayoutSize(){
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float heightRate=dm.heightPixels/(float)1920;
        float widthRate=dm.widthPixels/(float)1080;

        RelativeLayout mainly=findViewById(R.id.mainly);
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mainly.getLayoutParams();
        params.height= (int) (params.height*heightRate);
        params.width= (int) (params.width*widthRate);
        mainly.setLayoutParams(params);

        if(dm.widthPixels==1080 || dm.widthPixels==1440)
            return;

        changeLinearViewSize((ViewGroup) findViewById(R.id.record_list),widthRate,heightRate);
    }

    ////////////////////////////////////////////////////數據處理///////////////////////////////////////////////////
    //讀資料庫
    private void ReadDBRecord(int year,int month,int day){
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        amount_list.clear();
        year_list.clear();
        month_list.clear();
        day_list.clear();
        recordID_list.clear();
        try {
            if(month==0){
                if(year==0) { //顯示所有紀錄
                    cursor = myDB.rawQuery("select record.金額, record.年, record.月, record.日, record.id" +
                                    " from record where record.收支屬性=? and record.分類=? and record.細項=?"
                            , new String[]{String.valueOf(inOutAttribute), type, detail});
                    getSupportActionBar().setSubtitle("  所有紀錄:"+type+"-"+detail);
                }
                else {
                    cursor = myDB.rawQuery("select record.金額, record.年, record.月, record.日, record.id" +
                                    " from record where record.年=? and record.收支屬性=? and record.分類=? and record.細項=?"
                            , new String[]{String.valueOf(year), String.valueOf(inOutAttribute), type, detail});
                    getSupportActionBar().setSubtitle("  "+String.valueOf(year)+"年: "+type+"-"+detail);
                }
            }else if(day!=0){ //自訂日期
                cursor = myDB.rawQuery("select record.金額, record.年, record.月, record.日, record.id" +
                                " from record where record.年=? and record.月=? and record.日=? and record.收支屬性=? and record.分類=? and record.細項=?"
                        ,new String[]{String.valueOf(year),String.valueOf(month),String.valueOf(day),String.valueOf(inOutAttribute),type,detail});
                getSupportActionBar().setSubtitle("  "+String.valueOf(year)+"年"+String.valueOf(month)+"月"+String.valueOf(day)+"日: "+type+"-"+detail);
            }else {
                cursor = myDB.rawQuery("select record.金額, record.年, record.月, record.日, record.id" +
                                " from record where record.年=? and record.月=? and record.收支屬性=? and record.分類=? and record.細項=?"
                        , new String[]{String.valueOf(year), String.valueOf(month),String.valueOf(inOutAttribute),type,detail});
                getSupportActionBar().setSubtitle("  "+String.valueOf(year)+"年"+String.valueOf(month)+"月: "+type+"-"+detail);
            }
            if(cursor!=null) { //取得資料
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for (int i=0;i<iRow;i++){
                    int amount = cursor.getInt(0);
                    int dbYear = cursor.getInt(1);
                    int dbMonth = cursor.getInt(2);
                    int dbDay = cursor.getInt(3);
                    int id = cursor.getInt(4);
                    amount_list.add(amount);
                    year_list.add(dbYear);
                    month_list.add(dbMonth);
                    day_list.add(dbDay);
                    recordID_list.add(id);
                    cursor.moveToNext();
                }
                // 5. 關閉 DB
                myDB.close();
            }
            else {
                //Toast.makeText(this,"Hint 1: 請將db準備好!",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this,"Query wrong",Toast.LENGTH_SHORT).show();
        }
    }

    //設定顯示的資料
    private void setInf(){
        LinearLayout recordLinear = (LinearLayout) findViewById(R.id.record_list); //要顯示的layout
        recordLinear.removeAllViews();
        for(int i=0;i<recordID_list.size();i++){
            RelativeLayout recordlist_element_detail = (RelativeLayout) getLayoutInflater().inflate(R.layout.report_recordlist_element_detail,null);
            final TextView detailName = (TextView) recordlist_element_detail.findViewById(R.id.detail);
            TextView detailAmount = (TextView) recordlist_element_detail.findViewById(R.id.cost);
            TextView detailDate = (TextView) recordlist_element_detail.findViewById(R.id.date);
            detailName.setText(detail);
            if(inOutAttribute==1){ //支出
                detailAmount.setText("-$"+String.valueOf(amount_list.get(i)));
                detailAmount.setTextColor(Color.RED);
            }else{ //收入
                detailAmount.setText("+$"+String.valueOf(amount_list.get(i)));
                detailAmount.setTextColor(Color.rgb(4,117,2));
            }
            //取得星期幾
            final String strDate=String.valueOf(year_list.get(i)+"/"+month_list.get(i)+"/"+day_list.get(i));
            SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");
            Date date=null;
            try {
                date = format.parse(strDate);
            }catch (ParseException e){ }
            final String week=" "+getWeekOfDate(date); //儲存星期幾
            detailDate.setText(strDate+week);

            final int recordID = recordID_list.get(i);
            recordlist_element_detail.setOnClickListener(new View.OnClickListener() { //綁定依照類別查詢的Activity
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(Report_type_detail_activity.this, Bookkeeping_activity.class);
                    intent.putExtra("CALL","modBookkeeping");
                    intent.putExtra("RECORDID",recordID);
                    Report_type_detail_activity.this.startActivityForResult(intent,0);
                    overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
                }
            });
            recordLinear.addView(recordlist_element_detail);
            //新增底線
            addBaseline(this,recordLinear,R.drawable.segment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            Report_type_detail_activity.this.finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ReadDBRecord(year,month,day);
        setInf();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.translate_out);
    }
}