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
 * This class handles everything that concerns adding new locations.
 * 
 */

package org.ubicompforall.CityExplorer.gui;

import java.util.ArrayList;
import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.DatabaseInterface;
import org.ubicompforall.CityExplorer.data.IntentPassable;
import org.ubicompforall.CityExplorer.data.Poi;
import org.ubicompforall.CityExplorer.data.PoiAddress;
import org.ubicompforall.CityExplorer.map.LocationActivity;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class NewPoiActivity extends Activity implements OnClickListener{

	/** The Constant CHOOSE_POI. */
	protected static final int CHOOSE_POI = 1;
	
	/** The name view. */
	private EditText nameView;
	
	/** The description view. */
	private EditText descriptionView;
	
	/** The adr view. */
	private EditText addrView;
	
	/** The zip view. */
// ZIP code removed
//	private EditText zipView; 
	
	/** The city view. */
	private EditText cityView;
	
	/** The tel view. */
	private EditText telView;
	
	/** The opening hours view. */
	private EditText openingHoursView;
	
	/** The web page view. */
	private EditText webPageView;
	
	/** The image url view. */
	private EditText imageURLView;
	
	/** The name of the new poi. */
	private String name;
	
	/** The description of the new poi. */
	private String description;
	
	/** The category  of the new poi. */
	private String cat;
	
	/** The street of the new poi. */
	private String street;
	
	/** The city of the new poi. */
	private String city;
	
	/** The latitude and longitude of the new poi **/
	Double lat, lng;
	
	/** The telephone number of the new poi. */
	private String tel;
	
	/** The opening hours of the new poi. */
	private String openingHours;
	
	/** The web page of the new poi. */
	private String webPage;
	
	/** The image url of the new poi. */
	private String imageUrl;
	
	/** The zip code of the new poi. */
//	ZIP code removed 
//	private int zip;
	
	/** The save poi button. */
	private Button savePoiButton;
	
	/** The choose poi button. */
	private Button choosePoiButton;
	
	/** The search button, for opening the browser in Google image search. */
	private ImageButton searchButton;
	
	/** The category spinner item. */
	private Spinner catView;
	
	/** The DataBase object. */
	private DatabaseInterface db;
	
	/** The arraylist containing all the category names. */
	private ArrayList<String> category;

	protected Context context;

    boolean wantToGoBack = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.newpoi);
		init();
		initDB();
	}//onCreate

	
	private void initDB() {
		new Thread(new Runnable(){
			public void run(){
				db = DBFactory.getInstance( context);
				category = db.getCategoryNames();
				debug(0, "Categories is "+category );
				//Collections.sort(category);
				ArrayAdapter<String> categories = new ArrayAdapter<String>( context, android.R.layout.simple_spinner_item, category);
				//debug(0, "Categories is "+categories );	//android.widget.ArrayAdapter

				catView.setAdapter(categories);
			}
		} ).start();
	}//initDB


	private void debug(int i, String string) {
		CityExplorer.debug(i, string);
	}//debug


	/**
	 * Initializes the activity.
	 */
	private void init() {
		nameView = (EditText)findViewById(R.id.editname);
		descriptionView = (EditText)findViewById(R.id.editdescription);
		catView = (Spinner)findViewById(R.id.editcategory);
		addrView = (EditText)findViewById(R.id.editaddress);
//	ZIP code removed
//		zipView = (EditText)findViewById(R.id.editzip);
		cityView = (EditText)findViewById(R.id.editcity);
		telView = (EditText)findViewById(R.id.edittelephone);
		openingHoursView = (EditText)findViewById(R.id.editopeningHours);
		webPageView = (EditText)findViewById(R.id.editwebpage);
		imageURLView = (EditText) findViewById(R.id.editImage);
		savePoiButton = (Button) findViewById(R.id.savePoiButton);
		choosePoiButton = (Button) findViewById(R.id.choosePoiButton);
		searchButton = (ImageButton) findViewById(R.id.browserButton);

		choosePoiButton.setOnClickListener(this);
		searchButton.setOnClickListener(this);
		savePoiButton.setOnClickListener(this);
	}//init

	/**
	 * Checks if a string contains only numbers.
	 * 
	 * @param s The string to be checked.
	 * @return True if string contains only numbers, false otherwise.
	 */
	private boolean isNumbers(String s){
		s = s.replaceAll("/[#+ ]/", "");
		s = s.trim();
		try{
			Integer.parseInt(s);
		}catch (NumberFormatException e) {
			debug(0, "NotANumber: "+s );
			return false;
		}
		return true;
	}//isNumbers

	/**
	 * Converts a String to an int.
	 * 
	 * @param text The input text, containing only numbers.
	 * @return An int that is converted successfully from a String.
	 */
