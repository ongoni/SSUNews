package com.ongoni.csit.ssunews.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SSUDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ssudb.sqlite";
    private static final int DB_VERSION = 2;

    public SSUDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + SSUDbContract.TABLE_NAME + "("
                + SSUDbContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SSUDbContract.COLUMN_GUID + " INTEGER NOT NULL UNIQUE, "
                + SSUDbContract.COLUMN_TITLE + " TEXT NOT NULL, "
                + SSUDbContract.COLUMN_DESCRIPTION + " TEXT, "
                + SSUDbContract.COLUMN_LINK + " TEXT NOT NULL, "
                + SSUDbContract.COLUMN_PUBDATE + " DATETIME"
                + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + SSUDbContract.TABLE_NAME);
        onCreate(db);
    }
}