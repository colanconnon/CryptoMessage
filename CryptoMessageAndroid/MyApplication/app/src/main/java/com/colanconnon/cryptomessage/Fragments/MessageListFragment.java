package com.colanconnon.cryptomessage.Fragments;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.colanconnon.cryptomessage.Activities.LoginActivity;
import com.colanconnon.cryptomessage.Activities.MessageDetailActivity;
import com.colanconnon.cryptomessage.Adapters.MessageListAdapter;
import com.colanconnon.cryptomessage.Database.DatabaseDatasource;
import com.colanconnon.cryptomessage.R;
import com.colanconnon.cryptomessage.Utils.JsonFetcher;
import com.colanconnon.cryptomessage.encryption.RSAKeyManager;
import com.colanconnon.cryptomessage.models.Conversation;
import com.colanconnon.cryptomessage.services.FetchMessagesService;

import junit.framework.AssertionFailedError;

import org.json.JSONObject;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class MessageListFragment extends Fragment {
    private ListView messageListView;
    UpdateMessageList updateMessageList;
    IntentFilter intentFilter;
    private ArrayList<Conversation> conversations;
    public MessageListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        messageListView = (ListView) view.findViewById(R.id.messageListListView);

        intentFilter = new IntentFilter("com.colanconnon.cryptomessage.custom.intent.action.UpdateMessageList");
        updateMessageList = new UpdateMessageList();
        loadMessages();
        RSAKeyManager rsaKeyManager = new RSAKeyManager(getActivity().getApplicationContext());
        if(!rsaKeyManager.isKeysInEditor()){
            rsaKeyManager.generateKeyPair();

            new RegisterDeviceTask().execute();
        }
        Intent intent1 = new Intent(getActivity().getApplicationContext(), FetchMessagesService.class);
        getActivity().startService(intent1);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMessages();
        getActivity().registerReceiver(updateMessageList, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(updateMessageList);
    }

    private void loadMessages(){
        DatabaseDatasource databaseDatasource = new DatabaseDatasource(getActivity().getApplicationContext());
        conversations = databaseDatasource.getConversations();
        MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity().getApplicationContext(), conversations);
        messageListView.setAdapter(messageListAdapter);
        messageListView.setOnItemClickListener(new ConversationOnItemListClick());
    }

    private class ConversationOnItemListClick implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            long conversationId = conversations.get(position).getConversationID();
            String conversationName = conversations.get(position).getConversationName();
            Intent intent = new Intent(getActivity().getApplicationContext(), MessageDetailActivity.class);
            intent.putExtra("conversationId", conversationId);
            intent.putExtra("conversationName", conversationName);
            startActivity(intent);

        }
    }
    public class UpdateMessageList extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            loadMessages();
        }
    }
    private class RegisterDeviceTask extends AsyncTask<Void,Void,JSONObject>{

        String url = "http://cryptomessage.mobi/api/createdevice/";
        @Override
        protected JSONObject doInBackground(Void... params) {
            try{
                RSAKeyManager rsaKeyManager = new RSAKeyManager(getActivity());
                rsaKeyManager.getKeysFromEditor();

                JSONObject jsonPostObject = new JSONObject();
                jsonPostObject.put("publicKey", rsaKeyManager.getPublicKeyString());
                JSONObject jsoneResponseObject = JsonFetcher.JSONPOSTRequestWithAuthHeader(url,getActivity(),jsonPostObject);


            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
        }
    }


}
