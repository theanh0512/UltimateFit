package ultimate.fit.ultimatefit.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;

import ultimate.fit.ultimatefit.R;

/**
 * Created by User on 6/10/2016.
 */

public class SettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);

            Preference p = getPreferenceScreen();
            if (p != null) {
                PreferenceGroup pGroup = (PreferenceGroup) p;
                for (int i = 0; i < pGroup.getPreferenceCount(); i++) {
                    Preference p2 = pGroup.getPreference(i);
                    if(p2 instanceof ListPreference){
                        ListPreference listPref = (ListPreference) p2;
                        p2.setSummary(listPref.getEntry());
                    }
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_unit_key))) {
                Preference connectionPref = findPreference(key);
                if (connectionPref instanceof ListPreference) {
                    // Set summary to be the user-description for the selected value
                    ListPreference listPreference = (ListPreference) connectionPref;
                    //connectionPref.setSummary(listPreference.getEntry());
                    int prefIndex = listPreference.findIndexOfValue(listPreference.getValue());
                    if (prefIndex >= 0) {
                        connectionPref.setSummary(listPreference.getEntries()[prefIndex]);
                    }
                }
            }
//            else if(key.equals(getString(R.string.pref_orm_manual_input_key))) {
//                Preference connectionPref = findPreference(key);
//                if(connectionPref instanceof SwitchPreference){
//                    SwitchPreference switchPreference = (SwitchPreference) connectionPref;
//
//                }
//            }
        }
    }
}
