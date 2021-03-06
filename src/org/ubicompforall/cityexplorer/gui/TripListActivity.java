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

package org.ubicompforall.cityexplorer.gui;

import java.util.ArrayList;


import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;
import org.ubicompforall.cityexplorer.data.*;
import org.ubicompforall.cityexplorer.map.MapsActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @description:
 * This class shows a single trip in a list.
 */

public class TripListActivity extends ListActivity{

	/**
	 * Request code for launching calendar.
	 */
	private static final int CALENDAR = 1;

	/**
	 * Request code for launching "poi-chooser".
	 */
	protected static final int ADD_TO_TRIP = 2;

	/**
	 * Arraylist holding all the pois in the shown trip.
	 */
	private ArrayList<Poi> pois = new ArrayList<Poi>();

	/**
	 * Custom made adapter for pois.
	 */
	private PoiAdapter poiAdapter;

	/**
	 * This activity's listview.
	 */
	private ListView lv;

	/**
	 * Trip object holding this particular trip.
	 */
	private Trip trip;

	/**
	 * This activity's resources.
	 */
	private Resources res;

	/**
	 * This activity's context.
	 */
	private Activity context = this;

	/**
	 * The current location of the user.
	 */
	private Location userLocation;

	/**
	 * Database object for easily reaching the database methods.
	 */
	private DatabaseInterface db;

