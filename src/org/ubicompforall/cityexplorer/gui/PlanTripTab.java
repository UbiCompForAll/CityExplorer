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
 * This class handles all the action going on in the tours tab.
 * 
 */
package org.ubicompforall.cityexplorer.gui;

import java.util.ArrayList;


import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;
import org.ubicompforall.cityexplorer.data.DBFactory;
import org.ubicompforall.cityexplorer.data.DatabaseUpdater;
import org.ubicompforall.cityexplorer.data.IntentPassable;
import org.ubicompforall.cityexplorer.data.Poi;
import org.ubicompforall.cityexplorer.data.SeparatedListAdapter;
import org.ubicompforall.cityexplorer.data.Trip;
import org.ubicompforall.cityexplorer.data.TripAdapter;
import org.ubicompforall.cityexplorer.gui.TripListActivity;
import org.ubicompforall.cityexplorer.map.MapsActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class PlanTripTab extends PlanActivityTab{

	/**
	 * Field containing this activity's resources.
	 */
	private Resources res;
	
	/**
	 * Field containing the adapter.
	 */
	private SeparatedListAdapter adapter;
	
	/**
	 * Field containing this activity's ListView.
	 */
	private ListView lv;
	
	/**
	 * Field containing the trip currently being modified.
	 */
	private Trip trip;
	
	/**
	 * Field containing the request code for new trip.
	 */
	protected static final int NEW_TRIP = 3;
	
	/**
	 * Field containing the request code for add to trip.
	 */
	protected static final int ADD_TO_TRIP = 4;
	
	/**
	 * Field containing the request code from other activities.
	 */
	private int requestCode;
	
	/**
	 * Field containing all pois from an existing trip, when creating a new trip from an existing.
	 */
	private ArrayList<Poi> existingPois;
	
	/**
	 * Field containing the name of the newly created trip.
	 */
	private String tripName;

	private ArrayList<Trip> downloadedTrips;

	/**
	 * Field giving access to databaseupdater methods.
	 */
	private DatabaseUpdater du;
	
	/**
	 * Field containing the request code for downloading trips.
	 */
	protected static final int DOWNLOAD_TRIP = 7;
	

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		debug(1,"");
		if( existingPois != null ){
			int nrOfPoIs = existingPois.size();
			for (Poi p : existingPois) {
				trip.addPoi(p);
				DBFactory.getInstance(this).addPoiToTrip(trip, p);				
			}
			existingPois = null;
			Toast.makeText(this, 
					nrOfPoIs + " location(s) added to " + tripName + ".", Toast.LENGTH_LONG).show();
		}
		adapter.notifyDataSetChanged();
	}// onResume
	
	/**
	 * Initializes the activity.
	 */
	private void init() {
		requestCode = getIntent().getIntExtra("requestCode",0);
		lv = getListView();

		if (requestCode == CityExplorer.REQUEST_ADD_TO_TRIP || requestCode == NewTripActivity.ADD_TO_TRIP || requestCode == DOWNLOAD_TRIP){
			lv.setOnItemLongClickListener(null);
		}else {			
			lv.setOnItemLongClickListener(new DrawPopup());
		}
		
		if(requestCode == DOWNLOAD_TRIP){			
			du = new DatabaseUpdater(this);
			TripAdapter tripAdapter = new TripAdapter(this, R.layout.plan_listitem, du.getInternetTrips());
			adapter = new SeparatedListAdapter(this, SeparatedListAdapter.INTERNET_TRIPS);
			adapter.addSection("Server Tours", tripAdapter);
		}else {			
			adapter = new SeparatedListAdapter(this, SeparatedListAdapter.TRIP_LIST);
		}
		res = getResources();
		lv.setAdapter(adapter);
		//adapter.notifyDataSetChanged(); //Moved to onResume?
	}//init

	/**
	 * Checks if the given trip is empty.
	 * @param t The trip you want to check.
	 * @return True if the trip is empty, false otherwise.
	 */
	private boolean isEmptyTrip(Trip t) {
		if(t.getPois().size() > 0){
			return false;
		}else {
			return true;
		}
	}//isEmptyTrip

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		if( l.getAdapter().getItemViewType(pos) == SeparatedListAdapter.TYPE_SECTION_HEADER ){
			//Pressing a section header.
			return;
		}
		trip = (Trip) l.getAdapter().getItem(pos);

		if(requestCode == NewTripActivity.ADD_TO_TRIP){	//In case PlanTripTab was called only to select a trip (e.g. one to copy into a new trip).
			if(!isEmptyTrip(trip)){
				Intent resultIntent = new Intent();
				resultIntent.putExtra(IntentPassable.TRIP, trip);
				setResult( Activity.RESULT_OK, resultIntent );
				finish();
			}else {
				Toast.makeText(this, "This tour has no locations", Toast.LENGTH_LONG).show();
			}
			return;
		} // NewTrip->REQUEST_ADD_TO_TRIP

		if (requestCode == CityExplorer.REQUEST_ADD_TO_TRIP){
			Intent resultIntent = new Intent();
			resultIntent.putExtra(IntentPassable.TRIP, trip);
			setResult( Activity.RESULT_OK, resultIntent );
			finish();
			return;
		} // if REQUEST_ADD_TO_TRIP
		
		if (requestCode == DOWNLOAD_TRIP){

			if(downloadedTrips == null){				
				downloadedTrips = new ArrayList<Trip>();
			}
			if(!downloadedTrips.contains(trip)){
				v.setBackgroundColor(0xff9ba7d5);
				downloadedTrips.add(trip);
			}else {
				v.setBackgroundColor(Color.TRANSPARENT);
				downloadedTrips.remove(trip);
			}
			return;
		} // if download trip

		Intent details = new Intent( this, TripListActivity.class );
		details.putExtra("trip", trip);
		startActivity(details);
	} // onListItemClick

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//super.onPrepareOptionsMenu(menu);
		if (requestCode == CityExplorer.REQUEST_ADD_TO_TRIP || requestCode == NewTripActivity.ADD_TO_TRIP){
			menu.setGroupVisible(R.id.planMenuGroupTrip, false);
		}
		if(requestCode == PlanTripTab.DOWNLOAD_TRIP){
			menu.removeItem(R.id.planMenuNewTrip);
		}
		return true;
	}//onPrepareOptionsMenu

	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		debug(0, "here" );
		super.onCreateOptionsMenu(menu);
		menu.setGroupVisible(R.id.planMenuGroupPoi, false);
		return true;
	}//onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if(item.getItemId() == R.id.planMenuNewTrip){
			Intent newTrip = new Intent(PlanTripTab.this, NewTripActivity.class);
			newTrip.putExtra("requestCode", NEW_TRIP);
			startActivityForResult(newTrip, NEW_TRIP);

		}

