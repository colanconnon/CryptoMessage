package com.colanconnon.cryptomessage.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.colanconnon.cryptomessage.Utils.ConversationSorter;
import com.colanconnon.cryptomessage.Utils.MessageSorter;
import com.colanconnon.cryptomessage.models.Conversation;
import com.colanconnon.cryptomessage.models.Message;

import java.util.ArrayList;

/**
 * Created by colan on 3/5/2015.
 */
public class DatabaseDatasource {
    private SQLiteDatabase database;
    private Database dbhelper;

    public DatabaseDatasource(Context context){
        dbhelper = new Database(context, Database.dbname, null, Database.version);
    }
    public void openReadableDb() {
        database = dbhelper.getReadableDatabase();
    }

    public void openWriteableDb() {
        database = dbhelper.getWritableDatabase();
    }

    private void closeDb() {
        if (database != null) {
            database.close();
        }
    }

    public void insertMessage(Message message){
        /*
         private static final String createMessageTable =
            "Create table message (messageID INTEGER, " +
                    "text TEXT, key TEXT, messageSenderId INTEGER, messageSender TEXT " +
                    "isOwner TEXT, deviceID INTEGER, conversationID INTEGER";
         */
        ContentValues contentValues = new ContentValues();
        contentValues.put("messageID", message.getId());
        contentValues.put("text", message.getText());
        contentValues.put("key", message.getEncryptionKey());
        contentValues.put("messageSenderId", message.getMessageSenderID());
        contentValues.put("messageSender", message.getMessageSender());
        contentValues.put("isOwner", String.valueOf(message.isOwner()));
        contentValues.put("deviceID", message.getDeviceID());
        contentValues.put("conversationID", message.getConversationID());
        contentValues.put("conversationName", message.getConversationName());
        contentValues.put("date", message.getMessageDate());
        this.openWriteableDb();
        database.insert("message", null, contentValues);
        closeDb();
    }
//    private void getConversations(){
//        ArrayList<Conversation> conversations = new ArrayList<>();
//        ArrayList<Message> messages = new ArrayList<>();
//        this.openReadableDb();
//        Cursor cursor = database.query("messages",null,null,null,null,null,null);
//        while(cursor.moveToNext()){
//            Message message = new Message();
//            message.setConversationID(cursor.getLong(cursor.getColumnIndex("conversationID")));
//            message.setDeviceID(cursor.getLong(cursor.getColumnIndex("deviceID")));
//            message.setOwner(Boolean.getBoolean(cursor.getString(cursor.getColumnIndex("isOwner"))));
//            message.setText(cursor.getString(cursor.getColumnIndex("text")));
//            message.setMessageSender(cursor.getString(cursor.getColumnIndex("messageSender")));
//            message.setEncryptionKey(cursor.getString(cursor.getColumnIndex("key")));
//            message.setId(cursor.getLong(cursor.getColumnIndex("messageID")));
//            message.setMessageSenderID(cursor.getLong(cursor.getColumnIndex("messageSenderId")));
//            message.setMessageDate(cursor.getString(cursor.getColumnIndex("date")));
//            messages.add(message);
//        }
//        for(Message message: messages){
//            boolean addedMessage = false;
//            //if in a conversation add to conversations, else new conversation
//            for (Conversation conversation: conversations){
//                if(conversation.getConversationID() == message.getConversationID()){
//                    conversation.getMessages().add(message);
//                    addedMessage = true;
//                }
//            }
//            if (!addedMessage){
//                Conversation conversation = new Conversation();
//                conversation.setConversationID(message.getConversationID());
//                conversation.setMessages(new ArrayList<Message>());
//                conversation.getMessages().add(message);
//            }
//        }
//
//    }
    public ArrayList<Message> getMessages(long conversationId){
        ArrayList<Message> messages = new ArrayList<>();
        this.openReadableDb();
        String where = "conversationID = ?";
        String[] whereArgs = {String.valueOf(conversationId)};
        Cursor cursor =database.query("message",null,where,whereArgs,null,null,null);
        while(cursor.moveToNext()) {
            Message message = new Message();
            message.setConversationID(cursor.getLong(cursor.getColumnIndex("conversationID")));
            message.setDeviceID(cursor.getLong(cursor.getColumnIndex("deviceID")));
            if (cursor.getString(cursor.getColumnIndex("isOwner")).equals("true")) {
                message.setOwner(true);
            }
            else{
                message.setOwner(false);
            }
            message.setText(cursor.getString(cursor.getColumnIndex("text")));
            message.setMessageSender(cursor.getString(cursor.getColumnIndex("messageSender")));
            message.setEncryptionKey(cursor.getString(cursor.getColumnIndex("key")));
            message.setId(cursor.getLong(cursor.getColumnIndex("messageID")));
            message.setMessageSenderID(cursor.getLong(cursor.getColumnIndex("messageSenderId")));
            message.setMessageDate(cursor.getString(cursor.getColumnIndex("date")));
            message.setConversationName(cursor.getString(cursor.getColumnIndex("conversationName")));
            messages.add(message);
        }
        MessageSorter messageSorter = new MessageSorter(messages);
        try{
           ArrayList<Message> messagesSorted = messageSorter.sortAsc();
            return messagesSorted;
        }
       catch (Exception e){
            e.printStackTrace();
       }
        closeDb();
        return messages;
    }
    public ArrayList<Conversation> getConversations(){
        ArrayList<Conversation> conversations = new ArrayList<>();
        this.openReadableDb();
        Cursor cursor = database.query("message",null,null,null,"conversationID",null,null);
        while (cursor.moveToNext()){
            Conversation conversation = new Conversation();
            conversation.setConversationID(cursor.getLong(cursor.getColumnIndex("conversationID")));
            conversation.setConversationName(cursor.getString(cursor.getColumnIndex("conversationName")));
            conversations.add(conversation);
        }
        for (Conversation conversation: conversations){
            conversation.setMessages(getMessages(conversation.getConversationID()));
        }
        ConversationSorter conversationSorter = new ConversationSorter(conversations);
        ArrayList<Conversation> conversationSorted = conversationSorter.sortAsc();
        closeDb();
        return conversationSorted;
    }
    public Message getLastMessage (){
        ArrayList<Message> messages = new ArrayList<>();
        this.openReadableDb();
        Cursor cursor = database.query("message",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            Message message = new Message();
            message.setConversationID(cursor.getLong(cursor.getColumnIndex("conversationID")));
            message.setDeviceID(cursor.getLong(cursor.getColumnIndex("deviceID")));
            message.setOwner(Boolean.getBoolean(cursor.getString(cursor.getColumnIndex("isOwner"))));
            message.setText(cursor.getString(cursor.getColumnIndex("text")));
            message.setMessageSender(cursor.getString(cursor.getColumnIndex("messageSender")));
            message.setEncryptionKey(cursor.getString(cursor.getColumnIndex("key")));
            message.setId(cursor.getLong(cursor.getColumnIndex("messageID")));
            message.setMessageSenderID(cursor.getLong(cursor.getColumnIndex("messageSenderId")));
            message.setMessageDate(cursor.getString(cursor.getColumnIndex("date")));
            messages.add(message);
        }
        MessageSorter messageSorter = new MessageSorter(messages);
        try{
            ArrayList<Message> messagesSorted = messageSorter.sortDesc();
            Log.e("testingmessages", String.valueOf(messagesSorted.size()));
            return messagesSorted.get(0);
        }
        catch (Exception e) {
            return null;
        }
        finally {
            closeDb();
        }
    }
}
