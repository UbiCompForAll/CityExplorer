/**
 * @contributor(s): Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			22 November 2011
 * @revised:		15 December 2011
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
 * This class tracks existing DBs.
 * This class keeps hold of the tabs used in import mode.
 */

package org.ubicompforall.CityExplorer.gui;

import java.io.IOException;
import java.util.Locale;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;
import org.ubicompforall.CityExplorer.data.PoiAddress;
import org.ubicompforall.CityExplorer.map.LocationActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class MyPreferencesActivity extends Activity implements OnClickListener{ //Based on ImportActivity

	//Activity fields
	SharedPreferences settings;	// common settings for all the activities in the whole application
	Editor editor;	// Editor for changing the shared preferences
	private TextView dbName_text;	// cityName_text
	private EditText url_edit;
	
	private static void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug

	public static void storeDbFolderSetting( Context context, String folderName ){
		Editor editor = context.getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0).edit();
		debug(2, "committing db-folder:"+folderName );
		editor.putString( CityExplorer.SETTINGS_DB_FOLDER, folderName ); //BAD BUG FIXED! RS-120608
		editor.commit();
	}//storeCitySettings

	public static void storeDbNameSetting( Context context, String dbFileName ){
		Editor editor = context.getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0).edit();
		debug(2, "committing db:"+dbFileName );
		editor.putString( CityExplorer.SETTINGS_DB_NAME, dbFileName );
		editor.commit();
	}//storeDbSettings

	// END STATIC METHODS
	/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializes the activity screen etc.
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		debug(0, "create");

		setContentView( R.layout.preferencesview );
	}//onCreate

	@Override
	public void onResume(){
		super.onResume();
		debug(1, "resume");

		initLocation();

		settings = getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0);
		editor = settings.edit();	// Remember to commit changes->onPause etc.

		//JF: Don't want one folder pr. city
//		cityName_text = (TextView) findViewById( R.id.pref_city );
//		cityName_text.setText( getSelectedCityName( this ) );
//		findViewById(R.id.cityBrowserButton).setOnClickListener(this);

		dbName_text = (TextView) findViewById( R.id.pref_db );
		dbName_text.setText( getSelectedDbName( this ) );
		findViewById(R.id.DbBrowserButton).setOnClickListener(this);

		url_edit = (EditText) findViewById( R.id.pref_url );
		url_edit.setText( getCurrentDbDownloadURL(this) );
		findViewById(R.id.UrlBrowserButton).setOnClickListener(this);
	} // onResume
	
	@Override
	public void onPause(){
		super.onPause();
		String db = dbName_text.getText().toString();
		String url = url_edit.getText().toString();
		storeDbNameSetting( this, db );	//		editor.putString( CityExplorer.SETTINGS_DB_NAME, db );
		editor.putString( CityExplorer.SETTINGS_DB_URL, url );
		editor.commit();
		debug(1, "committed sync url: "+url+db );
	} // onPause
	

	/***
	 * Get address for a given lat/lng pair
	 * @param lat
	 * @param lng
	 * @return The best reversed geo-coding guess as a string
	 */
