package com.example.lifeassistant_project.activity_update;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextThemeWrapper;

import java.io.*;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseBehavior {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static SQLiteDatabase myDB;
    private static Cursor cursor;

    private static ArrayList<Integer> accountIDList = new ArrayList<Integer>();

    public static ArrayList<Integer> getAccountIDList() { return accountIDList; }

    public static void init()
    {
//        ClientProgress client = new ClientProgress();
//        AccountPackage selectAccount = new AccountPackage();
//        selectAccount.setRequestAction(3);
//        selectAccount.setID(0); //select all records.
//        client.setBookkeeping(selectAccount);
//        Thread cThread = new Thread(client);
//        cThread.start();
//
//        synchronized (client)
//        {
//            try
//            {
//                System.out.println("WAITING");
//                client.wait();
//                System.out.println("GOGO");
//            }catch (Exception e)
//            {
//                System.out.println(e);
//            }
//        }
//
//        ArrayList<AccountPackage> serverAccountList = client.getRcvAccountData();
//        if(serverAccountList.size() == 0)
//        {
//            System.out.println("Connection failed.");
//            return;
//        }
//
//        for (AccountPackage ele : serverAccountList)
//        {
//            accountIDList.add(ele.getID());
//        }
    }

    public static void synchronizeServer2Client()
    {
        System.out.println("Synchronize start.");

        myDB = SQLiteDatabase.openOrCreateDatabase(PATH + "/databases/" + DBNAME, null);
        ArrayList<AccountPackage> clientAccountList = getClientAccountList(myDB, cursor);


        ClientProgress client = new ClientProgress();
        AccountPackage selectAccount = new AccountPackage();
        selectAccount.setRequestAction(3);
        selectAccount.setUser(LoginPackage.getUserName());
        if(!selectAccount.getUser().equals("Null")) selectAccount.setID(1);
        client.setBookkeeping(selectAccount);
        Thread cThread = new Thread(client);
        cThread.start();

        synchronized (client)
        {
            try
            {
//                System.out.println("WAITING");
                client.wait();
//                System.out.println("GOGO");
            }catch (Exception e)
            {
                System.out.println(e);
            }
        }

        ArrayList<AccountPackage> serverAccountList = client.getRcvAccountData();

        //stupid method but it works!
        myDB.execSQL("DELETE FROM record");

        for (AccountPackage ele : serverAccountList)
        {
            ContentValues values = new ContentValues();
            values.put("金額",ele.getMoney());
            values.put("年",ele.getYear());
            values.put("月",ele.getMonth());
            values.put("日",ele.getDay());
            values.put("分類",ele.getItem());
            values.put("細項",ele.getDetail());
            values.put("發票","null");
            values.put("備註","null");
            values.put("id", ele.getID());
            values.put("收支屬性",ele.getType() ? 1 : 0);

            myDB.insert("record", null, values);
        }

        System.out.println("synchronization success.");
    }

//    still in considering.
//    public static void synchronizeClient2Server()
//    {
//        System.out.println("Synchronize start.");
//
//        ArrayList<AccountPackage> clientAccountList = getClientAccountList(myDB, cursor);
//
//        ClientProgress client = new ClientProgress();
//        AccountPackage selectAccount = new AccountPackage();
//        selectAccount.setRequestAction(3);
//        selectAccount.setUser(LoginPackage.getUserName());
//        client.setBookkeeping(selectAccount);
//        Thread cThread = new Thread(client);
//        cThread.start();
//
//        synchronized (client)
//        {
//            try
//            {
//                System.out.println("WAITING");
//                client.wait();
//                System.out.println("GOGO");
//            }catch (Exception e)
//            {
//                System.out.println(e);
//            }
//        }
//
//        ArrayList<AccountPackage> serverAccountList = client.getRcvAccountData();
//
//        System.out.println("synchronization success.");
//    }

    private static ArrayList<AccountPackage> getClientAccountList(SQLiteDatabase myDB, Cursor cursor)
    {
        ArrayList<AccountPackage> resultList = new ArrayList<AccountPackage>();

        cursor = myDB.rawQuery("SELECT * FROM record", null);

        cursor.moveToFirst();
        for(int i = 0;i < cursor.getCount(); i++)
        {
            AccountPackage tempAccount = new AccountPackage();
            tempAccount.setMoney(cursor.getInt(0));     //money
            tempAccount.setYear(cursor.getInt(1));      //year
            tempAccount.setMonth(cursor.getInt(2));     //month
            tempAccount.setDay(cursor.getInt(3));       //day
            tempAccount.setItem(cursor.getString(4));   //Item
            tempAccount.setDetail(cursor.getString(5)); //Detail
            tempAccount.setType(cursor.getInt(8) == 0 ? false : true);     //Type
            tempAccount.setID(cursor.getInt(9));       //ID
            tempAccount.setUser(LoginPackage.getUserName());


            resultList.add(tempAccount);
            cursor.moveToNext();
        }

        return resultList;
    }
}
