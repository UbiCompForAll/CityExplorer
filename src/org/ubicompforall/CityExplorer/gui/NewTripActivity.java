package org.ubicompforall.CityExplorer.gui;

import java.util.ArrayList;

import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.DatabaseInterface;
import org.ubicompforall.CityExplorer.data.IntentPassable;
import org.ubicompforall.CityExplorer.data.Poi;
import org.ubicompforall.CityExplorer.data.Trip;

import org.ubicompforall.CityExplorer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * This class handles everything that concerns adding new tours.
 * 
 * @author Kristian Greve Hagen
 *
 */
public class NewTripActivity extends Activity implements OnClickListener{

	/** The name view. */
	private EditText nameView;
	
	/** The description view. */
	@SuppressWarnings("unused")
	private EditText descriptionView;
	
	/** The existing trip view. */
	private TextView existingTripView;
	
	/** The name of the new trip. */
	private String name;
	
	/** The description of the new trip. */
	private String description;
	
	/** The existing trip. */
	private String existingTrip;
	
	/** The fixed trip. */
	private RadioButton fixedTrip;
	
	/** The free trip. */
	private RadioButton freeTrip;
	
	/** The from existing button. */
	private Button fromExistingButton;
	
	/** The save trip button. */
	private Button saveTripButton;
	
	/** The boolean free, indicating if this is a free trip or not. */
	private boolean free;
	
	/** The request code it gets from other activities. */
	private int requestCode;
	
	/** The Constant ADD_TO_TRIP. */
	protected static final int ADD_TO_TRIP = 2;
	
	/** The existing pois, fetched from the old trip you will create the new from. */
	private ArrayList<Poi> existingPois;
	
	/** The trip name. */
	private String trip = "";
	
	/** The DataBase object. */
	private DatabaseInterface db;
	
	/** The trip. */
	private Trip t;
	
	/** The Constant TRIPNAME. */
	protected static final String TRIPNAME = "tripname";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newtrip);
		init();
	}

	/**
	 * Initializes the activity.
	 */
	private void init() {
		requestCode = getIntent().getIntExtra("requestCode",0);
		nameView = (EditText)findViewById(R.id.editname);
		descriptionView = (EditText)findViewById(R.id.editdescription);
		fixedTrip = (RadioButton)findViewById(R.id.fixedTrip);
		freeTrip = (RadioButton)findViewById(R.id.freeTrip);
		existingTripView = (TextView)findViewById(R.id.editfromexistingLabel);
		existingTrip = existingTripView.getText().toString();
		trip = "None";
		existingTripView.append(" " + trip);
		fromExistingButton = (Button)findViewById(R.id.editfromexisting);
		fromExistingButton.setOnClickListener(this);
		saveTripButton = (Button)findViewById(R.id.saveTrip);
		saveTripButton.setOnClickListener(this);
		fixedTrip.setChecked(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_trip_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.saveTrip){
			saveTrip();
		}
		return true;
	}

	/**
	 * Checks all the mandatory input fields and saves a trip in the database.
	 */
	private void saveTrip(){
		name = nameView.getText().toString();
		if(name.trim().equals(""))
		{	
			Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
			return;
		}
		description = "";//descriptionView.getText().toString();

		if(freeTrip.isChecked()){
			free = true;
		}else {
			free = false;
		}
		db = DBFactory.getInstance(this);
		ArrayList<Trip> allTrips = db.getAllTrips();
		ArrayList<Trip> allEmptyTrips = db.getAllEmptyTrips();


		int idAllTrips = 0, idAllEmptyTrips = 0, pId;
		if(allTrips.size() != 0){
			idAllTrips = allTrips.get(allTrips.size()-1).getIdPrivate();

		}

		if(allEmptyTrips.size() != 0){
			idAllEmptyTrips = allEmptyTrips.get(allEmptyTrips.size()-1).getIdPrivate();
		}
		if(idAllEmptyTrips>idAllTrips){
			pId=idAllEmptyTrips+1;
		}else{
			pId=idAllTrips+1;
		}


		Trip trip = new Trip.Builder(name).description(description).freeTrip(free).idPrivate(pId).build();

		db.newTrip(trip);

		if (requestCode == PlanTabTrip.NEW_TRIP){
			Intent resultIntent = new Intent();
			resultIntent.putExtra(IntentPassable.POILIST, existingPois);
			resultIntent.putExtra(IntentPassable.TRIP, trip);
			resultIntent.putExtra(TRIPNAME, trip.getLabel().toString());
			setResult( Activity.RESULT_OK, resultIntent );
			finish();
		} else {
			Log.d("CityExplorer", "REQUEST CODE = "+requestCode);
		}
		finish();
	}

	@Override
	public void onClick(View v) {
		if(v == fromExistingButton){			
			Intent selectTrip = new Intent(this, PlanTabTrip.class);
			selectTrip.putExtra("requestCode", ADD_TO_TRIP );
			startActivityForResult(selectTrip, ADD_TO_TRIP);
		}
		if(v == saveTripButton){
			saveTrip();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode==Activity.RESULT_CANCELED){
			return;
		}
		switch (requestCode){
		case ADD_TO_TRIP:
			t = (Trip) data.getParcelableExtra(IntentPassable.TRIP);
			if (t!=null){ 				
				existingPois = t.getPois();

				trip = t.getLabel();
				existingTripView.setText(existingTrip + " " + trip);
			}
			break;
		default:
			break;
		}

	}
}
