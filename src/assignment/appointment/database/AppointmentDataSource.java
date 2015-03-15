package assignment.appointment.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AppointmentDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_APPOINTMENT_ID, MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_DETAIL };

	public AppointmentDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createAppointment(int date, String title, String time, String detail) {
		ContentValues values = new ContentValues();
		// values.put(MySQLiteHelper.COLUMN_APPOINTMENT_ID, null);
		values.put(MySQLiteHelper.COLUMN_DATE, date);
		values.put(MySQLiteHelper.COLUMN_TITLE, title);
		values.put(MySQLiteHelper.COLUMN_TIME, time);
		values.put(MySQLiteHelper.COLUMN_DETAIL, detail);
		database.insert(MySQLiteHelper.TABLE_APPOINTMENT, null, values);		
	}

	public void deleteAppointment(int appointmentId) {
		database.delete(MySQLiteHelper.TABLE_APPOINTMENT, MySQLiteHelper.COLUMN_APPOINTMENT_ID + " = ?", new String[] { String.valueOf(appointmentId) });
	}
	
	public void deleteAppointmentByDate(int date){
		database.delete(MySQLiteHelper.TABLE_APPOINTMENT, MySQLiteHelper.COLUMN_DATE + " = ?", new String[] { String.valueOf(date) });
	}	

	public void updateAppointment(int appointmentId, String title, String time, String detail) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_TITLE, title);
		values.put(MySQLiteHelper.COLUMN_TIME, time);
		values.put(MySQLiteHelper.COLUMN_DETAIL, detail);
		database.update(MySQLiteHelper.TABLE_APPOINTMENT, values, MySQLiteHelper.COLUMN_APPOINTMENT_ID + " = ?", new String[] { String.valueOf(appointmentId) });
	}
	
	public void updateAppointment(int appointmentId,int date, String title, String time, String detail) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_DATE, date);
		values.put(MySQLiteHelper.COLUMN_TITLE, title);
		values.put(MySQLiteHelper.COLUMN_TIME, time);
		values.put(MySQLiteHelper.COLUMN_DETAIL, detail);
		database.update(MySQLiteHelper.TABLE_APPOINTMENT, values, MySQLiteHelper.COLUMN_APPOINTMENT_ID + " = ?", new String[] { String.valueOf(appointmentId) });
	}

	public List<Appointment> getAllAppointment() {
		List<Appointment> listAppointments = new ArrayList<Appointment>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_APPOINTMENT, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Appointment appointment = cursorToAppointment(cursor);
			listAppointments.add(appointment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return listAppointments;
	}

	public List<Appointment> getAppointmentByDate(int date) {
		List<Appointment> listAppointments = new ArrayList<Appointment>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_APPOINTMENT, allColumns, MySQLiteHelper.COLUMN_DATE + " =?", new String[] { String.valueOf(date) }, null, null, MySQLiteHelper.COLUMN_TIME + " asc");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Appointment appointment = cursorToAppointment(cursor);
			listAppointments.add(appointment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return listAppointments;
	}
	
	public List<Appointment> getAppointmentByDateAndTitle(int date, String title) {
		List<Appointment> listAppointments = new ArrayList<Appointment>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_APPOINTMENT, allColumns, MySQLiteHelper.COLUMN_DATE + " =? and " + MySQLiteHelper.COLUMN_TITLE+" =?", new String[] { String.valueOf(date), title }, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Appointment appointment = cursorToAppointment(cursor);
			listAppointments.add(appointment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return listAppointments;
	}
	

	private Appointment cursorToAppointment(Cursor cursor) {
		Appointment appointment = new Appointment();
		appointment.setAppointmentId((int) cursor.getLong(0));
		appointment.setDate((int) cursor.getLong(1));
		appointment.setTitle(cursor.getString(2));
		appointment.setTime(cursor.getString(3));
		appointment.setDetail(cursor.getString(4));
		return appointment;
	}
}