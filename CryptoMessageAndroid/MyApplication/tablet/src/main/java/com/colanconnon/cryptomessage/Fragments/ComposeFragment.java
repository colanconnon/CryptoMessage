package com.colanconnon.cryptomessage.Fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.colanconnon.cryptomessage.Activities.MessageDetailActivity;
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
public class ComposeFragment extends Fragment {
    private EditText composeUsernameTxt;
    private EditText composeMessageTxt;
    private Button composeSendButton;
    private ArrayList<Recipient> recipients;
    private String username;
    private long conversationID;
    private int userID;
    private String conversationName;
    protected ProgressDialog progressDialog;



    public ComposeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recipients = new ArrayList<>();
        SharedPreferences prefs = getActivity().getSharedPreferences("com.colanconnon.cryptomessage", Context.MODE_PRIVATE);
        username = prefs.getString("username", null);
        userID = prefs.getInt("userID", 0);
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        DialogUtil.showDialog(getActivity(), "Enter recipients usernames " +
                "in the textbox, and if there are multiple use a comma to separate the names");
        composeSendButton = (Button) view.findViewById(R.id.composeSendButton);
        composeMessageTxt = (EditText) view.findViewById(R.id.composeMessageTxt);
        composeMessageTxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    composeSendButton.performClick();
                    return true;
                }
                if (keyCode == EditorInfo.IME_ACTION_DONE) {
                    composeSendButton.performClick();
                    return true;
                }
                return false;
            }
        });
        composeUsernameTxt = (EditText) view.findViewById(R.id.composeUsernameTxt);
        composeSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewConversationTask().execute();

                new GetDevicesTask().execute();
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending message. Please wait");

        return view;
    }

    private class NewConversationTask extends AsyncTask<Void,Void,JSONObject>{
        String URL = "http://cryptomessage.mobi/api/createconversation/";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent intent1 = new Intent(getActivity().getApplicationContext(), FetchMessagesService.class);
            getActivity().stopService(intent1);
            progressDialog.show();


        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("name",composeUsernameTxt.getText().toString()  + "," + username);
                conversationName = composeUsernameTxt.getText().toString()  + "," + username;

                JSONObject jsonResponse = JsonFetcher.JSONPOSTRequestWithAuthHeader(URL, getActivity(), jsonObject);
                Log.e("JSon", jsonResponse.toString());
                conversationID = jsonResponse.getInt("id");

                return jsonResponse;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class GetDevicesTask extends AsyncTask<Void,Void,Boolean>{
        String url = "http://cryptomessage.mobi/api/getdevices/";
        @Override
        protected Boolean doInBackground(Void... params) {
            String text = composeUsernameTxt.getText().toString()  + "," + username;
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
                    JSONObject jsonResponseObject = JsonFetcher.JSONGetObjectRequestWithAuthHeader(url,getActivity());
                    recipient.setDeviceID(jsonResponseObject.getInt("id"));
                    recipient.setPublicKey(jsonResponseObject.getString("publicKey"));
                }
                catch (Exception e){
                   e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtil.showDialog(getActivity(),"Recipient " + recipient.getUserName() + " Doesn't exist");
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

        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success.equals(Boolean.TRUE)){
                new SendMessagesTask().execute();
            }
            else{
                Toast.makeText(getActivity(),"Failed to send the messages", Toast.LENGTH_LONG).show();
            }


        }
    }
    private class SendMessagesTask extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            new FetchMessageServiceTask().execute();
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
                    message.setText(composeMessageTxt.getText().toString());
                    JSONObject jsonObject = new JSONObject();
                    message.setConversationID(conversationID);
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




            Intent intent1 = new Intent(getActivity().getApplicationContext(), FetchMessagesService.class);
            getActivity().startService(intent1);
            progressDialog.dismiss();

            Fragment fragment = null;
            fragment = new MessageDetailFragment();
            FragmentManager fragmentManager = getFragmentManager();
            Bundle args = new Bundle();
            args.putLong("conversationId", conversationID);
            args.putString("conversationName", conversationName);
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.details_frag, fragment).addToBackStack(null).commit();

//            Intent intent2 = new Intent(getActivity().getApplicationContext(), MessageDetailActivity.class);
//
//
//            intent2.putExtra("conversationId", conversationID);
//            intent2.putExtra("conversationName", conversationName);
//            startActivity(intent2);
        }
    }


}
