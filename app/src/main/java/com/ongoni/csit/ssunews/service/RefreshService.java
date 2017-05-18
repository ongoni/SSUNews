package com.ongoni.csit.ssunews.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.ongoni.csit.ssunews.Article;
import com.ongoni.csit.ssunews.NewsListActivity;
import com.ongoni.csit.ssunews.R;
import com.ongoni.csit.ssunews.RSSParser;
import com.ongoni.csit.ssunews.db.SSUDbContract;
import com.ongoni.csit.ssunews.db.SSUDbHelper;

import java.util.List;

public class RefreshService extends Service {

    public static final String ACTION_REFRESH = "com.ongoni.csit.ssunews.service" +
            ".RefreshService.ACTION_REFRESH";
    public static final String NO_INTERNET = "com.ongoni.csit.ssunews.service" +
            ".RefreshService.NO_INTERNET";
    private static final String LOG_TAG = "RefreshService";
    private static final String url = "http://sgu.ru/news.xml";

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Thread refreshThread;

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                loadData();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
        Intent refreshIntent = new Intent();
        refreshIntent.setAction(ACTION_REFRESH);
        sendBroadcast(refreshIntent);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);
    }

    private void loadData() {

        if (isOnline()) {
            List<Article> netData = new RSSParser().parse(url);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    showInProgressNotification();
                }
            });

            SQLiteDatabase db = new SSUDbHelper(RefreshService.this).getWritableDatabase();
            int newNewsCounter = 0;
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
                        if (insertedId == -1L) {
                            Log.i(LOG_TAG, "skipped article guid = " + article.guid);
                        } else {
                            newNewsCounter++;
                        }
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }

            try {
                Thread.sleep(3_000);
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

            if (newNewsCounter > 0) {
                sendDataRefreshedNotification(newNewsCounter);
            }
        } else {
            Intent noInternetIntent = new Intent();
            noInternetIntent.setAction(NO_INTERNET);
            sendBroadcast(noInternetIntent);
        }
    }

    private void sendDataRefreshedNotification(int newNewsCounter) {
        Intent startIntent = new Intent(this, NewsListActivity.class);
        PendingIntent notificationIntent = PendingIntent.getActivity(
                this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("SSU News refreshed")
                .setContentText(newNewsCounter + " news added")
                .setContentIntent(notificationIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ssu_logo_small)
                .build();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, notification);
    }

    private void showInProgressNotification () {
        Notification notification = new Notification.Builder(this)
                .setContentTitle("SSU RSS Data is refreshing")
                .setContentText("Wait until complete")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ssu_logo_small)
                .build();
        startForeground(1, notification);
    }

    private void hideInProgressNotification () {
        stopForeground(false);
    }
}
