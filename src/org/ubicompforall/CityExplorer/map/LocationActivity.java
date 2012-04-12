/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:		24 Jan 2012
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
 * This class keeps hold of the tabs used in planning mode.
 * 
 */

package org.ubicompforall.CityExplorer.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;
import org.ubicompforall.CityExplorer.gui.MyPreferencesActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
//import android.os.Looper; //Necessary? Bug?
import android.os.Message;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class MapsActivity.
 */
public class LocationActivity extends MapActivity{ // implements LocationListener{

	/***
	 * STATIC (GLOBAL) FIELDS
	 */
	static Integer latE6 = 0;
	static Integer lngE6 = 0;

	/***
	 * FIELDS
	 */
	private MapActivity context;	// Where to show pop-up dialogs
	private boolean connection;
	
	/***
	 * Debug method to include the filename, line-number and method of the caller
	 */
	private static void debug(int level, String message) {
		CityExplorer.debug(level, message);
	}

	/** The map controller. */
	private MapController mapController;

	
	// MyMapActivity OVERRIDE methods
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationlayout);

		final MapView mapView = (com.google.android.maps.MapView) findViewById(R.id.location_mapview);
		context = this;
		connection = false;	// Assume connection is not present until tested. // Move to CityExplorer.DATACONNECTION_NOTIFIED is slightly different
		//CityExplorer.DATACONNECTION_NOTIFIED = false;	// Assume connection has not been tested yet. // Move to CityExplorer.java?

		List<Overlay> listOfOverlays = new ArrayList<Overlay>();
		if (mapView==null){
			debug(0, "Where's mapview?" );
		}else{
			mapView.setBuiltInZoomControls(true);

			listOfOverlays = mapView.getOverlays();

			mapController  = mapView.getController();
			mapController.setZoom(15);

			int[] lat_lng = MyPreferencesActivity.getLatLng (this);
			mapController.animateTo( new GeoPoint( lat_lng[0], lat_lng[1] ) );
		}
		listOfOverlays.clear();
		listOfOverlays.add( new MapOverlay() );

		//drawOverlays(); // From MapsActivity. Just put the first 50 POIs for example
	} // onCreate

	@Override
	public void onPause(){
		super.onPause();
		SharedPreferences settings = getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0);
		Editor editor = settings.edit();
		editor.putInt( CityExplorer.LAT, latE6 );
		editor.putInt( CityExplorer.LNG, lngE6 );
		editor.commit();
		debug(0, "committed: lat=" + Integer.toString( latE6 ) + ", lng="+ Integer.toString( lngE6 ) );
	}//onPause
	
	@Override
	public void onBackPressed() {
		Bundle bundle = new Bundle();
	    bundle.putDoubleArray( "lat_lng", new double[]{latE6/1e6,lngE6/1e6} );

	    Intent mIntent = new Intent();
	    mIntent.putExtras(bundle);
	    debug(1, "lat, lng is "+latE6+", "+lngE6 );
	    setResult(RESULT_OK, mIntent);
	    super.onBackPressed();
	    finish();
	} // onBackPressed


	public static Double[] runGetAddressFromLocation( Context context, String locName, final Handler handler ) {
		//Looper.prepare();	// Necessary?
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());   
		try {
			List<Address> adrList = geocoder.getFromLocationName( locName, 1);
			if (adrList != null && adrList.size() > 0) {
				Address address = adrList.get(0);
				// sending back first address lat and longitude
				latE6	= (int)(address.getLatitude()*1e6);
				lngE6	= (int)(address.getLongitude()*1e6);
			}
		} catch (IOException e) {
			Toast.makeText( context, "Data connection needed for Geocoder to verify Address!", Toast.LENGTH_LONG).show();
		} finally {
			Message msg = Message.obtain();
			msg.setTarget(handler);

			Bundle bundle = new Bundle();
			bundle.putDouble("lat", latE6/1e6);
			bundle.putDouble("lng", lngE6/1e6);
			msg.setData(bundle);
			//setResult( Activity.RESULT_OK );
            msg.sendToTarget();
        } // try - catch - finally
		Double[] lat_lng = {latE6/1e6, lngE6/1e6};	// Trondheim Torg: {63430396N, 10395041E};
		return lat_lng;
	}//runGetAddressFromLocation

	// STATIC METHODS //
	public static Double[] getAddressFromLocation( final Context context, final String locName, final Handler handler ) {
		Double[] lat_lng = {latE6/1e6, lngE6/1e6};	// Trondheim Torg: {63430396N, 10395041E};
		Thread geocoderThread = new Thread() {
			@Override public void run() {
				runGetAddressFromLocation( context, locName, handler );
	        } // run
	    }; //Thread Class
	    geocoderThread.start();
	    
	    debug(0, "lat_lng is "+latE6+", "+lngE6 );
		return lat_lng;
	}//getAddressFromLocation

	// END STATIC METHODS //
	
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	} // isRouteDisplayed


	///////////////////////////////////////////////////////////////////////////

	class MapOverlay extends com.google.android.maps.Overlay{
		@Override
		public boolean onTouchEvent(MotionEvent e, MapView mapView){
			connection = CityExplorer.ensureConnected( context );
			if ( connection || CityExplorer.DATACONNECTION_NOTIFIED ){ //For downloading DBs
				if (e.getAction() == MotionEvent.ACTION_UP ){
				    GeoPoint p = mapView.getMapCenter();
					latE6 = p.getLatitudeE6();
					lngE6 = p.getLongitudeE6();
					debug(0, "updated: lat=" + latE6 + ", lng="+ lngE6 );
					
					//Update screen with new coordinates
					TextView tv = (TextView) findViewById(R.id.map_lat);
					tv.setText( Integer.toString( p.getLatitudeE6() ) );
					tv = (TextView) findViewById(R.id.map_lng);
					tv.setText( Integer.toString( p.getLongitudeE6() ) );
					if (connection){
						tv = (TextView) findViewById(R.id.map_name);
						tv.setText( MyPreferencesActivity.getAddress( p.getLatitudeE6(), p.getLongitudeE6(), LocationActivity.this) );
					}
				}
			}else{
				CityExplorer.showNoConnectionDialog( context, "", "", null, 0 );
				//Toast.makeText( context, R.string.map_gps_disabled_toast, Toast.LENGTH_LONG).show();
			}
			return false;
		} // onTouchEvent

//		/* (non-Javadoc)
//		 * @see android.view.View.OnClickListener#onClick(android.view.View)
//		 */
//		@Override
//		public void onClick(View v){
//			debug(0, "onClick");
//			mapController.animateTo( new GeoPoint(63000000, 10000000) );
//		} // onClick

	} // class MapOverlay


} // LocationActivity


