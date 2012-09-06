/**
 * @contributor(s): Rune Sætre (NTNU), Jacqueline Floch (SINTEF)
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

package org.ubicompforall.cityexplorer.buildingblock;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.data.SQLiteConnector;
import org.ubicompforall.simplelanguage.Task;
import org.ubicompforall.simplelanguage.runtime.TaskTrigger;
import org.ubicompforall.simplelanguage.runtime.TriggerMonitor;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class SomePoiTriggerMonitor implements TriggerMonitor, AndroidBuildingBlockInstance, LocationListener{
	Context ctx;	//Which "activity" is running/executing this Trigger
	private LocationManager locationManager;	//Keep track of the users location
	TaskTrigger taskTrigger;
	Task task;
	
	//Move to final static Contract class
	public static final String SCHEME = "content";
	public static final String AUTHORITY = "org.ubicompforall.cityexplorer.provider";
	public static final String POI_TABLE = "PoiTable";
	//private static final String _ID_COL = "_ID_COL";

	//public static final Uri CONTENT_URI = Uri( AUTHORITY, POI_TABLE );
	public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).appendPath(POI_TABLE).build();
	private static final Integer PROXIMITY_DISTANCE = 2000;	//Warn if a POI is within 1000 meters

	//METHODS

	/***
	 * Rune: I think this method should be called onCreate, to be more in-line with the Android/Mobile Terminology
	 */
	@Override
	public void setContext(Context context) {
		this.ctx = context;
		debug(1, "Is this the same as onCreate?");
		locationManager = (LocationManager) ctx.getSystemService( Context.LOCATION_SERVICE );
	}//AndroidBuildingBlockInstance.setContext

	
	/***
	 * task is the whole composition
	 * taskTrigger is for something completely different: 
	 */
	@Override
	public void startMonitoring( Task task, TaskTrigger taskTrigger ) {
		this.taskTrigger = taskTrigger;
		this.task = task;

		// TODO: Check whether or not GPS is on
		Toast.makeText(ctx, "Make sure GPS is on for "+ task.getName(), Toast.LENGTH_LONG).show();

		debug(1, "context is "+ ctx );
		
		// Get location from Android
		Location lastKnownLocation = locationManager.getLastKnownLocation( getProvider() );
		onLocationChanged( lastKnownLocation );
		
		// Register the listener with the Location Manager to receive location updates
		//Make sure onLocationChanged is called every time the user moves
		locationManager.requestLocationUpdates( getProvider(), 0, 0, this);
	}//TriggerMonitor.startMonitoring

	
	@Override
	public void stopMonitoring() {
	    locationManager.removeUpdates(this);
	}//TriggerMonitor.stopMonitoring

	
	public void debug(int level, String str){
		CityExplorer.debug(level,str);
	}
	
	/***
	 * @return An array with all poi ids => lat/lng from the __current__ (?) database
	 */
	public TreeMap<String, Double[]>
	 getAllPois(){
		TreeMap<String, Double[]> pois = new TreeMap<String, Double[] >();
		String error = "";
		
		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = {
				SQLiteConnector._ID_COL,		// Contract class constant for the _ID_COL column name
				SQLiteConnector.POI_NAME_COL,	// Contract class constant for the name column name
				SQLiteConnector.LAT_COL,		// Contract class constant for the location lat column name
				SQLiteConnector.LON_COL			// Contract class constant for the location lng column name
		};
		
		String mSelectionClause = "POI.address_id = ADDR._id";	// Define the selection clause string
		String[] mSelectionArgs = null;	// Initializes a null array instead of any selection arguments
		String mSortClause = SQLiteConnector.POI_NAME_COL;		// Define the sorting clause string

		debug(0, "Looking for "+ CONTENT_URI );
		Cursor mCursor = null;
		
		// Get data from database
		try{
			mCursor = ctx.getContentResolver().query(
					//UserDictionary.Words.CONTENT_URI,   // The content URI of the words table == vnd.android.cursor.dir/vnd.google.userword
					//CONTENT_URI = org.ubicompforall.cityexplorer.provider/P,   // The content URI of the words table
					CONTENT_URI,   // The content URI of the words table
					mProjection,                        // The columns to return for each row
					mSelectionClause,                   // Selection criteria
					mSelectionArgs,                     // Selection criteria
					mSortClause );		// The sort order for the returned rows
	    }catch (SQLiteException e){
	    	e.printStackTrace();
	    	debug(-1, e.getMessage() );
	    	error = e.getMessage();
	    	debug(-1, "ERROR in POI_TABLE select "+mProjection[1]+" etc..." );
	    }//try - catch

		// Add PoIs to pois
		if (mCursor != null){
			while( mCursor.moveToNext() ){
				pois.put( mCursor.getString(1), new Double[]{ mCursor.getDouble(2), mCursor.getDouble(3) } );
				//CityExplorer.debug(0, "stored "+mCursor.getString(1)+" "+mCursor.getDouble(2)+" "+mCursor.getDouble(3) );
			}
		}else{
			debug(-1, "NO Cursor! "+error );
		}
		
		return pois;
	}//getAllPois

	 public SortedMap<Integer,String>
	  getPoiDistancesSorted(Double[] myPos, TreeMap<String, Double[]> pois) {
		TreeMap<Integer, String> distances = new TreeMap<Integer, String>();

		for ( Entry<String, Double[]> name_pos : pois.entrySet() ) {
			float[] results = new float[] { 0, 0, 0 };
			Location.distanceBetween(myPos[0], myPos[1],
					name_pos.getValue()[0], name_pos.getValue()[1], results);
			//debug(0, "Distance from "+myPos[0]+" to "+name_pos.getValue()[0]+" is "+results[0] );
			distances.put( Math.round(results[0]), name_pos.getKey() );
		}
		return distances;
	 }//getPoiDistancesSorted( myPos[latitude,longitude], pois)

	 
