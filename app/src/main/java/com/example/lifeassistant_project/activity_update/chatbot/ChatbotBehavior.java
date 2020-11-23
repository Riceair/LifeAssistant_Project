package com.example.lifeassistant_project.activity_update.chatbot;

import android.accounts.Account;
import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.packages.AccountPackage;
import com.example.lifeassistant_project.activity_update.packages.DataPackage;
import com.example.lifeassistant_project.activity_update.packages.SchedulePackage;
import com.example.lifeassistant_project.activity_update.packages.SentenceHandler;
import com.example.lifeassistant_project.activity_update.static_handler.DatabaseBehavior;
import com.example.lifeassistant_project.activity_update.static_handler.LoginHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatbotBehavior {
    private int behaviorMode;
    /*
    0 -> Default mode. To detect what user want to do with chatBot with nature language.
    1 -> Ready mode. The chatBot detect user's intent, and ready to listen to user's detail operation.
    2 -> Still undefined.
    */

    public int getBehaviorMode() {
        return behaviorMode;
    }
    public void setDefaultMode() {this.behaviorMode = 0;}
    public void setReadyMode() {
        this.behaviorMode = 1;
        if(this.supposeIntent != 0)
        {
            this.currentIntent = this.supposeIntent;
            this.supposeIntent = 0;
        }
        if(this.supposeOperation != 0)
        {
            this.currentOperation = this.supposeOperation;
            this.supposeOperation = 0;
        }
    }

    private SentenceHandler sentenceHandler;
    private SentenceHandler sendSentence;
    private ArrayList<DataPackage> selectedPackage;
    private String selectType = "def";
    private ClientProgress client;
    private String errorMessage;
    private boolean searchFlag;
    private int currentIntent, currentOperation, supposeIntent, supposeOperation;

    public SentenceHandler getSentenceHandler() {
        return sentenceHandler;
    }

    public String getErrorMessage() { return errorMessage; }

    public ArrayList<DataPackage> getSelectedPackage() { return selectedPackage; }

    public String getSelectType() { return selectType; }

    public int getCurrentIntent() { return currentIntent; }

    public int getCurrentOperation() { return currentOperation; }

    public boolean isSearchFlag() { return searchFlag; }

    public ChatbotBehavior()
    {
        this.behaviorMode = 0;
        this.client = new ClientProgress();
        this.sentenceHandler = new SentenceHandler();
        this.errorMessage = "default error message.";
        this.supposeIntent = 0;
        this.searchFlag = false;
    }
    public void sendSentence()
    {
        this.client.setPackage(this.sendSentence);
        Thread cThread = new Thread(this.client);
        cThread.start();

        synchronized (client)
        {
            try {
                client.wait();
            }
            catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        this.sentenceHandler = (SentenceHandler) client.getRcvPackageList().get(0);
        this.currentIntent = this.sentenceHandler.getIntent();
        this.currentOperation = this.sentenceHandler.getOperation();

        if(this.currentIntent == 0)
        {
            this.behaviorMode = 0;
        }
        else if(this.currentIntent == 1 || this.currentIntent == 2)
        {
            this.behaviorMode = 1;
        }

        //not a reliable logic validation. need to be remade.
        if((this.sendSentence.getIntent() == 1 || this.sendSentence.getIntent() == 2) ||
                (this.currentIntent == 0 && this.sentenceHandler.getFulfillment().substring(0, 1).equals("已")))
        {
            DatabaseBehavior.synchronizeServer2Client();
        }
    }

    public String getResponse()
    {
        this.searchFlag = false;

        if(this.currentIntent == 0 && this.currentOperation == 0)
        {
            this.selectType = "def";

            if(this.sentenceHandler.getRcvSelectedList().size() == 0 && !this.sentenceHandler.getFulfillment().contains("null_package"))
                return this.sentenceHandler.getFulfillment();
            else if(this.sentenceHandler.getFulfillment().contains("null_package"))
                return "很抱歉，沒有任何欲搜尋的結果！";

            this.searchFlag = true;
            this.selectedPackage = this.sentenceHandler.getRcvSelectedList();
            this.selectType = this.sentenceHandler.getCalculateType();

//            AccountPackage temp = (AccountPackage) this.selectedPackage.get(1);
//            System.out.println("DEBUG//////////////////////////");
//            System.out.println(this.sentenceHandler.getCalculateType());
//            System.out.println(temp.getMoney());

            boolean isAccountPackage = true;

            try{
                AccountPackage temp = (AccountPackage) this.selectedPackage.get(0);
            }catch (ClassCastException e)
            {
                SchedulePackage temp = (SchedulePackage) this.selectedPackage.get(0);
                isAccountPackage = false;
            }catch (Exception e)
            {
                throw e;
            }

            if(isAccountPackage)
                return "好的！以下是您欲查詢的記帳項目：";
            else if(!isAccountPackage)
                return "好的！以下是您欲查詢的行程項目：";
        }
        else if(this.currentIntent == 1)
        {
            System.out.println("記帳");
            switch (this.currentOperation)
            {
                case 1:
                    return "好的！請說出您想要記下的帳目。（您可以告訴我：記帳金額、時間、以及類型）";
                case 2:
                    return "好的！請告訴我您想刪除哪一時間的帳目。";
                case 3:
                    return "修改記帳??";
                case 4:
                    return "好的！請告訴我您想要查詢哪些帳目。（您可以告訴我：記帳金額、時間）";
            }
        }
        else if(this.currentIntent == 2)
        {
            switch (this.currentOperation)
            {
                case 1:
                    return "好的！請說出您想要我記住的事情。";
                case 2:
                    return "好的！請告訴我您想刪除哪一時間的行程。";
                case 3:
                    return "修改行程??";
                case 4:
                    return "好的！請告訴我您想要查詢哪些行程。（您可以告訴我：時間）";
            }
        }
        else if(this.currentIntent == 3)
        {
            //猜測意圖
            this.checkSupposedIntentAndOperation(this.sentenceHandler.getFulfillment());
            return this.sentenceHandler.getFulfillment();
        }
        else if(this.currentIntent == 4)
        {
            return "好的！以下是" + this.TransWeatherTime(this.sentenceHandler.getFulfillment()) + "的天氣預報：";
        }
        else
        {
            System.out.println(this.currentIntent);
            return "Error! intent or operation code has exception.";
        }
        return null;
    }
    public boolean generateSendSentence(String message)
    {
        this.sendSentence = new SentenceHandler();
        String detMessage = checkForChineseNumber(message);
//        System.out.println(detMessage);
        if(this.behaviorMode == 0)
        {
            this.sendSentence.setIntent(0);
            this.sendSentence.setOperation(0);
            this.sendSentence.setFulfillment(detMessage+ "@" + LoginHandler.getUserName());
        }
        else if(this.behaviorMode == 1)
        {
            int id = (int) (Math.random() * 99999)+1;

            this.sendSentence.setIntent(this.currentIntent);
            this.sendSentence.setOperation(this.currentOperation);
            this.sendSentence.setFulfillment(detMessage + "@" + LoginHandler.getUserName() + "#" + id);
        }
        else
        {
            System.out.println("chatbot mode error!");
            return false;
        }

        return true;
    }

    public boolean ifNeedSubWindow()
    {
        return (this.searchFlag
                || this.currentIntent == 4
                || this.currentIntent == 3);
    }

    private String checkForChineseNumber(String message)
    {
        String resultString;
        resultString = message.replace('一', '1');
        resultString = resultString.replace('二', '2');
        resultString = resultString.replace('三', '3');
        resultString = resultString.replace('四', '4');
        resultString = resultString.replace('五', '5');
        resultString = resultString.replace('六', '6');
        resultString = resultString.replace('七', '7');
        resultString = resultString.replace('八', '8');
        resultString = resultString.replace('九', '9');

        return resultString;
    }

    private void checkSupposedIntentAndOperation(String message)
    {
        if(message.contains("記帳"))
            this.supposeIntent = 1;
        else if(message.contains("行程"))
            this.supposeIntent = 2;
        else
        {
            System.out.println("Supposing intent error! please check the fulfillment of sentenceHandler.");
            this.supposeIntent = 0;
        }

        if(message.contains("增") || message.contains("加") || message.contains("記") || message.contains("入"))
            this.supposeOperation = 1;
        else if(message.contains("刪"))
            this.supposeOperation = 2;
        else if(message.contains("改"))
            this.supposeOperation = 3;
        else if(message.contains("查") || message.contains("看"))
            this.supposeOperation = 4;
        else
        {
            System.out.println("Supposing operation error! please check the fulfillment of sentenceHandler.");
            this.supposeOperation = 0;
        }
    }

    private String TransInt2WeekWord(int w)
    {
        switch (w)
        {
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "日";
            default:
                return "Null";
        }
    }

    public String TransWeatherTime(String message)
    {
        int number = Integer.parseInt(message.substring(0, 1));

        switch (number)
        {
            case 0:
                return "今天";
            case 1:
                return "明天";
            case 2:
                return "後天";
            default:
                Calendar calendar = Calendar.getInstance();
                boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);
                int weekDay = calendar.get(Calendar.DAY_OF_WEEK);

                if(isFirstSunday)
                {
                    weekDay--;
                    if(weekDay == 0)
                        weekDay = 7;
                }

                number += weekDay;
                while(number > 7)
                    number -= 7;
                return "星期" + this.TransInt2WeekWord(number);
        }
    }

    public int TransWeatherTime()
    { return (int) this.sentenceHandler.getFulfillment().charAt(0); }
}
