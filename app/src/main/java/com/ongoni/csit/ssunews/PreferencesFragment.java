package com.ongoni.csit.ssunews;

import android.os.Bundle;
import android.util.Log;

public class PreferencesFragment extends android.preference.PreferenceFragment {

    private static final String LOG_TAG = "PreferencesFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_fragment);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setBackgroundColor(getResources().getColor(android.R.color.white));
    }
}
