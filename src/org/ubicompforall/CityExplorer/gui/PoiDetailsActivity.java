/**
 * @contributor(s): Kristian Greve Hagen (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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
 * This class handles the activity of showing detailed information about a PoI.
 * 
 */

package org.ubicompforall.CityExplorer.gui;

import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.ubicompforall.CityExplorer.data.*;
import org.ubicompforall.CityExplorer.map.MapsActivity;

import android.util.Log;

import org.ubicompforall.CityExplorer.R;

import android.app.Activity;
import android.content.*;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class PoiDetailsActivity extends Activity implements LocationListener, OnClickListener {

	/**
	 * Field containing the TextView of the title.
	 */
	private TextView 	title;

	/**
	 * Field containing the TextView of the address.
	 */
	private TextView 	address;

	/**
	 * Field containing the TextView of the description.
	 */
	private TextView 	description;

	/**
	 * Field containing the TextView of the category.
	 */
	private TextView	category;

	/**
	 * Field containing the TextView of the telephone.
	 */
	private TextView	telephone;

	/**
	 * Field containing the TextView of the webPage.
	 */
	private TextView	webPage;

	/**
	 * Field containing the TextView of the opening hours.
	 */
	private TextView	openingHours;

	/**
	 * Constant field describing this activity.
	 */
	public static int POI_TRIP_POS = 1;

	/**
	 * Field containing a single poi.
	 */
	private Poi poi;

	/**
	 * Field containing a single trip
	 */
	private Trip trip;

	/**
	 * Field containing the number of the poi in the trip.
	 */
	private int poiNumber;

	/**
	 * Field containing the users current location.
	 */
	private Location userLocation;

	/**
	 * Field containing the previous poi ImageButton. 
	 */
	private ImageButton prevPoi;

	/**
	 * Field containing the next poi ImageButton. 
	 */
	private ImageButton nextPoi;

	/**
	 * Field containing the ImageView of the poi.
	 */
	private ImageView poiImage;

	/**
	 * Field containing all the pois in the trip.
	 */
	private ArrayList<Poi> pois;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_poi_layout);

		if(getIntent().getParcelableExtra("poi") != null)
		{
			poi = (Poi) getIntent().getParcelableExtra("poi");
		}
		else
		{
			System.out.println("No poi supplied.. exit activity");
			this.finish();
		}

		if(getIntent().getParcelableExtra("trip") != null)
		{
			prevPoi = (ImageButton) findViewById(R.id.previousPoiButton);
			nextPoi = (ImageButton) findViewById(R.id.nextPoiButton);
			prevPoi.setVisibility(0);
			nextPoi.setVisibility(0);
			prevPoi.setOnClickListener(this);
			nextPoi.setOnClickListener(this);
			trip = getIntent().getParcelableExtra("trip");
			pois = new ArrayList<Poi>();
			for (Poi p : trip.getPois()) {
				pois.add(p);
			}
			poiNumber = getIntent().getIntExtra("poiNumber", 0);
			if(poiNumber==0){
				prevPoi.setEnabled(false);
				if(pois.size()==1){
					nextPoi.setEnabled(false);	
				}	
			}else if(poiNumber==pois.size()-1){
				nextPoi.setEnabled(false);
			}

		}
		else
		{
			System.out.println("No trip supplied..");
		}

		title 		= (TextView)findViewById(R.id.label);
		address 	= (TextView)findViewById(R.id.address);
		description	= (TextView)findViewById(R.id.description);
		category	= (TextView)findViewById(R.id.category);
		telephone 	= (TextView)findViewById(R.id.telephone);
		openingHours =(TextView)findViewById(R.id.openingHours);
		webPage		= (TextView)findViewById(R.id.webPage);
		poiImage = (ImageView) findViewById(R.id.imageContainer);

		showPoiDetails(poi);
		initGPS();
	}

	/**
	 * Method fetches information for the current PoI, and shows it in the GUI.
	 * @param poi The poi to fetch information from.
	 */
	private void showPoiDetails(Poi poi) {
		title.setText(		poi.getLabel());
		description.setText(poi.getDescription());
// ZIP code removed
//		address.setText(	poi.getAddress().getStreet() + "\n" + poi.getAddress().getZipCode() + "\n" + poi.getAddress().getCity());
		address.setText(	poi.getAddress().getStreet() + "\n" + poi.getAddress().getCity());
		category.setText(poi.getCategory());
		telephone.setText(poi.getTelephone());
		openingHours.setText(poi.getOpeningHours());
		webPage.setText(poi.getWebPage());

		if(!poi.getImageURL().equals("")){
			final String imageURL = poi.getImageURL();

			new Thread(new Runnable(){
				public void run(){

					DefaultHttpClient	httpClient = new DefaultHttpClient();
					HttpGet			httpGet = new HttpGet(imageURL); //have user-inserted url
					HttpResponse		httpResponse;
					final HttpEntity	entity;

					try {
						httpResponse = httpClient.execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							entity = httpResponse.getEntity();

							if (entity != null) {

								//converting into bytemap and inserting into imageView

								poiImage.post(new Runnable(){
									public void run(){
										byte[] imageBytes = new byte[0];

										try {
											imageBytes = EntityUtils.toByteArray(entity);
										} catch (Throwable t){}

										poiImage.setImageBitmap(
												BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)
										);
									}
								});
							} else {
								Log.d("CityExplorer","entity == null?");
							}
						} else {
							Log.d("CityExplorer","(httpResponse.getStatusLine().getStatusCode() == not OK ");
						}

					} catch (Exception e) {
						Log.d("CityExplorer","Error fetching image");
					}
				}
			}
			).start();
		}else {
			poiImage.setImageBitmap(null);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.poi_menu, menu);
		if(poi.isFavourite()){
			menu.findItem(R.id.poiMenufavourite).setIcon(R.drawable.favstar_on);
		}else{
			menu.findItem(R.id.poiMenufavourite).setIcon(R.drawable.favstar_off);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemID = item.getItemId();
		int favID = R.id.poiMenufavourite;
		int dirID = R.id.poiMenuDirections;
		int mapID = R.id.poiMenuMap;
		if(itemID==favID){

			if(poi.isFavourite()){
				poi  = new Poi( poi.modify().favourite(false) );

				DBFactory.getInstance(this).editPoi(poi);
				return true;

			}
			poi = poi.modify().favourite(true).build();
			DBFactory.getInstance(this).editPoi(poi);
			return true;

		}if(itemID==dirID){

			//Latitude and longitude for current position
			double slon = userLocation.getLongitude();
			double slat = userLocation.getLatitude();
			//Latitude and longitude for selected poi
			double dlon = poi.getGeoPoint().getLongitudeE6()/1E6;
			double dlat = poi.getGeoPoint().getLatitudeE6()/1E6;

			Intent navigate = new Intent(PoiDetailsActivity.this, NavigateFrom.class);
			navigate.putExtra("slon", slon);
			navigate.putExtra("slat", slat);
			navigate.putExtra("dlon", dlon);
			navigate.putExtra("dlat", dlat);
			startActivity(navigate);

			return true;
		}if(itemID==mapID){
			Intent showInMap = new Intent(PoiDetailsActivity.this, MapsActivity.class);
			ArrayList<Poi> selectedPois = new ArrayList<Poi>();
			selectedPois.add(poi);
			showInMap.putParcelableArrayListExtra(IntentPassable.POILIST, selectedPois);

			startActivity(showInMap);
			return true;
		}
		return true;
	}

	/**
	 * Method containing GPS initialization.
	 */
	void initGPS()
	{
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//TODO: change to gps
		onLocationChanged(lastKnownLocation);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		userLocation = location;
		System.out.println("Inside onLocationChanged in PoiDetailsActivity");

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

	@Override
	public void onClick(View v) {
		if(v.getId() == prevPoi.getId()){
			nextPoi.setEnabled(true);
			poiNumber--;
			showPoiDetails(trip.getPoiAt(poiNumber));
			if(poiNumber==0){
				prevPoi.setEnabled(false);
			}else {
				prevPoi.setEnabled(true);
			}
		}
		if(v.getId() == nextPoi.getId()){
			prevPoi.setEnabled(true);
			poiNumber++;
			showPoiDetails(trip.getPoiAt(poiNumber));
			if(poiNumber==pois.size()-1){
				nextPoi.setEnabled(false);
			}else {
				nextPoi.setEnabled(true);
			}
		}
		this.setResult(poiNumber);
	}
}