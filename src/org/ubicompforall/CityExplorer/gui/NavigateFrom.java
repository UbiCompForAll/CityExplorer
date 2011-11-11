package org.ubicompforall.CityExplorer.gui;

import java.util.ArrayList;

import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.Poi;

import org.ubicompforall.CityExplorer.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

// TODO: Auto-generated Javadoc
/**
 * 
 * This class handles the ability to navigate from one location to another.
 * 
 * @author Kristian Greve Hagen
 *
 */
public class NavigateFrom extends Activity implements OnClickListener, OnCheckedChangeListener{

	/** The longitude for the current position. */
	private double slon;
	
	/** The latitude for the current position. */
	private double slat;

	/** The longitude for the selected poi. */
	private double dlon;
	
	/** The latitude for the selected poi. */
	private double dlat;
	
	/** The navigate button. */
	private Button navigateButton;
	
	/** The navigate from current pos. */
	private RadioButton navigateFromCurrentPos;
	
	/** The navigate from poi. */
	private RadioButton navigateFromPoi;
	
	/** The spinner item. */
	private Spinner spinner;
	
	/** All the pois. */
	private ArrayList<Poi> pois;
	
	/** The intent. */
	private Intent i;
	
	/** The radio group. */
	private RadioGroup radioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigate_from);

		init();
	}

	/**
	 * Initializes the activity and setting all things right the first time.
	 */
	private void init() {
		radioGroup = (RadioGroup) findViewById(R.id.navigateRadioGroup);
		navigateButton = (Button)findViewById(R.id.navigateButton);
		navigateButton.setOnClickListener(this);

		navigateFromCurrentPos = (RadioButton)findViewById(R.id.navigateFromCurrentPos);
		navigateFromPoi = (RadioButton)findViewById(R.id.navigateFromPoi);
		navigateFromCurrentPos.setChecked(true);
		radioGroup.setOnCheckedChangeListener(this);

		i = getIntent();
		
		dlon = i.getExtras().getDouble("dlon");
		dlat = i.getExtras().getDouble("dlat");

		spinner = (Spinner) findViewById(R.id.spinnerPoi);
		spinner.setEnabled(false);
		pois = DBFactory.getInstance(this).getAllPois();

		ArrayList<String> poiNames = new ArrayList<String>();

		for (Poi p : pois) {
			poiNames.add(p.getLabel());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, poiNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.navigateButton){
			slon = i.getExtras().getDouble("slon");
			slat = i.getExtras().getDouble("slat");
			
			if(navigateFromCurrentPos.isChecked()){
				Uri uri = Uri.parse("http://maps.google.com/maps?f=d&source=s_d&saddr="+
						slat+","+slon+"&daddr="+dlat+","+dlon+"&dirflg=w");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);		
			}
			if(navigateFromPoi.isChecked()){
				int pos = spinner.getSelectedItemPosition();

				System.out.println("SPINNER: " + pois.get(pos).getLabel());
				
				Poi p = pois.get(pos);
				
				//slon = p.getGeoPoint().getLongitudeE6()/1E6;
				//slat = p.getGeoPoint().getLatitudeE6()/1E6;
				slon = p.getAddress().getLongitude();
				slat = p.getAddress().getLatitude();
				
				Uri uri = Uri.parse("http://maps.google.com/maps?f=d&source=s_d&saddr="+
						slat+","+slon+"&daddr="+dlat+","+dlon+"&dirflg=w");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);		
			}
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(radioGroup.getCheckedRadioButtonId() == navigateFromCurrentPos.getId()){
			spinner.setEnabled(false);
		}
		
		if(radioGroup.getCheckedRadioButtonId() == navigateFromPoi.getId()){
			spinner.setEnabled(true);
		}
	}
}