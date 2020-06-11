package com.example.lifeassistant_project.menu_activity;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeassistant_project.R;
import com.example.lifeassistant_project.bookkeeping_activity_update.BookkeepingClient;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Bookkeeping_activity extends AppCompatActivity {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static final String FILTER_TABLE = "filter";
    private static final String BK_TABLE = "record";

    private SQLiteDatabase myDB;
    private Cursor cursor;
    private List<String> list = new ArrayList<>();
    private EditText filterinput;

    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏狀態列(綠色的那塊)
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);  //全螢幕
        setContentView(R.layout.activity_bookkeeping_activity);

        // 資料庫
        File dbDir = new File(PATH, "databases");
        dbDir.mkdir();
        File FdbFile = new File(PATH+"/databases",DBNAME);
        if(!FdbFile.exists() || !FdbFile.isFile())
            copyAssets(PATH); //初始資料庫複製到路徑
        filterinput = (EditText) findViewById(R.id.filterinput);

        ////////////////////////////////日期/////////////////////////////////
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
                new DatePickerDialog(Bookkeeping_activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String format = setDataFormat(year,month,day);
                        dateText.setText(format);
                    }
                },mYear,mMonth,mDay).show();
            }
        });

        Button cancel = (Button) findViewById(R.id.cancelbutton);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bookkeeping_activity.this.finish();
            }
        });
    }

    /////////////////////////////////////////////////////////日期/////////////////////////////////////////////////////////
    private String setDataFormat(int year, int monthOfYear,int dayOfMonth){
        return String.valueOf(year)+"-"+String.valueOf(monthOfYear+1)+"-"+String.valueOf(dayOfMonth);
    }

    /////////////////////////////////////////////////////////連網/////////////////////////////////////////////////////////
    public void clickToUpdate(View view){
        writeToBKDB();
        BookkeepingClient client = new BookkeepingClient();
        new Thread(client).start();
    }

    /////////////////////////////////////////////////////////記帳資料庫/////////////////////////////////////////////////////////
    private void writeToBKDB(){
        //將表單內容讀入
        int cost = 0;
        int year = 0;
        int month = 0;
        int day = 0;
        TextView textView = (TextView) findViewById(R.id.costinput); //金額
        if (!textView.getText().toString().equals(""))
            cost = Integer.parseInt(textView.getText().toString());
        else {
            Toast.makeText(this,"請輸入金額",Toast.LENGTH_SHORT).show();
            return;
        }
        textView = (TextView) findViewById(R.id.filterinput); //分類
        String type = textView.getText().toString();
        textView = (TextView) findViewById(R.id.itemsinput); //細項
        String item = textView.getText().toString();

        if(type.equals("")) {
            Toast.makeText(this,"請選擇分類",Toast.LENGTH_SHORT).show();
            return;
        }else if(item.equals("")){
            Toast.makeText(this,"請輸入細項",Toast.LENGTH_SHORT).show();
            return;
        }

        textView = (TextView) findViewById(R.id.receiptinput); //發票
        String receipt =textView.getText().toString();
        textView = (TextView) findViewById(R.id.quotesinput); //備註
        String quote = textView.getText().toString();
        textView = (TextView) findViewById(R.id.dateinput); //日期
        String date = textView.getText().toString();
        if (!textView.getText().toString().equals("")) {
            year = Integer.parseInt(date.substring(0, 4));
            month = Integer.parseInt(date.substring(5,date.indexOf('-',5)));
            day = Integer.parseInt(date.substring(date.indexOf('-',5)+1,date.length()));
        }
        int attribute=0; //收入或支出
        RadioButton radioOutlay = (RadioButton) findViewById(R.id.outlay);
        if(radioOutlay.isChecked()){
            attribute=1;
        }

        list.clear();
        ContentValues values = new ContentValues();
        values.put("金額",cost);
        values.put("年",year);
        values.put("月",month);
        values.put("日",day);
        values.put("分類",type);
        values.put("細項",item);
        values.put("發票",receipt);
        values.put("備註",quote);
        values.put("收支屬性",attribute);

        myDB = openOrCreateDatabase(DBNAME,MODE_PRIVATE,null);
        long result=-1L;
        while (true) {
            try {
                int id = (int) (Math.random() * 99999);
                values.put("id", id);
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

        Bookkeeping_activity.this.finish();
    }

    /////////////////////////////////////////////////////////選項資料庫/////////////////////////////////////////////////////////
    //讀資料庫
    public void clickToReadFDB(View view) {
        // 2. 準備資料庫
        myDB = openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        try {
            cursor = myDB.query(FILTER_TABLE, null, null, null,
                    null, null, null);
            if(cursor!=null) {
                int iRow = cursor.getCount(); // 取得資料記錄的筆數
                cursor.moveToFirst();
                for(int i=0; i<iRow; i++) {
                    String name = cursor.getString(0);
                    list.add(name);
                    cursor.moveToNext();
                }
                // 3. 準備ArrayList去接資料庫的list
                final ArrayList<String> arrayList = new ArrayList<>(list);
                arrayList.add("新增選項...");
                // 4. 將ArrayList 轉成String array
                final String[] categoryList = new String[arrayList.size()];
                for(int i=0;i<arrayList.size();i++){
                    categoryList[i]=arrayList.get(i);
                }

                // 建立清單彈跳視窗
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(Bookkeeping_activity.this);
                dialog_list.setItems(categoryList,new DialogInterface.OnClickListener(){
                   @Override
                   public void onClick(DialogInterface dialog, int which){
                       if (categoryList[which].equals("新增選項..."))
                           addDBbox();
                       else filterinput.setText(categoryList[which]);
                   }
                });
                dialog_list.show();
                // 5. 關閉 DB
                myDB.close();
                list.clear();
                arrayList.clear();
            }
            else {
                Toast.makeText(this,"Hint 1: 請將db準備好!",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    //資料庫新增介面
    private void addDBbox(){
        LayoutInflater inflater = LayoutInflater.from(Bookkeeping_activity.this);
        final View v = inflater.inflate(R.layout.bookkeeping_add_category,null);
        new AlertDialog.Builder(Bookkeeping_activity.this)
                .setTitle("請輸入要新增的類別")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText editText = (EditText) v.findViewById(R.id.addCategory);
                        addToDB(editText.getText().toString());
                    }
                }).show();
    }
    //資料庫新增
    private void addToDB(String newCategory){
        if(newCategory.equals("")) return;
        ContentValues values = new ContentValues();
        values.put("name",newCategory);
        myDB = openOrCreateDatabase(FILTER_TABLE,MODE_PRIVATE,null);
        long result = myDB.insert(FILTER_TABLE,null,values);
        if(result!=-1L){
            myDB.close();
            filterinput.setText(newCategory);
            Toast.makeText(Bookkeeping_activity.this,"類別新增成功",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(Bookkeeping_activity.this,"類別新增失敗",Toast.LENGTH_SHORT).show();
        }
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
