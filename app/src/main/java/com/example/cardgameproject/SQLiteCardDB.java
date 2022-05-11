package com.example.cardgameproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteCardDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CardGame.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLES = "CREATE TABLE "
            + DatabaseContract.UsersTable.TABLE + " ("
            + DatabaseContract.UsersTable._ID + " INTEGER PRIMARY KEY, "
            + DatabaseContract.UsersTable.COLUMN_NAME + " TEXT,"
            + DatabaseContract.UsersTable.COLUMN_PASSWORD + " TEXT)";

    private static final String SQL_DROP_TABLES = "DROP TABLE IF EXISTS " +
            DatabaseContract.UsersTable.TABLE;

    public SQLiteCardDB(Context context){
        super(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLES);
        onCreate(db);
    }
}
