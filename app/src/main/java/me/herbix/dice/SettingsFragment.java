package me.herbix.dice;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Chaofan on 2017/2/1.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
