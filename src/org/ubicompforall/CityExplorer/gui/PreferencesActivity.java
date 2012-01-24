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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;
import org.ubicompforall.CityExplorer.map.LocationActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TextView;

public class PreferencesActivity extends Activity implements OnClickListener{ //Based on ImportActivity

	//Activity fields
	SharedPreferences settings;
	TextView tv;
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		debug(0, "create");
		super.onCreate( savedInstanceState );
		setContentView( R.layout.preferencesview );
		init();
	}//onCreate

	@Override
	public void onResume(){
		debug(0, "resume");
		super.onResume( );
		initLocation();
	} // onResume
	
	private static void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	/**
	 * Initializes the activity screen etc.
	 */
	private void init() {
		settings = getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0);

		initDbUrl();
		initLocation();
	}//init


	private void initDbUrl() {
		String url = settings.getString(CityExplorer.URL, CityExplorer.RUNE_URL );
		tv = null;//(TextView) findViewById(R.id.pref_url);
		if (tv==null){	debug(0, "where is tv pref_url ? " );
		}else{
			tv.setText( url );
		}
	} // initDbUrl


	private void initLocation() {
		//Write default lat/lng location
		int lat = settings.getInt( CityExplorer.LAT, CityExplorer.TRONDHEIM_LAT );
		int lng = settings.getInt( CityExplorer.LNG, CityExplorer.TRONDHEIM_LNG );
		debug(0, "lat is "+lat );
		debug(0, "lng is "+lng );
		String address = getAddress( lat, lng, this );
		
		//Print prefs, and register click-listener(this)
		TableLayout tl = (TableLayout) findViewById(R.id.pref_location);
		tl.setOnClickListener( this );

		//Latitude
		tv = (TextView) findViewById(R.id.pref_lat);
		if (tv==null){	debug(0, "where is tv?" );
		}else{
			tv.setText( Integer.toString( lat )+" (change: Push here)" );
		}
		//Longitude
		tv = (TextView) findViewById(R.id.pref_lng);
		if (tv==null){	debug(0, "where is tv?" );
		}else{
			tv.setText( Integer.toString(lng)+" (change: Push here)" );
		}
		//Place-name
		tv = (TextView) findViewById(R.id.pref_loc);
		if (tv==null){	debug(0, "where is tv?" );
		}else{
			tv.setText( address );
		}
	} // initLocation

	/***
	 * Get address for a given lat/lng pair
	 * @param lat
	 * @param lng
	 * @return The best reversed geo-coding guess as a string
	 */
	public static String getAddress(int lat, int lng, Context context) {
		String address="";
		try {
			Geocoder geocoder = new Geocoder( context, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(lat/1E6, lng/1E6, 1);
			if ( addresses.size() > 0 ){
				address = addresses.get(0).getSubAdminArea()+" - "+addresses.get(0).getAdminArea();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address;
	} // getAddress


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pref_location){
			debug(0, "view clicked was LOCATION");
			startActivity( new Intent( this, LocationActivity.class));
		} // if location was clicked
	} //onClick

}//class
