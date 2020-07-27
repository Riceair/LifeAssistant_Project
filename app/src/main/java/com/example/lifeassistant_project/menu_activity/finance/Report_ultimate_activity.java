package com.example.lifeassistant_project.menu_activity.finance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Report_ultimate_activity extends AppCompatActivity {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static final String FILTER_TABLE = "filter";
    private static final String BK_TABLE = "record";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private int recordID;
    private int inOutAttribute;
    private TextView costinput,dateinput,filterinput,itemsinput,receiptinput,quotesinput;
    private RadioButton outlay,income;
    private List<String> out_list = new ArrayList<>();
    private List<String> in_list = new ArrayList<>();
    private List<String> self_out_list = new ArrayList<>();
    private List<String> self_in_list = new ArrayList<>();

    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_ultimate_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("  編輯記帳");
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

        ReadDBRecord(); //讀取該筆資料的內容
        ReadFDB(); //讀取類別選項的資料

        ////////////////////////////////日期/////////////////////////////////
        Button datePicker = (Button) findViewById(R.id.datepicker);
        datePicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final Calendar c=Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(Report_ultimate_activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String format = setDataFormat(year,month,day);
                        dateinput.setText(format);
                    }
                },mYear,mMonth,mDay).show();
            }
        });

        //////////////////////////////切換收入支出//////////////////////////////
        RadioGroup attributeinput=findViewById(R.id.attributeinput);
        attributeinput.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                RadioButton checkedRadioButton=(RadioButton)radioGroup.findViewById(checkedID);
                if(checkedRadioButton.getText().equals("支出")){
                    inOutAttribute=1;
                    filterinput.setText("");
                }else{
                    inOutAttribute=0;
                    filterinput.setText("");
                }
            }
        });
    }

    //日期
    private String setDataFormat(int year, int monthOfYear,int dayOfMonth){
        return String.valueOf(year)+"-"+String.valueOf(monthOfYear+1)+"-"+String.valueOf(dayOfMonth);
    }

    ////////////////////////////////////////////依讀進的條件改變/////////////////////////////////
    //讀資料庫
    private void ReadDBRecord(){
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try{
            cursor=myDB.rawQuery("select * from record where record.id=?",new String[]{String.valueOf(recordID)});
            if(cursor!=null){
                cursor.moveToFirst();
                setInf();
            }else{
                Toast.makeText(this,"資料庫無資料",Toast.LENGTH_SHORT).show();
            }
            myDB.close();
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    //依照資料庫讀進的資料填入
    private void setInf(){
        costinput.setText(String.valueOf(cursor.getInt(0)));
        mYear=cursor.getInt(1);
        mMonth=cursor.getInt(2);
        mDay=cursor.getInt(3);
        dateinput.setText(mYear+"-"+mMonth+"-"+mDay);
        filterinput.setText(cursor.getString(4));
        itemsinput.setText(cursor.getString(5));
        if(cursor.getString(6)!=null)
            receiptinput.setText(cursor.getString(6));
        if(cursor.getString(7)!=null)
            quotesinput.setText(cursor.getString(7));
        inOutAttribute=cursor.getInt(8);
        if(inOutAttribute==1){
            outlay.setChecked(true);
        }else{
            income.setChecked(true);
        }
    }

    //////////////////////////////////////////////////////修改與刪除記帳資料///////////////////////////////////////////////////////
    public void clickToUpdate(View view){
        if(costinput.getText().toString().equals("")){
            Toast.makeText(this,"請輸入金額",Toast.LENGTH_SHORT).show();
            return;
        }else if(filterinput.getText().toString().equals("")){
            Toast.makeText(this,"請選擇分類",Toast.LENGTH_SHORT).show();
            return;
        }else if(itemsinput.getText().toString().equals("")){
            Toast.makeText(this,"請輸入細項",Toast.LENGTH_SHORT).show();
            return;
        }

        String date = dateinput.getText().toString();
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5,date.indexOf('-',5)));
        int day = Integer.parseInt(date.substring(date.indexOf('-',5)+1,date.length()));

        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        ContentValues values=new ContentValues();
        values.put("金額",costinput.getText().toString());
        values.put("年",year);
        values.put("月",month);
        values.put("日",day);
        values.put("分類",filterinput.getText().toString());
        values.put("細項",itemsinput.getText().toString());
        values.put("發票",receiptinput.getText().toString());
        values.put("備註",quotesinput.getText().toString());
        values.put("收支屬性",inOutAttribute);
        myDB.update("record",values,"id="+recordID,null);
        myDB.close();
        finish();
    }

    public void clickToDelete(View view){
        new AlertDialog.Builder(Report_ultimate_activity.this)
                .setTitle("確定刪除").setNegativeButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
                myDB.delete("record","id="+recordID,null);
                myDB.close();
                finish();
            }
        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        }).show();
    }
    //////////////////////////////////////////////////////修改與刪除記帳資料///////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////類別選項處理/////////////////////////////////////////////////////////
    //類別選項顯示(按下選擇類別)
    public void clickToShowTypeList(View view){
        ArrayList<String> arrayList;
        if(inOutAttribute==1){
            arrayList = new ArrayList<>(out_list);
        }else{
            arrayList = new ArrayList<>(in_list);
        }
        arrayList.add("新增選項...");
        arrayList.add("刪除選項...");

        // 將ArrayList 轉成String array
        final String[] categoryList = new String[arrayList.size()];
        for(int i=0;i<arrayList.size();i++){
            categoryList[i]=arrayList.get(i);
        }

        // 建立清單彈跳視窗
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(Report_ultimate_activity.this);
        dialog_list.setItems(categoryList,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                if (categoryList[which].equals("新增選項..."))
                    addDBbox();
                else if(categoryList[which].equals("刪除選項..."))
                    delDBbox();
                else filterinput.setText(categoryList[which]);
            }
        });
        dialog_list.show();
    }

    //讀資料庫
    public void ReadFDB() {
        // 準備資料庫
        out_list.clear();
        in_list.clear();
        self_out_list.clear();
        self_in_list.clear();
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try {
            //取得支出的List
            cursor = myDB.rawQuery("select filter.name from filter where filter.type=1",null);
            if(cursor!=null) {
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for(int i=0; i<iRow; i++) {
                    String name = cursor.getString(0);
                    out_list.add(name);
                    cursor.moveToNext();
                }
            } else {
                Toast.makeText(this,"類別讀取失敗",Toast.LENGTH_SHORT).show();
            }

            //取得收入的List
            cursor = myDB.rawQuery("select filter.name from filter where filter.type=0",null);
            if(cursor!=null) {
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for(int i=0; i<iRow; i++) {
                    String name = cursor.getString(0);
                    in_list.add(name);
                    cursor.moveToNext();
                }
            } else {
                Toast.makeText(this,"類別讀取失敗",Toast.LENGTH_SHORT).show();
            }

            //取得使用者自訂支出的List
            cursor = myDB.rawQuery("select filter.name from filter where filter.type=1 and filter.isDefault!=1",null);
            if(cursor!=null) {
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for(int i=0; i<iRow; i++) {
                    String name = cursor.getString(0);
                    self_out_list.add(name);
                    cursor.moveToNext();
                }
            } else {
                Toast.makeText(this,"類別讀取失敗",Toast.LENGTH_SHORT).show();
            }

            //取得使用者自訂收入的List
            cursor = myDB.rawQuery("select filter.name from filter where filter.type=0 and filter.isDefault!=1",null);
            if(cursor!=null) {
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for(int i=0; i<iRow; i++) {
                    String name = cursor.getString(0);
                    self_in_list.add(name);
                    cursor.moveToNext();
                }
            } else {
                Toast.makeText(this,"類別讀取失敗",Toast.LENGTH_SHORT).show();
            }
            myDB.close();
        }
        catch (Exception e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    //類別資料庫新增介面
    private void addDBbox(){
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(Report_ultimate_activity.this)
                .setTitle("請輸入要新增的類別")
                .setView(editText)
                .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addToDB(editText.getText().toString());
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        }).show();
    }
    //類別資料庫新增
    private void addToDB(String newCategory){
        if(newCategory.equals("")) return;
        ContentValues values = new ContentValues();
        values.put("name",newCategory);
        values.put("type",inOutAttribute);
        values.put("isDefault",0);
        myDB = openOrCreateDatabase(DBNAME,MODE_PRIVATE,null);
        long result = myDB.insert(FILTER_TABLE,null,values);
        if(result!=-1L){
            filterinput.setText(newCategory);
            Toast.makeText(Report_ultimate_activity.this,"類別新增成功",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(Report_ultimate_activity.this,"已存在該類別",Toast.LENGTH_SHORT).show();
        }
        myDB.close();
        ReadFDB();
    }
    //類別資料庫刪除介面
    private void delDBbox(){
        ArrayList<String> arrayList;
        if(inOutAttribute==1){
            arrayList = new ArrayList<>(self_out_list);
        }else{
            arrayList = new ArrayList<>(self_in_list);
        }
        if(arrayList.size()==0){
            Toast.makeText(this,"無可刪除的類別",Toast.LENGTH_SHORT).show();
            return;
        }

        // 將ArrayList 轉成String array
        final String[] categoryList = new String[arrayList.size()];
        for(int i=0;i<arrayList.size();i++){
            categoryList[i]=arrayList.get(i);
        }

        // 建立清單彈跳視窗
        final int[] chose = new int[1];
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(Report_ultimate_activity.this);
        dialog_list.setItems(categoryList,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                chose[0] =which;
                new AlertDialog.Builder(Report_ultimate_activity.this)
                        .setTitle("確定刪除").setNegativeButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delToDB(categoryList[chose[0]]);
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                }).show();
            }
        });
        dialog_list.show();
    }
    //類別資料庫刪除
    private void delToDB(String delCategory){
        myDB = openOrCreateDatabase(DBNAME,MODE_PRIVATE,null);
        int result=myDB.delete(FILTER_TABLE,"name="+delCategory,null);
        if(result!=-1L){
            filterinput.setText("");
            Toast.makeText(Report_ultimate_activity.this,"類別刪除成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Report_ultimate_activity.this,"類別刪除失敗",Toast.LENGTH_SHORT).show();
        }
        myDB.close();
        ReadFDB();
    }
    /////////////////////////////////////////////////////////類別選項處理/////////////////////////////////////////////////////////

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