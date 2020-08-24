package com.example.lifeassistant_project.activity_update;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PackageHandler
{
    PackageHandler()
    {

    }

    static public byte[] accountPackageEncode(AccountPackage acPkg) throws UnsupportedEncodingException {
        final int ID_size = 4, money_size = 4, year_size = 4, month_size = 1, day_size = 1, item_size = 18, detail_size = 18, receipt_size = 3, note_size = 90, status_size = 1, action_size = 1, user_size = 20;

        ByteBuffer buf = ByteBuffer.allocate(1024);

        ByteBuffer b_temp;
        buf.put("acc".getBytes("UTF-8"));
        Integer temp = new Integer(0);

        buf.put(TransInt2ByteArray(acPkg.getID(), ID_size));

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

        buf.put(TransString2ByteArray(acPkg.getReceipt(), receipt_size));
        buf.put(TransString2ByteArray(acPkg.getNote(), note_size));

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
        final int ID_size = 4, money_size = 4, year_size = 4, month_size = 1, day_size = 1, item_size = 18, detail_size = 18, receipt_size = 3, note_size = 90, status_size = 1, action_size = 1, user_size = 20;
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
        temp = 0;
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
        temp = 0;
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
        temp = 0;
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

        result.setItem(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + item_size), item_size));
        currentSize += item_size;

        result.setDetail(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + detail_size), detail_size));
        currentSize += detail_size;

        result.setReceipt(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + receipt_size), receipt_size));
        currentSize += receipt_size;

        result.setNote(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + note_size), note_size));
        currentSize += note_size;

