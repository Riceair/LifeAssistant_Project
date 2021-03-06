package com.example.lifeassistant_project.activity_update.static_handler;

import com.example.lifeassistant_project.activity_update.packages.SentenceHandler;
import com.example.lifeassistant_project.activity_update.packages.*;
import com.google.zxing.qrcode.decoder.Version;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

public class PackageHandler
{
    static private int PACKAGE_TYPE_SIZE = 3;
    PackageHandler() {}

    static public byte[] accountPackageEncode(AccountPackage acPkg) throws UnsupportedEncodingException {
        final int ID_size = 4, money_size = 4, year_size = 4, month_size = 1, day_size = 1, item_size = 18, detail_size = 30, receipt_size = 3, note_size = 90, status_size = 1, action_size = 1, user_size = 20;

        ByteBuffer buf = ByteBuffer.allocate(AccountPackage.PACKAGE_SIZE);

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
            int length = Math.min(acPkg.getDetail().getBytes().length, detail_size);
            for(int i = length; i < detail_size; i++)
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
        final int ID_size = 4, money_size = 4, year_size = 4, month_size = 1, day_size = 1, item_size = 18, detail_size = 30, receipt_size = 3, note_size = 90, status_size = 1, action_size = 1, user_size = 20;
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

//    static public AccountPackage accountPackageDecodeByString(String message)
//    {
//        final int PACKAGE_TYPE_SIZE = 3;
//        final int ID_size = 4, money_size = 4, year_size = 4, month_size = 1, day_size = 1, item_size = 18,
//                detail_size = 30, receipt_size = 3, note_size = 90, status_size = 1, action_size = 1, user_size = 20;
//        int ptr = 0;
//
//        AccountPackage result = new AccountPackage();
//        ptr += PACKAGE_TYPE_SIZE;
//
//        result.setID(Integer.parseInt(message.substring(ptr, ptr + ID_size)));
//        ptr += ID_size;
//        result.setMoney(Integer.parseInt(message.substring(ptr, ptr + money_size)));
//        ptr += money_size;
//        result.setYear(Integer.parseInt(message.substring(ptr, ptr + year_size)));
//        ptr += year_size;
//        result.setMonth(Integer.parseInt(message.substring(ptr, ptr + month_size)));
//        ptr += month_size;
//        result.setDay(Integer.parseInt(message.substring(ptr, ptr + day_size)));
//        ptr += day_size;
//        result.setItem(message.substring(ptr, ptr + item_size));
//        ptr += item_size;
//        result.setDetail(message.substring(ptr, ptr + detail_size));
//        ptr += detail_size;
//        result.setReceipt(message.substring(ptr, ptr + receipt_size));
//        ptr += receipt_size;
//        result.setNote(message.substring(ptr, ptr + note_size));
//        ptr += note_size;
//        result.setType(Integer.parseInt(message.substring(ptr, ptr + status_size)) == 1);
//        ptr += status_size;
//        result.setRequestAction(Integer.parseInt(message.substring(ptr, ptr + action_size)));
//        ptr += action_size;
//
//        return result;
//    }

    static public byte[] schedulePackageEncode(SchedulePackage scPkg) throws UnsupportedEncodingException {
        final int ID_SIZE = 4, TODO_SIZE = 36, YEAR_SIZE = 4, MONTH_SIZE = 1, DAY_SIZE = 1,
                START_TIME_SIZE = 4, END_TIME_SIZE = 4, OPERATION_CODE_SIZE = 1, USER_SIZE = 20;

        ByteBuffer buf = ByteBuffer.allocate(SchedulePackage.PACKAGE_SIZE);
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

        buf.put(TransString2ByteArray(scPkg.getTodo(), TODO_SIZE));

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

        b_temp = ByteBuffer.allocate(OPERATION_CODE_SIZE);
        temp = scPkg.getRequestAction();
        for(int i = 0;i < OPERATION_CODE_SIZE;i++)
        {
            b_temp.put(temp.byteValue());
            temp /= 256;
        }
        buf.put(ReverseArray.Reverse_ByteBuffer(b_temp.array()));

        if(scPkg.getUser().getBytes().length == USER_SIZE) buf.put(scPkg.getUser().getBytes("UTF-8"));
        else // < 18
        {
            b_temp = ByteBuffer.allocate(USER_SIZE);
            b_temp.put(scPkg.getUser().getBytes("UTF-8"));
            for(int i = scPkg.getUser().getBytes().length; i < USER_SIZE; i++)
            {
                b_temp.put((byte)0);
            }
            buf.put(b_temp.array());
        }
//        System.out.println(buf.array().length);

        return buf.array();
    }

    static public SchedulePackage schedulePackageDecode(byte[] message)
    {
        final int ID_SIZE = 4, TODO_SIZE = 36, YEAR_SIZE = 4, MONTH_SIZE = 1, DAY_SIZE = 1,
                START_TIME_SIZE = 4, END_TIME_SIZE = 4, OPERATION_CODE_SIZE = 1, USER_SIZE = 20;

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

        result.setTodo(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + TODO_SIZE), TODO_SIZE));
        currentSize += TODO_SIZE;

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

