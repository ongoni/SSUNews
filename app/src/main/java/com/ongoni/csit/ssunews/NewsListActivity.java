package com.ongoni.csit.ssunews;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class NewsListActivity extends Activity
        implements NewsListFragment.Listener {

    private static final String LOG_TAG = "NewsListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
    }

    @Override
    public void onArticleClicked(Article article) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            PreviewFragment fragment = new PreviewFragment();
            fragment.getArguments().putString("url", article.link);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else  {
            PreviewFragment f = (PreviewFragment) getFragmentManager()
                    .findFragmentById(R.id.preview_fragment);
            f.getArguments().putString("url", article.link);
            f.reload();
        }
    }
}