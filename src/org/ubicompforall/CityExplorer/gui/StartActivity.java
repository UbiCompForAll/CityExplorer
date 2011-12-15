/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:
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
 *
 */

package org.ubicompforall.CityExplorer.gui;

import java.util.ArrayList;

import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.DatabaseInterface;
import org.ubicompforall.CityExplorer.data.IntentPassable;
import org.ubicompforall.CityExplorer.data.Poi;
import org.ubicompforall.CityExplorer.map.MapsActivity;

import org.ubicompforall.CityExplorer.R;

import android.app.Activity;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class StartActivity extends Activity implements OnClickListener, LocationListener{
	private static final String C = "CityExplorer";

	//RS-111122, "implements LocationListener{" moved to CityExplorer.java

	/**
	 * The buttons in this activity.
	 */
	protected static final Button[] STARTBUTTONS = new Button[3];
	protected static final int[] 	STARTBUTTON_IDS = new int[]{R.id.startButton1, R.id.startButton2, R.id.startButton3};
	
	//private static final Button[] SETTINGBUTTONS = new Button[1];
	//private static final int[] 	SETTINGBUTTON_IDS = new int[]{R.id.startButton3};

	/**
	 * The user's current location.
	 */
	protected Location userLocation; // Inherited by SettingsActivity

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startlayout);
		Log.d("CityExplorer", "StartActivity~72");

		setButtonListeners(STARTBUTTONS, STARTBUTTON_IDS);

		initGPS(); //RS-111208 Move to CityExplorer.java Application (Common for all activities)

		
		//startActivity(new Intent(this, ImportActivity.class));
		
		
	}//onCreate

	@Override
	protected void onResume() {
		super.onResume();
		setButtonListeners(STARTBUTTONS, STARTBUTTON_IDS);
	}//onResume

	public void setButtonListeners(Button[] buttons, int[] buttonIds) {
		if (buttons.length == buttonIds.length){
			for(Integer b=0; b<buttonIds.length; b++){
				buttons[b] = (Button) findViewById(buttonIds[b]);
				if (buttons[b] != null){
					buttons[b].setOnClickListener(this);
				}else{
					Log.d("CityExplorer", "StartActivity~100: BUTTON["+b+"] was NULL for "+buttons);
				}//if button not found
			}//for each startButton
		}else{
			Log.d(C, "StartActivity.java: Mismatch between buttons[] and buttonsIds[]");
		}
	}//setStartButtons

	@Override
	public void onClick(View v) {
		Log.d(C, "Clicked: "+v);
		if (v.getId() == R.id.startButton1){
			startActivity(new Intent(StartActivity.this, PlanActivity.class));

		}else if (v.getId() == R.id.startButton2){
			Log.d(C, "Clicked: ExploreButton...");
			if(userLocation != null){
				Intent showInMap = new Intent(StartActivity.this, MapsActivity.class);

				DatabaseInterface db = DBFactory.getInstance(this);
				ArrayList<Poi> poiList = db.getAllPois();
				ArrayList<Poi> poiListNearBy = new ArrayList<Poi>();

	//			ExportImport.send(this, poiList);
	//			startActivity(new Intent(StartActivity.this, ExportImport.class));
				for (Poi p : poiList) {
					double dlon = p.getGeoPoint().getLongitudeE6()/1E6;
					double dlat = p.getGeoPoint().getLatitudeE6()/1E6;

					Location dest = new Location("dest");
					dest.setLatitude(dlat);
					dest.setLongitude(dlon);

					if ( userLocation.distanceTo(dest) <= 5000 ){
						poiListNearBy.add(p);
					}
				}//for POIs
				if(poiListNearBy.size()>0){
					showInMap.putParcelableArrayListExtra(IntentPassable.POILIST, poiListNearBy);
					startActivity(showInMap);
				}//if POIsNearBy
			}else{ //userLocation == null, Check out GPS setting in CityExplorer.java
				Toast.makeText(this, R.string.map_gps_disabled_toast, Toast.LENGTH_LONG).show();
				Log.d("CityExplorer", " did you forget to activate GPS??! ");
				Log.d("CityExplorer", " TODO: Proceede with lastknown location (GSM/WiFi/GPS) from preferences");
			}//if userLocation, else improvise!

		}else if (v.getId() == R.id.startButton3){
			startActivity(new Intent(StartActivity.this, SettingsActivity.class));

		}else{
			Log.d(C, "Unknown button clicked: "+v);
		}//if v== button-Plan|Explore|Import
	}//onClick


	/* RS-111122: Moved to CityExplorer.java common Application settings */
	/**
	 * Initializes the GPS on the device.
	 * */
	void initGPS()
	{
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//TODO: change to gps
		onLocationChanged(lastKnownLocation);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	//Moved to CityExplorer.java common Application settings
	@Override
	public void onLocationChanged(Location location) {
		this.userLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}//class


/*
Old Student Code:
buttonPlan	 = (Button) findViewById(R.id.startButtonPlan);
if (buttonPlan != null){
	buttonPlan.setOnClickListener(this);
}else{
	Log.d("CityExplorer", "Plan-button was NULL ~77");
}
buttonExplore = (Button) findViewById(R.id.startButtonExplore);
if (buttonExplore != null){
	buttonExplore.setOnClickListener(this);
}else{
	Log.d("CityExplorer", "Explorer-button was NULL ~83");
}
buttonSettings = (Button) findViewById(R.id.startButtonSettings);
if (buttonSettings != null){
	buttonSettings.setOnClickListener(this);
}else{
	Log.d("CityExplorer", "Import-button was NULL ~90");
}
*/