        result.setRequestAction(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + OPERATION_CODE_SIZE), OPERATION_CODE_SIZE));
        currentSize += OPERATION_CODE_SIZE;

        result.setUser(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + USER_SIZE), USER_SIZE));
        currentSize += USER_SIZE;

        return result;
    }

//    static public SchedulePackage schedulePackageDecodeByString(String message)
//    {
//        final int PACKAGE_TYPE_SIZE = 3;
//        final int ID_SIZE = 4, TODO_SIZE = 36, YEAR_SIZE = 4, MONTH_SIZE = 1, DAY_SIZE = 1,
//                START_TIME_SIZE = 4, END_TIME_SIZE = 4, OPERATION_CODE_SIZE = 1, USER_SIZE = 20;
//        int ptr = 0;
//
//        SchedulePackage result = new SchedulePackage();
//        ptr += PACKAGE_TYPE_SIZE;
//
//        result.setID(Integer.parseInt(message.substring(ptr, ptr + ID_SIZE)));
//        ptr += ID_SIZE;
//        result.setTodo(message.substring(ptr, ptr + TODO_SIZE));
//        ptr += TODO_SIZE;
//        result.setYear(Integer.parseInt(message.substring(ptr, ptr + YEAR_SIZE)));
//        ptr += YEAR_SIZE;
//        result.setMonth(Integer.parseInt(message.substring(ptr, ptr + MONTH_SIZE)));
//        ptr += MONTH_SIZE;
//        result.setDay(Integer.parseInt(message.substring(ptr, ptr + DAY_SIZE)));
//        ptr += DAY_SIZE;
//        result.setStart_time(Integer.parseInt(message.substring(ptr, ptr + START_TIME_SIZE)));
//        ptr += START_TIME_SIZE;
//        result.setEnd_time(Integer.parseInt(message.substring(ptr, ptr + END_TIME_SIZE)));
//        ptr += END_TIME_SIZE;
//        result.setRequestAction(Integer.parseInt(message.substring(ptr, ptr + OPERATION_CODE_SIZE)));
//        ptr += OPERATION_CODE_SIZE;
//
//        return result;
//    }

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
        final int INTENT_SIZE = 4, OPERATION_SIZE = 4, FULFILLMENT_SIZE = 90, CAL_TYPE_SIZE = 3, TYPE_SIZE = 3;

        SentenceHandler result = new SentenceHandler();
        int temp = 0, currentSize = 0;
        String tempString;
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        result.setIntent(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + INTENT_SIZE), INTENT_SIZE));
        currentSize += INTENT_SIZE;

        result.setOperation(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + OPERATION_SIZE), OPERATION_SIZE));
        currentSize += OPERATION_SIZE;

        if(result.getIntent() == 0 && result.getOperation() == 0)
        {
            final String[] KEY_WORD_LIST = {"def", "sum", "avg"};
            boolean searchFlag = false;
            String typeCheck = TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + CAL_TYPE_SIZE), CAL_TYPE_SIZE);
            for(String keyWord : KEY_WORD_LIST)
            {
                if (typeCheck.equals(keyWord))
                {
                    searchFlag = true;
                    break;
                }
            }

            if(searchFlag)
            {
                result.setCalculateType(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + CAL_TYPE_SIZE), CAL_TYPE_SIZE));
                currentSize += CAL_TYPE_SIZE;

                typeCheck = TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + TYPE_SIZE), TYPE_SIZE);
                ArrayList<DataPackage> tempList = new ArrayList<>();
                int packageSize = 0;

                //shitty code
                if(typeCheck.equals("acc"))
                {
                    packageSize = AccountPackage.PACKAGE_SIZE;
                }
                else
                {
                    packageSize = SchedulePackage.PACKAGE_SIZE;
                }

                for(int i = currentSize;i < message.length; i += packageSize)
                {
                    typeCheck = TransByteArray2String(Arrays.copyOfRange(message, i, i + TYPE_SIZE), TYPE_SIZE);
                    if(typeCheck.equals("acc"))
                        tempList.add(PackageHandler.accountPackageDecode(Arrays.copyOfRange(message, i + TYPE_SIZE, i + packageSize)));
                    else if(typeCheck.equals("sch"))
                        tempList.add(PackageHandler.schedulePackageDecode(Arrays.copyOfRange(message, i + TYPE_SIZE, i + packageSize)));
                    else
                        break;
                }
                if(tempList.size() == 0)
                    result.setFulfillment("null_package");
                result.setRcvSelectedList(tempList);
