/**
 * @contributor(s): Kristian Greve Hagen (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 *
 * Copyright (C) 2011-2012 UbiCompForAll Consortium (SINTEF, NTNU)
 * for the UbiCompForAll project
 *
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 */

/**
 * @description:
 * This class handles everything that concerns adding new tours.
 * 
 */

package org.ubicompforall.cityexplorer.gui;

import java.util.ArrayList;


import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;
import org.ubicompforall.cityexplorer.data.DBFactory;
import org.ubicompforall.cityexplorer.data.DatabaseInterface;
import org.ubicompforall.cityexplorer.data.IntentPassable;
import org.ubicompforall.cityexplorer.data.Poi;
import org.ubicompforall.cityexplorer.data.Trip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class NewTripActivity extends Activity implements OnClickListener{

	/** The name view. */
	private EditText nameView;
	
	/** The description view. */
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
	
	/** The Constant REQUEST_ADD_TO_TRIP. */
	protected static final int ADD_TO_TRIP = TripListActivity.ADD_TO_TRIP;
	
	/** The existing pois, fetched from the existing trip you will create the new from. */
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
	} // onCreate

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug



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
	} // init

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_trip_menu, menu);
		return true;
	} // onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.saveTrip){
			saveTrip();
		}
		return true;
	} // onOptionsItemSelected

	/**
	 * Checks all the mandatory input fields and saves a trip in the database.
	 */
	private void saveTrip(){
		name = nameView.getText().toString();
		if(name.trim().equals("")){	
			Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
			return;
		}
		description = "";
		if (descriptionView != null){
			description = descriptionView.getText().toString();
		}

		if(freeTrip.isChecked()){
			free = true;
		}else {
			free = false;
		}

		//Get new biggest ID
		db = DBFactory.getInstance(this);
		ArrayList<Trip> allTrips = db.getTripsWithoutPOIs( CityExplorer.TYPE_ALL );
		//ArrayList<Trip> allEmptyTrips = db.getTrips( CityExplorer.TYPE_ALL );
		int idAllTrips = 0, pId;
		if(allTrips.size() != 0){
			idAllTrips = allTrips.get(allTrips.size()-1).getIdPrivate();
		}
//		if(allEmptyTrips.size() != 0){
//			idAllEmptyTrips = allEmptyTrips.get(allEmptyTrips.size()-1).getIdPrivate();
//		}
//		if(idAllEmptyTrips>idAllTrips){
//			pId=idAllEmptyTrips+1;
//		}else{
			pId=idAllTrips+1;
//		}

		Trip trip = new Trip.Builder(name).description(description).freeTrip(free).idPrivate(pId).build();
		db.newTrip(trip);

		if (requestCode == PlanTripTab.NEW_TRIP){
			Intent resultIntent = new Intent();
			resultIntent.putExtra(IntentPassable.POILIST, existingPois);
			resultIntent.putExtra(IntentPassable.TRIP, trip);
			resultIntent.putExtra(TRIPNAME, trip.getLabel().toString());
			setResult( Activity.RESULT_OK, resultIntent );
			finish();
		} else {
			debug(0, "REQUEST CODE = "+requestCode);
		}
		finish();
	} // saveTrip

	@Override
	public void onClick(View v) {
		if(v == fromExistingButton){			
			Intent selectTrip = new Intent(this, PlanTripTab.class);
			selectTrip.putExtra("requestCode", ADD_TO_TRIP );
			startActivityForResult(selectTrip, ADD_TO_TRIP);
		}
		if(v == saveTripButton){
			saveTrip();
		}
	} // onClick

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
	} // onActivityResult
} // class NewTripActivity
