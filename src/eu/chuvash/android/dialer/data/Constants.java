package eu.chuvash.android.dialer.data;

import android.provider.BaseColumns;

/**
 * @author Anatoly Mironov
 * This interface extends BaseColumns and provides table name
 * and column names for the database
 * Inspired by Ed Burnettes Hello, Android 1, pp 165.
 * These constants are imported as static in CallLogData
 */
public interface Constants extends BaseColumns {	   
	public static final String DATABASE_NAME = "call_log.db";
	public static final int DATABASE_VERSION = 7;
	public static final String TABLE_NAME = "call";
	public static final String PHONE_NUMBER = "phone";
	public static final String TIMESTAMP = "time";
	public static final String DATE = "DATE(" + TIMESTAMP + ", 'localtime')";
	// sqlite may be used to get date and time in any format:
	//http://www.sqlite.org/lang_datefunc.html
	public static final String TIME = "STRFTIME('%H:%M', " + TIMESTAMP + ", 'localtime')";
}