//	public static String getAddressArea(int lat, int lng, Context context) {
//		String address="";
//		try {
//			Geocoder geocoder = new Geocoder( context, Locale.getDefault());
//			List<Address> addresses = geocoder.getFromLocation(lat/1E6, lng/1E6, 1);
//			if ( addresses.size() > 0 && addresses.get(0).getSubAdminArea() != null ){
//				address += addresses.get(0).getSubAdminArea() + " - ";
//			}
//			if ( addresses.size() > 0 && addresses.get(0).getAdminArea() != null ){
//				address += addresses.get(0).getAdminArea();
//			}
//		} catch (IOException e) {
//			debug(0, "What's wrong with lat="+lat+", lng="+lng+", error is "+ e.getLocalizedMessage() );
//			e.printStackTrace();
//		}
//		return address;
//	} // getAddressArea

	public static String getCurrentDbDownloadURL ( Context context ){
		String defaultDbDownloadURL = context.getResources().getString( R.string.default_dbDownloadURL );
		//debug(0, "Setting default_dbDownloadURL: "+ defaultDbDownloadURL );

		SharedPreferences settings = context.getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0);
		SharedPreferences.Editor editor = settings.edit();
		String settings_dbDownloadURL = settings.getString ( CityExplorer.SETTINGS_DB_URL, defaultDbDownloadURL );
		//String settings_dbDownloadURL = "";

		//update DB DOWNLOAD URL in setting - in case not yet set
		//add the default downloadURL to settings - in case settings was set to blank
		if ( ! settings_dbDownloadURL.contains( "/" ) ){ //	if ( settings_dbDownloadURL.equals("") ){
			settings_dbDownloadURL = defaultDbDownloadURL;
		}
		debug(1, "Setting settings_dbDownloadURL: "+ settings_dbDownloadURL );
		editor.putString( CityExplorer.SETTINGS_DB_URL, settings_dbDownloadURL );
		editor.commit();
		return settings_dbDownloadURL;
	} // getCurrentDbDownloadPath	// Used to be: getDbPath

	public static int [] getLatLng (Context context ){
		//add Lat and Lng to settings - if not yet set
		SharedPreferences settings = context.getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0);
		
		//Get defaults from integers.xml
		Integer lat = context.getResources().getInteger( R.integer.default_lat );
		Integer lng = context.getResources().getInteger( R.integer.default_lng );

		//Get stored values if existing
		lat = settings.getInt( CityExplorer.SETTINGS_LAT, lat );
		lng = settings.getInt( CityExplorer.LNG, lng );

		SharedPreferences.Editor editor = settings.edit();	// Make sure lat and lng are correctly set			
		editor.putInt( CityExplorer.SETTINGS_LAT, lat);
		editor.putInt( CityExplorer.LNG, lng);
		editor.commit();
		int[] lat_lng = {lat, lng};	// Trondheim Torg: {63430396N, 10395041E};

		return lat_lng;
	} // getLatLng


	private void initLocation() {
		
		//Write default lat/lng location
		int[] lat_lng = getLatLng (this);

		debug(2, "lat is "+lat_lng [0]+", lng is "+ lat_lng [1] );
		//String address = getCurrentAddress( lat_lng [0], lat_lng [1], this );
		PoiAddress address = getCurrentAddress( this, lat_lng );

		//Print prefs, and register click-listener(this)
		TableLayout tl = (TableLayout) findViewById(R.id.pref_location);
		tl.setOnClickListener( this );

		//Latitude
		TextView tv = (TextView) findViewById(R.id.pref_lat);
		if (tv==null){	debug(0, "where is tv?" );
		}else{
			tv.setText( Long.toString( lat_lng [0] )+" (Click to change)" );
		}
		//Longitude
		tv = (TextView) findViewById(R.id.pref_lng);
		if (tv==null){	debug(0, "where is tv?" );
		}else{
			tv.setText( Long.toString(lat_lng [1])+" (Click to change)" );
		}
		//Place-name
		tv = (TextView) findViewById(R.id.pref_loc);
		if ( tv==null || address==null ){
			debug(0, "where is tv address? LatLng-Service not available?" );
		}else{
			tv.setText( address.toString() );
		}
	} // initLocation

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode==Activity.RESULT_CANCELED){
			return;
		}else{
			debug(0, "Result was "+data );
		}
	}//onActivityResult
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pref_location){
			debug(0, "view clicked was LOCATION");
			CityExplorer.showProgressDialog( v.getContext(), "Loading Map" );
			startActivity( new Intent( this, LocationActivity.class));
			// if location was clicked
			
		}else if ( v.getId() == R.id.pref_db ){ 
			debug(0, "view clicked was set DB_PATH automatically" );
		}else if (v.getId() == R.id.DbBrowserButton){
			startActivity( new Intent( this, ImportActivity.class ) );
		}else{
			debug(0, "Clicked v.getId: "+v.getId() );
			//String url = "http://www.example.com";
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData( Uri.parse( url_edit.getText().toString() ) );
			startActivityForResult( intent, 0);
		}// Switch on different key-press events
	} //onClick

	
	public void setDbName(String newDbName) {
		dbName_text.setText( newDbName );
	} //setDbName

	public void setDbUrl(String newDbUrl) {
		url_edit.setText( newDbUrl );
	} //setDbUrl

	//public static PoiAddress getCurrentAddress(Context context, ) {
	public static PoiAddress getCurrentAddress(Context context, int[] latLng ){ // = getLatLng(context);
		Geocoder geocoder = new Geocoder( context, Locale.getDefault());
		PoiAddress adr = null;
		try {
			Address address = geocoder.getFromLocation(latLng[0]/1E6, latLng[1]/1E6, 1).get(0);
			//debug(1, "Adr(0) is "+address );
			adr = new PoiAddress.Builder( address.getLocality() )
			 .street( address.getAddressLine(0) )
			 .latitude( latLng[0]/1E6 )
			 .longitude( latLng[1]/1E6 )
			 //.zipCode(c.getInt( key.get("ADDR.zipcode") ))	// ZIP code removed, but needed for Google Maps
			.build();
		} catch (IOException e) {
			debug(0, "Couldn't get address for latLng "+latLng+", "+ e.getMessage() );
		}
		return adr;
	}//getCurrentAddress

	public static String getSelectedDbFolder ( Context context ){
		String defaultCityName = context.getResources().getString( R.string.default_dbFolderName );
		
		SharedPreferences settings = context.getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0);
		String settingsDbFolder = settings.getString ( CityExplorer.SETTINGS_DB_FOLDER, defaultCityName );
		CityExplorer.debug(1, "settingsCityName is "+settingsDbFolder);

		//update City NAME in setting - in case not yet set
		SharedPreferences.Editor editor = settings.edit();
		//add the default name to settings - if settings was set to blank
		if ( settingsDbFolder.equals("") ){
			settingsDbFolder = defaultCityName;
			CityExplorer.debug(0, "settingsCityName is "+settingsDbFolder);
		}
		editor.putString( CityExplorer.SETTINGS_DB_FOLDER, settingsDbFolder );
		editor.commit();

		return settingsDbFolder;
	}//getCurrentCityFile

	public static String getSelectedDbName ( Context context ){
		String defaultDbName = context.getResources().getString( R.string.default_dbName );
		
		SharedPreferences settings = context.getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0);
		String settingsDbName = settings.getString ( CityExplorer.SETTINGS_DB_NAME, defaultDbName );
		CityExplorer.debug(1, "settingsDbName is "+settingsDbName);

		//update DB NAME in setting - in case not yet set
		SharedPreferences.Editor editor = settings.edit();	// Make sure the default DB url is correctly set			
		//add the default name to settings - if settings was set to blank
		if ( settingsDbName.equals("") ){ // || settingsDbName.equals("http:") ){ OLD DEBUG CODE
			settingsDbName = defaultDbName;
			CityExplorer.debug(0, "settingsDbName is "+settingsDbName);
		}
		editor.putString( CityExplorer.SETTINGS_DB_NAME, settingsDbName );
		editor.commit();

		return settingsDbName;
	}//getCurrentDbFile	// Used to be: getDbPath

}//class MyPreferencesActivity
