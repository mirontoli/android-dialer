package eu.chuvash.android.dialer.data;

import static android.provider.BaseColumns._ID;
import static eu.chuvash.android.dialer.data.Constants.DATABASE_NAME;
import static eu.chuvash.android.dialer.data.Constants.DATABASE_VERSION;
import static eu.chuvash.android.dialer.data.Constants.DATE;
import static eu.chuvash.android.dialer.data.Constants.PHONE_NUMBER;
import static eu.chuvash.android.dialer.data.Constants.TABLE_NAME;
import static eu.chuvash.android.dialer.data.Constants.TIME;
import static eu.chuvash.android.dialer.data.Constants.TIMESTAMP;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CallLogData extends SQLiteOpenHelper {	      
	private static final String TAG = "CallLogData";
	//public static final String[] FROM = { PHONE_NUMBER, TIMESTAMP, _ID };
	public static final String[] FROM = { PHONE_NUMBER, DATE, TIME, _ID };

	public CallLogData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE " + TABLE_NAME + " (" 
					+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					// default value: 
					//http://www.sqlite.org/lang_createtable.html
					+ TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " 
					+ PHONE_NUMBER + " TEXT NOT NULL);";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String query = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
		db.execSQL(query);
		onCreate(db);
	}
	public void addCall(String phone) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		//cv.put(TIME, System.currentTimeMillis());
		cv.put(PHONE_NUMBER, phone);
		try {
			db.insertOrThrow(TABLE_NAME, null, cv);
		}
		catch (Exception e) {
			Log.d(TAG, "addCall exception: " + e);
		}
		finally {
			close();
		}
	}
	public Cursor getCalls() {
		String orderBy = TIMESTAMP + " DESC";
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, FROM, null, null, null, null, orderBy);
		}
		catch (Exception e){
			Log.d(TAG, "getCalls exception: " + e);
		}
		return cursor;
	}
}
