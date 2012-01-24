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
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.os.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class MyPreferencesActivity extends Activity implements OnClickListener{ //Based on ImportActivity

	//Activity fields
	static SharedPreferences settings;	// common settings for all the activities in the whole application
	Editor editor;	// Editor for changing the shared preferences
	private EditText url_edit;
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		debug(0, "create");

		setContentView( R.layout.preferencesview );

		settings = getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0);
		editor = settings.edit();	// Remember to commit changes->onPause etc.
		url_edit = (EditText) findViewById( R.id.pref_url );

		init();
	}//onCreate

	@Override
	public void onPause(){
		super.onPause();
		String url = url_edit.getText().toString();
		debug(2, "pause with "+url+", editor is "+editor );
		editor.putString( CityExplorer.URL, url );
		editor.commit();
		debug(2, "committed:"+url_edit.getText().toString() );
	} // onResume
	
	@Override
	public void onResume(){
		super.onResume();
		debug(0, "resume");
		init();
	} // onResume
	
	private static void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug

	
	public static String getDbPath( SharedPreferences settings ){
		if (settings==null){
			debug(0, "Where is settings?" );
			return null;
		}else{
			return settings.getString( CityExplorer.URL, CityExplorer.RUNE_URL );
		}
	} // getDbPath

	/**
	 * Initializes the activity screen etc.
	 */
	private void init() {

		initDbUrl();
		initLocation();
	}//init


	private void initDbUrl() {
		String url = getDbPath( settings );
		url_edit.setText( url );
	} // initDbUrl


	private void initLocation() {
		//Write default lat/lng location
		int lat = settings.getInt( CityExplorer.LAT, CityExplorer.TRONDHEIM_LAT );
		int lng = settings.getInt( CityExplorer.LNG, CityExplorer.TRONDHEIM_LNG );
		debug(2, "lat is "+lat+", lng is "+lng );
		String address = getAddress( lat, lng, this );
		
		//Print prefs, and register click-listener(this)
		TableLayout tl = (TableLayout) findViewById(R.id.pref_location);
		tl.setOnClickListener( this );

		//Latitude
		TextView tv = (TextView) findViewById(R.id.pref_lat);
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
			// if location was clicked
			
		}else if ( v.getId() == R.id.pref_db ){ 
			debug(0, "view clicked was set DB_PATH automaticall" );
			
		}// Switch on different key-press events
	} //onClick

}//class
