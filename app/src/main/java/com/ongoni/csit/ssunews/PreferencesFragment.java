package com.ongoni.csit.ssunews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

public class PreferencesFragment extends Fragment {

    private static final String LOG_TAG = "PreferencesFragment";

    private Switch notifications;
    private Switch wifiOnly;
    private Switch periodicUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View v = inflater.inflate(R.layout.preferences_fragment, container, false);
        v.setBackgroundColor(getResources().getColor(android.R.color.white));

        this.notifications = (Switch) v.findViewById(R.id.notifications);
        this.wifiOnly = (Switch) v.findViewById(R.id.wifi_only);
        this.periodicUpdate = (Switch) v.findViewById(R.id.periodic_updates);
        init();

        return v;
    }

    private void init() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        notifications.setChecked(prefs.getBoolean("notifications", true));
        notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onNotificationsSwitched(isChecked);
            }
        });

        wifiOnly.setChecked(prefs.getBoolean("wifi_only", false));
        wifiOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onWiFiSwitched(isChecked);
            }
        });

        periodicUpdate.setChecked(prefs.getBoolean("periodic_update", true));
        periodicUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onPeriodicUpdateSwitched(isChecked);
            }
        });
    }

    private void onNotificationsSwitched(boolean checked) {
        getActivity().getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putBoolean("notifications", checked)
                .apply();
    }

    private void onWiFiSwitched(boolean checked) {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("wifi_only", checked);
        editor.apply();
    }

    private void onPeriodicUpdateSwitched(boolean checked) {
        getActivity().getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putBoolean("periodic_update", checked)
                .apply();
    }
}
