package com.ssunews.ongoni.ssunews;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ssunews.ongoni.ssunews.db.SSUDbContract;
import com.ssunews.ongoni.ssunews.db.SSUDbHelper;

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
        Log.d(LOG_TAG, "loadInBackground");

        List<Article> netData = new RSSParser().parse("http://sgu.ru/news.xml");
        SQLiteDatabase db = new SSUDbHelper(getContext()).getWritableDatabase();

        db.beginTransaction();
        try {
            if (netData != null) {
                for (Article article : netData) {
                    ContentValues cv = new ContentValues();
                    cv.put(SSUDbContract.COLUMN_GUID, article.guid);
                    cv.put(SSUDbContract.COLUMN_TITLE, article.title);
                    cv.put(SSUDbContract.COLUMN_DESCRIPTION, article.description);
                    cv.put(SSUDbContract.COLUMN_PUBDATE, article.pubDate);
                    cv.put(SSUDbContract.COLUMN_LINK, article.link);
                    db.insert(SSUDbContract.TABLE_NAME, null, cv);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

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
        }

        return data;
    }

    @Override
    protected void onReset() {
        this.data = null;
    }
}