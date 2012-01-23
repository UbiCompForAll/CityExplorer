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
 * This class keeps hold of the tabs used in planning mode.
 * 
 */

package org.ubicompforall.CityExplorer.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.IntentPassable;
import org.ubicompforall.CityExplorer.data.Poi;
import org.ubicompforall.CityExplorer.gui.NavigateFrom;
import org.ubicompforall.CityExplorer.gui.PoiDetailsActivity;
import org.ubicompforall.CityExplorer.gui.QuickActionPopup;
import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * The Class MapsActivity.
 */
public class LocationActivity extends MapActivity implements LocationListener, OnClickListener{

	//GLOBAL CONSTANTS
	private static final int DEBUG = CityExplorer.DEBUG;
	private static final String GENERAL_SETTINGS = CityExplorer.GENERAL_SETTINGS;

	// DEFAULT GEO-POINT for first map view
	private static final String LAT = CityExplorer.LAT;
	private static final String LNG = CityExplorer.LNG;
	private static final int TRONDHEIM_LAT = CityExplorer.TRONDHEIM_LAT;	//63°25′36″N ;
	private static final int TRONDHEIM_LNG = CityExplorer.TRONDHEIM_LNG;	//10°23′48″E ;

	/** The map view. */
	private MapView mapView;
	
	/** The map controller. */
	private MapController mapController;
	
	/** The location icon. */
	private MapTargetOverlay locationIcon;
	
	/** The poi overlays. */
	//private ArrayList<MapTargetOverlay> poiOverlays = new ArrayList<MapTargetOverlay>();
	
	/** The overlays. */
	private List<Overlay> overlays; 
	
	/** The current geo point. */
	private GeoPoint currentGeoPoint = new GeoPoint(0, 0);
	
	/** The poi clicked. */
	boolean poiClicked = false;

	/** The qa. */
	private QuickActionPopup qa;
	

	
	/***
	 * Debug method to include the filename, line-number and method of the caller
	 */
	private static void debug(int d, String msg) {
		if (DEBUG >= d) {
			CityExplorer.debug(0, msg );
		}
	} // debug

	
	
	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState the saved instance state
	 */
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		debug(0, "So far so good ;-)" );
		setContentView(R.layout.locationlayout);
		debug(0, "..., but why so slow? ONly ON first invocation, I mean ;-)" );

		poiClicked		= false;
		mapView 		= (MapView) findViewById(R.id.mapview);
		mapView.setReticleDrawMode( MapView.ReticleDrawMode.DRAW_RETICLE_OVER );
		mapController 	= mapView.getController();
		mapView.setBuiltInZoomControls(true);
		mapController.setZoom(15);

		//---- Overlays: ----
		locationIcon 	= new MapTargetOverlay(this, R.drawable.map_marker, new GeoPoint(0, 0));
		overlays 		= mapView.getOverlays();
		overlays.add(locationIcon);

