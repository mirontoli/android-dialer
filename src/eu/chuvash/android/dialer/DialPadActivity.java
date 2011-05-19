package eu.chuvash.android.dialer;

import eu.chuvash.android.dialer.data.CallLogData;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class DialPadActivity extends Activity {
	private static final String TAG = "DialPadActivity";
	//TODO incorporate into preferences
	private String url = "http://www.programvaruteknik.nu/dt031g/dialpad/sounds/";
	private String destination = "/sdcard/dialpad/sounds/";

	private DialPadView dpv;
	private EditText numberField = null;
	private CallLogData callLog;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);               
        initializeButtonArrowClickListeners();
        findDialPadView();
        initPrefChangeListener();
        callLog = new CallLogData(this);
        if (SettingsActivity.isMusicWanted(this)) {
        	new InitSoundTask().execute(false);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d(TAG, "running onOptionsItemSelected");
    	switch(item.getItemId()) {
    		case R.id.download_menu : startDownloadActivity(); break;
    		case R.id.settings_menu : startSettingsActivity(); break;
    		case R.id.call_log_menu : startCallLogActivity();
    	}
    	return false;
    }
    private void findDialPadView() {
    	dpv = (DialPadView) findViewById(R.id.dialpad);
    }
    private void initPrefChangeListener() {
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    	pref.registerOnSharedPreferenceChangeListener(prefListener);
    }
    // use an instance field instead for inner class
    // http://stackoverflow.com/questions/2542938/slharedpreferences-onsharedpreferencechangelistener-not-being-called-consistently
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sp,
				String key) {
			Log.d(TAG, "running onSharedPreferenceChanged");
			String musicKey = SettingsActivity.OPT_MUSIC;
			String dirKey = SettingsActivity.OPT_DIR;
			boolean toPlay = sp.getBoolean(musicKey, SettingsActivity.OPT_MUSIC_DEFAULT);
			Log.d(TAG, "running onSharedPreferenceChanged");
			
			//load sound if dial_sound entry value has changed OR music turns on
			if (key.equals(dirKey)) {
				Log.d(TAG, "key.equals(\"dial_sound\")");
				new InitSoundTask().execute(true);
			}
			else if ((key.equals(musicKey) && toPlay)) {
				new InitSoundTask().execute(false);
			}
		}
    };
	private class InitSoundTask extends AsyncTask<Boolean, Void, Void> {	
		// http://developer.android.com/reference/android/os/AsyncTask.html
		private ProgressDialog pd = new ProgressDialog(DialPadActivity.this);
		@Override
		protected void onPreExecute() {
			pd.setMessage(getString(R.string.loading_sound_label));
			/*
			 * When you change sound in preference without pressing on buttons
			 * An exeption is thrown and application stopped:
			 * "attempted to add application window with unknow token"
			 * Issue: http://code.google.com/p/android/issues/detail?id=3953
			 * resolved with try and catch
			 */
			try {
				pd.show();
			}
			catch (Exception e) {
				Log.d(TAG, "problem med att visa Dialog i InitSoundTask.onPreExecute: " + e.toString());
			}
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Boolean... renew) {
			boolean toRenew = renew[0].booleanValue();
			long start = System.currentTimeMillis();
			if (!toRenew) {
				DialPadActivity.this.dpv.initDpSound();
				Log.d(TAG, "1 running doInBackground InitSoundTask - renew is " +  renew);
			}
			else {
				DialPadActivity.this.dpv.renewDpSound();
				Log.d(TAG, "2 running doInBackground InitSoundTask - renew is " +  renew);
			}
			long end = System.currentTimeMillis();
			long time = end - start;
			Log.d(TAG, "tid för att initialisera ljuden: " + time + " ms");
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if (this.pd != null && this.pd.isShowing()) {
				pd.dismiss();
			}
		}
	}
    public void dialButtonClick(View v) {
    	int buttonId = v.getId();
    	dpv.playSoundEffect(buttonId);
    	putNumberToEditText(buttonId);
    }
    public void buttonPhone_click(View v) {
    	if (numberField != null) {
    		String phone = numberField.getText().toString();
    		callLog.addCall(phone);
        	String uriString = "tel:" + phone;
        	Intent intent = new Intent(Intent.ACTION_CALL);
        	intent.setData(Uri.parse(uriString));
        	startActivity(intent);
    	}
    }
    public void putNumberToEditText(int buttonId) {
    	switch (buttonId) {
			case R.id.dial_button0 : putNumber("0");break;
			case R.id.dial_button1 : putNumber("1");break;
			case R.id.dial_button2 : putNumber("2");break;
			case R.id.dial_button3 : putNumber("3");break;
			case R.id.dial_button4 : putNumber("4");break;
			case R.id.dial_button5 : putNumber("5");break;
			case R.id.dial_button6 : putNumber("6");break;
			case R.id.dial_button7 : putNumber("7");break;
			case R.id.dial_button8 : putNumber("8");break;
			case R.id.dial_button9 : putNumber("9");break;
			case R.id.dial_button_s : putNumber("*");break;
			case R.id.dial_button_p : putNumber("#");
    	}
    }
    public void putNumber(String number) {
    	if (numberField == null) {
    		numberField = (EditText) findViewById(R.id.phone_number_field);
    	}
    	numberField.append(number);
    }
    private void startDownloadActivity() {
    	Intent intent = new Intent(this, DownloadActivity.class);
    	intent.putExtra("url", url);
    	intent.putExtra("destination", destination);
    	startActivity(intent);
    }
    private void startSettingsActivity() {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
    }
    private void startCallLogActivity() {
    	Intent intent = new Intent(this, CallLogActivity.class);
    	startActivity(intent);
    }
    private void initializeButtonArrowClickListeners() {
    	ImageButton buttonArrow = (ImageButton) findViewById(R.id.button_arrow);
    	buttonArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (numberField != null && numberField.length() > 0) {
					String previousNumbers = numberField.getText().toString();
					String numbersMinusTheLast = previousNumbers.substring(0, numberField.length() - 1);
					numberField.setText(numbersMinusTheLast);
				}
			}
		});
    	buttonArrow.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (numberField != null) {
					numberField.setText("");
				}
				return false;
			}
		});
    }
}