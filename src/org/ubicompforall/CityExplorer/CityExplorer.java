/*
 * Copyright (C) 2011 Rune Sætre
 *	for the UbiCompForAll project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.ubicompforall.CityExplorer;

import android.app.Application;
import android.content.Context;
//import android.preference.PreferenceActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This is an example of a {@link android.app.Application} class.  Ordinarily you would use
 * a class like this as a central repository for information that might be shared between multiple
 * activities.
 * In this case, we have not defined any specific work for this Application.
 * See samples/UbiTerms/tests/src/org.ubicompforall.ubiterms/ApiDemosApplicationTests for an example
 * of how to perform unit tests on an Application object.
 */
public class CityExplorer extends Application implements LocationListener{

    @Override
    public void onCreate() {
        /*
         * This populates the default values from the preferences XML file. See
         * {@link DefaultValues} for more details.
         */
        PreferenceManager.setDefaultValues(this, R.xml.default_values, false);
		initGPS(); //RS-111122 Moved to CityExplorer.java Application (Common for all activites)
    }//onCreate

    @Override
    public void onTerminate() {
    	//TODO: save preferences now???
    	Log.d("CityExplorer", "Save the preferences now?");
    }//onTerminate

	/**
	 * Initializes the GPS on the device.
	 */
	void initGPS(){
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//TODO: change to gps
		onLocationChanged(lastKnownLocation); //RS-111122 Make sure the last known location is stored in global preferences

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}//initGPS

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
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
	
}//CityExplorer

/*
public class DefaultValues extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        // or (R.xml.default_values) as loaded in the CityExplorer.java Application-class;
    }
}
*/
