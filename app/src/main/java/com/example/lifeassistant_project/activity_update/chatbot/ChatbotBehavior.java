package com.example.lifeassistant_project.activity_update.chatbot;

import com.example.lifeassistant_project.activity_update.ClientProgress;
import com.example.lifeassistant_project.activity_update.packages.AccountPackage;
import com.example.lifeassistant_project.activity_update.packages.DataPackage;
import com.example.lifeassistant_project.activity_update.packages.SchedulePackage;
import com.example.lifeassistant_project.activity_update.packages.SentenceHandler;
import com.example.lifeassistant_project.activity_update.static_handler.DatabaseBehavior;
import com.example.lifeassistant_project.activity_update.static_handler.LoginHandler;
import com.example.lifeassistant_project.activity_update.static_handler.PackageHandler;

import java.util.ArrayList;

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
            this.currentIntent = this.supposeIntent;
    }

    private SentenceHandler sentenceHandler;
    private SentenceHandler sendSentence;
    private ClientProgress client;
    private String errorMessage;
    private int currentIntent, currentOperation, supposeIntent;

    public SentenceHandler getSentenceHandler() {
        return sentenceHandler;
    }

    public String getErrorMessage() { return errorMessage; }

    public ChatbotBehavior()
    {
        this.behaviorMode = 0;
        this.client = new ClientProgress();
        this.sentenceHandler = new SentenceHandler();
        this.errorMessage = "default error message.";
        this.supposeIntent = 0;
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

        if(this.sentenceHandler.getIntent() == 0)
        {
            this.behaviorMode = 0;
        }
        else if(this.sentenceHandler.getIntent() == 1 || this.sentenceHandler.getIntent() == 2)
        {
            this.behaviorMode = 1;
        }

        if(this.sendSentence.getIntent() == 1 || this.sendSentence.getIntent() == 2)
        {
            DatabaseBehavior.synchronizeServer2Client_Account();
        }
    }
    public String getResponse()
    {
        if(this.sentenceHandler.getIntent() == 0 && this.sentenceHandler.getOperation() == 0)
        {
            if(this.sentenceHandler.getRcvSelectedList().size() == 0)
                return this.sentenceHandler.getFulfillment();

            ArrayList<DataPackage> resultList = this.sentenceHandler.getRcvSelectedList();

            AccountPackage temp = (AccountPackage) resultList.get(1);
//            System.out.println("DEBUG//////////////////////////");
//            System.out.println(this.sentenceHandler.getCalculateType());
//            System.out.println(temp.getMoney());
            return "好的！以下是您欲查詢的記帳項目：";
        }
        else if(this.sentenceHandler.getIntent() == 1)
        {
            System.out.println("記帳");
            switch (this.sentenceHandler.getOperation())
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
        else if(this.sentenceHandler.getIntent() == 2)
        {
            switch (this.sentenceHandler.getOperation())
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
        else if(this.sentenceHandler.getIntent() == 3)
        {
            //猜測意圖
            this.checkSupposedIntent(this.sentenceHandler.getFulfillment());
            return this.sentenceHandler.getFulfillment();
        }
        else if(this.sentenceHandler.getIntent() == 4)
        {
            return "好的！以下是最近一週的天氣預報：";
        }
        else
        {
            System.out.println(this.sentenceHandler.getIntent());
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
        return ((this.sentenceHandler.getIntent() == 1 || this.sentenceHandler.getIntent() == 2) && (this.sentenceHandler.getOperation() == 4))
                || this.sentenceHandler.getIntent() == 4
                || this.sentenceHandler.getIntent() == 3;
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

    private void checkSupposedIntent(String message)
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
    }
}
