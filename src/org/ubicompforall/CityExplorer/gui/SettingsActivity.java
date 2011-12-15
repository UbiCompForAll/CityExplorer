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

import org.ubicompforall.CityExplorer.R;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class SettingsActivity extends StartActivity implements OnClickListener, LocationListener{
	private static final String C = "CityExplorer";

	//RS-111122, "implements LocationListener{" moved to CityExplorer.java

	/**
	 * The buttons in this activity. Inherited from StartActivity
	private static final Button[] STARTBUTTONS = new Button[3];
	private static final int[] 	STARTBUTTON_IDS = new int[]{R.id.startButton1, R.id.startButton2, R.id.startButton3};
	 */
	/**
	 * The user's current location. Inherited from StartActivity
	private Location userLocation;
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startlayout);
		Log.d("CityExplorer", "77~SettingsActivity");

		setButtonListeners(STARTBUTTONS, STARTBUTTON_IDS);
		//DEBUG: Remove startButton2 & 3 from startLayoutview
		RelativeLayout start = (RelativeLayout) findViewById(R.id.startView);
		if (start != null){
			start.removeView( findViewById(R.id.startButton3) );
			Button b1 = (Button) findViewById(R.id.startButton1);
			b1.setText( getResources().getString(R.string.importdata) );
			Button b2 = (Button) findViewById(R.id.startButton2);
			b2.setText( getResources().getString(R.string.setlocation));
		}else{
			Log.d(C, "Couldn't find the startview in SettingsActivity~75");
		}
		//initGPS(); //RS-111208 Move to CityExplorer.java Application (Common for all activities)
		//startActivity(new Intent(StartActivity.this, ImportActivity.class));
	}//onCreate

	@Override
	public void onClick(View v) {
		Log.d(C, "Clicked: "+v);
		if (v.getId() == R.id.startButton1){
			startActivity(new Intent(SettingsActivity.this, ImportActivity.class));

		}else if (v.getId() == R.id.startButton2){
			Log.d(C, "Clicked: Button2... Empty");

		}else if (v.getId() == R.id.startButton3){
			Log.d(C, "Clicked: Button2... Empty");

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
