package com.ssunews.ongoni.ssunews;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ssunews.ongoni.ssunews.SSUDbContract;
import com.ssunews.ongoni.ssunews.SSUDbHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        String res = null;
        try {
            URL url = new URL("http://sgu.ru/news.xml");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try {
                InputStream istream = conn.getInputStream();
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                try {
                    byte[] buf = new byte[32 * 1024];
                    while (true) {
                        int bytesRead = istream.read(buf);
                        if (bytesRead < 0) {
                            break;
                        }
                        ostream.write(buf, 0, bytesRead);
                    }
                    res = ostream.toString("UTF-8");
                } finally {
                    istream.close();
                    ostream.close();
                }

            } finally {
                conn.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        SSUNewsXmlParser parser = new SSUNewsXmlParser();

        if (res.startsWith("\n")) {
            res = res.substring(1);
        }
        if (res.startsWith("\uFEFF")) {
            res = res.substring(1);
        }

        List<Article> netData = null;

        try {
            InputStream in = new ByteArrayInputStream(res.getBytes("UTF-8"));
            try {
                netData = parser.parse(res);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SQLiteDatabase db = new SSUDbHelper(getContext()).getWritableDatabase();

        db.beginTransaction();
        try {
            if (netData != null) {
                for (Article article : netData) {
                    ContentValues cv = new ContentValues();
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

        Cursor cursor = db.query(SSUDbContract.TABLE_NAME, new String[] {
                SSUDbContract.COLUMN_TITLE,
                SSUDbContract.COLUMN_DESCRIPTION,
                SSUDbContract.COLUMN_LINK,
                SSUDbContract.COLUMN_PUBDATE
        }, null, null, null, null, SSUDbContract.COLUMN_PUBDATE);

        this.data = new ArrayList<Article>();

        try {
            while (cursor.moveToNext()) {
                Article article = new Article(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
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