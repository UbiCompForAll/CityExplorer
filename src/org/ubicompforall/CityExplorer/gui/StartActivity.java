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
import java.io.FilenameFilter;
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
import org.ubicompforall.library.communication.CommunicationFactory;
import org.ubicompforall.simplelanguage.SimpleLanguagePackage;
import org.ubicompforall.simplelanguage.UserService;
import org.ubicompforall.simplelanguage.runtime.RuntimeEnvironment;
import org.ubicompforall.ubicomposer.android.ModelUtils;
import org.ubicompforall.ubicomposer.android.TaskListActivity;
import org.ubicompforall.ubicomposer.util.UserServiceUtils;
import org.ubicompforall.ubicomprun.android.RuntimeEnvironmentInstance;
import org.ubicompforall.ubicomprun.android.UserServiceExecutionService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity implements OnClickListener{
	
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
		
		userLocation = verifyUserLocation (userLocation, this );		//Init user location
	}//onCreate

	@Override
	protected void onResume() {
		super.onResume();
		setButtonListeners(STARTBUTTONS, STARTBUTTON_IDS);
	}//onResume

	/***
	 * Simply call debug method define by the City Explorer application
	 */
	private static void debug(int level, String message ) {
		CityExplorer.debug (level, message );		
	} //debug

	/***
	 * Associate listeners to buttons 
	 */	
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
		
		// Two configurations of the application are available: 
		// with/without support for user service composition
		if ( ! CityExplorer.ubiCompose ){
			Button b2 = (Button) findViewById(R.id.startButton2);
			b2.setText( getResources().getString(R.string.showMaps));
		}
		
	}//setButtonListeners

	/***
	 * Called when a button in a view has been clicked 
	 */	
	@Override
	public void onClick(View v) {
		debug(2, "Clicked: "+v );
		if (v.getId() == R.id.startButton1){  		// Button PLAN TOUR
						
			startActivity(new Intent( this, PlanActivity.class));

		} else if (v.getId() == R.id.startButton2){ // Button PERZONALIZE or SHOW MAP depending of flag settings
			
			if ( CityExplorer.ubiCompose ){ 		// PERSONALIZE i.e. support for user service composition
//TODO: The Perzonalize Activity may be removed later on (this requires to composition tool to support start/stop
				startActivity( new Intent( this, PersonalizeActivity.class));

			}else{									// SHOW MAP
//TODO: Starting the maps activity is too slow!!! How to show a progress bar etc.?
				//Toast.makeText(this, "Loading Maps...", Toast.LENGTH_LONG).show();
				setProgressBarVisibility(true);
				exploreCity(this);
			}

		} else if (v.getId() == R.id.startButton3){ // Button SETTINGS
			Intent settingsActivity = new Intent( this, SettingsActivity.class );
			startActivity( settingsActivity );

		} else{
			debug(0, "Unknown button clicked: "+v);
		}//if v== button-Plan|Explore|Import
	}//onClick


	/***
	 * Get all PoIs from db and show on map
	 */
// This method is static since it is called in PlanPoiTab (SHOW MAP is a menu option in this activity)
// Should be moved to PlanPoiTab in the case SHOW MAP is removed from the Start activity (only PESONALIZE available)
	
//TODO: This method should be run in a background Thread because db.getAllPois is quite time-consuming!
	
//TODO: Apply filtering
	public static void exploreCity (Context context) {
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
	
	/***
	 * Get user location from preferences, if not already set.
	 */
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


}//class