//public class LocationActivity extends MapActivity implements LocationListener, OnClickListener{
//
//	private static final String GENERAL_SETTINGS = CityExplorer.GENERAL_SETTINGS;
//
//	// DEFAULT GEO-POINT for first map view
//	private static final String LAT = CityExplorer.LAT;
//	private static final String LNG = CityExplorer.LNG;
//	private static final int TRONDHEIM_LAT = CityExplorer.TRONDHEIM_LAT;	//63°25′36″N ;
//	private static final int TRONDHEIM_LNG = CityExplorer.TRONDHEIM_LNG;	//10°23′48″E ;
//
//
//	/**
//	 * Init the GPS
//	 */
//	void initGPS(){
//		// Acquire a reference to the system Location Manager
//		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		onLocationChanged(lastKnownLocation);
//
//		//Register the listener with the Location Manager to receive location updates
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//	}
//
//	
//
//
//	/* (non-Javadoc)
//	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
//	 */
//	@Override
//	public void onLocationChanged(Location location){ //new location received from the GPS
//		debug(0, "location changed" );
//		if(location==null){
//			return;
//		}
//		currentGeoPoint = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
//		if (mapController == null){
//			debug(0, "OOps, mapcontroller was NULL!");
//		}else{
//			mapController.animateTo(currentGeoPoint);	//move map to the new location
//		}// if mapController found
//
//		if (locationIcon == null){
//			debug(0, "locationIcon was NOT set!! Remember new ..." );
//		}else{
//			locationIcon.updatePos(currentGeoPoint);	//update the position of the icon.
//		} // if locationIcon found
//	} // onLocationChanged
//
//
//	/**
//	 * called by an icon overlay when it is pressed.
//	 * @param i icon overlay
//	 */
//	public void onPress(MapTargetOverlay i){
//		debug(0, "Pressed!" );
//	} // onPress
//
//	/***
//	 * Make sure GPS is enabled, or
//	 * Store the last known location as the default location
//	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
//	 */
//	@Override
//	public void onProviderDisabled(String provider){
//		Toast.makeText(this, R.string.map_gps_disabled_toast, Toast.LENGTH_LONG).show();
//	} // onProviderDisabled
//
//
//	/* (non-Javadoc)
//	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
//	 */
//	@Override
//	public void onProviderEnabled(String provider){
//	}
//
//
//	/* (non-Javadoc)
//	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
//	 */
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras){}	
//
//
//	/**
//	 * Gets the current location.
//	 * @return the current location
//	 */
//	public GeoPoint getCurrentLocation(){
//		return currentGeoPoint;
//	} // getCurrentLocation
//
//	
////			public void onClick(View view){
////				//Latitude and longitude for current position
////				double slng = getCurrentLocation().getLongitudeE6()/1E6;
////				double slat = getCurrentLocation().getLatitudeE6()/1E6;
////			}
//
