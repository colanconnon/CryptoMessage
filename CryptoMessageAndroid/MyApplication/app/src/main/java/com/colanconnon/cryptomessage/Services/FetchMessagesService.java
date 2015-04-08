package com.colanconnon.cryptomessage.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.colanconnon.cryptomessage.Activities.MessageDetailActivity;
import com.colanconnon.cryptomessage.Database.DatabaseDatasource;
import com.colanconnon.cryptomessage.R;
import com.colanconnon.cryptomessage.Utils.JsonFetcher;
import com.colanconnon.cryptomessage.encryption.AES256Encryption;
import com.colanconnon.cryptomessage.encryption.RSAEncrypt;
import com.colanconnon.cryptomessage.encryption.RSAKeyManager;
import com.colanconnon.cryptomessage.encryption.SymmetricKeyEncryptionStrategy;
import com.colanconnon.cryptomessage.models.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by colan on 3/8/2015.
 */
public class FetchMessagesService extends Service {
    private Timer timer;
    private  String URL= "http://cryptomessage.mobi/api/messages/";
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("test", "Started service");
        startFetching();
    }

    private void startFetching(){
        final DatabaseDatasource databaseDatasource = new DatabaseDatasource(this.getApplicationContext());
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                URL= "http://cryptomessage.mobi/api/messages/";
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null){
                    Message message1= databaseDatasource.getLastMessage();
                    int id =0;
                    if(message1 != null){
                        id = (int) message1.getId();
                    }

                    URL = URL + id + "/";
                    JSONArray jsonObject1 = JsonFetcher.JSONGetArrayRequestWithAuthHeader(URL, getApplicationContext());


                    try{
                        for(int i =0; i < jsonObject1.length(); i++){
                            JSONObject jsonObject2 = jsonObject1.getJSONObject(i);
                            JSONObject messageSender = jsonObject2.getJSONObject("messageSender");
                            JSONObject conversation = jsonObject2.getJSONObject("conversation");
                            Message message = new Message();
                            message.setId(jsonObject2.getLong("id"));
                            message.setMessageDate(jsonObject2.getString("date"));
                            message.setEncryptionKey(jsonObject2.getString("key"));
                            message.setDeviceID(jsonObject2.getLong("device"));
                            message.setOwner(jsonObject2.getBoolean("owner"));
                            message.setConversationID(conversation.getLong("id"));
                            message.setConversationName(conversation.getString("name"));
                            message.setMessageSenderID(messageSender.getLong("id"));
                            message.setMessageSender(messageSender.getString("username"));
                            message.setText(jsonObject2.getString("text"));
                            RSAKeyManager rsaKeyManager = new RSAKeyManager(FetchMessagesService.this);
                            rsaKeyManager.getKeysFromEditor();
                            SymmetricKeyEncryptionStrategy encryptionStrategy = new AES256Encryption();
                            RSAEncrypt rsaEncrypt = new RSAEncrypt();
                            rsaEncrypt.setPrivateKey(rsaKeyManager.getPrivateKey());
                            rsaEncrypt.decrypt(message);
                            encryptionStrategy.decrypt(message);
                            databaseDatasource.insertMessage(message);
                            sendNotification();
                        }
                        Intent intent = new Intent("com.colanconnon.cryptomessage.custom.intent.action.UpdateMessageList");
                        FetchMessagesService.this.sendBroadcast(intent);
                        Intent intent1 = new Intent("com.colanconnon.cryptomessage.custom.intent.action.UpdateMessageDetail");
                        FetchMessagesService.this.sendBroadcast(intent1);

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }
            }
        };
        timer = new Timer(true);
        int delay = 5;
        int interval = 1000 * 20; // 20 seconds
        timer.schedule(task, delay, interval);

    }

    private void sendNotification() {
        //TODO send a notification
        Intent notificationIntent = new Intent(this, MessageDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        DatabaseDatasource datasource = new DatabaseDatasource(this);
        Message msg = datasource.getLastMessage();
        if (msg != null) {
            String text1 = msg.getText();
            notificationIntent.putExtra("conversationId", msg.getConversationID());
            notificationIntent.putExtra("conversationName", msg.getConversationName());
            int flags = PendingIntent.FLAG_CANCEL_CURRENT;
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags);
            int icon = R.drawable.ic_launcher;
            CharSequence tickerText = "You have a new message from " + msg.getMessageSender();
            CharSequence contentTitle = msg.getMessageSender() + " Sent you a new message saying:";
            CharSequence contentText = text1;
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification notification = new NotificationCompat.Builder(this).setSmallIcon(icon).setTicker(tickerText).setContentTitle(contentTitle).setContentText(contentText).setContentIntent(pendingIntent).setAutoCancel(true).setSound(uri).build();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            final int NOTIFICATION_ID = 1;
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