	/**
	 * TextView that contains the title of the trip.
	 */
	private TextView title;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.triplist);
		init();

		poiAdapter.notifyDataSetChanged();
		userLocation = StartActivity.verifyUserLocation( userLocation, this );
	}//onCreate

	
	private void debug(int i, String string) {
		CityExplorer.debug(i, string);
	}

	public Trip getTrip() {
		return trip;
	}//getTrip

	/**
	 * Initializes the activity.
	 */
	private void init() {
		db = DBFactory.getInstance(context);
		res = getResources();

		if(getIntent().getParcelableExtra("trip") != null){
			trip = (Trip) getIntent().getParcelableExtra("trip");
			title = (TextView)findViewById(R.id.triplabel);
			title.setText(trip.getLabel());
		}else{
			debug(0, "No trip supplied.. exit activity");
			this.finish();
		}
		debug(2, "FreeTrip="+trip.isFreeTrip() );

		lv = getListView();
		lv.setOnItemLongClickListener(new DrawPopup());
		poiAdapter = new PoiAdapter( this, R.layout.plan_listitem, pois );
		lv.setAdapter(poiAdapter);

		for (Poi p : trip.getPois()) {
			pois.add(p);
		}
	}// init

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.clear();
		getMenuInflater().inflate(R.menu.triplistmenu, menu);

		if(trip.isFreeTrip() == false && trip.isEmpty() == false){			
			menu.findItem(R.id.triplistMenuCalendar).setVisible(true);
		}

		return true;
	}//onCreateOptionsMenu
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.triplistMenuAddPoi:
			Intent selectPoi = new Intent(this, PlanPoiTab.class);
			selectPoi.putExtra(IntentPassable.TRIP, trip);
			selectPoi.putExtra("requestCode", ADD_TO_TRIP);
			startActivityForResult(selectPoi, ADD_TO_TRIP);
			break;
		case R.id.triplistMenuMap:
			Intent showInMap = new Intent(this, MapsActivity.class);
			showInMap.putExtra(IntentPassable.TRIP, trip);
			startActivity(showInMap);
			break;
		case R.id.triplistMenuDelete:
			db.deleteTrip(trip);
			finish();
			break;
		case R.id.triplistMenuCalendar:
			Intent calendar = new Intent(this, CalendarActivity.class);
			calendar.putExtra("trip", trip);
			startActivityForResult(calendar, CALENDAR);
			break;
		default:
			break;
		}
		return true;
	}//onOptionsItemSelected

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		debug(0,"requestCode="+requestCode+", REQUEST_ADD_TO_TRIP="+ADD_TO_TRIP+", CALENDER="+CALENDAR+", etc...");
		if(resultCode==Activity.RESULT_CANCELED){
			return;
		}
		switch (requestCode){
		case ADD_TO_TRIP:
			debug(2, "Returned pois to add to trip!" ); //From using PoiList to select new POIs to add to trip
			trip = data.getParcelableExtra(IntentPassable.TRIP);
			pois = trip.getPois();
			poiAdapter.replaceAll(pois);
			lv.setAdapter(poiAdapter);
			poiAdapter.notifyDataSetChanged();
			break;
		case CALENDAR:
			this.trip = data.getParcelableExtra(IntentPassable.TRIP);
			poiAdapter.notifyDataSetChanged();
			break;
		default:
			debug(0,"No handler for result="+resultCode );
			break;
		}
	}//onActivityResult

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {

		Poi p = (Poi) l.getAdapter().getItem(pos);
		debug(0, "POI " + (pois.size()-1) + ": " + pois.get(pois.size()-1).getImageURL());
		Intent details = new Intent(TripListActivity.this, PoiDetailsActivity.class);
		details.putExtra("poi", p);
		details.putExtra("trip", trip);
		details.putExtra("poiNumber", pos);

		startActivity(details);
	}//onListItemClick

	/**
	 * Show quick actions when the user long-presses an item 
	 */
	final private class DrawPopup implements AdapterView.OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {

			final Poi	p 			= (Poi) parent.getAdapter().getItem(pos);
			final AdapterView<?> par = parent;
			final int	idx			= pos;
			final int[] xy 			= new int[2]; v.getLocationInWindow(xy);

			final Rect rect 		= new Rect(	xy[0], 
					xy[1], 
					xy[0]+v.getWidth(), 
					xy[1]+v.getHeight());

			final QuickActionPopup qa = new QuickActionPopup(TripListActivity.this, v, rect);

			Drawable mapviewIcon	= res.getDrawable(android.R.drawable.ic_menu_mapmode);
			Drawable directIcon		= res.getDrawable(android.R.drawable.ic_menu_directions);
			Drawable deleteIcon		= res.getDrawable(android.R.drawable.ic_menu_delete);

			// declare quick actions 			
			qa.addItem(deleteIcon, "Delete from tour", new OnClickListener(){

				public void onClick(View view){
					db.deleteFromTrip(trip, trip.getPoiAt(idx));

					trip.removePoi(idx);

					//delete from list
					((PoiAdapter)par.getAdapter()).remove(p);	
					((PoiAdapter)par.getAdapter()).notifyDataSetChanged();
					qa.dismiss();
				}
			});

			qa.addItem(mapviewIcon,	R.string.activity_menu_showOnMap,	new OnClickListener(){
				public void onClick(View view){
					Intent showInMap = new Intent(TripListActivity.this, MapsActivity.class);
					ArrayList<Poi> selectedPois = new ArrayList<Poi>();
					selectedPois.add(p);
					showInMap.putParcelableArrayListExtra(IntentPassable.POILIST, selectedPois);

					startActivity(showInMap);
					qa.dismiss();
				}
			});

			qa.addItem(directIcon,	"Get directions",	new OnClickListener(){
				public void onClick(View view){
					//Latitude and longitude for current position
					double slon = userLocation.getLongitude();
					double slat = userLocation.getLatitude();
					//Latitude and longitude for selected poi
					double dlon = p.getGeoPoint().getLongitudeE6()/1E6;
					double dlat = p.getGeoPoint().getLatitudeE6()/1E6;

					Intent navigate = new Intent(TripListActivity.this, NavigateFrom.class);
					navigate.putExtra("slon", slon);
					navigate.putExtra("slat", slat);
					navigate.putExtra("dlon", dlon);
					navigate.putExtra("dlat", dlat);
					startActivity(navigate);

					qa.dismiss();

				}
			});

			qa.show();

			return true;
		}//onItemLongClick
	}//DrawPoput Class

}//TripListActivity
