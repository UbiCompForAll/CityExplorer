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
import org.ubicompforall.CityExplorer.data.Trip;
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
import android.widget.Button;
import android.widget.Toast;

/**
 * The Class MapsActivity.
 */
public class MapsActivity extends MapActivity implements LocationListener, OnClickListener{
	
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
	private MapIconOverlay locationIcon;
	
	/** The next button. */
	private Button nextButton;
	
	/** The prev button. */
	private Button prevButton;

	/** The poi overlays. */
	private ArrayList<MapIconOverlay> poiOverlays = new ArrayList<MapIconOverlay>();
	
	/** The trip overlay. */
	private MapTripOverlay tripOverlay;
	
	/** The overlays. */
	private List<Overlay> overlays; 
	
	/** The current geo point. */
	private GeoPoint currentGeoPoint = new GeoPoint(0, 0);
	
	/** The qa. */
	private QuickActionPopup qa;
	
	/** The poi clicked. */
	boolean poiClicked = false;

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState the saved instance state
	 */
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maplayout);

		poiClicked		= false;
		mapView 		= (MapView) findViewById(R.id.mapview);
		mapController 	= mapView.getController();
		mapView.setBuiltInZoomControls(true);
		mapController.setZoom(15);

		//---- Button layout ----
		nextButton 		= (Button)findViewById(R.id.MapNextButton);
		nextButton.setVisibility(Button.INVISIBLE);
		nextButton.setOnClickListener(this);
		prevButton 		= (Button)findViewById(R.id.MapPrevButton);
		prevButton.setVisibility(Button.INVISIBLE);
		prevButton.setOnClickListener(this);
		
		//---- Overlays: ----
		locationIcon 	= new MapIconOverlay(this, R.drawable.map_marker, new GeoPoint(0, 0));
		overlays 		= mapView.getOverlays();
		overlays.add(locationIcon);
		
		initWifi();
		initGPS();
		drawOverlays();
	}//onCreate


	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	/**
	 * Draw overlays.
	 */
	private void drawOverlays(){
		HashMap<String, Bitmap> categoryIcons
		 = DBFactory.getInstance(this).getUniqueCategoryNamesAndIcons();	//Time-consuming?

		if( getIntent().hasExtra(IntentPassable.TRIP)){ //Draw a Trip if present
			Trip trip = (Trip) getIntent().getParcelableExtra(IntentPassable.TRIP);
			System.out.println("GOT TRIP ="+trip.getLabel()+" poi count:"+trip.getPois().size());

			tripOverlay = new MapTripOverlay(trip);
			overlays.add(tripOverlay);

			for (Poi poi : trip.getPois()){
				MapIconOverlay poiOverlay = new MapIconOverlay(this, R.drawable.favstar_on, poi.getGeoPoint());
				poiOverlay.setPoi(poi);
				poiOverlay.setImage(categoryIcons.get(poi.getCategory()));

				poiOverlays.add(poiOverlay);
				overlays.add(poiOverlay);
			}
			if(trip.getPois().size() > 0)
				mapController.animateTo(trip.getPoiAt(0).getGeoPoint());//go to first poi
			
			nextButton.setVisibility(Button.VISIBLE);
			prevButton.setVisibility(Button.VISIBLE);
			
			if(trip.getPois().size() > 1){
				prevButton.setEnabled(false);
			}
		}//if (intent.hasExtra)
		
		if( getIntent().hasExtra(IntentPassable.POILIST) ){ //Draw a list of POI if pressent
			ArrayList<Parcelable> pois = (ArrayList<Parcelable>) getIntent().getParcelableArrayListExtra(IntentPassable.POILIST);
			//System.out.println(pois);
			for (Parcelable parcelable : pois){
				Poi poi = (Poi) parcelable;

				MapIconOverlay poiOverlay = new MapIconOverlay(this, R.drawable.favstar_on, poi.getGeoPoint());
				poiOverlay.setPoi(poi);
				poiOverlay.setImage(categoryIcons.get(poi.getCategory()));
				/*debug(0, "adding image for category poi.getCagegory="+poi.getCategory()
						+", val="+categoryIcons.get(poi.getCategory()) ); */

				poiOverlays.add(poiOverlay);
				overlays.add(poiOverlay);
			}
			if ( poiOverlays.size() >0 ){
				debug(0, "poiOverlays is "+poiOverlays );
				mapController.animateTo( poiOverlays.get(0).getGeoPoint() );//go to current location
			}else{
				debug(0, "Could NOT find the POI overlays!!!" );
				SharedPreferences settings = getSharedPreferences( GENERAL_SETTINGS, 0);
				int lat = settings.getInt( LAT, TRONDHEIM_LAT );
				int lng = settings.getInt( LNG, TRONDHEIM_LNG );
				mapController.animateTo( new GeoPoint( lat, lng ));//go to current location
			} // valid POIs, get position and pan to it
		}//if (Intent.hasExtra(POILIST)
	}//drawOverlays


	/**
	 * Init the GPS.
	 */
	void initGPS(){
		debug(0, "InitGPS now..." );
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		onLocationChanged(lastKnownLocation);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	
	/***
	 * Make sure Wifi or Data connection is enabled
	 */
	void initWifi(){
		boolean connected = CityExplorer.isConnected(this);
		if ( ! connected ){
			//Toast.makeText(this, R.string.map_wifi_disabled_toast, Toast.LENGTH_LONG).show();
			CityExplorer.showNoConnectionDialog( this );
		}
	} //initWifi


	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() 
	{
		//required method for mapactivity. ignore this.
		return false;
	}


	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location){ //new location received from the GPS
		if(location==null){
			return;
		}
		currentGeoPoint = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
		locationIcon.updatePos(currentGeoPoint);	//update the position of the icon.
		//mapController.animateTo(currentGeoPoint);	//move map to the new location
	} // onLocationChanged


	/***
	 * Make sure GPS is enabled
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider){
		Toast.makeText(this, R.string.map_gps_disabled_toast, Toast.LENGTH_LONG).show();
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
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{}	
	
	/**
	 * Gets the current location.
	 *
	 * @return the current location
	 */
	public GeoPoint getCurrentLocation()
	{
		return currentGeoPoint;
	}
	
	
	/**
	 * called by an icon overlay when it is pressed.
	 * 
	 * @param i icon overlay
	 */
	public void onPress(MapIconOverlay i)
	{
		final MapIconOverlay icon = i;
		
		//Toast.makeText(context, "Press incoming..."+poi.getLabel(), Toast.LENGTH_SHORT).show();
		System.out.println("POI CLICKED!!!!");
		int[] xy 	= new int[]{icon.getScreenPts().x,icon.getScreenPts().y+icon.getImage().getHeight()};

		Rect rect 	= new Rect(xy[0],xy[1],xy[0],xy[1]);

		if(qa != null)
			qa.dismiss();

		qa = new QuickActionPopup(MapsActivity.this, mapView, rect);
		qa.setTitle(icon.getPoi().getLabel());

		Drawable	favIcon	= MapsActivity.this.getResources().getDrawable(android.R.drawable.ic_menu_info_details);
		qa.addItem(favIcon,	"Details",	new OnClickListener(){

			public void onClick(View view)
			{
				Intent details = new Intent(MapsActivity.this, PoiDetailsActivity.class);
				
				if(tripOverlay != null)//got trip
				{
					details.putExtra("poi", tripOverlay.getTrip().getPoiAt(tripOverlay.getCurrentPoiIndex()));
					details.putExtra("trip", tripOverlay.getTrip());
					details.putExtra("poiNumber", tripOverlay.getCurrentPoiIndex());

					startActivityForResult(details, PoiDetailsActivity.POI_TRIP_POS);
				}
				else
				{
					
					details.putExtra("poi", icon.getPoi());

					startActivity(details);
				}
				

				qa.dismiss();
			}
		});

		Drawable	directIcon	= MapsActivity.this.getResources().getDrawable(android.R.drawable.ic_menu_directions);
		qa.addItem(directIcon,	"Get directions",	new OnClickListener(){

			public void onClick(View view){

				//Latitude and longitude for current position
				double slon = MapsActivity.this.getCurrentLocation().getLongitudeE6()/1E6;
				double slat = MapsActivity.this.getCurrentLocation().getLatitudeE6()/1E6;
				//Latitude and longitude for selected poi
				double dlon = icon.getGeoPoint().getLongitudeE6()/1E6;
				double dlat = icon.getGeoPoint().getLatitudeE6()/1E6;

				Intent navigate = new Intent(MapsActivity.this, NavigateFrom.class);
				navigate.putExtra("slon", slon);
				navigate.putExtra("slat", slat);
				navigate.putExtra("dlon", dlon);
				navigate.putExtra("dlat", dlat);
				MapsActivity.this.startActivity(navigate);


				qa.dismiss();

			}
		});

		qa.show();
	} // onPress


	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		if(tripOverlay == null)
			return;
		
		if(v.getId() == nextButton.getId())
		{
			System.out.println("Next "+(tripOverlay.getPois().size()-1)+" >= "+(tripOverlay.getCurrentPoiIndex()+1));
			if(tripOverlay.getPois().size()-1 >= tripOverlay.getCurrentPoiIndex()+1)
			{
				prevButton.setEnabled(true);
				tripOverlay.setCurrentPoiIndex(tripOverlay.getCurrentPoiIndex()+1);
				mapController.animateTo(tripOverlay.getTrip().getPoiAt(tripOverlay.getCurrentPoiIndex()).getGeoPoint());//go to next poi
			}
			if(tripOverlay.getPois().size()-1 == tripOverlay.getCurrentPoiIndex())//last poi
			{
				nextButton.setEnabled(false);
			}
		}
		else if(v.getId() == prevButton.getId())
		{
			System.out.println("Prev "+tripOverlay.getCurrentPoiIndex()+" > 0");
			if(tripOverlay.getCurrentPoiIndex() > 0)
			{
				nextButton.setEnabled(true);
				tripOverlay.setCurrentPoiIndex(tripOverlay.getCurrentPoiIndex()-1);
				mapController.animateTo(tripOverlay.getTrip().getPoiAt(tripOverlay.getCurrentPoiIndex()).getGeoPoint());//go to prev poi
			}
			if(tripOverlay.getCurrentPoiIndex() == 0)//first poi
			{
				prevButton.setEnabled(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == PoiDetailsActivity.POI_TRIP_POS)
		{
			tripOverlay.setCurrentPoiIndex(resultCode);
			mapController.animateTo(tripOverlay.getTrip().getPoiAt(tripOverlay.getCurrentPoiIndex()).getGeoPoint());//go to poi
			
			prevButton.setEnabled(true);
			nextButton.setEnabled(true);
			if(tripOverlay.getPois().size()-1 == tripOverlay.getCurrentPoiIndex())//last poi
			{
				nextButton.setEnabled(false);
				
			}
			if(tripOverlay.getCurrentPoiIndex() == 0)//first poi
			{
				prevButton.setEnabled(false);
				
			}
		}
	}
	
}
