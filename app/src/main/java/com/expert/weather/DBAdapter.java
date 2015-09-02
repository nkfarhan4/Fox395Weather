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

public class DBAdapter {
    public static final String KEY_ROWID = "_id";
    /* public static final String KEY_NAME = "playername";
     public static final String KEY_SCORE = "score";*/
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "MyDB1";

    private static final int DATABASE_VERSION = 1;


    private static final String CITY =
            "create table CITY (_id integer primary key autoincrement," +
                    "place text);";


    private static final String OLD_DATA =
            "create table OLD_DATA (_id integer primary key autoincrement," +
                    "place text," +
                    "mainTemp text," +
                    "tempCode text," +
                    "date_time text," +
                    "winSpeed text," +
                    "winDirection text," +
                    "visibilty text," +
                    "humidity text," +
                    "col1Tile text," +
                    "col1TempCode text," +
                    "col1HighTemp text," +
                    "col1LowTemp text," +
                    "col2Tile text," +
                    "col2TempCode text," +
                    "col2HighTemp text," +
                    "col2LowTemp text," +
                    "col3Tile text," +
                    "col3TempCode text," +
                    "col3HighTemp text," +
                    "col3LowTemp text," +
                    "col4Tile text," +
                    "col4TempCode text," +
                    "col4HighTemp text," +
                    "col4LowTemp text," +
                    "col5Tile text," +
                    "col5TempCode text," +
                    "col5HighTemp text," +
                    "\"col5LowTemp text);";


    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CITY);
                db.execSQL(OLD_DATA);


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS CITY");
            db.execSQL("DROP TABLE IF EXISTS OLD_DATA");


            onCreate(db);
        }
    }


    //---opens the database---
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    //---closes the database---
    public void close() {
        DBHelper.close();
    }


    public Cursor getALLLIST() throws SQLException {
        String selectQuery = "SELECT * FROM CITY";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public long deleteALL() throws SQLException {
        return db.delete("CITY", null, null);
    }

    public long insertRecord(String name) {
        ContentValues initialValues = new ContentValues();

        initialValues.put("place", name);//1

        Log.e("insert in CITY ", "ok");

        return db.insert("CITY", null, initialValues);
    }

    /*"place text," +
            "mainTemp text," +
            "tempCode text," +
            "date_time text," +
            "winSpeed text," +
            "winDirection text," +
            "visibilty text," +
            "humidity text," +
            "col1Tile text,"+
            "col1TempCode text,"+
            "col1HighTemp text,"+
            "col1LowTemp text,"+
            "col2Tile text,"+
            "col2TempCode text,"+
            "col2HighTemp text,"+
            "col2LowTemp text,"+
            "col3Tile text,"+
            "col3TempCode text,"+
            "col3HighTemp text,"+
            "col3LowTemp text,"+
            "col4Tile text,"+
            "col4TempCode text,"+
            "col4HighTemp text,"+
            "col4LowTemp text,"+
            "col5Tile text,"+
            "col5TempCode text,"+
            "col5HighTemp text,"+
            "\"col5LowTemp text);";*/


    public long insertOLDDATARecord(String place, String mainTemp, String tempCode, String date_time, String winSpeed,
                                    String winDirection, String visibilty, String humidity,
                                    String col1Tile, String col1TempCode, String col1HighTemp, String col1LowTemp,
                                    String col2Tile, String col2TempCode, String col2HighTemp, String col2LowTemp,
                                    String col3Tile, String col3TempCode, String col3HighTemp, String col3LowTemp,
                                    String col4Tile, String col4TempCode, String col4HighTemp, String col4LowTemp,
                                    String col5Tile, String col5TempCode, String col5HighTemp, String col5LowTemp) {

        ContentValues initialValues = new ContentValues();

        initialValues.put("place", place);
        initialValues.put("mainTemp", mainTemp);
        initialValues.put("tempCode", tempCode);
        initialValues.put("date_time", date_time);
        initialValues.put("winSpeed", winSpeed);
        initialValues.put("winDirection", winDirection);
        initialValues.put("visibilty", visibilty);
        initialValues.put("humidity", humidity);

        initialValues.put("col1Tile", col1Tile);
        initialValues.put("col1TempCode", col1TempCode);
        initialValues.put("col1HighTemp", col1HighTemp);
        initialValues.put("col1LowTemp", col1LowTemp);

        initialValues.put("col2Tile", col2Tile);
        initialValues.put("col2TempCode", col2TempCode);
        initialValues.put("col2HighTemp", col2HighTemp);
        initialValues.put("col2LowTemp", col2LowTemp);

        initialValues.put("col3Tile", col3Tile);
        initialValues.put("col3TempCode", col3TempCode);
        initialValues.put("col3HighTemp", col3HighTemp);
        initialValues.put("col3LowTemp", col3LowTemp);

        initialValues.put("col4Tile", col4Tile);
        initialValues.put("col4TempCode", col4TempCode);
        initialValues.put("col4HighTemp", col4HighTemp);
        initialValues.put("col4LowTemp", col4LowTemp);

        initialValues.put("col5Tile", col5Tile);
        initialValues.put("col5TempCode", col5TempCode);
        initialValues.put("col5HighTemp", col5HighTemp);
        initialValues.put("col5LowTemp", col5LowTemp);


        Log.e("insert in OLD_DATA ", "ok");

        return db.insert("OLD_DATA", null, initialValues);
    }


}