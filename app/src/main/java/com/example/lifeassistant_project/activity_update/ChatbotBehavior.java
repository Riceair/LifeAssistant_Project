package com.example.lifeassistant_project.activity_update;

public class ChatbotBehavior {
    private int behaviorMode;
    /*
    0 -> Default mode. To detect what user want to do with chatBot with nature language.
    1 -> Ready mode. The chatBot detect user's intent, and ready to listen to user's detail operation.
    2 ->

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
                System.out.println("WAITTING");
                client.wait();
                System.out.println("GOGOGO");
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
//        else if(this.sentenceHandler.getIntent() )
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
//          stupid method.
            int id = (int) (Math.random() * 99999)+1;
//            boolean generateDone = false;
//            while (!generateDone)
//            {
//                generateDone = true;
//                for (int i : DatabaseBehavior.getAccountIDList())
//                {
//                    if (i == id)
//                    {
//                        id = (int) (Math.random() * 99999)+1;
//                        generateDone = false;
//                        break;
//                    }
//                }
//            }

            this.sendSentence.setIntent(sentenceHandler.getIntent());
            this.sendSentence.setOperation(sentenceHandler.getOperation());
            this.sendSentence.setFulfillment(message + "@" + LoginPackage.getUserName() + "#" + id);
        }

        return true;
    }
}
