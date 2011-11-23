/**
 * @contributor(s): Kristian Greve Hagen (NTNU), Jacqueline Floch (SINTEF), Rune S¾tre (NTNU)
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
import java.util.Collections;
import java.util.List;

import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.DatabaseInterface;
import org.ubicompforall.CityExplorer.data.IntentPassable;
import org.ubicompforall.CityExplorer.data.Poi;
import org.ubicompforall.CityExplorer.data.PoiAddress;

import org.ubicompforall.CityExplorer.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
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
	
	/** The addr view. */
	private EditText addrView;
	
	/** The zip view. */
	private EditText zipView; 
	
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
	
	/** The telephone number of the new poi. */
	private String tel;
	
	/** The opening hours of the new poi. */
	private String openingHours;
	
	/** The web page of the new poi. */
	private String webPage;
	
	/** The image url of the new poi. */
	private String imageUrl;
	
	/** The zip code of the new poi. */
	private int zip;
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newpoi);
		init();
	}

	/**
	 * Initializes the activity.
	 */
	private void init() {
		nameView = (EditText)findViewById(R.id.editname);
		descriptionView = (EditText)findViewById(R.id.editdescription);
		catView = (Spinner)findViewById(R.id.editcategory);
		addrView = (EditText)findViewById(R.id.editaddress);
		zipView = (EditText)findViewById(R.id.editzip);
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

		db = DBFactory.getInstance(this);
		category = db.getCategoryNames();
		Collections.sort(category);
		ArrayAdapter<String> categories = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category);

		catView.setAdapter(categories);
	}

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
	}

	/**
	 * Converts a String to an int.
	 * 
	 * @param text The input text, containing only numbers.
	 * @return An int that is converted successfully from a String.
	 */
	private int stringToInt(String text){
		int n=-1;
		try {
			n=  Integer.parseInt(text);
		} catch ( NumberFormatException ne){
			ne.printStackTrace();
		}
		return n;
	}

	/**
	 * Checks all the mandatory input fields and saves a location in the database.
	 */
	private void savePoi(){
		boolean badAddress = false;
		name = nameView.getText().toString();
		if(name.trim().equals(""))
		{	
			Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
			return;
		}
		description = descriptionView.getText().toString();
		cat = (String) catView.getSelectedItem();
		street = addrView.getText().toString();
		zip = stringToInt(zipView.getText().toString());
		city = cityView.getText().toString();
		tel = telView.getText().toString();
		openingHours = openingHoursView.getText().toString();
		webPage = webPageView.getText().toString();
		imageUrl = imageURLView.getText().toString();

		if(street.trim().equals(""))
		{	
			Toast.makeText(this, "Please enter an address", Toast.LENGTH_LONG).show();
			return;
		}
		if(city.trim().equals(""))
		{	
			Toast.makeText(this, "Please enter a city", Toast.LENGTH_LONG).show();
			return;
		}
		if(!tel.equals("") && !isNumbers(tel)){
			Toast.makeText(this, "Invalid phone number", Toast.LENGTH_LONG).show();
			return;
		}

		// Gets the latitude and longitude of the poi, by giving the street, city and zip.
		Geocoder coder = new Geocoder(this);
		double lat = 0;
		double lon = 0;
		try {
			StringBuilder searchString = new StringBuilder(street);
			if( !city.trim().equals(""))
				searchString.append(", "+city);
			if(zip > 0)
				searchString.append(", "+zip);
			List<Address> foundAdresses = coder.getFromLocationName(searchString.toString(), 1); //Search addresses

			Address x = foundAdresses.get(0);
			lat = x.getLatitude();
			lon = x.getLongitude();
		}
		catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Invalid address or city", Toast.LENGTH_LONG).show();
			badAddress=true;
		}
		if(badAddress){
			return;
		}

		PoiAddress.Builder ab = new PoiAddress.Builder
		(city) 
		.street(street)
		.longitude(lon).latitude(lat);

		if (zip != -1) {
			ab.zipCode(zip);
		}

		Poi p = new Poi.Builder(name, ab.build())
		.description(description)
		.category(cat)
		.favourite(false)
		.webPage(webPage)
		.telephone(tel)
		.openingHours(openingHours)
		.imageURL(imageUrl).build(); 

		db.newPoi(p);

		finish();

	}

	/**
	 * Checks if a string contains only numbers.
	 * 
	 * @param s The string to be checked.
	 * @return True if string contains only numbers, false otherwise.
	 */
	private boolean isNumbers(String s){
		s.trim();
		try{
			Integer.parseInt(s);
		}catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v == choosePoiButton){
			Intent selectPoi = new Intent(this, PlanTabPoi.class);
			selectPoi.putExtra("requestCode", CHOOSE_POI);
			startActivityForResult(selectPoi, CHOOSE_POI);
		}
		else if(v == savePoiButton){
			savePoi();
		}
		else if(v == searchButton){
			String name = nameView.getText().toString();
			name = name.replace(' ', '+');

			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, 
					Uri.parse("http://www.google.com/m/search?tbm=isch&hl=en&" +
							"source=hp&biw=1037&bih=635&" +
							"q="+name+"&gbv=2&aq=f&aqi=g2&aql=&oq="));
			startActivity(launchBrowser);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_CANCELED){
			return;
		}
		switch (requestCode){
		case CHOOSE_POI:
			Poi p = (Poi) data.getParcelableExtra(IntentPassable.POI);
			String name = p.getLabel();
			String description = p.getDescription();
			String cat = p.getCategory();
			String street = p.getAddress().getStreet();
			String city = p.getAddress().getCity();
			String tel = p.getTelephone();
			String openingHours = p.getOpeningHours(); 
			String webPage = p.getWebPage();
			String imageUrl = p.getImageURL();
			int zip = p.getAddress().getZipCode();

			int pos = 0;
			for (String c : category) {
				if(!cat.equals(c)){
					pos++;
				}else {					
					break;
				}
			}

			nameView.setText(name);
			descriptionView.setText(description);
			addrView.setText(street);
			zipView.setText(""+zip);
			cityView.setText(city);
			telView.setText(tel);
			openingHoursView.setText(openingHours);
			webPageView.setText(webPage);
			imageURLView.setText(imageUrl);
			catView.setSelection(pos);

			break;
		default:
			break;
		}
	}
}