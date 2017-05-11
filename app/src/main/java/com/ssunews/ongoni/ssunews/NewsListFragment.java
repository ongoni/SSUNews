package com.ssunews.ongoni.ssunews;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ssunews.ongoni.ssunews.service.RefreshService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by contest on 27.04.2017.
 */

public class NewsListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Article>> {

    private static final String LOG_TAG = "NewsListFragment";

    private final BroadcastReceiver refreshBroadcastReciever = new RefreshBroadcastReciever();
    private final List<Article> data = new ArrayList<>();

    private NewsItemAdapter adapter = null;

    private final class RefreshBroadcastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isResumed()) {
                getActivity().getLoaderManager().restartLoader(0, null, NewsListFragment.this);
                Toast.makeText(getActivity(), "Data refreshed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface Listener {
        void onArticleClicked(Article article);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View v = inflater.inflate(R.layout.news_list_fragment, container, false);

        ListView articlesList = (ListView) v.findViewById(R.id.articlesList);
        articlesList.setAdapter(adapter);
        articlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);
                if (isResumed()) {
                    Listener l = (Listener) getActivity();
                    l.onArticleClicked(article);
                }
            }
        });

        Button refreshBtn = (Button) v.findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getActivity(), RefreshService.class);
                getActivity().startService(serviceIntent);
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        this.adapter = new NewsItemAdapter(getActivity(), data);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(RefreshService.REFRESH_ACTION);
        getActivity().registerReceiver(refreshBroadcastReciever, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");
        return new DataLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        Log.d(LOG_TAG, "onLoadFinished");
        this.data.clear();
        this.data.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
    }
}