package com.cascadia.example;

import android.os.Bundle;
import android.preference.PreferenceFragment;


/**
 * Created by seanchung on 11/1/15.
 */
public class SettingsFragment extends PreferenceFragment {

    // creates preferences GUI from preferences.xml file in res/xml
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences); // load from XML
    }

}