// Each time the position changes, get the closest PoI 
	 
	@Override
	public void onLocationChanged( Location location ){
		debug(-1, "Let's go to location changed!: " );
		if ( location != null ){
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			TreeMap<String, Double[]> pois = getAllPois();
			SortedMap <Integer,String> distances = getPoiDistancesSorted ( new Double[]{latitude,longitude}, pois);

// Use loop to get several pois
//			for (Entry<Integer, String> dist_name : distances.entrySet()) {
//				if ( dist_name.getKey() < PROXIMITY_DISTANCE ){
				
				Integer firstKey = distances.firstKey(); 
				if ( firstKey < PROXIMITY_DISTANCE ){		//TODO: proximity distance can be defined as a parameter of Trigger
					debug(1, "Close call: "+ distances.get(firstKey) );
					Map<String, Object> parameterMap = new HashMap<String, Object>();
					parameterMap.put( task.getTrigger().getName()+".poiName", distances.get(firstKey) );
					taskTrigger.invokeTask(task, parameterMap);
				}else{
					debug(1, "NOT CLOSE to any PoI. Closest is "+ distances.get(firstKey) );
				}//Check proximity

			//this.stopMonitoring();	//Stop this type of trigger? No, wait for next...
			//TODO: What about one-shot triggers? Excluding the same POI for a certain amount of time?
		}//if location provided
	}////LocationListener.onLocationChanged
	
	@Override
	public void onProviderDisabled(String provider) {
		debug(0, "Location_Provider DISABLED: "+provider );
		Toast.makeText(ctx, "Disabled: "+provider, Toast.LENGTH_LONG).show();
		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		ctx.startActivity(intent);
	}//LocationListener.onProviderDisabled
	
	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(ctx, "Enabled: "+provider, Toast.LENGTH_SHORT).show(); //
		//ctx.startActivity(new Intent(ctx, LocationPickerActivity.class));
	}//LocationListener.onProviderEnabled

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(ctx, "Provider "+provider+", Status="+status, Toast.LENGTH_SHORT).show();
	}//LocationListener.onStatusChanged

	public void disableProviders( ) {
		locationManager = (LocationManager) ctx.getSystemService( Context.LOCATION_SERVICE );
		locationManager.removeUpdates( this );
		debug(0, "Removed updates for "+locationManager.getProviders(true) );
	}//disableProviders

	/**
	 * Init the Network/GPS/WiFi Location Provider.
	 */
	public void initLocation( Context context ){
		String provider = getProvider();
		debug(0, "Init Location provider: "+provider );
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	
		Location lastKnownLocation = locationManager.getLastKnownLocation( provider );
		//debug(0, "context is "+context );
		//if ( context instanceof CityExplorer ){
			onLocationChanged( lastKnownLocation );
			// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates( provider, 0, 0, this );
		//}
	}//initGPS

	public String getProvider(){
	    locationManager = (LocationManager) ctx.getSystemService( Context.LOCATION_SERVICE );
	    Criteria criteria = new Criteria();
	    //criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    criteria.setAltitudeRequired(false);
	    criteria.setBearingRequired(false);
	    criteria.setCostAllowed(false);
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
	    return locationManager.getBestProvider(criteria, true); //true: Enabled providers only
	}//getProvider
	
	public void retrieveLocation() {
	    String provider = getProvider();
	    if (provider==null){
	    	CityExplorer.debug(-1, "Ooops! Provider == null" );
	    }else{
	    	debug(2, "Using provided: "+provider );
	    	locationManager.requestLocationUpdates(provider, 0, 0, this);
	    }
	}//retrieveLocation
	
}//class PoiTrigger
