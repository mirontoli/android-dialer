package eu.chuvash.android.dialer;

import java.io.File;

import eu.chuvash.android.dialer.util.FILE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {
	private static final String TAG = "SettingsActivity";
	static final String OPT_MUSIC = "music";
	static final boolean OPT_MUSIC_DEFAULT = true;
	static final String OPT_DIR = "dial_sound";
	private static final String OPT_DIR_DEFAULT = "";
	
	// TODO create a preference instead of hardcoding
	private static final String dirPath = "/sdcard/dialpad/sounds/";
	
	// http://www.kaloer.com/android-preferences
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		initSoundPref();
	}
	private static SharedPreferences getPref(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		return p;
	}
	public static boolean isMusicWanted(Context context) {
		return getPref(context).getBoolean(OPT_MUSIC, OPT_MUSIC_DEFAULT);
	}
	public static String getPreferredSoundDir(Context context) {
		String soundPath =  getPref(context).getString(OPT_DIR, OPT_DIR_DEFAULT);
		Log.d(TAG, "soundPath: " + soundPath);
		return soundPath;
	}
	private void initSoundPref() {
		ListPreference soundPref = (ListPreference) findPreference("dial_sound");
		File[] dirs = FILE.getChildrenDirs(dirPath);
		if (dirs != null && dirs.length > 0) {
			String[] entries = new String[dirs.length];
			String[] entryValues = new String[dirs.length];
			for (int i = 0; i < dirs.length; i++) {
				entries[i] = dirs[i].getName();
				entryValues[i] = dirs[i].getPath();
				Log.d(TAG, "path to dir: " + entryValues[i]);
			}
			soundPref.setEntries(entries);
			soundPref.setEntryValues(entryValues);
			//TODO  What about default?
		}
		else {
			soundPref.setEnabled(false);
			soundPref.setSummary(getString(R.string.settings_sound_at_disabled));
		}
		
	}
}