// JF: Removed as no support for update provided	
//		if(item.getItemId() == R.id.planMenuUpdateTrips){
//			if(requestCode == DOWNLOAD_TRIP){
//				if (downloadedTrips==null){
//					Toast.makeText(this, "No trips selected", Toast.LENGTH_LONG).show();
//					return false;
//				}else {
//					int[] res = du.storeTrips(downloadedTrips);
//					
//					Toast.makeText(this, res[0]+" trips added, "+res[1]+" trips updated", Toast.LENGTH_LONG).show();
//				}
//				finish();
//			}else {				
//				Intent downloadPoi= new Intent(PlanTripTab.this, PlanTripTab.class);
//				downloadPoi.putExtra("requestCode", DOWNLOAD_TRIP);
//				startActivityForResult(downloadPoi, DOWNLOAD_TRIP);
//			}
//		} // if planMenu->UpdateTrip // RS-120123
		
		return true;
	}//onOptionsItemSelected

	/**
	 * Shows quick actions when the user long-presses an item.
	 */
	final private class DrawPopup implements AdapterView.OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
			if(parent.getAdapter().getItemViewType(pos) == SeparatedListAdapter.TYPE_SECTION_HEADER)
			{
				//Pressing a section header
				return true;
			}

			final Trip	clicked_trip 			= (Trip) parent.getAdapter().getItem(pos);
			final int	idx			= pos;
			final int[] xy 			= new int[2]; v.getLocationInWindow(xy);
			final Rect rect 		= new Rect(	xy[0], 
					xy[1], 
					xy[0]+v.getWidth(), 
					xy[1]+v.getHeight());

			final QuickActionPopup qa = new QuickActionPopup(PlanTripTab.this, v, rect);

			Drawable	addPoiIcon	= res.getDrawable(android.R.drawable.ic_menu_add);
			Drawable	mapviewIcon	= res.getDrawable(android.R.drawable.ic_menu_mapmode);
			Drawable	deleteIcon	= res.getDrawable(android.R.drawable.ic_menu_delete);
			Drawable	timeIcon	= res.getDrawable(android.R.drawable.ic_menu_recent_history);


			// Declare quick actions 

			// 1: Show on Map
			qa.addItem(mapviewIcon,	R.string.activity_menu_showOnMap,	 new OnClickListener(){
				public void onClick(View view){
					Intent showInMap = new Intent(PlanTripTab.this, MapsActivity.class);
					showInMap.putExtra(IntentPassable.TRIP, clicked_trip);
					startActivity(showInMap);
					qa.dismiss();
				}
			});

			// 2: Add Location
			qa.addItem(addPoiIcon, R.string.activity_triplist_menu_addPoi, new OnClickListener(){
				public void onClick(View view)
				{
					
					Intent selectPoi = new Intent(PlanTripTab.this, PlanPoiTab.class);
					selectPoi.putExtra(IntentPassable.TRIP, clicked_trip);
					selectPoi.putExtra("requestCode", ADD_TO_TRIP);
					startActivityForResult(selectPoi, ADD_TO_TRIP);

					qa.dismiss();
				}
			});

			// 3: Deletes
			qa.addItem(deleteIcon, "Delete", new OnClickListener(){
				public void onClick(View view){	
					((TripAdapter)adapter.getAdapter(idx)).remove(clicked_trip);
					adapter.notifyDataSetChanged();
					qa.dismiss();
				}
			});

			// 4: Show Time Table (TODO: if fixed tour!)	%% RS-120815
			if( clicked_trip != null && clicked_trip.isFreeTrip() == false ){ // && clicked_trip.isEmpty() == false){
				//From TripListActivity: menu.findItem(R.id.triplistMenuCalendar).setVisible(true);
				qa.addItem(timeIcon, R.string.activity_triplist_menu_calendar, new OnClickListener(){
					public void onClick(View view){
						Intent calendar = new Intent( PlanTripTab.this, CalendarActivity.class );
						calendar.putExtra(IntentPassable.TRIP, clicked_trip);
						startActivity( calendar );
						qa.dismiss();
					}
				});
			}else{// If fixed tour (with time-table)
				debug(2, "trip was free (without time-schedule?)" );
			}

			qa.show();
			return true;
		} // onItemLongClick
	} // DrawPopup

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode==Activity.RESULT_CANCELED){
			return;
		}
		switch (requestCode){
		case ADD_TO_TRIP:
			Poi poi = (Poi) data.getParcelableExtra(IntentPassable.POI);
			if (poi == null){ 
				break;
			}
			trip.addPoi(poi);
			Toast.makeText(this, poi.getLabel() + " added to " + trip.getLabel() + ".", Toast.LENGTH_LONG).show();
			DBFactory.getInstance(this).addPoiToTrip(trip, poi);
		break;
		case NEW_TRIP:
			existingPois = data.getParcelableArrayListExtra(IntentPassable.POILIST);
			trip = data.getParcelableExtra(IntentPassable.TRIP);
			tripName = data.getStringExtra(NewTripActivity.TRIPNAME);
			if (existingPois==null){ 
				break;
			}
			if(trip==null){
				break;
			}
		break;
		default:
			break;
		}
	}
}//PlanTripTab