//        for(int i = currentSize - detail_size; i < currentSize; i++)
//            buffer.put(message[i]);
//        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
//        buffer.clear();
//        result.setDetail(tempString);

        currentSize += status_size;
        temp = 0;
        for(int i = currentSize - status_size ;i < currentSize; i++)
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

        result.setRequestAction(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + action_size), action_size));
        currentSize += action_size;

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
        {
            if(message[i] == 0) break;
            buffer.put(message[i]);
        }
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
//        System.out.println(tempString.split(tempString.substring(10,11))[0]);
        buffer.clear();
        result.setCity(tempString.split(tempString.substring(10,11))[0]);//split null character in the String.
        currentSize += PERIOD_SIZE;

        for(int i = currentSize - PERIOD_SIZE; i < currentSize; i++)
            buffer.put(message[i]);
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
        buffer.clear();
        result.setPeriod(tempString.split(tempString.substring(10,11))[0]);//split null character in the String.
        currentSize += SITUATION_SIZE;

        for(int i = currentSize - SITUATION_SIZE; i < currentSize; i++)
            buffer.put(message[i]);
        tempString = new String(buffer.array(), StandardCharsets.UTF_8);
        buffer.clear();
        result.setSituation(tempString.split(tempString.substring(10,11))[0]);//split null character in the String.
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
//        System.out.println(temp);
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
//        System.out.println(temp);
        result.setMin_temperature(temp);

        return result;
    }

    static public byte[] sentencePackageEncode(SentenceHandler sentenceHandler) throws UnsupportedEncodingException {
        final int INTENT_SIZE = 4, OPERATE_SIZE = 4, MES_SIZE = 90;

        ByteBuffer buf = ByteBuffer.allocate(1024);

        ByteBuffer b_temp;
        buf.put("sen".getBytes("UTF-8"));

        buf.put(TransInt2ByteArray(sentenceHandler.getIntent(), INTENT_SIZE));

        buf.put(TransInt2ByteArray(sentenceHandler.getOperation(), OPERATE_SIZE));

        buf.put(TransString2ByteArray(sentenceHandler.getFulfillment(), MES_SIZE));

        return buf.array();
    }

    static public SentenceHandler sentencePackageDecode(byte[] message)
    {
        final int INTENT_SIZE = 4, OPERATION_SIZE = 4, FULFILLMENT_SIZE = 90;

        SentenceHandler result = new SentenceHandler();
        int temp = 0, currentSize = 0;
        String tempString;
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        result.setIntent(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + INTENT_SIZE), INTENT_SIZE));
        currentSize += INTENT_SIZE;

        result.setOperation(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + OPERATION_SIZE), OPERATION_SIZE));
        currentSize += OPERATION_SIZE;

        result.setFulfillment(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + FULFILLMENT_SIZE), FULFILLMENT_SIZE));
        currentSize += FULFILLMENT_SIZE;


        return result;
}

    static public byte[] LoginPackageEncode(LoginPackage loPkg) throws UnsupportedEncodingException {
        final int NAME_SIZE = 20;
        int PASSWORD_SIZE;
        if (loPkg.getName().equals("key"))
        {
            PASSWORD_SIZE = 64;
        }
        else
        {
            PASSWORD_SIZE = 20;
        }

        ByteBuffer buf = ByteBuffer.allocate(1024);

        buf.put("log".getBytes("UTF-8"));

        buf.put(TransString2ByteArray(loPkg.getName(), NAME_SIZE));
        buf.put(TransString2ByteArray(loPkg.getPassword(), PASSWORD_SIZE));

        return buf.array();
    }

    static public String LoginPackageDecode(byte[] message)
    {
        final int PASS_SIZE = 2, KEY_SIZE = 64;
        int temp = 0, currentSize = 0;
        String tempString;

        tempString = TransByteArray2String(Arrays.copyOfRange(message,currentSize ,currentSize+PASS_SIZE), PASS_SIZE);
        currentSize += PASS_SIZE;

        if(!tempString.equals("OK")) return "NO";

        tempString = TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + KEY_SIZE), KEY_SIZE);
        currentSize += KEY_SIZE;

        return tempString;
    }

    static public byte[] ReceiptQRPackageEncode() throws UnsupportedEncodingException
    {
        ByteBuffer buf = ByteBuffer.allocate(1024);

        ByteBuffer b_temp;
        buf.put("rqr".getBytes("UTF-8"));

        return buf.array();
    }

    static private byte[] TransInt2ByteArray(int num, int MES_SIZE)
    {
        ByteBuffer b_temp = ByteBuffer.allocate(MES_SIZE);
        Integer temp = num;
        for(int i = 0;i < MES_SIZE;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }

        return ReverseArray.Reverse_ByteBuffer(b_temp.array());
    }
    static private int TransByteArray2Int(byte[] message, int MES_SIZE)
    {
        int temp = 0;
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for(int i = 0;i < MES_SIZE; i++)
        {
            temp = temp << 8;
            int temp_m = (int)message[i];
            if(temp_m < 0)
            {
                temp_m = ((temp_m ^ 255) + 1) * -1;
            }
            temp += temp_m;
        }

        return temp;
    }
    static private String TransByteArray2String(byte[] message, int MES_SIZE)
    {
        ByteBuffer buffer = ByteBuffer.allocate(MES_SIZE);

        ByteBuffer temp_buf = ByteBuffer.allocate(3);
        for(int i = 0;i < 3;i++)
            temp_buf.put((byte)0);
        String nullString = new String(temp_buf.array(), StandardCharsets.UTF_8);

        for(int i = 0; i < MES_SIZE; i++)
        {
            if(message[i] == 0) break;
            buffer.put(message[i]);
        }

        try{
            return new String(buffer.array(), StandardCharsets.UTF_8).split(nullString)[0];
        }catch (Exception e)
        {
            return "null";
        }
    }
    static private byte[] TransString2ByteArray(String message, int MES_SIZE) throws UnsupportedEncodingException {
        if(message.getBytes().length == MES_SIZE) return message.getBytes("UTF-8");
        else // < 18
        {
            ByteBuffer b_temp = ByteBuffer.allocate(MES_SIZE);
            b_temp.put(message.getBytes("UTF-8"));
            for(int i = message.getBytes().length; i < MES_SIZE; i++)
            {
                b_temp.put((byte)0);
            }
            return b_temp.array();
        }
    }
}

