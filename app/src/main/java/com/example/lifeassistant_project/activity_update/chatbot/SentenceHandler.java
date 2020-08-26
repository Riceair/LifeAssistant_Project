package com.example.lifeassistant_project.activity_update.chatbot;

public class SentenceHandler {
    private int intent, operation;
    private String fulfillment;

    public SentenceHandler()
    {
        this.intent = 0;
        this.operation = 0;
        this.fulfillment = "null";
    }

    public int getOperation() { return operation; }

    public void setOperation(int operation) { this.operation = operation; }

    public int getIntent() {
        return intent;
    }

    public void setIntent(int intent) {
        this.intent = intent;
    }

    public String getFulfillment() {
        return fulfillment;
    }

    public void setFulfillment(String fulfillment) {
        this.fulfillment = fulfillment;
    }
}
