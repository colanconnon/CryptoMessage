package com.colanconnon.cryptomessage.Fragments;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.colanconnon.cryptomessage.Adapters.MessageDetailAdapter;
import com.colanconnon.cryptomessage.Database.DatabaseDatasource;
import com.colanconnon.cryptomessage.R;
import com.colanconnon.cryptomessage.Utils.DialogUtil;
import com.colanconnon.cryptomessage.Utils.JsonFetcher;
import com.colanconnon.cryptomessage.encryption.AES256Encryption;
import com.colanconnon.cryptomessage.encryption.RSAEncrypt;
import com.colanconnon.cryptomessage.encryption.RSAKeyManager;
import com.colanconnon.cryptomessage.encryption.SymmetricKeyEncryptionStrategy;
import com.colanconnon.cryptomessage.models.Message;
import com.colanconnon.cryptomessage.models.Recipient;
import com.colanconnon.cryptomessage.services.FetchMessagesService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MessageDetailFragment extends Fragment {
    private ListView messageDetailListView;
    private TextView messageDetailUsernameTxtView;
    private EditText messageDetailMessageText;
    private Button messageDetailSendButton;
    long conversationId;
    String conversationName;
    protected ProgressDialog progressDialog;
    private ArrayList<Recipient> recipients;
    private String username;
    private int userID;
    MessageDetailAdapter messageDetailAdapter;
    private IntentFilter intentFilter;
    private UpdateMessageDetail updateMessageDetail;

    public MessageDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_message_detail, container, false);
        messageDetailListView = (ListView) view.findViewById(R.id.messageDetailListView);
        messageDetailUsernameTxtView = (TextView) view.findViewById(R.id.messageDetailUsernameTxtView);
        messageDetailSendButton = (Button) view.findViewById(R.id.messageDetailSendButton);
        messageDetailMessageText = (EditText) view.findViewById(R.id.messageDetailMessageText);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending message. Please wait");

        intentFilter = new IntentFilter("com.colanconnon.cryptomessage.custom.intent.action.UpdateMessageDetail");
        updateMessageDetail = new UpdateMessageDetail();

        recipients = new ArrayList<>();
        messageDetailSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageDetailMessageText.getText().toString().length() > 0){

                    new GetDevicesTask().execute();
                }
            }
        });
        messageDetailMessageText.setOnKeyListener(new View.OnKeyListener() {
              @Override
              public boolean onKey(View v, int keyCode, KeyEvent event) {
                  if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                          (keyCode == KeyEvent.KEYCODE_ENTER)) {
                      messageDetailSendButton.performClick();
                      return true;
                  }
                  if (keyCode == EditorInfo.IME_ACTION_DONE) {
                    messageDetailSendButton.performClick();
                    return true;
                }
                  return false;
              }
          });



        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            conversationId = extras.getLong("conversationId");
            conversationName = extras.getString("conversationName");

        }
        Bundle args = getArguments();
        conversationId = args.getLong("conversationId",0);
        conversationName = args.getString("conversationName", "");
        SharedPreferences prefs = getActivity().getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
        username = prefs.getString("username", null);
        userID = prefs.getInt("userID", 0);
        messageDetailUsernameTxtView.setText(conversationName);
        loadMessages();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        loadMessages();
        getActivity().registerReceiver(updateMessageDetail, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(updateMessageDetail);
    }

    private void loadMessages(){
        DatabaseDatasource databaseDatasource = new DatabaseDatasource(getActivity().getApplicationContext());
        ArrayList<Message> messages = databaseDatasource.getMessages(conversationId);
        messageDetailAdapter = new MessageDetailAdapter(getActivity().getApplicationContext(), messages,username);
        messageDetailListView.setAdapter(messageDetailAdapter);

    }

    private class GetDevicesTask extends AsyncTask<Void,Void,Boolean>{
        String url = "http://cryptomessage.mobi/api/getdevices/";
        @Override
        protected Boolean doInBackground(Void... params) {
            String text = conversationName;
            List<String> list = new ArrayList<>(Arrays.asList(text.split(",")));
            for(String item : list){
                Log.e("TAG", item);
                Recipient recipient = new Recipient();
                recipient.setUserName(item);
                recipients.add(recipient);
            }
            for(final Recipient recipient:recipients){
                try{
                    url = "http://cryptomessage.mobi/api/getdevices/" + recipient.getUserName() + "/";
                    JSONObject jsonResponseObject = JsonFetcher.JSONGetObjectRequestWithAuthHeader(url, getActivity());
                    recipient.setDeviceID(jsonResponseObject.getInt("id"));
                    recipient.setPublicKey(jsonResponseObject.getString("publicKey"));
                }
                catch (Exception e){
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtil.showDialog(getActivity(), "Recipient " + recipient.getUserName() + " Doesn't exist");
                        }
                    });
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            Intent intent1 = new Intent(getActivity().getApplicationContext(), FetchMessagesService.class);
            getActivity().stopService(intent1);

        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success.equals(Boolean.TRUE)){
                new SendMessagesTask().execute();
            }
            else{
                Toast.makeText(getActivity(), "Failed to send the messages", Toast.LENGTH_LONG).show();
            }


        }
    }
    private class SendMessagesTask extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            new FetchMessageServiceTask().execute();
            recipients.clear();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            for(Recipient recipient: recipients){
                try{
                    String url = "http://cryptomessage.mobi/api/messagedetail/";
                    Message message = new Message();
                    message.setText(messageDetailMessageText.getText().toString());
                    JSONObject jsonObject = new JSONObject();
                    message.setConversationID(conversationId);
                    message.setMessageSenderID(userID);
                    if(recipient.getUserName().equals(username)){
                        message.setOwner(true);
                    }
                    else{
                        message.setOwner(false);
                    }
                    SymmetricKeyEncryptionStrategy encryptionStrategy = new AES256Encryption();
                    Message message1 = encryptionStrategy.encrypt(message);

                    PublicKey publicKey = recipient.getPubPublicKey();
                    RSAEncrypt rsaEncrypt = new RSAEncrypt();
                    rsaEncrypt.setPublicKey(publicKey);
                    Message message2 = rsaEncrypt.encrypt(message1);

                    jsonObject.put("key", message2.getEncryptionKey());
                    jsonObject.put("device", recipient.getDeviceID());
                    jsonObject.put("messageSender", message2.getMessageSenderID());
                    jsonObject.put("conversation", message2.getConversationID());
                    jsonObject.put("text", message2.getText());
                    jsonObject.put("owner", message2.isOwner());


                    JSONObject jsonResponse = JsonFetcher.JSONPOSTRequestWithAuthHeader(url,getActivity(),jsonObject);


                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    private class UpdateMessageDetail extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            loadMessages();
        }
    }
    private class FetchMessageServiceTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
           DatabaseDatasource databaseDatasource = new DatabaseDatasource(getActivity().getApplicationContext());
           String URL= "http://cryptomessage.mobi/api/messages/";
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null){
                Message message1= databaseDatasource.getLastMessage();
                int id =0;
                if(message1 != null){
                    id = (int) message1.getId();
                }

                URL = URL + id + "/";
                JSONArray jsonObject1 = JsonFetcher.JSONGetArrayRequestWithAuthHeader(URL, getActivity().getApplicationContext());


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
                        RSAKeyManager rsaKeyManager = new RSAKeyManager(getActivity());
                        rsaKeyManager.getKeysFromEditor();
                        SymmetricKeyEncryptionStrategy encryptionStrategy = new AES256Encryption();
                        RSAEncrypt rsaEncrypt = new RSAEncrypt();
                        rsaEncrypt.setPrivateKey(rsaKeyManager.getPrivateKey());
                        rsaEncrypt.decrypt(message);
                        encryptionStrategy.decrypt(message);
                        databaseDatasource.insertMessage(message);
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            loadMessages();
            Intent intent1 = new Intent(getActivity().getApplicationContext(), FetchMessagesService.class);
            getActivity().startService(intent1);
            loadMessages();
            messageDetailMessageText.setText("");
            progressDialog.dismiss();
        }
    }



}
