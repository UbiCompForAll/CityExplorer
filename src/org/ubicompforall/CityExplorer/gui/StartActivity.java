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
import android.widget.RelativeLayout;
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
	private int[] STARTBUTTONIDS = new int[]{R.id.startButtonPlan, R.id.startButtonExplore, R.id.startButtonSettings};
	private String[] STARTBUTTONNAMES = new String[]{"R.id.startButtonPlan", "R.id.startButtonExplore", "R.id.startButtonSettings"};
	private Button[] STARTBUTTONS = new Button[3];

	/**
	 * The user's current location.
	 */
	private Location userLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startlayout);
		Log.d("CityExplorer", "StartActivity~72");

		setStartButtons();

		initGPS(); //RS-111208 Move to CityExplorer.java Application (Common for all activites)
		//startActivity(new Intent(StartActivity.this, ImportActivity.class));
	}//onCreate

	public void setStartButtons() {
		for(int b=0; b<STARTBUTTONS.length; b++){
			//Log.d(C, "b is "+b);
			STARTBUTTONS[b] = (Button) findViewById(STARTBUTTONIDS[b]);
			if (STARTBUTTONS[b] != null){
				STARTBUTTONS[b].setOnClickListener(this);
			}else{
				Log.d("CityExplorer", STARTBUTTONNAMES[b]+" - button was NULL ~92");
			}//if button not found
		}//for each startButton
	}//setStartButtons

	public int[] getStartButtons() {
		return STARTBUTTONIDS;
	}

	@Override
	public void onClick(View v) {
		Log.d(C, "Clicked: "+v);
		if (v.getId() == R.id.startButtonPlan){
			startActivity(new Intent(StartActivity.this, PlanActivity.class));

		}else if (v.getId() == R.id.startButtonSettings){
			startActivity(new Intent(StartActivity.this, ImportActivity.class));
			//DEBUG: Remove importbutton from startlayoutview
			RelativeLayout sv = (RelativeLayout) findViewById(R.id.startView);
			//sv.removeView(v);

		}else if (v.getId() == R.id.startButtonExplore){
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
