package com.ongoni.csit.ssunews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ongoni.csit.ssunews.db.SSUDbContract;
import com.ongoni.csit.ssunews.db.SSUDbHelper;

import java.util.ArrayList;
import java.util.List;

public class DataLoader extends AsyncTaskLoader<List<Article>> {

    private static final String LOG_TAG = "DataLoader";

    private List<Article> data;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (data == null) {
            Log.d(LOG_TAG, "forceLoad");
            forceLoad();
        } else {
            Log.d(LOG_TAG, "deliverResult");
            deliverResult(data);
        }
    }

    @Override
    public List<Article> loadInBackground() {

        SQLiteDatabase db = new SSUDbHelper(getContext()).getReadableDatabase();

        Cursor cursor = db.query(SSUDbContract.TABLE_NAME, new String[]{
                SSUDbContract.COLUMN_TITLE,
                SSUDbContract.COLUMN_DESCRIPTION,
                SSUDbContract.COLUMN_PUBDATE,
                SSUDbContract.COLUMN_LINK
        }, null, null, null, null, SSUDbContract.COLUMN_PUBDATE + " DESC");

        this.data = new ArrayList<Article>();

        try {
            while (cursor.moveToNext()) {
                Article article = new Article();
                article.title = cursor.getString(0);
                article.description = cursor.getString(1);
                article.pubDate = cursor.getString(2);
                article.link = cursor.getString(3);
                data.add(article);
            }
        } finally {
            cursor.close();
            db.close();
        }
        Log.d(LOG_TAG, "loadInBackground finished");
        return data;
    }

    @Override
    protected void onReset() {
        this.data = null;
    }
}