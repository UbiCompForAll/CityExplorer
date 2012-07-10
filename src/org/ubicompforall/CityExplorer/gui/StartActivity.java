/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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
 *
 */

package org.ubicompforall.CityExplorer.gui;

import java.io.File;
import java.util.ArrayList;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;
import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.DatabaseInterface;
import org.ubicompforall.CityExplorer.data.IntentPassable;
import org.ubicompforall.CityExplorer.data.Poi;
import org.ubicompforall.CityExplorer.map.MapsActivity;
import org.ubicompforall.CityExplorer.gui.MyPreferencesActivity;
import org.ubicompforall.descriptor.UbiCompDescriptorPackage;
import org.ubicompforall.simplelanguage.SimpleLanguagePackage;
import org.ubicompforall.simplelanguage.UserService;
//import org.ubicompforall.ubicomposer.android.TaskListActivity;
//import org.ubicompforall.ubicomposer.util.UserServiceUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends Activity implements OnClickListener{
	//RS-111122, "implements LocationListener{" move to CityExplorer.java (Application
	
	/***
	 * The current db connection
	 */
	static DatabaseInterface db;

	/**
	 * The buttons in this activity.
	 */
	protected static final Button[] STARTBUTTONS = new Button[3];
	protected static final int[] 	STARTBUTTON_IDS = new int[]{R.id.startButton1, R.id.startButton2, R.id.startButton3};

	/**
	 * The user's current location.
	 */
	protected static Location userLocation; // Inherited by SettingsActivity

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    debug(3, "Go!" );
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startlayout);
		setButtonListeners(STARTBUTTONS, STARTBUTTON_IDS);
		debug(1, "Download folder is "+CityExplorer.SHARED_FILE_PATH ); //Not used?
		
		// TODO: FOR DEBUGGING
		//startActivity(new Intent( this, LocationActivity.class) );
		//startActivity(new Intent( this, ImportDB.class) );
		//startActivity(new Intent( this, PlanActivity.class) );
		// TODO: FOR DEBUGGING

		//initGPS(); //RS-111208 Move to CityExplorer.java Application (Common for all activities)
		userLocation = verifyUserLocation( userLocation, this );		//Init userLocation
	}//onCreate

	@Override
	protected void onResume() {
		super.onResume();
		setButtonListeners(STARTBUTTONS, STARTBUTTON_IDS);
	}//onResume


	private static void debug(int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	public void setButtonListeners(Button[] buttons, int[] buttonIds) {
		if (buttons.length == buttonIds.length){
			for(Integer b=0; b<buttonIds.length; b++){
				buttons[b] = (Button) findViewById(buttonIds[b]);
				if (buttons[b] != null){
					buttons[b].setOnClickListener(this);
				}else{
					debug(0, "BUTTON["+(b+1)+"] was NULL for "+buttons);
				}//if button not found
			}//for each startButton
		}else{
			debug(0, "StartActivity.java: Mismatch between buttons[] and buttonsIds[]");
		}
		if ( ! CityExplorer.ubiCompose ){
			Button b2 = (Button) findViewById(R.id.startButton2);
			b2.setText( getResources().getString(R.string.showMaps));
		}//if ubiComposer enabled
	}//setButtonListeners

	@Override
	public void onClick(View v) {
		debug(2, "Clicked: "+v );
		if (v.getId() == R.id.startButton1){  // Button PLAN TOUR
						
			startActivity(new Intent( this, PlanActivity.class));

		}else if (v.getId() == R.id.startButton2){ // Button COMPOSE or SHOW MAPS depending of flag settings
			if ( CityExplorer.ubiCompose ){
				// Composition settings
				String fileName = initComposition (this);
				
		    	if ( fileName != null) {
//		    		Intent taskListIntent = new Intent(this, TaskListActivity.class);
//		    		taskListIntent.setData(Uri.fromFile(getFileStreamPath(fileName)));
//		    		this.startActivity(taskListIntent);
		    	}
				
			}else{
				// Button EXPLORE CITY MAP
				//Starting the maps activity is too slow!!! How to show a progress bar etc.?
				//Toast.makeText(this, "Loading Maps...", Toast.LENGTH_LONG).show();
				setProgressBarVisibility(true);
				exploreCity(this);
			}

		}else if (v.getId() == R.id.startButton3){ // Button SETTINGS
			Intent settingsActivity = new Intent( this, SettingsActivity.class );
			startActivity( settingsActivity );

		}else{
			debug(0, "Unknown button clicked: "+v);
		}//if v== button-Plan|Explore|Import
	}//onClick

	// FOR DEBUGGING
	//			ExportImport.send(this, poiList);
	//settingsActivity.putParcelableArrayListExtra( IntentPassable.POILIST, new ArrayList<Poi>() );
	//			startActivity(new Intent( this, ExportImport.class ));
//TEST CODE
//	try {
//	wait(500);
//} catch (InterruptedException e) {
//	e.printStackTrace();
//}

	/***
	 * This method should be run in a background Thread because db.getAllPois is quite time-consuming!
	 */
// This method is static since it is called in PlanPoiTab
// Should be moved to PlanPoiTab if no longer used here.
	public static void exploreCity( Context context) {
		debug(0, "Clicked ExploreMap Button...");
		if (userLocation == null){
			debug(0, "userLocation is null!!!");
			Toast.makeText(context, R.string.map_gps_disabled_toast, Toast.LENGTH_LONG).show();
		}
		userLocation = verifyUserLocation( userLocation, context );
		Intent showInMap = new Intent(context, MapsActivity.class);

		db = DBFactory.getInstance(context);	// Already initialized in the CityExplorer.java application
		debug(0, "Getting all POIs from "+db );
		ArrayList<Poi> poiList = db.getAllPois();
		ArrayList<Poi> poiListNearBy = new ArrayList<Poi>();

		for (Poi p : poiList) {
			double dlon = p.getGeoPoint().getLongitudeE6()/1E6;
			double dlat = p.getGeoPoint().getLatitudeE6()/1E6;

			Location dest = new Location("dest");
			dest.setLatitude(dlat);
			dest.setLongitude(dlon);

			if ( userLocation != null  &&  userLocation.distanceTo(dest) <= 5000 ){
				poiListNearBy.add(p);
			}else{ // if POIsNearBy
				debug(0, "User location is "+userLocation );
			}
		}//for POIs
		
		showInMap.putParcelableArrayListExtra(IntentPassable.POILIST, poiListNearBy);
		context.startActivity(showInMap);
	}//exploreCity
	
	public static Location verifyUserLocation( Location userLocation, Context context ) {
		int[] lat_lng = MyPreferencesActivity.getLatLng ( context );

		if( userLocation == null){
			debug(2, "No GPS: Proceede with lastknown location (GSM/WiFi/GPS) from preferences");
			userLocation = new Location("");
			userLocation.setLatitude( lat_lng [0]/1E6 );	// Store current latitude location
			userLocation.setLongitude( lat_lng [1]/1E6 );	// Store current longitude location
		}//userLocation == null, Check out GPS setting in CityExplorer.java

		debug(2, "lat_lng is "+ lat_lng[0] + ", "+ lat_lng[1] );
		return userLocation;
	}//verifyUserLocation

	/***
	 * This method prepares the file needed for composition before calling the composition tool. 
	 * The composition descriptors were copied  
	 */
	public String initComposition (Context context) {
		
		// Build name of user service file
		String dbFilename = MyPreferencesActivity.getSelectedDbName (context); 		// Get current DbFileName
		String userServiceFilename = dbFilename.replace(".sqlite", "UserService.simplelanguage");
		
		// Check whether or not a composition file exists
		File file = this.getFileStreamPath(userServiceFilename);

//		if(! file.exists()) {		// create the composition file
//			SimpleLanguagePackage pkg = SimpleLanguagePackage.eINSTANCE;
//			UbiCompDescriptorPackage pkg2 = UbiCompDescriptorPackage.eINSTANCE;
//	    	UserService userService = UserServiceUtils.newUserService();
//	    	userService.setName(dbFilename + "user service");
//	    	UserServiceUtils.addLibraryToUserService(
//	    		context.getFileStreamPath("Communication.ubicompdescriptor").getAbsolutePath(), userService);
//	    	UserServiceUtils.addLibraryToUserService(
//	    			context.getFileStreamPath("CityExplorer.ubicompdescriptor").getAbsolutePath(), userService);
//			UserServiceUtils.saveUserService(getFileStreamPath(userServiceFilename).getAbsolutePath(), userService);
//		}
		
		return userServiceFilename;
		
	}//initComposition

	/* RS-111122: Moved to MapsActivity.java common Application settings */
	/**
	 * Initializes the GPS on the device.		//Move to MapsActivity
	 **/
//	void initGPS(){
//		// Acquire a reference to the system Location Manager
//		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//		//Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		//onLocationChanged(lastKnownLocation);
//		// Register the listener with the Location Manager to receive location updates
//		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//	}//initGPS

	/* TODO Auto-generated method stub
	// TODO Try to run slow methods in background Threads!
	//Moving to CityExplorer.java common Application settings?
	@Override
	public void onLocationChanged(Location location) {
		this.userLocation = location;
	}
	*/
}//class

