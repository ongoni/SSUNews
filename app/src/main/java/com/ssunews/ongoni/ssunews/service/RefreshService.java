package com.ssunews.ongoni.ssunews.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.ssunews.ongoni.ssunews.Article;
import com.ssunews.ongoni.ssunews.RSSParser;
import com.ssunews.ongoni.ssunews.db.SSUDbContract;
import com.ssunews.ongoni.ssunews.db.SSUDbHelper;

import java.util.List;

public class RefreshService extends Service {

    private static final String LOG_TAG = "RefreshService";
    private Thread refreshThread;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private final Runnable refrehRunnable = new Runnable() {
        @Override
        public void run() {
            List<Article> netData = new RSSParser().parse("http://sgu.ru/news.xml");
            SQLiteDatabase db = new SSUDbHelper(RefreshService.this).getWritableDatabase();

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
                db.close();

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostRefresh();
                    }
                });
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        if (intent != null && refreshThread == null) {
            this.refreshThread = new Thread(refrehRunnable);
            refreshThread.start();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    private void onPostRefresh() {
        Log.d(LOG_TAG, "onPostRefresh");
        this.refreshThread = null;
        stopSelf();
    }
}
