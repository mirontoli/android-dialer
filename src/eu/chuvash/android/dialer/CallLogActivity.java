package eu.chuvash.android.dialer;

import eu.chuvash.android.dialer.data.CallLogData;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class CallLogActivity extends ListActivity {
	
	private static final String TAG = "CallLogActivity";
	private static final int[] TO = { R.id.call_entry_phone, R.id.call_entry_date, R.id.call_entry_time };
	private CallLogData callLog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_log);
		callLog = new CallLogData(this);
		showCalls();
	}
	private void showCalls() {
		Cursor cursor = callLog.getCalls();
		startManagingCursor(cursor);
		try {
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.call_entry, cursor, CallLogData.FROM, TO);
			setListAdapter(adapter);
		}
		catch (Exception e) {
			Log.d(TAG, "showCalls exception: " + e);
		}
	}
}
