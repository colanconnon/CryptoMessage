package com.colanconnon.cryptomessage.Utils;

import com.colanconnon.cryptomessage.models.Message;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by colan on 3/8/2015.
 */
public class MessageSorter {

    private ArrayList<Message> arrayToSort;

    public MessageSorter(ArrayList<Message> arrayToSort){
        this.arrayToSort = arrayToSort;
    }
    public ArrayList<Message> sortDesc() throws ParseException
    {
        Thread thread = new Thread(new Runnable()
        {

            @Override
            public void run() {
                if ( arrayToSort.size() > 2)
                {
                    Collections.sort(arrayToSort,  new MessageComparator());
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
        return arrayToSort;

    }
    public ArrayList<Message> sortAsc() throws ParseException
    {
        Thread thread = new Thread(new Runnable()
        {

            @Override
            public void run() {
                if ( arrayToSort.size() > 1)
                {
                    Collections.sort(arrayToSort,  new MessageComparator());
                    Collections.reverse(arrayToSort);
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
        return arrayToSort;

    }
    public class MessageComparator implements Comparator<Message> {
        @Override
        public int compare(Message message1, Message message2) {
            int id1 =(int) message1.getId();
            int id2 =(int) message2.getId();
            return id2 - id1;
        }
    }
}
