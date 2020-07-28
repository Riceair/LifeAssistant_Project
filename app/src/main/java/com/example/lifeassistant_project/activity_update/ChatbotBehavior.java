package com.example.lifeassistant_project.activity_update;

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
        this.errorMessage = "";
    }
    public void sendSentence()
    {
        this.client.setChatBot(this.sendSentence);
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

        this.sentenceHandler = client.getRcvSentence();
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
            DatabaseBehavior.synchronizeServer2Client();
        }
    }
    public String getResponse()
    {
        if(this.sentenceHandler.getIntent() == 0 && this.sentenceHandler.getOperation() == 0)
        {
            return this.sentenceHandler.getFulfillment();
        }
        else if(this.sentenceHandler.getIntent() == 1)
        {
            switch (this.sentenceHandler.getOperation())
            {
                case 1:
                    return "好的！請說出您想要記下的帳目。（您可以告訴我：記帳金額、時間、以及類型）";
                case 2:
                    return "已將您告訴我的記帳項目刪除完畢。";
                case 3:
                    return "修改記帳??";
                case 4:
                    return "好的！以下是您欲查詢的記帳項目：";
            }
        }
        else if(this.sentenceHandler.getIntent() == 2)
        {
            switch (this.sentenceHandler.getOperation())
            {
                case 1:
                    return "好的！請說出您想要我記住的事情。";
                case 2:
                    return "好的！我已經將這件事項刪除完畢。";
                case 3:
                    return "修改行程??";
                case 4:
                    return "好的，以下是您的行程：";
            }
        }
        else
        {
            return "Error! intent or operation code has exception.";
        }
        return null;
    }
    public boolean generateSendSentence(String message)
    {
        this.sendSentence = new SentenceHandler();
        if(this.behaviorMode == 0)
        {
            this.sendSentence.setIntent(0);
            this.sendSentence.setOperation(0);
            this.sendSentence.setFulfillment(message);
        }
        else if(this.behaviorMode == 1)
        {
            int id = (int) (Math.random() * 99999)+1;

            this.sendSentence.setIntent(sentenceHandler.getIntent());
            this.sendSentence.setOperation(sentenceHandler.getOperation());
            this.sendSentence.setFulfillment(message + "@" + LoginPackage.getUserName() + "#" + id);
        }

        return true;
    }
}
