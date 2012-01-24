/**
 * @contributor(s): Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			22 November 2011
 * @revised:		15 December 2011
 *
 * Copyright (C) 2011 UbiCompForAll Consortium (SINTEF, NTNU)
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
 * This class tracks existing DBs.
 * This class keeps hold of the tabs used in import mode.
 */

package org.ubicompforall.CityExplorer.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.ubicompforall.CityExplorer.data.FileSystemConnector;
//import org.ubicompforall.CityExplorer.data.DatabaseUpdater;
import org.ubicompforall.CityExplorer.data.DB;
import org.ubicompforall.CityExplorer.data.IntentPassable;
import org.ubicompforall.CityExplorer.data.DBFileAdapter;
import org.ubicompforall.CityExplorer.data.SeparatedListAdapter;
import org.ubicompforall.CityExplorer.map.MapsActivity;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ImportTabLocal extends ListActivity implements LocationListener, OnMultiChoiceClickListener, DialogInterface.OnClickListener{

	/** Field containing the String of the category settings, used in shared preferences. */
	private static String CATEGORY_SETTINGS = "catset";

	/*** Field containing all DBs.*/
	//private ArrayList<Poi> allPois = new ArrayList<Poi>();
	private ArrayList<DB> allDBs = new ArrayList<DB>();

	/*** Field containing this activity's resources.*/
	private Resources res;

	/*** Field containing the {@link SeparatedListAdapter} that holds all the other adapters.*/
	private SeparatedListAdapter adapter;

	/*** Field containing this activity's {@link ListView}.*/
	private ListView lv;

	/*** Field containing the users current location.*/
	private Location userLocation;

	/*** Field containing an {@link ArrayList} of the categories.*/
	private ArrayList<String> categories;

	/*** Field containing this activity's context.*/
	private Context context;  // This could be the pwd? What context? For drawing output?

	/*** Field containing a {@link HashMap} for the checked categories in the filter.*/
	private HashMap<String, Boolean> CheckedCategories = new HashMap<String, Boolean>();

	/*** Field containing the request code from other activities.*/
	private int requestCode;

	/*** Field containing a single DB.*/
	//private POI poi;
	//private DB db;

	/*** Field giving access to databaseUpdater methods.*/
	//private DatabaseUpdater du;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		debug(0, "ImportTabLocal~118 create");
		init();		
	}

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug



	/**
	 * Initializes the activity.
	 */
	private void init() {
		setContext(this);
		requestCode = getIntent().getIntExtra("requestCode",0);
		lv = getListView();
		lv.setOnItemLongClickListener(new DrawPopup());

		//allPois = FileSystemConnector.getInstance(this).getAllPois();
		allDBs = getAllDBs();
		adapter = new SeparatedListAdapter(this, SeparatedListAdapter.LOCAL_DBS);

		res = getResources();
		lv.setAdapter(adapter);
		//categories = FileSystemConnector.getInstance(this).getUniqueCategoryNames();
		//Collections.sort(categories);
		categories = new ArrayList<String>();
		categories.add("/data/data");
		CheckedCategories.put("Favourites", true);
		buildFilter();
		makeSections();
		//lv.setAdapter(adapter);

		//initGPS(); //For maps?
	}//init

	private ArrayList<DB> getAllDBs() {
		allDBs.add( new DB( "Rune", "Rock'n'Roll" ) );
		return allDBs;
	}//getAllDBs

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Makes the category sections that is shown in the list. 
	 */
	private void makeSections(){
		for (DB db : allDBs){
			if( !adapter.getSectionNames().contains(db.getCategory())){ //category does not exist, create it.
				ArrayList<DB> list = new ArrayList<DB>();

				DBFileAdapter testAdapter;
				testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
				adapter.addSection(db.getCategory(), testAdapter);
			}
			((DBFileAdapter)adapter.getAdapter(db.getCategory())).add(db);//add to the correct section
			((DBFileAdapter)adapter.getAdapter(db.getCategory())).notifyDataSetChanged();
		}
	}

	/**
	 * Updates the category sections in the list, e.g. after choosing filtering.
	 */
	@SuppressWarnings("unchecked")
	private void updateSections()
	{
		allDBs = new FileSystemConnector().getAllDBs();
		ArrayList<String> sectionsInUse = new ArrayList<String>(); 
		for (DB db : allDBs){
			//ignore sections that are turned off:
			if(CheckedCategories.keySet().contains(db.getCategory())){
				if( !CheckedCategories.get(db.getCategory())){ //this section is turned off:
					continue;
				}
			}
			sectionsInUse.add(db.getCategory());
			if(!adapter.getSectionNames().contains(db.getCategory())){
				ArrayList<DB> list = new ArrayList<DB>();
				list.add(db);
				DBFileAdapter testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
				adapter.addSection(db.getCategory(), testAdapter);
			}//if contains category
		}//for DBs
		ArrayList<String> ListSections = (ArrayList<String>) adapter.getSectionNames().clone();
		for(String sec : ListSections){
			if( !sectionsInUse.contains(sec) && !sec.equalsIgnoreCase("Favourites")){	
				adapter.removeSection(sec);
			}
		}//for sections
		lv.setAdapter(adapter);
	}//updateSections

	/**
	 * Builds the filter list.
	 */
	private void buildFilter(){
		SharedPreferences settings = getSharedPreferences(CATEGORY_SETTINGS, 0);
		for (String cat : categories){
			boolean checked = settings.getBoolean(cat, true);
			CheckedCategories.put(cat, checked);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (requestCode == 2){
			debug(0, "Code Two!");
			menu.removeItem(R.id.planMenuNewPoi);
			menu.removeItem(R.id.planMenuSharePois);
			menu.removeItem(R.id.planMenuUpdatePois);
			menu.removeItem(R.id.planMenuAddPois);
		}
//		else if (requestCode == SHARE_DB)
//		{	
//			menu.removeItem(R.id.planMenuAddDBs);
//			menu.removeItem(R.id.planMenuNewDB);
//			menu.removeItem(R.id.planMenuUpdateDBs);
//		}
//		else if(requestCode == DOWNLOAD_DB){
//			menu.removeItem(R.id.planMenuAddDBs);
//			menu.removeItem(R.id.planMenuNewDB);
//			menu.removeItem(R.id.planMenuShareDBs);
//			menu.removeItem(R.id.planMenuFilter);
//		}
//		else if(requestCode == PlanTabTrip.ADD_TO_TRIP  || requestCode == TripListActivity.ADD_TO_TRIP){
//			menu.removeItem(R.id.planMenuNewDB);
//			menu.removeItem(R.id.planMenuShareDBs);
//			menu.removeItem(R.id.planMenuUpdateDBs);
//		}
//		else
//		{
//			menu.removeItem(R.id.planMenuAddDBs);
//		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.setGroupVisible(R.id.planMenuGroupTrip, false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

//		if(item.getItemId() == R.id.planMenuNewDB){
//			Intent newDB = new Intent(ImportTabLocal.this, NewDBActivity.class);
//			startActivity(newDB);
//		}
//
//		if(item.getItemId() == R.id.planMenuFilter)
//		{
//			categories = FileSystemConnector.getInstance(this).getUniqueCategoryNames();
//			Collections.sort(categories);
//
//			AlertDialog.Builder alert = new AlertDialog.Builder(this);
//			alert.setTitle("Filter");
//			ArrayList<String> cat = (ArrayList<String>) categories.clone();
//			cat.add(0, "Favourites");
//
//
//			boolean[] CheckedCat = new boolean[cat.size()];
//			for (String c : cat)
//			{
//				if(CheckedCategories.get(c) == null)
//				{
//					CheckedCategories.put(c, true);
//				}
//				CheckedCat[cat.indexOf(c)] = CheckedCategories.get(c);
//			}
//
//			String[] array = new String[cat.size()];
//			cat.toArray(array);
//			alert.setMultiChoiceItems(array, CheckedCat, this);
//			alert.setPositiveButton("OK", this);
//			alert.create();
//			alert.show();
//		}
//
//		if(item.getItemId() == R.id.planMenuUpdateDBs)
//		{
//			if(requestCode == DOWNLOAD_DB){
//				if (downloadedDBs==null){
//					Toast.makeText(this, "No locations selected", Toast.LENGTH_LONG).show();
//					return false;
//				}else {
//					int[] res = du.storeDBs(downloadedDBs);
//					Toast.makeText(context, res[0]+" locations added, "+res[1]+" locations updated", Toast.LENGTH_LONG).show();
//				}
//				finish();
//			}else {				
//				Intent downloadDB= new Intent(ImportTabLocal.this, ImportTabLocal.class);
//				downloadDB.putExtra("requestCode", DOWNLOAD_DB);
//				startActivityForResult(downloadDB, DOWNLOAD_DB);
//			}
//		}
//
//		if(item.getItemId() == R.id.planMenuShareDBs)
//		{
//			if(requestCode == SHARE_DB){
//				if (shareDBs==null){
//					Toast.makeText(this, "No locations selected", Toast.LENGTH_LONG).show();
//					return false;
//				}else {
//					Sharing.send(this, shareDBs);
//					shareDBs = null;
//				}
//				finish();
//			}else {
//				Intent shareDB= new Intent(ImportTabLocal.this, ImportTabLocal.class);
//				shareDB.putExtra("requestCode", SHARE_DB);
//				startActivityForResult(shareDB, SHARE_DB);
//			}
//		}
//
//		if(item.getItemId() == R.id.planMenuAddDBs)
//		{
//			if(requestCode == PlanTabTrip.ADD_TO_TRIP || requestCode == TripListActivity.ADD_TO_TRIP){
//				if (selectedDBs==null){
//					Toast.makeText(this, "No locations selected", Toast.LENGTH_LONG).show();
//					return false;
//				}else {
//					for (DB p : selectedDBs) {
//						trip.addDB(p);
//						FileSystemConnector.getInstance(this).addDBToTrip(trip, p);						
//					}
//					Toast.makeText(this, selectedDBs.size() + " locations added to " + trip.getLabel() + ".", Toast.LENGTH_LONG).show();
//					selectedDBs = null;
//				}
//				Intent resultIntent = new Intent();
//				resultIntent.putExtra(IntentPassable.TRIP, trip);
//				setResult( Activity.RESULT_OK, resultIntent );
//				finish();
//			}
//		}

		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		if(l.getAdapter().getItemViewType(pos) == SeparatedListAdapter.TYPE_SECTION_HEADER){
			//Pressing a header
			debug(0, "Pressed a header... Dummy!");
			return;
		}
		DB d = (DB) l.getAdapter().getItem(pos);

		if (requestCode == 3){//NewPoiActivity.CHOOSE_DB){
			Intent resultIntent = new Intent();
			//resultIntent.putExtra(IntentPassable.DB, p);
			debug(0, "I just found DB "+d);
			setResult( Activity.RESULT_OK, resultIntent );
			finish();
			return;
		}


//		Intent details = new Intent(ImportTabLocal.this, DBDetailsActivity.class);
		Intent details = new Intent(ImportTabLocal.this, StartActivity.class);
		details.putExtra(IntentPassable.POI, true);

		startActivity(details);
	}

	/**
	 * Shows quick actions when the user long-presses an item.
	 */
	final private class DrawPopup implements AdapterView.OnItemLongClickListener {
		public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {

			if(parent.getAdapter().getItemViewType(pos) == SeparatedListAdapter.TYPE_SECTION_HEADER)
			{
				Intent showInMap = new Intent(ImportTabLocal.this, MapsActivity.class);
				Adapter sectionAd = adapter.getAdapter(parent.getAdapter().getItem(pos).toString());
				ArrayList<DB> selectedDBs = new ArrayList<DB>();
				for (int i = 0; i < sectionAd.getCount(); i++)
				{
					selectedDBs.add((DB) sectionAd.getItem(i));
				}
				//showInMap.putParcelableArrayListExtra(IntentPassable.DBLIST, selectedDBs);
				startActivity(showInMap);
				return true;
			}

			final DB	d 			= (DB) parent.getAdapter().getItem(pos);
			final AdapterView<?> par = parent;
			final int[] xy 			= new int[2]; v.getLocationInWindow(xy);
			final Rect rect 		= new Rect(	xy[0], 
					xy[1], 
					xy[0]+v.getWidth(), 
					xy[1]+v.getHeight()
				);

			final QuickActionPopup qa = new QuickActionPopup(ImportTabLocal.this, v, rect);
			//Drawable addToTripIcon	= res.getDrawable(android.R.drawable.ic_menu_add);
			Drawable mapviewIcon	= res.getDrawable(android.R.drawable.ic_menu_mapmode);
			Drawable directIcon		= res.getDrawable(android.R.drawable.ic_menu_directions);
			//Drawable shareIcon		= res.getDrawable(android.R.drawable.ic_menu_share);
			Drawable deleteIcon		= res.getDrawable(android.R.drawable.ic_menu_delete);

			// Declare quick actions 
			Drawable	favIcon	= res.getDrawable(R.drawable.favstar_off);
			qa.addItem(favIcon,	"",	new OnClickListener(){
				public void onClick(View view){
					//set as favourite
					DB db = d;
//					db = db.modify().favourite(true).build();
//					FileSystemConnector.getInstance(ImportTabLocal.this).editdb(db);//update db;
//					alldbs.remove(d);
//					alldbs.add(db);
					Toast.makeText(ImportTabLocal.this, db.getLabel() + " added to favourites.", Toast.LENGTH_LONG).show();
					adapter.notifyDataSetChanged();//update list
					qa.dismiss();
				}
			});

			qa.addItem(mapviewIcon,	"Show on map", new OnClickListener(){
				public void onClick(View view){
					Intent showInMap = new Intent(ImportTabLocal.this, MapsActivity.class);
					ArrayList<DB> selectedDBs = new ArrayList<DB>();
					selectedDBs.add(d);
//					showInMap.putParcelableArrayListExtra(IntentPassable.DBLIST, selectedDBs);
					startActivity(showInMap);
					qa.dismiss();
				}
			});

			qa.addItem(directIcon, "Get directions", new OnClickListener(){
				public void onClick(View view){
					//Latitude and longitude for current position
					double slon = userLocation.getLongitude();
					double slat = userLocation.getLatitude();
					//Latitude and longitude for selected DB
//					double dlon = d.getGeoDBnt().getLongitudeE6()/1E6;
//					double dlat = d.getGeoDBnt().getLatitudeE6()/1E6;

					Intent navigate = new Intent(ImportTabLocal.this, NavigateFrom.class);
					navigate.putExtra("slon", slon);
					navigate.putExtra("slat", slat);
//					navigate.putExtra("dlon", dlon);
//					navigate.putExtra("dlat", dlat);
					startActivity(navigate);

					qa.dismiss();
				}
			});

			qa.addItem(deleteIcon, "Delete", new OnClickListener(){
				public void onClick(View view){
					new FileSystemConnector().deleteDB(d);
					updateSections();
					((SeparatedListAdapter)par.getAdapter()).notifyDataSetChanged();
					qa.dismiss();
				}
			});
			qa.show();
			return true;
		}//onItemLongClick
	}//DrawPopup

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode==Activity.RESULT_CANCELED){
			return;
		}
	}//onActivityResult

	/*** Initializes the GPS of the phone.*/
	void initGPS(){
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		onLocationChanged(lastKnownLocation);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}//initGPS, why here??

	@Override
	public void onLocationChanged(Location location) {
		this.userLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, R.string.map_gps_disabled_toast, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Waiting for GPS lock", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * Handles click events in the filter dialog.
	 */
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked){
		@SuppressWarnings("unchecked")
		ArrayList<String> cat = (ArrayList<String>) categories.clone();
		cat.add(0, "Favourites");

		CheckedCategories.remove(cat.get(which));
		CheckedCategories.put(cat.get(which), isChecked);
	}

	/**
	 * Handles the buttons in the filter dialog. 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(DialogInterface dialog, int which){
		//add selection to settings:
		SharedPreferences settings = getSharedPreferences(CATEGORY_SETTINGS, 0);
		SharedPreferences.Editor editor = settings.edit();
		//editor.putBoolean("key", value);

		ArrayList<String> cat = (ArrayList<String>) categories.clone();
		cat.add(0, "Favourites");

		for (String title : cat){
			boolean isChecked = CheckedCategories.get(title);

			//preferences:
			editor.putBoolean(title, isChecked);

			if( !isChecked){
				if(adapter.getSectionNames().contains(title))
					adapter.removeSection(title);
			}else{
				ArrayList<DB> list = new ArrayList<DB>();

				for (DB db : allDBs){
					if (title.equals("Favourites") ){ //add to favorite section
						list.add(db);
					}else if(db.getCategory().equals(title)){
						list.add(db);
					}
				}//for DBs
				DBFileAdapter testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
				adapter.addSection(title, testAdapter);
			}//if checked
		}//for categories

		// Commit the edits!
		editor.commit();

		lv.setAdapter(adapter);
	}//onClick

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}//ImportTabLocal
