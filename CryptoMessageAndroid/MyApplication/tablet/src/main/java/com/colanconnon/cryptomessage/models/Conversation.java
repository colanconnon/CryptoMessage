package com.colanconnon.cryptomessage.models;

import java.util.ArrayList;

/**
 * Created by colan on 3/5/2015.
 */
public class Conversation {
    private Long conversationID;
    private ArrayList<Message> messages;
    private String conversationName;

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getConversationName() {
        return conversationName;
    }

    public Long getConversationID() {
        return conversationID;
    }

    public void setConversationID(Long conversationID) {
        this.conversationID = conversationID;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
    public Message getLastMessage(){
        return messages.get(messages.size() - 1);
    }
    public String getConversationParticipants(){
        // place holder for now

        return messages.get(messages.size()).getMessageSender();
    }
}
