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
import org.ubicompforall.CityExplorer.data.PoiAddress;
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
		if ( latE6 ==0 && lngE6 ==0 ){
			debug(0, "lat/lng was still 0" );
		}else{
			SharedPreferences settings = getSharedPreferences( CityExplorer.GENERAL_SETTINGS, 0);
			//Toast.makeText( context, "Updating Latitude/Longitude...", Toast.LENGTH_LONG).show();
			Editor editor = settings.edit();
			editor.putInt( CityExplorer.SETTINGS_LAT, latE6 );
			editor.putInt( CityExplorer.LNG, lngE6 );
			editor.commit();
			debug(0, "committed: lat=" + Integer.toString( latE6 ) + ", lng="+ Integer.toString( lngE6 ) );
		}//if lat/lng not set
	}//onPause
	
	@Override
	public void onBackPressed() {
		Bundle bundle = new Bundle();
	    bundle.putIntArray( "lat_lng", new int[]{latE6,lngE6} );

	    Intent mIntent = new Intent();
	    mIntent.putExtras(bundle);
	    debug(2, "lat, lng is "+latE6+", "+lngE6 );
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
			Toast.makeText( context, "Data connection needed for GeoCoder to verify Address!", Toast.LENGTH_LONG).show();
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
					int[] latLng = new int[]{ latE6, lngE6 };
					//debug(0, "updated: lat=" + latE6 + ", lng="+ lngE6 ); //Constantly called
					
					if (connection){
						TextView tv = (TextView) findViewById(R.id.map_name);
						PoiAddress adr = MyPreferencesActivity.getCurrentAddress( LocationActivity.this, latLng );
						if ( tv !=null && adr !=null ){
							tv.setText( MyPreferencesActivity.getCurrentAddress( LocationActivity.this, latLng ).toString() );
						}
					}else{
						//Update screen with new coordinates
						//tv = (TextView) findViewById(R.id.map_lng);
						TextView tv = (TextView) findViewById(R.id.map_name);
						tv.setText( Integer.toString( p.getLatitudeE6() ) +"/"+ Integer.toString( p.getLongitudeE6() ) );
					}
				}
			}else{
				CityExplorer.showNoConnectionDialog( context, "", "", null );
			}
			return false;
		} // onTouchEvent

	} // class MapOverlay

} // LocationActivity

