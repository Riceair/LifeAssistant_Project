package com.example.lifeassistant_project.activity_update;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;


public class DatabaseBehavior {
    private static final String PATH = "/data/data/com.example.lifeassistant_project";
    private static final String DBNAME = "myDB.db";
    private static SQLiteDatabase myDB;
    private static Cursor cursor;

    private static ArrayList<Integer> accountIDList = new ArrayList<Integer>();

    public static void synchronizeServer2Client_Account()
    {
        System.out.println("Synchronize start.");

        myDB = SQLiteDatabase.openOrCreateDatabase(PATH + "/databases/" + DBNAME, null);
//        ArrayList<AccountPackage> clientAccountList = getClientAccountList(myDB, cursor);


        ClientProgress client = new ClientProgress();
        AccountPackage selectAccount = new AccountPackage();
        selectAccount.setRequestAction(3);
        selectAccount.setUser(LoginHandler.getUserName());
        if(!selectAccount.getUser().equals("Null")) selectAccount.setID(1);
        client.setBookkeeping(selectAccount);
        Thread cThread = new Thread(client);
        cThread.start();

        synchronized (client)
        {
            try
            {
                client.wait();
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
            values.put("發票",ele.getReceipt());
            values.put("備註",ele.getNote());
            values.put("id", ele.getID());
            values.put("收支屬性",ele.getType() ? 1 : 0);

            myDB.insert("record", null, values);
        }

        System.out.println("synchronization success.");
    }

    public static void synchronizeServer2Client_Schedule()
    {
        System.out.println("Synchronize Schedule start.");

        myDB = SQLiteDatabase.openOrCreateDatabase(PATH + "/databases/" + DBNAME, null);
//        ArrayList<SchedulePackage> clientScheduleList = getClientAccountList(myDB, cursor);


        ClientProgress client = new ClientProgress();
        SchedulePackage selectSchedule = new SchedulePackage();
        selectSchedule.setRequestAction(3);
        selectSchedule.setUser(LoginHandler.getUserName());
        if(!selectSchedule.getUser().equals("Null")) selectSchedule.setID(1);
        client.setPlan(selectSchedule);
        Thread cThread = new Thread(client);
        cThread.start();

        synchronized (client)
        {
            try
            {
                client.wait();
            }catch (Exception e)
            {
                System.out.println(e);
            }
        }

        ArrayList<SchedulePackage> serverScheduleList = client.getRcvScheduleData();

        //stupid method but it works!
        myDB.execSQL("DELETE FROM schedule_record");

        for (SchedulePackage ele : serverScheduleList)
        {
            ContentValues values = new ContentValues();
            values.put("事情",ele.getTodo());
            values.put("年",ele.getYear());
            values.put("月",ele.getMonth());
            values.put("日",ele.getDay());
            values.put("開始時間",ele.getStart_time());
            values.put("結束時間",ele.getEnd_time());
            values.put("id", ele.getID());

            myDB.insert("schedule_record", null, values);
        }

        System.out.println("synchronization success.");
    }

    public static void synchronizeData()
    {
        synchronizeServer2Client_Account();
        synchronizeServer2Client_Schedule();
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
            tempAccount.setUser(LoginHandler.getUserName());


            resultList.add(tempAccount);
            cursor.moveToNext();
        }

        return resultList;
    }

    private static ArrayList<SchedulePackage> getClientScheduleList(SQLiteDatabase myDB, Cursor cursor)
    {
        ArrayList<SchedulePackage> resultList = new ArrayList<SchedulePackage>();

        cursor = myDB.rawQuery("SELECT * FROM schedule_record", null);

        cursor.moveToFirst();
        for(int i = 0;i < cursor.getCount(); i++)
        {
            SchedulePackage tempSchedule = new SchedulePackage();
            tempSchedule.setTodo(cursor.getString(0));     //_todo
            tempSchedule.setYear(cursor.getInt(1));      //year
            tempSchedule.setMonth(cursor.getInt(2));     //month
            tempSchedule.setDay(cursor.getInt(3));       //day
            tempSchedule.setStart_time(cursor.getInt(4));   //start_time
            tempSchedule.setEnd_time(cursor.getInt(5)); //end_time
            tempSchedule.setID(cursor.getInt(6));       //ID
            tempSchedule.setUser(LoginHandler.getUserName());


            resultList.add(tempSchedule);
            cursor.moveToNext();
        }

        return resultList;
    }
}
