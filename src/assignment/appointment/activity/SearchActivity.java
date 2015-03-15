package assignment.appointment.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;
import assignment.appointment.database.Appointment;
import assignment.appointment.database.AppointmentDataSource;

public class SearchActivity extends Activity {

	final Context context = this;
	private Button buttonSearch;
	private EditText titleSearch;
	private static AppointmentDataSource appointmentDataSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		titleSearch = (EditText) findViewById(R.id.titleSearch);
		initListenerOnButton();
		appointmentDataSource = new AppointmentDataSource(this);
		appointmentDataSource.open();
	}

	public void initListenerOnButton() {
		buttonSearch = (Button) findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<Appointment> listAllAppointments = appointmentDataSource.getAllAppointment();
				String searchString = titleSearch.getText().toString();
				List<Appointment> listResultAppointments = getListAppointmentMathString(listAllAppointments, searchString);
				TableLayout table = (TableLayout) findViewById(R.id.table_list_appointment);

				if (table.getChildCount() > 2) {
					table.removeViews(2, table.getChildCount() - 2);
				}
				for (int i = 0; i < listResultAppointments.size(); i++) {
					Appointment appointment = listResultAppointments.get(i);
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

					table.addView(row, i + 2);
				}

			}

		});
	}

	protected List<Appointment> getListAppointmentMathString(List<Appointment> listAllAppointments, String searchString) {
		// TODO Auto-generated method stub
		List<Appointment> listResult = new ArrayList<Appointment>();
		for (int i = 0; i < listAllAppointments.size(); i++) {
			Appointment aAppointment = listAllAppointments.get(i);
			if (aAppointment.getTitle().contains(searchString)) {
				listResult.add(aAppointment);
			}
		}

		return listResult;
	}

}
