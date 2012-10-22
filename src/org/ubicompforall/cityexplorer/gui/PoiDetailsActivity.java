/**
 * @contributor(s): Kristian Greve Hagen (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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
 * This class handles the activity of showing detailed information about a PoI.
 * 
 */

package org.ubicompforall.cityexplorer.gui;

import java.net.UnknownHostException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;
import org.ubicompforall.cityexplorer.data.*;
import org.ubicompforall.cityexplorer.map.MapsActivity;

import android.app.Activity;
import android.content.*;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class PoiDetailsActivity extends Activity implements OnClickListener {

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

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


///////////////////////////////////////////////////////////////////////////////
	
	/***
	 * onCreate: StartUp Method
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_poi_layout);

		Intent myIntent = getIntent();
		if(myIntent.getParcelableExtra("poi") != null){
			poi = (Poi) myIntent.getParcelableExtra("poi");
		}else{
			debug(0, "No poi supplied.. exit activity");
			this.finish();
		}

		if(myIntent.getParcelableExtra("trip") != null){
			prevPoi = (ImageButton) findViewById(R.id.previousPoiButton);	// TODO: POI MUST BE UPDATED WHEN THE ARROW ARE USED
			nextPoi = (ImageButton) findViewById(R.id.nextPoiButton);
			prevPoi.setVisibility(0);
			nextPoi.setVisibility(0);
			prevPoi.setOnClickListener(this);
			nextPoi.setOnClickListener(this);
			trip = myIntent.getParcelableExtra("trip");
			pois = new ArrayList<Poi>();
			for (Poi p : trip.getPois()) {
				pois.add(p);
			}
			poiNumber = myIntent.getIntExtra("poiNumber", 0);
			if(poiNumber==0){
				prevPoi.setEnabled(false);
				if(pois.size()==1){
					nextPoi.setEnabled(false);	
				}	
			}else if(poiNumber==pois.size()-1){
				nextPoi.setEnabled(false);
			}

		}else{
			debug(0, "No trip supplied..");
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
	} // onCreate

	/**
	 * Method fetches information for the current PoI, and shows it in the GUI.
	 * @param poi The poi to fetch information from.
	 */
	private void showPoiDetails(Poi poi) {
		title.setText(		poi.getLabel());
		description.setText(poi.getDescription());
//		address.setText(	poi.getAddress().getStreet() + "\n" + poi.getAddress().getZipCode() + "\n" + poi.getAddress().getCity()); // ZIP code removed

		address.setText(	poi.getAddress().getStreet() + "\n" + poi.getAddress().getCity());
		category.setText(poi.getCategory());
		telephone.setText(poi.getTelephone());
		openingHours.setText(poi.getOpeningHours());
		webPage.setText(poi.getWebPage());

		if(!poi.getImageURL().equals("")){
			final String imageURL = poi.getImageURL();

			new Thread(new Runnable(){
				public void run(){
					try{

						DefaultHttpClient	httpClient = new DefaultHttpClient();
						HttpGet	httpGet = new HttpGet(imageURL); //have user-inserted url
						HttpResponse		httpResponse;
						final HttpEntity	entity;

						httpResponse = httpClient.execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							entity = httpResponse.getEntity();

							if (entity != null) {
								//converting into bytemap and inserting into imageView
								poiImage.post( new Runnable(){
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
								debug(0, "entity == null?");
							}
						} else {
							debug(0, "(httpResponse.getStatusLine().getStatusCode() == not OK ");
						}

					} catch ( UnknownHostException e) {
						//Toast.makeText(PoiDetailsActivity.this, "Enable Internet to show Picture", Toast.LENGTH_SHORT );
						debug(0, "Enable Internet to show Picture: "+imageURL );
					} catch ( Exception e) {
						debug(-1, "Error fetching image: "+imageURL );
						e.printStackTrace();
					}//try-catch
				}//run
			}//new Runnable class
			).start();
		}else {
			poiImage.setImageBitmap(null);
		}//if img, else blank
	}//showPoiDetails

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.poi_menu, menu);
		if(poi.isFavorite()){
			menu.findItem(R.id.poiMenufavorite).setIcon(R.drawable.favstar_on);
		}else{
			menu.findItem(R.id.poiMenufavorite).setIcon(R.drawable.favstar_off);
		}
		return super.onPrepareOptionsMenu(menu);
	}//onPrepareOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemID = item.getItemId();
		int editID = R.id.poiMenuEdit;
		int favID = R.id.poiMenufavorite;
		int dirID = R.id.poiMenuDirections;
		int mapID = R.id.poiMenuMap;

		//1: Edit
		if(itemID==editID){
			debug(0, "Handle Edit-selection here!" );
			Intent editIntent = new Intent( this, NewPoiActivity.class );
			editIntent.putExtra( IntentPassable.POI, poi );	//setResult( Activity.RESULT_OK, resultIntent );
			startActivity( editIntent );
			finish ();		// After editing, the details should be refreshed. This activity should thus be restarted.
		}
		//2: Favorite
		if(itemID==favID){
			if(poi.isFavorite()){
				poi  = new Poi( poi.modify().favourite(false) );
				DBFactory.getInstance(this).editPoi(poi);
				return true;
			}
			poi = poi.modify().favourite(true).build();
			DBFactory.getInstance(this).editPoi(poi);
			return true;
		}
		//3: Directions
		if(itemID==dirID){
			userLocation = StartActivity.verifyUserLocation( userLocation, this );

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
		}
		//4: Map
		if(itemID==mapID){
			Intent showInMap = new Intent(PoiDetailsActivity.this, MapsActivity.class);
			ArrayList<Poi> selectedPois = new ArrayList<Poi>();
			selectedPois.add(poi);
			showInMap.putParcelableArrayListExtra(IntentPassable.POILIST, selectedPois);

			startActivity(showInMap);
			return true;
		}
		return true;
	}//onOptionsItemSelected

	@Override
	public void onClick(View v) {
		if(v.getId() == prevPoi.getId()){
			nextPoi.setEnabled(true);
			poiNumber--;
			poi=trip.getPoiAt(poiNumber);
			showPoiDetails(poi);
			if(poiNumber==0){
				prevPoi.setEnabled(false);
			}else {
				prevPoi.setEnabled(true);
			}
		}
		if(v.getId() == nextPoi.getId()){
			prevPoi.setEnabled(true);
			poiNumber++;
			poi=trip.getPoiAt(poiNumber);
			showPoiDetails(poi);
//			showPoiDetails(trip.getPoiAt(poiNumber));
			if(poiNumber==pois.size()-1){
				nextPoi.setEnabled(false);
			}else {
				nextPoi.setEnabled(true);
			}
		}
		this.setResult(poiNumber);
	}
}//end class PoiDetailsActivity
