/**
 * @contributor(s): Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			22 November 2011
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
 *
 *
 */


package org.ubicompforall.CityExplorer;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.DatabaseInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * This is an example of a {@link android.app.Application} class.  Ordinarily you would use
 * a class like this as a central repository for information that might be shared between multiple
 * activities.
 * In this case, we have not defined any specific work for this Application.
 * See samples/UbiTerms/tests/src/org.ubicompforall.ubiterms/ApiDemosApplicationTests for an example
 * of how to perform unit tests on an Application object.
 */
public class CityExplorer extends Application{ // implements LocationListener // For GPS
	
	public static final int DEBUG = 1;
	public static final String C = "CityExplorer";
	
	// Constant keys for GENERAL SETTINS
	public static final String GENERAL_SETTINGS = "SETTINGS";
	public static final String URL = "Url";
	public static final String LAT = "Lat";
	public static final String LNG = "Long";
	
	// DEFAULT GEO-POINT for first map view - moved to @string/default_lat_lng // Trondheim Torvet 63°25′49″N  10°23′42″E ;
	//  Default URL moved to @string/default_url
	//	public static final String RUNE_URL = "http://www.idi.ntnu.no/~satre/ubicomp/cityexplorer/"; //CityExplorer.sqlite
	
	public static final int TYPE_ALL = 0;
	public static final int TYPE_FREE = 1;
	public static final int TYPE_FIXED = 2;
	
	/***
	 * The global current db connection
	 */
	public static DatabaseInterface db;
	private static boolean verifiedDataConnection;
	private static String googleURL = "http://www.google.com";
	
	@Override
	public void onCreate() {
	    /*
	     * This populates the default values from the preferences XML file. See
	     * {@link DefaultValues} for more details.
	     */
	    PreferenceManager.setDefaultValues( this, R.xml.default_values, false);
	
	    //Local debug (stacktrace level = 3)
	    //StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		//Log.d(C, ste.getFileName().replace("java", "")+ste.getLineNumber()+'~'+ste.getMethodName()+'~'+"Start CityExplorer.java" );
	    debug(0, "Start CityExplorer.java" );
	
		//initGPS(); //RS-111122 Move to CityExplorer.java Application (Common for all activities) ? When?
	
	    // And what about DB-loading?  Initialize the single instance here :-)
		db = DBFactory.getInstance(this);
		
		//Remember to verify that the web is available!
		verifiedDataConnection = false;
	}//onCreate

    @Override
    public void onTerminate() {
    	//TODO: save preferences now???

        //Local debug (stacktrace level = 3)
        StackTraceElement st = Thread.currentThread().getStackTrace()[2];
		Log.d(C, st.getFileName().replace("java", "")+st.getLineNumber()+'~'+st.getMethodName()+'~'+"Save the preferences now?");
    }//onTerminate


    /*
	 * Debug method to include the filename, line-number and method of the caller
	 */
	public static void debug(int d, String msg) {
		if (DEBUG >= d) {
			StackTraceElement[] st = Thread.currentThread().getStackTrace();
			int stackLevel = 2;
			while ( st[stackLevel].getMethodName().equals("debug") || st[stackLevel].getMethodName().equals("access$0") ){
				stackLevel++;
			}
			StackTraceElement e = st[stackLevel];
			Log.d(C, e.getMethodName() + ": " + msg + " at (" + e.getFileName()+":"+e.getLineNumber() +")" );
		}
	} // debug



	public static boolean isConnected( Activity context ) {
		ConnectivityManager connectivityManager = (ConnectivityManager)
		    context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
		    networkInfo = connectivityManager.getActiveNetworkInfo();
		}
		if ( networkInfo == null ){
			Toast.makeText( context, R.string.map_wifi_disabled_toast, Toast.LENGTH_LONG).show();
			return false; //Network is not enabled
		}else{
			boolean activated = networkInfo.getState() == NetworkInfo.State.CONNECTED ? true : false ;
			if ( activated ){
				//Ping Google
				activated = verifyGoogleConnection ( context );
			}
			return activated;
		}
	} // isConnected


    private static boolean verifyGoogleConnection( Activity context ) {
    	boolean googleAvailable = false;
		if ( ! verifiedDataConnection ){
			HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response;
			try {
				response = httpclient.execute( new HttpGet( googleURL ) );
			    StatusLine statusLine = response.getStatusLine();
// HTTP status is OK even if not logged in to NTNU
			    if( statusLine.getStatusCode() == HttpStatus.SC_OK ) {
			    	verifiedDataConnection = true;
			    	if (true) {	// Connection to google should be checked. TODO
			    		googleAvailable = true;
			    	} else { // else if svar fra andre ->false
						//Closes the connection on failure
						response.getEntity().getContent().close();
	
						//throw new IOException( statusLine.getReasonPhrase() );
						Toast.makeText( context, "Cannot connect to the Web... Are you logged in?", Toast.LENGTH_LONG).show();
						Uri uri = Uri.parse( googleURL );
						context.startActivity( new Intent(Intent.ACTION_VIEW, uri) );
						googleAvailable = true;
				    }
			    } else {
			    	context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			    }
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} // try downloading db's from the Web, catch and print exceptions
			// googleAvailable = false; //test TODO
		} // if not already loaded once before
		return googleAvailable;
	}// verifyDataConnection

	/**
     * Display a dialog that user has no Internet connection
     * @param ctx1
     *
     * Code from: http://osdir.com/ml/Android-Developers/2009-11/msg05044.html
     */
    public static void showNoConnectionDialog(Context context) {
        final Context ctx = context;	// Protect original context!
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage( R.string.no_connection );
        builder.setTitle( R.string.no_connection_title );
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });

        builder.show();
    } // showNoConnectionDialog


/* Not valid for Application ?!!
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		System.getProperty("java.version");
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
*/

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
