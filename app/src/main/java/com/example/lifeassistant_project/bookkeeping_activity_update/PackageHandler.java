package com.example.lifeassistant_project.bookkeeping_activity_update;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

public class PackageHandler
{
    PackageHandler()
    {

    }

    static public byte[] accountPackageEncode(AccountPackage acPkg) throws UnsupportedEncodingException {
        final int ID_size = 4, money_size = 4, year_size = 1, month_size = 1, day_size = 1, item_size = 18, detail_size = 18, status_size = 1, action_size = 1, user_size = 20;

        ByteBuffer buf = ByteBuffer.allocate(1024);
        int currentLength = 0;

        ByteBuffer b_temp;
        buf.put("acc".getBytes("UTF-8"));
        Integer temp = new Integer(0);

        b_temp = ByteBuffer.allocate(ID_size);
        temp = acPkg.getID();
        for(int i = 0;i < ID_size;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        b_temp = ByteBuffer.allocate(money_size);
        temp = acPkg.getMoney();
        for(int i = 0;i < money_size;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        b_temp = ByteBuffer.allocate(year_size);
        temp = acPkg.getYear();
        for(int i = 0;i < year_size;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        b_temp = ByteBuffer.allocate(month_size);
        temp = acPkg.getMonth();
        for(int i = 0;i < month_size;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        b_temp = ByteBuffer.allocate(day_size);
        temp = acPkg.getDay();
        for(int i = 0;i < day_size;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        if(acPkg.getItem().getBytes().length == item_size) buf.put(acPkg.getItem().getBytes("UTF-8"));
        else // < 18
        {
            b_temp = ByteBuffer.allocate(item_size);
            b_temp.put(acPkg.getItem().getBytes("UTF-8"));
            for(int i = acPkg.getItem().getBytes().length; i < item_size; i++)
            {
                b_temp.put((byte)0);
            }
            buf.put(b_temp.array());
        }

        if(acPkg.getDetail().getBytes().length == detail_size) buf.put(acPkg.getDetail().getBytes("UTF-8"));
        else // < 18
        {
            b_temp = ByteBuffer.allocate(detail_size);
            b_temp.put(acPkg.getDetail().getBytes("UTF-8"));
            for(int i = acPkg.getDetail().getBytes().length; i < detail_size; i++)
            {
                b_temp.put((byte)0);
            }
            buf.put(b_temp.array());
        }

        b_temp = ByteBuffer.allocate(status_size);
        if(acPkg.getType()) temp = 1;
        else temp = 0;
        for(int i = 0;i < status_size;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(b_temp.array());

        b_temp = ByteBuffer.allocate(action_size);
        temp = acPkg.getRequestAction();
        for(int i = 0;i < action_size;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(b_temp.array());

        if(acPkg.getUser().getBytes().length == user_size) buf.put(acPkg.getUser().getBytes("UTF-8"));
        else // < 18
        {
            b_temp = ByteBuffer.allocate(user_size);
            b_temp.put(acPkg.getUser().getBytes("UTF-8"));
            for(int i = acPkg.getUser().getBytes().length; i < user_size; i++)
            {
                b_temp.put((byte)0);
            }
            buf.put(b_temp.array());
        }
//        System.out.println(buf.array().length);

        return buf.array();
    }

    static public AccountPackage accountPackageDecode(byte[] message)
    {
        final int ID_size = 4, money_size = 4, year_size = 1, month_size = 1, day_size = 1, item_size = 18, detail_size = 18, status_size = 1, user_size = 20;
        AccountPackage result = new AccountPackage();
        int temp = 0, currentSize = ID_size;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String tempString;
        for(int i = 0;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setID(temp);
        currentSize += money_size;
        for(int i = currentSize - money_size ;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setMoney(temp);
        currentSize += year_size;
        for(int i = currentSize - year_size ;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setYear(temp);
        currentSize += month_size;
        for(int i = currentSize - month_size ;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setMonth(temp);
        currentSize += day_size;
        for(int i = currentSize - day_size ;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setDay(temp);
        currentSize += item_size;
        for(int i = currentSize - item_size; i < currentSize; i++)
            buffer.put(message[i]);
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
        buffer.clear();
        result.setItem(tempString);
        currentSize += detail_size;
        for(int i = currentSize - detail_size; i < currentSize; i++)
            buffer.put(message[i]);
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
        buffer.clear();
        result.setDetail(tempString);
        currentSize += status_size;
        for(int i = currentSize - day_size ;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        if(temp > 0) result.setType(true);
        else result.setType(false);

        currentSize += user_size;
        for(int i = currentSize - user_size; i < currentSize; i++)
            buffer.put(message[i]);
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
        buffer.clear();
        result.setUser(tempString);

        return result;
    }

    static public byte[] schedulePackageEncode(SchedulePackage scPkg) throws UnsupportedEncodingException {
        final int ID_SIZE = 4, TODO_SIZE = 36, YEAR_SIZE = 1, MONTH_SIZE = 1, DAY_SIZE = 1, START_TIME_SIZE = 4, END_TIME_SIZE = 4;

        ByteBuffer buf = ByteBuffer.allocate(1024);
        int currentLength = 0;

        ByteBuffer b_temp;
        buf.put("sch".getBytes("UTF-8"));
        Integer temp = new Integer(0);

        b_temp = ByteBuffer.allocate(ID_SIZE);
        temp = scPkg.getID();
        for(int i = 0;i < ID_SIZE;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        if(scPkg.getTodo().getBytes().length == TODO_SIZE) buf.put(scPkg.getTodo().getBytes("UTF-8"));
        else // < 18
        {
            b_temp = ByteBuffer.allocate(TODO_SIZE);
            b_temp.put(scPkg.getTodo().getBytes("UTF-8"));
            for(int i = scPkg.getTodo().getBytes().length; i < TODO_SIZE; i++)
            {
                b_temp.put((byte)0);
            }
            buf.put(b_temp.array());
        }

        b_temp = ByteBuffer.allocate(YEAR_SIZE);
        temp = scPkg.getYear();
        for(int i = 0;i < YEAR_SIZE;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        b_temp = ByteBuffer.allocate(MONTH_SIZE);
        temp = scPkg.getMonth();
        for(int i = 0;i < MONTH_SIZE;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        b_temp = ByteBuffer.allocate(DAY_SIZE);
        temp = scPkg.getDay();
        for(int i = 0;i < DAY_SIZE;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        b_temp = ByteBuffer.allocate(START_TIME_SIZE);
        temp = scPkg.getStart_time();
        for(int i = 0;i < START_TIME_SIZE;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        b_temp = ByteBuffer.allocate(END_TIME_SIZE);
        temp = scPkg.getEnd_time();
        for(int i = 0;i < END_TIME_SIZE;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        return buf.array();
    }

    static public SchedulePackage schedulePackageDecode(byte[] message)
    {
        final int ID_SIZE = 4, TODO_SIZE = 36, YEAR_SIZE = 1, MONTH_SIZE = 1, DAY_SIZE = 1, START_TIME_SIZE = 4, END_TIME_SIZE = 4;
        SchedulePackage result = new SchedulePackage();
        int temp = 0, currentSize = ID_SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String tempString;
        for(int i = 0;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setID(temp);

        currentSize += TODO_SIZE;
        for(int i = currentSize - TODO_SIZE; i < currentSize; i++)
            buffer.put(message[i]);
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
        buffer.clear();
        result.setTodo(tempString);

        currentSize += YEAR_SIZE;
        temp = 0;
        for(int i = currentSize - YEAR_SIZE;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setYear(temp);

        currentSize += MONTH_SIZE;
        temp = 0;
        for(int i = currentSize - MONTH_SIZE;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setMonth(temp);

        currentSize += DAY_SIZE;
        temp = 0;
        for(int i = currentSize - DAY_SIZE;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setDay(temp);

        currentSize += START_TIME_SIZE;
        temp = 0;
        for(int i = currentSize - START_TIME_SIZE;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setStart_time(temp);

        currentSize += END_TIME_SIZE;
        temp = 0;
        for(int i = currentSize - END_TIME_SIZE;i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setEnd_time(temp);

        return result;
    }

    static public byte[] weatherPackageEncode() throws UnsupportedEncodingException {
        ByteBuffer buf = ByteBuffer.allocate(1024);

        ByteBuffer b_temp;
        buf.put("wea".getBytes("UTF-8"));

        return buf.array();
    }

    static public WeatherPackage weatherPackageDecode(byte[] message)
    {
        final int DAY_SIZE = 1, MONTH_SIZE = 1, CITY_SIZE = 12, PERIOD_SIZE = 12, SITUATION_SIZE = 45, TEMPERATURE_SIZE = 1;
        WeatherPackage result = new WeatherPackage();
        int temp = 0, currentSize = MONTH_SIZE;
        String tempString;
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for(int i = 0; i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setMonth(temp);
        currentSize += DAY_SIZE;
        temp = 0;

        for(int i = currentSize - DAY_SIZE; i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setDay(temp);
        currentSize += CITY_SIZE;
        temp = 0;

        for(int i = currentSize - CITY_SIZE; i < currentSize; i++)
            buffer.put(message[i]);
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
        buffer.clear();
        result.setCity(tempString);
        currentSize += PERIOD_SIZE;

        for(int i = currentSize - PERIOD_SIZE; i < currentSize; i++)
            buffer.put(message[i]);
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
        buffer.clear();
        result.setPeriod(tempString);
        currentSize += SITUATION_SIZE;

        for(int i = currentSize - SITUATION_SIZE; i < currentSize; i++)
            buffer.put(message[i]);
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
        buffer.clear();
        result.setSituation(tempString);
        currentSize += TEMPERATURE_SIZE;

        for(int i = currentSize - TEMPERATURE_SIZE; i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setMax_temperature(temp);
        currentSize += TEMPERATURE_SIZE;
        temp = 0;

        for(int i = currentSize - TEMPERATURE_SIZE; i < currentSize; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }
        result.setMin_temperature(temp);

        return result;
    }
}

