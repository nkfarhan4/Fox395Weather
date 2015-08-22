package com.expert.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Krishna on 21-07-2015.
 */

public class DBAdapter { public static final String KEY_ROWID = "_id";
    /* public static final String KEY_NAME = "playername";
     public static final String KEY_SCORE = "score";*/
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "MyDB1";

    private static final int DATABASE_VERSION = 1;


    private static final String CITY =
            "create table CITY (_id integer primary key autoincrement," +
                    "place text);";





    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(CITY);



            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS CITY");


            onCreate(db);
        }
    }


    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }


    public Cursor getALLLIST() throws SQLException
    {
        String selectQuery = "SELECT * FROM CITY";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public long deleteALL() throws SQLException
    {
        return db.delete("CITY",null,null);
    }

    public long insertRecord(String name)
    {
        ContentValues initialValues = new ContentValues();

        initialValues.put("place", name);//1

        Log.e("insert in CITY ", "ok");

        return db.insert("CITY", null, initialValues);
    }









}