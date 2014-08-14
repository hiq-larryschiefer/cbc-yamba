package com.newcircle.yamba;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class YambaPrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);
    }
}