//	private int stringToInt(String text){
//		int n=-1;
//		try {
//			n=  Integer.parseInt(text);
//		} catch ( NumberFormatException ne){
//			ne.printStackTrace();
//		}
//		return n;
//	}// stringToInt
	
	/**
	 * Checks all the mandatory input fields and saves a location in the database.
	 */
	private void savePoi(){
		debug(2, "Save! FIRST lat is "+lat+" and lng is "+lng );
		name = nameView.getText().toString();
		if( name.trim().equals("") ){
			Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
			return;
		}
		description = descriptionView.getText().toString();
		cat = (String) catView.getSelectedItem();
		street = addrView.getText().toString();
// ZIP code removed
//		zip = stringToInt(zipView.getText().toString());
		city = cityView.getText().toString();
		tel = telView.getText().toString();
		openingHours = openingHoursView.getText().toString();
		webPage = webPageView.getText().toString();
		imageUrl = imageURLView.getText().toString();

		if(street.trim().equals("")){
			Toast.makeText(this, "Please enter an address", Toast.LENGTH_LONG).show();
			return;
		}
		if( city != null && city.trim().equals("") ){
			Toast.makeText(this, "Please enter a city", Toast.LENGTH_LONG).show();
			return;
		}
		if( ! ( tel.equals("") || isNumbers(tel) ) ){
			Toast.makeText(this, "Invalid phone number: "+tel, Toast.LENGTH_LONG).show();
			return;
		}

		// Gets the latitude and longitude of the poi, by giving the street, city and zip.
		StringBuilder searchString = new StringBuilder(street);
		if( city != null && ! city.trim().equals("") ){
			searchString.append(", "+city);
		}
		//debug(0, "Save! lat is "+lat+" and lng is "+lng );
		if ( CityExplorer.pingConnection( this, CityExplorer.MAGIC_URL ) ){
			//Double[] lat_lng = LocationActivity.getAddressFromLocation( this, searchString.toString(), new GeocoderHandler() ); //Ask online service
			Double[] lat_lng = LocationActivity.runGetAddressFromLocation( context, searchString.toString(), new GeocoderHandler() );
			Toast.makeText( context, "Verifying Address", Toast.LENGTH_LONG).show();
			lat = lat_lng[0]; lng = lat_lng[1];
			debug(2, "Got Address: "+lat+", "+lng );
		}else if( ! CityExplorer.DATACONNECTION_NOTIFIED ){ //Ask to connect
			CityExplorer.showNoConnectionDialog( this, "Data connection needed to verify address location",
					"Set Manually", new Intent( this, LocationActivity.class ), CityExplorer.REQUEST_LOCATION );
		}else if (lat != null && lng != null){ // Set Manually --- Move to where? Do directly from connectionDialog!
			debug(1, "Save! lat is "+lat+" and lng is "+lng );
			storeToDB();	//Save!
		}else{ //set lat and lng manually
			Toast.makeText( NewPoiActivity.this, "Set POI location", Toast.LENGTH_LONG).show();
			Intent selectLatLng = new Intent( this, LocationActivity.class ); //startActivityForResult()...
			startActivityForResult( selectLatLng, CityExplorer.REQUEST_LOCATION );
			debug(0, "Not able to find location of address in city online by Google Maps" );
		}// if web available - else not

		// ZIP code removed
	}// savePoi

	// HELPER CLASS FOR savePoi
	public boolean storeToDB(){
		if ( lat != 0 && lng != 0 ) {
			PoiAddress.Builder ab = new PoiAddress.Builder(city).street(street)
			.longitude(lng).latitude(lat);
		
			Poi p = new Poi.Builder( name, ab.build() )
			.description(description)
			.category(cat)
			.favourite(false)
			.webPage(webPage)
			.telephone(tel)
			.openingHours(openingHours)
			.imageURL(imageUrl).build(); 
		
			debug(0, "poi is "+p );
			db.newPoi(p);
			finish();
		}else{
			Toast.makeText( NewPoiActivity.this, "Invalid address or city", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}//storeToDB


	// HELPER CLASS FOR savePoi

	private class GeocoderHandler extends Handler {
	    @Override
	    public void handleMessage(Message message) {
            Bundle bundle = message.getData();
			lat = bundle.getDouble("lat");
			lng = bundle.getDouble("lng");
	        
			if ( lat != 0 && lng != 0 ) {
				debug(2, "City is "+city+", BUT WHY?!" );
				debug(2, "lat,lng is "+lat+", "+lng );
				PoiAddress.Builder ab = new PoiAddress.Builder(city).street(street)
				.longitude(lng).latitude(lat);

				Poi p = new Poi.Builder( name, ab.build() )
				.description(description)
				.category(cat)
				.favourite(false)
				.webPage(webPage)
				.telephone(tel)
				.openingHours(openingHours)
				.imageURL(imageUrl).build(); 

				db.newPoi(p);
				finish();
			}else{
				Toast.makeText( NewPoiActivity.this, "Invalid address or city", Toast.LENGTH_LONG).show();
			}
	    }//handleMessage
	}//GeocoderHandler

	// LISTENERS / OVERRIDE METHODS
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_poi_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.savePoi){
			savePoi();			
		}
		return true;
	}//onOptionsItemSelected

	@Override
	public void onClick(View v) {
		if(v == choosePoiButton){
			Intent selectPoi = new Intent( this, PlanPoiTab.class );
			selectPoi.putExtra("requestCode", CHOOSE_POI);
			startActivityForResult( selectPoi, CHOOSE_POI );
		}else if(v == savePoiButton){
			savePoi();
		}else if(v == searchButton){
			String name = nameView.getText().toString();
			name = name.replace(' ', '+');

			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, 
					Uri.parse("http://www.google.com/m/search?tbm=isch&hl=en&" +
							"source=hp&biw=1037&bih=635&" +
							"q="+name+"&gbv=2&aq=f&aqi=g2&aql=&oq="));
			startActivity(launchBrowser);
		}
	}//onClick
	
	/***
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		debug(2, "resultCode is "+resultCode+". Activity.RESULT_CANCELED is "+Activity.RESULT_CANCELED );
		debug(1, "requestCode is "+requestCode+". CHOOSE_POI is "+CHOOSE_POI );
		if( resultCode == Activity.RESULT_CANCELED ){
			return;
		}

		switch (requestCode){
		case CHOOSE_POI:
			Poi p = (Poi) data.getParcelableExtra( IntentPassable.POI );
			String name = p.getLabel();
			String description = p.getDescription();
			String cat = p.getCategory();
			String street = p.getAddress().getStreet();
			String city = p.getAddress().getCity();
			String tel = p.getTelephone();
			String openingHours = p.getOpeningHours(); 
			String webPage = p.getWebPage();
			String imageUrl = p.getImageURL();
			//update lat and lng
			lat = p.getGeoPoint().getLatitudeE6()/1E6;
			lng = p.getGeoPoint().getLongitudeE6()/1E6;
			debug( 0, "Now lat, lng is "+lat+", "+lng );

			//Put received values into the layout
			nameView.setText(name);
			descriptionView.setText(description);
			addrView.setText(street);
			cityView.setText(city);
			telView.setText(tel);
			openingHoursView.setText(openingHours);
			webPageView.setText(webPage);
			imageURLView.setText(imageUrl);
			int pos = 0;
			while( category == null ){
				debug(0, "Just waiting..." );
			}
			debug(0, "Categories is "+category );
			for (String c : category) {
				if(!cat.equals(c)){
					pos++;
				}else {					
					break;
				}
			}
			catView.setSelection(pos);
			break;
		case CityExplorer.REQUEST_LOCATION:
			double[] lat_lng = data.getDoubleArrayExtra("lat_lng");
			lat = lat_lng[0];
			lng = lat_lng[1];
			//lng = data.getDoubleExtra( "lng", lng );
			debug(0, "lat_lng is "+lat+", "+lng );
			break;
		case CityExplorer.REQUEST_KILL_BROWSER:
			debug(0, "Killing the login-browser..." );
			finishActivity( requestCode );
			break;
		default:
			break;
		}//switch case
	}//onActivityResult
	

	@Override
	public void onBackPressed() {
		// do something on back.
		if (wantToGoBack){
			super.onBackPressed();
		}else{
			Toast.makeText( this, "Remember to save! Press again to discard", Toast.LENGTH_LONG).show();
			wantToGoBack = true;
			debug(0, "back pressed!" );
		}
		return;
	} //onBackPressed

}//NewPoiActivity class
