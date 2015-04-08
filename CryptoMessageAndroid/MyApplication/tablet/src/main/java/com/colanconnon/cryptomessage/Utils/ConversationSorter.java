package com.colanconnon.cryptomessage.Utils;

import com.colanconnon.cryptomessage.models.Conversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by colan on 3/8/2015.
 */
public class ConversationSorter {
    private ArrayList<Conversation> conversationArrayList;

    public  ConversationSorter(ArrayList<Conversation> conversationArrayList){
        this.conversationArrayList = conversationArrayList;
    }
    public ArrayList<Conversation> sortDESC(){
        Thread thread = new Thread(new Runnable()
        {

            @Override
            public void run() {
                if ( conversationArrayList.size() > 2)
                {
                    Collections.sort(conversationArrayList, new ConversationComparator());
                }


            }
        });
        thread.start();
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {

            e.printStackTrace();
        }

        return conversationArrayList;
    }
    public ArrayList<Conversation> sortAsc(){
        Thread thread = new Thread(new Runnable()
        {

            @Override
            public void run() {
                if ( conversationArrayList.size() > 2)
                {
                    Collections.sort(conversationArrayList, new ConversationComparator());
                    Collections.reverse(conversationArrayList);

                }


            }
        });
        thread.start();
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {

            e.printStackTrace();
        }

        return conversationArrayList;
    }
    public class ConversationComparator implements Comparator<Conversation> {

        @Override
        public int compare(Conversation conversation1, Conversation conversation2) {
            int id1 =(int) conversation1.getLastMessage().getId();
            int id2 =(int) conversation2.getLastMessage().getId();
            return id1 - id2;
        }
    }
}
