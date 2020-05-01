package com.feup.sdis.messages;

public class MessageHandler implements Runnable {
    Message message;


    public MessageHandler(Object messageObj){
        System.out.println("Received: " + messageObj);
        if(messageObj instanceof Message)
            message = (Message) messageObj;
    }


    @Override
    public void run() {
        message.handle();
    }
    
}