//            currentSize += message.length - currentSize;
            }
            else
            {
                result.setFulfillment(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + FULFILLMENT_SIZE), FULFILLMENT_SIZE));
                currentSize += FULFILLMENT_SIZE;
            }
        }
        else
        {
            result.setFulfillment(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + FULFILLMENT_SIZE), FULFILLMENT_SIZE));
            currentSize += FULFILLMENT_SIZE;
        }

        return result;
}

    static public byte[] LoginPackageEncode(LoginPackage loPkg) throws UnsupportedEncodingException {
        final int NAME_SIZE = 20;
        final int PASSWORD_SIZE = (loPkg.getName().equals("key")) ? 64 : 20;

        ByteBuffer buf = ByteBuffer.allocate(1024);

        if (loPkg.ifRegister())
            buf.put("reg".getBytes("UTF-8"));
        else
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

    //dropped.
    static public byte[] ReceiptPackageEncode(ReceiptPackage rcPkg) throws UnsupportedEncodingException
    {
        final int USER_SIZE = 20;
        ByteBuffer buf = ByteBuffer.allocate(32);

        buf.put("rec".getBytes("UTF-8"));
        buf.put(TransString2ByteArray(rcPkg.getUser(), USER_SIZE));

        return buf.array();
    }
    //dropped.
    static public AccountPackage ReceiptPackageDecode(byte[] message)
    {
        final int ID_SIZE = 4, MONEY_SIZE = 4, YEAR_SIZE = 4, MONTH_SIZE = 1, DAY_SIZE = 1, DETAIL_SIZE = 30, RECEIPT_SIZE = 3, STATUS_SIZE = 1;
        int currentSize = 0;
        AccountPackage result = new AccountPackage();

        result.setID(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + ID_SIZE), ID_SIZE));
        currentSize += ID_SIZE;
        result.setMoney(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + MONEY_SIZE), MONEY_SIZE));
        currentSize += MONEY_SIZE;
        result.setYear(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + YEAR_SIZE), YEAR_SIZE));
        currentSize += YEAR_SIZE;
        result.setMonth(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + MONTH_SIZE), MONTH_SIZE));
        currentSize += MONTH_SIZE;
        result.setDetail(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + DAY_SIZE), DAY_SIZE));
        currentSize += DAY_SIZE;
        result.setDetail(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + DETAIL_SIZE), DETAIL_SIZE));
        currentSize += DETAIL_SIZE;
        result.setReceipt(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + RECEIPT_SIZE), RECEIPT_SIZE));
        currentSize += RECEIPT_SIZE;
        result.setType(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + STATUS_SIZE), STATUS_SIZE) == 1);
        currentSize += STATUS_SIZE;

        return result;
    }

    static public byte[] ReceiptQRPackageEncode() throws UnsupportedEncodingException
    {
        ByteBuffer buf = ByteBuffer.allocate(16);

        buf.put("rqr".getBytes("UTF-8"));
        return buf.array();
    }

    static public ReceiptQRPackage ReceiptQRPackageDecode(byte[] message)
    {
        final int DATE_SIZE = 8, HIT_NUMBER_SIZE = 56;
        int currentSize = 0;
        ReceiptQRPackage result = new ReceiptQRPackage();
        String tempString;

        tempString = TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + DATE_SIZE), DATE_SIZE);
        result.setYear(tempString.substring(0, 4));
        result.setMonth(tempString.substring(4, 6));
        currentSize += DATE_SIZE;

        tempString = TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + HIT_NUMBER_SIZE), HIT_NUMBER_SIZE);
        ArrayList<String> stringBuf = new ArrayList<String>();
        for(int i = 0;i < HIT_NUMBER_SIZE; i += 8)
        {
            String subTemp = tempString.substring(i, i + 8);
            if (subTemp.equals("xxxxxxxx")) break;
            stringBuf.add(subTemp);
        }
        result.setHitNumber(stringBuf);
        currentSize += HIT_NUMBER_SIZE;

        return result;
    }

    static public byte[] VersionPackageEncode(VersionPackage vrPkg) throws UnsupportedEncodingException
    {
        final int NAME_SIZE = 20;
        ByteBuffer buf = ByteBuffer.allocate(VersionPackage.PACKAGE_SIZE);

        buf.put("ver".getBytes("UTF-8"));
        buf.put(TransInt2ByteArray(vrPkg.getVersionCode(), 4));
        buf.put(TransString2ByteArray(vrPkg.getName(), NAME_SIZE));
        return buf.array();
    }

    static public VersionPackage VersionPackageDecode(byte[] message)
    {
        final int VERSIONCODE_SIZE = 4, NAME_SIZE = 20;
        int currentSize = 0;
        VersionPackage result = new VersionPackage();

        result.setVersionCode(TransByteArray2Int(Arrays.copyOfRange(message, currentSize, currentSize + VERSIONCODE_SIZE), VERSIONCODE_SIZE));
        currentSize += VERSIONCODE_SIZE;

        result.setName(TransByteArray2String(Arrays.copyOfRange(message, currentSize, currentSize + NAME_SIZE), NAME_SIZE));
        currentSize += NAME_SIZE;

        return result;
    }

    static public byte[] TransInt2ByteArray(int num, int MES_SIZE)
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
    static public int TransByteArray2Int(byte[] message, int MES_SIZE)
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
    static public String TransByteArray2String(byte[] message, int MES_SIZE)
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
    static public byte[] TransString2ByteArray(String message, int MES_SIZE) throws UnsupportedEncodingException {
        if(message.getBytes().length == MES_SIZE) return message.getBytes("UTF-8");
        else // < MES_SIZE
        {
            ByteBuffer b_temp = ByteBuffer.allocate(MES_SIZE);
            b_temp.put(message.getBytes("UTF-8"));
            int length = Math.min(message.getBytes().length, MES_SIZE);
            for(int i = length; i < MES_SIZE; i++)
            {
                b_temp.put((byte)0);
            }
            return b_temp.array();
        }
    }
}