		if ( ! CityExplorer.isConnected(this) ){
			CityExplorer.showNoConnectionDialog( this );
		}
		drawOverlays();
	}//onCreate


	
	/**
	 * Draw overlays.
	 */
	private void drawOverlays(){
		HashMap<String, Bitmap> categoryIcons
		 = DBFactory.getInstance(this).getUniqueCategoryNamesAndIcons();	//Time-consuming?

		//mapController.animateTo(trip.getPoiAt(0).getGeoPoint());//go to first poi
			
		if( getIntent().hasExtra(IntentPassable.POILIST) ){ //Draw a list of POI if present
			ArrayList<Parcelable> pois = (ArrayList<Parcelable>) getIntent().getParcelableArrayListExtra(IntentPassable.POILIST);
			//System.out.println(pois);
			for (Parcelable parcelable : pois){
				Poi poi = (Poi) parcelable;

				MapTargetOverlay poiOverlay = new MapTargetOverlay(this, R.drawable.favstar_on, poi.getGeoPoint());
				poiOverlay.setPoi(poi);
				poiOverlay.setImage(categoryIcons.get(poi.getCategory()));

				//poiOverlays.add(poiOverlay);
				overlays.add(poiOverlay);
			}
			SharedPreferences settings = getSharedPreferences( GENERAL_SETTINGS, 0);
			int lat = settings.getInt( LAT, TRONDHEIM_LAT );
			int lng = settings.getInt( LNG, TRONDHEIM_LNG );

			debug(0, "overlays is "+overlays);
			mapController.animateTo( new GeoPoint( lat, lng ));//go to current location
		}else{//if (Intent.hasExtra(POILIST)
			debug(0, "No Data for Location Intent!!!");

			//---- Overlays: ----
			locationIcon 	= new MapTargetOverlay(this, R.drawable.map_marker, new GeoPoint(0, 0));
			overlays 		= mapView.getOverlays();
			overlays.add(locationIcon);
		}
	}//drawOverlays


	/**
	 * Init the GPS
	 */
	void initGPS(){
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		onLocationChanged(lastKnownLocation);

		//Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	} // initGPS
	


	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location){ //new location received from the GPS
		if(location==null){
			return;
		}
		currentGeoPoint = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
		if (mapController == null){
			debug(0, "OOps, mapcontroller was NULL!");
		}else{
			mapController.animateTo(currentGeoPoint);	//move map to the new location
		}// if mapController found

		if (locationIcon == null){
			debug(0, "locationIcon was NOT set!! Remember new ..." );
		}else{
			locationIcon.updatePos(currentGeoPoint);	//update the position of the icon.
		} // if locationIcon found
	} // onLocationChanged


	/**
	 * called by an icon overlay when it is pressed.
	 * 
	 * @param i icon overlay
	 */
	public void onPress(MapTargetOverlay i){
		final MapTargetOverlay icon = i;
		
		//Toast.makeText(context, "Press incoming..."+poi.getLabel(), Toast.LENGTH_SHORT).show();
		System.out.println("POI CLICKED!!!!");
		int[] xy 	= new int[]{icon.getScreenPts().x,icon.getScreenPts().y+icon.getImage().getHeight()};

		Rect rect 	= new Rect(xy[0],xy[1],xy[0],xy[1]);

		if(qa != null)
			qa.dismiss();

		qa = new QuickActionPopup(LocationActivity.this, mapView, rect);
		qa.setTitle(icon.getPoi().getLabel());

		Drawable	favIcon	= LocationActivity.this.getResources().getDrawable(android.R.drawable.ic_menu_info_details);

		qa.addItem(favIcon,	"Details",	new OnClickListener(){
			public void onClick(View view){
				Intent details = new Intent(LocationActivity.this, PoiDetailsActivity.class);
				{	
					details.putExtra("poi", icon.getPoi());
					startActivity(details);
				}
				qa.dismiss();
			} //Listener.onClick
		}); // new OnClickListener() class

		Drawable	directIcon	= LocationActivity.this.getResources().getDrawable(android.R.drawable.ic_menu_directions);
		qa.addItem(directIcon,	"Get directions",	new OnClickListener(){

			public void onClick(View view){

				//Latitude and longitude for current position
				double slon = LocationActivity.this.getCurrentLocation().getLongitudeE6()/1E6;
				double slat = LocationActivity.this.getCurrentLocation().getLatitudeE6()/1E6;
				//Latitude and longitude for selected poi
				double dlon = icon.getGeoPoint().getLongitudeE6()/1E6;
				double dlat = icon.getGeoPoint().getLatitudeE6()/1E6;

				Intent navigate = new Intent(LocationActivity.this, NavigateFrom.class);
				navigate.putExtra("slon", slon);
				navigate.putExtra("slat", slat);
				navigate.putExtra("dlon", dlon);
				navigate.putExtra("dlat", dlat);
				LocationActivity.this.startActivity(navigate);


				qa.dismiss();

			}
		});

		qa.show();
	} // onPress

	/***
	 * Make sure GPS is enabled, or
	 * Store the last known location as the default location
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider){
		Toast.makeText(this, R.string.map_gps_disabled_toast, Toast.LENGTH_LONG).show();
		SharedPreferences settings = getSharedPreferences(GENERAL_SETTINGS, 0);
		Editor editor = settings.edit();
		editor.putInt( LAT, this.currentGeoPoint.getLatitudeE6() );
		editor.putInt( LNG, this.currentGeoPoint.getLongitudeE6() );
		debug(0, "Current LAT is "+this.currentGeoPoint.getLatitudeE6());
	} // onProviderDisabled


	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) 
	{}


	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){}	
	
	/**
	 * Gets the current location.
	 *
	 * @return the current location
	 */
	public GeoPoint getCurrentLocation(){
		return currentGeoPoint;
	}
	
	


//			public void onClick(View view){
//				//Latitude and longitude for current position
//				double slon = getCurrentLocation().getLongitudeE6()/1E6;
//				double slat = getCurrentLocation().getLatitudeE6()/1E6;
//			}


	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v){
		debug(0, "onClick");
		mapController.animateTo( new GeoPoint(63000000, 10000000) );
	} // onClick

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		debug(0, "onActivityResults");
		mapController.animateTo( new GeoPoint(63000000, 96000000) );
	} //onActivityResults


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	} // isRouteDisplayed
	
} // LocationActivity
