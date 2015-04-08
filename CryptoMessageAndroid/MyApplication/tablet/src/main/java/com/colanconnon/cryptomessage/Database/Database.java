package com.colanconnon.cryptomessage.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by colan on 3/5/2015.
 */
public class Database extends SQLiteOpenHelper {
    public static final String dbname = "CryptoMessageDb";
    public static final int version = 2;

    private static final String createMessageTable =
            "Create table message (messageID INTEGER, " +
                    "text TEXT, key TEXT, messageSenderId INTEGER, messageSender TEXT, " +
                    "isOwner TEXT, deviceID INTEGER, conversationID INTEGER, conversationName TEXT, date TEXT);";

    private static final String dropMessageTable = "DROP TABLE message";



    public Database(Context context, String dbname, CursorFactory cursorFactory, int version) {
        super(context, dbname, cursorFactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createMessageTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropMessageTable);
        db.execSQL(createMessageTable);
    }
}
