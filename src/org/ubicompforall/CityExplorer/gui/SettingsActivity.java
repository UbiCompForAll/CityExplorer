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

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;
import org.ubicompforall.CityExplorer.map.LocationActivity;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class SettingsActivity extends StartActivity implements OnClickListener, LocationListener{
	

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

		setButtonListeners(STARTBUTTONS, STARTBUTTON_IDS);
		RelativeLayout start = (RelativeLayout) findViewById(R.id.startView);
		//(Re-) Set button functionality
		if (start != null){
			Button b1 = (Button) findViewById(R.id.startButton1);
			b1.setText( getResources().getString(R.string.importdata) );
			Button b2 = (Button) findViewById(R.id.startButton2);
			b2.setText( getResources().getString(R.string.setlocation));
			//start.removeView( findViewById(R.id.startButton3) );
			Button b3 = (Button) findViewById( R.id.startButton3 );
			b3.setText( getResources().getString( R.string.preferences ) );
		}else{
			debug(0, "Couldn't find the startview in SettingsActivity~75");
		}
		//initGPS(); //RS-111208 Move to CityExplorer.java Application (Common for all activities)
		//startActivity(new Intent(StartActivity.this, ImportActivity.class));
	}//onCreate

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug



	/***
	 * Settings Menu Buttons: 1) Import, 2) Set Location 3) ?
	 */
	@Override
	public void onClick(View v) {
		//debug(0, "Clicked: "+v);
		if (v.getId() == R.id.startButton1){
			startActivity( new Intent( this, ImportActivity.class));

		}else if (v.getId() == R.id.startButton2){
			startActivity( new Intent( this, LocationActivity.class));

		}else if (v.getId() == R.id.startButton3){
			startActivity( new Intent( this, MyPreferencesActivity.class));

		}else{
			debug(0, "Unknown button clicked: "+v);
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

}//class


