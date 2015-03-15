package assignment.appointment.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_APPOINTMENT = "appointment";
	public static final String COLUMN_APPOINTMENT_ID = "appointmentid";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_TIME = "time";	
	public static final String COLUMN_DETAIL = "detail";

	private static final String DATABASE_NAME = "appointment.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement: create table appointment
	private static final String CREATE_TABLE_APPOINTMENT = "create table " + TABLE_APPOINTMENT 
			+ "(" + COLUMN_APPOINTMENT_ID + " integer primary key, " 
			+ COLUMN_DATE + " integer, "
			+ COLUMN_TITLE + " text, " 
			+ COLUMN_TIME + " text, "
			+ COLUMN_DETAIL + " text);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_APPOINTMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENT);
		onCreate(db);
	}

}
