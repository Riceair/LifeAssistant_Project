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
    2 -> Undefined.
    */

    public int getBehaviorMode() {
        return behaviorMode;
    }

    private SentenceHandler sentenceHandler;
    private SentenceHandler sendSentence;
    private ClientProgress client;
    private String errorMessage;

    public SentenceHandler getSentenceHandler() {
        return sentenceHandler;
    }

    public void setSentenceHandler(SentenceHandler sentenceHandler) { this.sentenceHandler = sentenceHandler; }

    public String getErrorMessage() { return errorMessage; }

    public ChatbotBehavior()
    {
        this.behaviorMode = 0;
        this.client = new ClientProgress();
        this.sentenceHandler = new SentenceHandler();
        this.errorMessage = "default error message.";
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

        SentenceHandler rcvSentence = (SentenceHandler) client.getRcvPackageList().get(0);
        this.sentenceHandler = rcvSentence;
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

            AccountPackage temp = (AccountPackage) resultList.get(0);
            System.out.println(temp.getMoney());
            return "好的！以下是您欲查詢的記帳項目：";
        }
        else if(this.sentenceHandler.getIntent() == 1)
        {
            System.out.println("記帳");
            switch (this.sentenceHandler.getOperation())
            {
                case 1:
//                    if(this.sentenceHandler.getFulfillment().equals("請重新輸入"))
//                        return "不好意思，我沒有聽懂您的意思，請再說一次。";
//                    else
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
        System.out.println(detMessage);
        if(this.behaviorMode == 0)
        {
            this.sendSentence.setIntent(0);
            this.sendSentence.setOperation(0);
            this.sendSentence.setFulfillment(detMessage+ "@" + LoginHandler.getUserName());
        }
        else if(this.behaviorMode == 1)
        {
            int id = (int) (Math.random() * 99999)+1;

            this.sendSentence.setIntent(sentenceHandler.getIntent());
            this.sendSentence.setOperation(sentenceHandler.getOperation());
            this.sendSentence.setFulfillment(detMessage + "@" + LoginHandler.getUserName() + "#" + id);
        }

        return true;
    }
    private String checkForChineseNumber(String message)
    {
        String resultString = new String(message);
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
}
