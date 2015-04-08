package com.colanconnon.cryptomessage.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.colanconnon.cryptomessage.R;
import com.colanconnon.cryptomessage.models.Conversation;
import com.colanconnon.cryptomessage.models.Message;

import java.util.ArrayList;

/**
 * Created by colan on 3/5/2015.
 */
public class MessageListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Conversation> conversations;

    public MessageListAdapter(Context context, ArrayList<Conversation> conversations){
        this.conversations = conversations;
        this.context = context;
    }
    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public Object getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater minflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = minflater.inflate(R.layout.message_list_item, null);
        }

        TextView messageListItemUsername = (TextView) convertView.findViewById(R.id.messageListItemUsername);
        TextView messageListItemContent = (TextView) convertView.findViewById(R.id.messageListItemContent);

        messageListItemUsername.setText(conversations.get(position).getLastMessage().getConversationName());
        messageListItemContent.setText(conversations.get(position).getLastMessage().getText());

        return convertView;
    }
}
