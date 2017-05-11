package com.ssunews.ongoni.ssunews.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.ssunews.ongoni.ssunews.Article;
import com.ssunews.ongoni.ssunews.NewsListActivity;
import com.ssunews.ongoni.ssunews.R;
import com.ssunews.ongoni.ssunews.RSSParser;
import com.ssunews.ongoni.ssunews.db.SSUDbContract;
import com.ssunews.ongoni.ssunews.db.SSUDbHelper;

import java.util.List;

public class RefreshService extends Service {

    public static final String REFRESH_ACTION = "com.ssunews.ongoni.ssunews.service" +
            ".RefreshService.ACTION_REFRESH";

    private static final String LOG_TAG = "RefreshService";

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Thread refreshThread;

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    loadData();
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            this.refreshThread = new Thread(refreshRunnable);
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
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(REFRESH_ACTION);
        sendBroadcast(broadcastIntent);
        sendDataRefreshedNotification();
    }

    private void loadData() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                showInProgressNotification();
            }
        });
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
                    long insertedId = db.insertWithOnConflict(SSUDbContract.TABLE_NAME,
                            null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                    if (insertedId == -1L)
                        Log.i(LOG_TAG, "skipped article guid = " + article.guid);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                hideInProgressNotification();
                onPostRefresh();
            }
        });
    }

    private void sendDataRefreshedNotification() {
        Intent startIntent = new Intent(this, NewsListActivity.class);
        PendingIntent notificationIntent = PendingIntent.getActivity(
                this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("SSU RSS Data refreshed")
                .setContentText("Press to open")
                .setContentIntent(notificationIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ssu_logo)
                .build();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, notification);
    }

    private void showInProgressNotification () {
        Notification notification = new Notification.Builder(this)
                .setContentTitle("SSU RSS Data is refreshing")
                .setContentText("Wait until complete")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ssu_logo)
                .build();
        startForeground(1, notification);
    }

    private void hideInProgressNotification () {
        stopForeground(false);
    }
}
