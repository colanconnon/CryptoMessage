package com.colanconnon.cryptomessage.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.colanconnon.cryptomessage.R;
import com.colanconnon.cryptomessage.models.Message;

import java.util.ArrayList;

/**
 * Created by colan on 3/10/2015.
 */
public class MessageDetailAdapter extends BaseAdapter {
    private ArrayList<Message> messages;
    private Context context;
    private String username;

    public MessageDetailAdapter(Context context, ArrayList<Message> messages, String username){
        this.messages = messages;
        this.context = context;
        this.username = username;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater minflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = minflater.inflate(R.layout.message_detail_item, null);
        }
        TextView messageDetailItemSender =  (TextView) convertView.findViewById(R.id.messageDetailItemSender);
        TextView messageDetailItemText = (TextView) convertView.findViewById(R.id.messageDetailItemText);
        Message message = messages.get(position);
        Log.e("messageTest", String.valueOf(message.isOwner()));
        if(message.getMessageSender().equals(username)){
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) messageDetailItemText.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            messageDetailItemSender.setGravity(Gravity.RIGHT);
            messageDetailItemText.setLayoutParams(layoutParams);
            messageDetailItemText.setText(message.getText());
            messageDetailItemSender.setText(message.getMessageSender());
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                messageDetailItemText.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.blue));
            } else {
                messageDetailItemText.setBackground(context.getResources().getDrawable(R.drawable.blue));
            }

        }
        else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) messageDetailItemText.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            messageDetailItemSender.setGravity(Gravity.LEFT);
            messageDetailItemText.setText(message.getText());
            messageDetailItemSender.setText(message.getMessageSender());
            messageDetailItemText.setLayoutParams(layoutParams);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                messageDetailItemText.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.orange));
            } else {
                messageDetailItemText.setBackground(context.getResources().getDrawable(R.drawable.orange));
            }


        }


        return convertView;
    }
}
