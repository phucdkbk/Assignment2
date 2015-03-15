package assignment.appointment.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView.OnDateChangeListener;
import assignment.appointment.database.Appointment;
import assignment.appointment.database.AppointmentDataSource;

public class AppointmentActivity extends Activity {

	final Context context = this;

	CalendarView calendar;
	private Calendar selectedDate;
	private Button buttonCreate;
	private Button buttonView;
	private Button buttonDelete;
	private Button buttonSearch;
	private Button buttonMove;
	private Button buttonTranslate;
	private Button buttonDeleteAll;
	private Button buttonSelectToDelete;
	private Appointment appointmentToMove;
	private int onMoveDateProcees;
	private final DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);

	private static AppointmentDataSource appointmentDataSource;

	public static void setAutoOrientationEnabled(Context context, boolean enabled) {
		Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setAutoOrientationEnabled(this, false);
		setContentView(R.layout.main);
		initializeCalendar();
		addListenerOnButton();
		appointmentDataSource = new AppointmentDataSource(this);
		appointmentDataSource.open();
		selectedDate = Calendar.getInstance();

	}

	public void addListenerOnButton() {
		buttonCreate = (Button) findViewById(R.id.button_create);
		buttonCreate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.pompt_create, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setView(promptsView);
				final EditText titleInput = (EditText) promptsView.findViewById(R.id.title);
				final EditText timeInput = (EditText) promptsView.findViewById(R.id.time);
				final EditText detailInput = (EditText) promptsView.findViewById(R.id.detail);
				// set dialog message
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
						int dateInt = Integer.valueOf(df.format(selectedDate.getTime()));
						List<Appointment> listAppointments = appointmentDataSource.getAppointmentByDateAndTitle(dateInt, timeInput.getText().toString());
						if (listAppointments != null && listAppointments.size() > 0) {
							Toast.makeText(context, "Can not create appoint with the same title in one day!", Toast.LENGTH_SHORT).show();
						} else {
							appointmentDataSource.createAppointment(dateInt, titleInput.getText().toString(), timeInput.getText().toString(), detailInput.getText().toString());
							Toast.makeText(context, "Create successfully", Toast.LENGTH_SHORT).show();
						}
					}
				});
				alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}

		});

		buttonView = (Button) findViewById(R.id.button_view);
		buttonView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.pompt_select_appointment, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setView(promptsView);
				DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
				int dateInt = Integer.valueOf(df.format(selectedDate.getTime()));
				TableLayout table = (TableLayout) promptsView.findViewById(R.id.table_list_appointment);

				List<Appointment> listAppointment = appointmentDataSource.getAppointmentByDate(dateInt);

				for (int i = 0; i < listAppointment.size(); i++) {
					Appointment appointment = listAppointment.get(i);

					TableRow row = new TableRow(context);
					row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

					TextView label_orderno = new TextView(context);
					label_orderno.setText(i + 1 + ".");
					label_orderno.setTextColor(Color.BLACK);
					label_orderno.setPadding(5, 5, 5, 5);
					row.addView(label_orderno);

					TextView label_time = new TextView(context);
					label_time.setText(appointment.getTime());
					label_time.setTextColor(Color.BLACK);
					label_time.setPadding(5, 5, 5, 5);
					row.addView(label_time);

					TextView label_title = new TextView(context);
					label_title.setText(appointment.getTitle());
					label_title.setTextColor(Color.BLACK);
					label_title.setPadding(5, 5, 5, 5);
					row.addView(label_title);

					table.addView(row, i);
				}

				final EditText titleInput = (EditText) promptsView.findViewById(R.id.select_appointment);
				TextView selectTitle = (TextView) promptsView.findViewById(R.id.select_appoint_text);
				selectTitle.setText("Select appointment number to update:");

				// set dialog message
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						int selected = Integer.parseInt(titleInput.getText().toString());
						LayoutInflater li = LayoutInflater.from(context);
						View promptsUpdate = li.inflate(R.layout.pompt_update, null);
						AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(context);
						confirmDialogBuilder.setView(promptsUpdate);
						EditText title = (EditText) promptsUpdate.findViewById(R.id.title);
						EditText time = (EditText) promptsUpdate.findViewById(R.id.time);
						EditText detail = (EditText) promptsUpdate.findViewById(R.id.detail);
						DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
						int dateInt = Integer.valueOf(df.format(selectedDate.getTime()));
						List<Appointment> listAppointment = appointmentDataSource.getAppointmentByDate(dateInt);
						final Appointment selectedAppointment = listAppointment.get(selected - 1);

						title.setText(selectedAppointment.getTitle());
						time.setText(selectedAppointment.getTime());
						detail.setText(selectedAppointment.getDetail());

						CustomOnclickListener aCustomOnclickListener = new CustomOnclickListener(selectedAppointment.getAppointmentId(), title, time, detail, appointmentDataSource);
						confirmDialogBuilder.setPositiveButton("UPDATE", aCustomOnclickListener);
						confirmDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
						AlertDialog alertDialog = confirmDialogBuilder.create();
						alertDialog.show();
					}
				});

				alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
			}
		});

		buttonDelete = (Button) findViewById(R.id.button_delete);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				buttonDeleteAll.setVisibility(View.VISIBLE);
				buttonSelectToDelete.setVisibility(View.VISIBLE);
			}
		});

		buttonSearch = (Button) findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, SearchActivity.class);
				startActivity(intent);
			}
		});

		buttonMove = (Button) findViewById(R.id.button_move);
		buttonMove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.pompt_select_appointment, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setView(promptsView);

				int dateInt = Integer.valueOf(df.format(selectedDate.getTime()));
				TableLayout table = (TableLayout) promptsView.findViewById(R.id.table_list_appointment);

				List<Appointment> listAppointment = appointmentDataSource.getAppointmentByDate(dateInt);

				for (int i = 0; i < listAppointment.size(); i++) {
					Appointment appointment = listAppointment.get(i);

					TableRow row = new TableRow(context);
					row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

					TextView label_orderno = new TextView(context);
					label_orderno.setText(i + 1 + ".");
					label_orderno.setTextColor(Color.BLACK);
					label_orderno.setPadding(5, 5, 5, 5);
					row.addView(label_orderno);

					TextView label_time = new TextView(context);
					label_time.setText(appointment.getTime());
					label_time.setTextColor(Color.BLACK);
					label_time.setPadding(5, 5, 5, 5);
					row.addView(label_time);

					TextView label_title = new TextView(context);
					label_title.setText(appointment.getTitle());
					label_title.setTextColor(Color.BLACK);
					label_title.setPadding(5, 5, 5, 5);
					row.addView(label_title);

					table.addView(row, i);
				}

				final EditText titleInput = (EditText) promptsView.findViewById(R.id.select_appointment);
				TextView selectTitle = (TextView) promptsView.findViewById(R.id.select_appoint_text);
				selectTitle.setText("Select appointment number to move:");

				// set dialog message
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						int selected = Integer.parseInt(titleInput.getText().toString());
						DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
						int dateInt = Integer.valueOf(df.format(selectedDate.getTime()));
						List<Appointment> listAppointment = appointmentDataSource.getAppointmentByDate(dateInt);
						appointmentToMove = listAppointment.get(selected - 1);
						onMoveDateProcees = 1;
						Toast.makeText(context, "Select date to move appoinment", Toast.LENGTH_SHORT).show();
					}
				});

				alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						onMoveDateProcees = 0;
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});

		buttonTranslate = (Button) findViewById(R.id.button_translate);
		buttonTranslate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});

		buttonDeleteAll = (Button) findViewById(R.id.button_deleteall);
		buttonDeleteAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {				
				DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
				int dateInt = Integer.valueOf(df.format(selectedDate.getTime()));
				appointmentDataSource.deleteAppointmentByDate(dateInt);
				Toast.makeText(context, "Delete all appointments successfully", Toast.LENGTH_SHORT).show();
			}
		});

		buttonSelectToDelete = (Button) findViewById(R.id.button_selecttodelete);
		buttonSelectToDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.pompt_select_appointment, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setView(promptsView);

				DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
				int dateInt = Integer.valueOf(df.format(selectedDate.getTime()));
				TableLayout table = (TableLayout) promptsView.findViewById(R.id.table_list_appointment);

				List<Appointment> listAppointment = appointmentDataSource.getAppointmentByDate(dateInt);

				for (int i = 0; i < listAppointment.size(); i++) {
					Appointment appointment = listAppointment.get(i);

					TableRow row = new TableRow(context);
					row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

					TextView label_orderno = new TextView(context);
					label_orderno.setText(i + 1 + ".");
					label_orderno.setTextColor(Color.BLACK);
					label_orderno.setPadding(5, 5, 5, 5);
					row.addView(label_orderno);

					TextView label_time = new TextView(context);
					label_time.setText(appointment.getTime());
					label_time.setTextColor(Color.BLACK);
					label_time.setPadding(5, 5, 5, 5);
					row.addView(label_time);

					TextView label_title = new TextView(context);
					label_title.setText(appointment.getTitle());
					label_title.setTextColor(Color.BLACK);
					label_title.setPadding(5, 5, 5, 5);
					row.addView(label_title);

					table.addView(row, i);
				}

				final EditText titleInput = (EditText) promptsView.findViewById(R.id.select_appointment);
				TextView selectTitle = (TextView) promptsView.findViewById(R.id.select_appoint_text);
				selectTitle.setText("Select appointment number to delete:");

				// set dialog message
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						int selected = Integer.parseInt(titleInput.getText().toString());
						LayoutInflater li = LayoutInflater.from(context);
						View promptsConfirmDetele = li.inflate(R.layout.pompt_confirm_delete, null);
						AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(context);
						confirmDialogBuilder.setView(promptsConfirmDetele);
						TextView confirmText = (TextView) promptsConfirmDetele.findViewById(R.id.confirmDeleteText);
						DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
						int dateInt = Integer.valueOf(df.format(selectedDate.getTime()));
						List<Appointment> listAppointment = appointmentDataSource.getAppointmentByDate(dateInt);
						final Appointment selectedAppointment = listAppointment.get(selected - 1);
						confirmText.setText("Would you like to delete event: \"" + selectedAppointment.getTitle() + "\"");

						confirmDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

								appointmentDataSource.deleteAppointment(selectedAppointment.getAppointmentId());
								Toast.makeText(context, "Delete appointment successfully", Toast.LENGTH_SHORT).show();
							}
						});
						confirmDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
						AlertDialog alertDialog = confirmDialogBuilder.create();
						alertDialog.show();
					}
				});

				alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});

		buttonDeleteAll.setVisibility(View.GONE);
		buttonSelectToDelete.setVisibility(View.GONE);

	}

	/*
	 * initialize calendar
	 */
	public void initializeCalendar() {
		calendar = (CalendarView) findViewById(R.id.calendar);
		// sets whether to show the week number.
		calendar.setShowWeekNumber(false);
		// sets the first day of week according to Calendar.
		// here we set Monday as the first day of the Calendar
		calendar.setFirstDayOfWeek(2);
		// The background color for the selected week.
		calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));
		// sets the color for the dates of an unfocused month.
		calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));
		// sets the color for the separator line between weeks.
		calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));
		// sets the color for the vertical bar shown at the beginning and at the
		// end of the selected date.
		calendar.setSelectedDateVerticalBar(R.color.darkgreen);
		//calendar.setDateTextAppearance(R.style.Widget_CalendarView_Custom);
		calendar.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				selectedDate.set(Calendar.YEAR, year);
				selectedDate.set(Calendar.MONTH, month);

				if (onMoveDateProcees == 1) {
					appointmentDataSource.updateAppointment(appointmentToMove.getAppointmentId(), Integer.valueOf(df.format(selectedDate.getTime())), appointmentToMove.getTitle(), appointmentToMove.getTime(), appointmentToMove.getDetail());
					Toast.makeText(context, "Move successfully", Toast.LENGTH_SHORT).show();
					onMoveDateProcees = 0;
				}
			}
		});

	}

	/*
	 *  custom listener to update appointment
	 */
	class CustomOnclickListener implements DialogInterface.OnClickListener {

		private int appointmentId;
		private EditText title;
		private EditText time;
		private EditText detail;
		private AppointmentDataSource appointmentDataSource;

		public CustomOnclickListener(int appointmentId, EditText title, EditText time, EditText detail, AppointmentDataSource appointmentDataSource) {
			super();
			this.appointmentId = appointmentId;
			this.title = title;
			this.time = time;
			this.detail = detail;
			this.appointmentDataSource = appointmentDataSource;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			appointmentDataSource.updateAppointment(appointmentId, title.getText().toString(), time.getText().toString(), detail.getText().toString());
			Toast.makeText(context, "Update successfully", Toast.LENGTH_SHORT).show();
		}
	}
}
