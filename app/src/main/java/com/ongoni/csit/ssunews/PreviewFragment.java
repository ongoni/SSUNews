package com.ongoni.csit.ssunews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class PreviewFragment extends Fragment {

    private static final String LOG_TAG = "PreviewFragment";

    private WebView webView;

    public PreviewFragment() {
        setArguments(new Bundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View v = inflater.inflate(R.layout.preview_fragment, container, false);
        this.webView = (WebView) v.findViewById(R.id.preview_webview);
        reload();
        return v;
    }

    public void reload() {
        String url = getArguments().getString("url");
        webView.loadUrl(url);
    }
}