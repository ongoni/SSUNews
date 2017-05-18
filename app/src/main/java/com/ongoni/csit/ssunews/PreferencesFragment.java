package com.ongoni.csit.ssunews;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.util.Log;

public class PreferencesFragment extends android.preference.PreferenceFragment {

    private static final String LOG_TAG = "PreferencesFragment";
    private CheckBoxPreference notifcations;
    private CheckBoxPreference wifiOnly;
    private ListPreference periodicUpdate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_fragment);
        this.notifcations = (CheckBoxPreference) findPreference("notifications");
        this.wifiOnly = (CheckBoxPreference) findPreference("wifi_only");
        this.periodicUpdate = (ListPreference) findPreference("periodic_update");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setBackgroundColor(getResources().getColor(android.R.color.white));
    }
}
