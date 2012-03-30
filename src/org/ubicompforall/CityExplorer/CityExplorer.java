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

import java.io.ByteArrayOutputStream;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
	
	//CONSTANTS for X
	public static final int TYPE_ALL = 0;
	public static final int TYPE_FREE = 1;
	public static final int TYPE_FIXED = 2;

	public static final int MSG_DOWNLOADED = 0;
	
	//CONSTANTS for result requests
	public static final int REQUEST_LOCATION = 10;
	
	//Public flags
	public static boolean DATACONNECTION_NOTIFIED = false;

	//Other fields
	private static ProgressDialog pd;

	/***
	 * The global current db connection
	 */
	public static DatabaseInterface db;
	//private static boolean verifiedDataConnection;
	//private static String googleURL = "http://www.google.com";
	
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
		
		//Always warn about missing data connection after startup/restart
		DATACONNECTION_NOTIFIED = false;
		
		//Verify that specific URLs on the web are available!
		//verifiedDataConnection = false;
	}//onCreate

    @Override
    public void onTerminate() {
    	//Save preferences now? Taken care of automatically by SharedPreferences object?

        //Local debug (stacktrace level = 3)
        StackTraceElement st = Thread.currentThread().getStackTrace()[2];
		Log.d(C, st.getFileName().replace("java", "")+st.getLineNumber()+'~'+st.getMethodName()+'~'+"Save the preferences now?");
    }//onTerminate


// STATIC METHODS

	/*
	 * Debug method to include the filename, line-number and method of the caller
	 */
	public static void debug(int d, String msg) {
		if (DEBUG >= d) {
			StackTraceElement[] st = Thread.currentThread().getStackTrace();
			int stackLevel = 2;
			while ( st[stackLevel].getMethodName().equals("debug") || st[stackLevel].getMethodName().matches("access\\$\\d+") ){
				stackLevel++;
			}
			StackTraceElement e = st[stackLevel];
			Log.d(C, e.getMethodName() + ": " + msg + " at (" + e.getFileName()+":"+e.getLineNumber() +")" );
		} // if verbose enough
	} // debug



	public static boolean ensureConnected( Activity context ) {
		ConnectivityManager connectivityManager	= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
		    networkInfo = connectivityManager.getActiveNetworkInfo();
		}
		if ( networkInfo == null ){
			return false; //Network is not enabled
		}else{

			boolean activated = networkInfo.getState() == NetworkInfo.State.CONNECTED ? true : false ;
/**			if ( activated ){
				//Ping Google
				activated = verifyGoogleConnection ( context );
			}
*/
			//Toast.makeText( context, "Network state is "+networkInfo.getState(), Toast.LENGTH_LONG).show();

			return activated;

		}
	} // isConnected


	//Ping Google
    public static boolean pingConnection( Activity context, String url ) {
    	boolean urlAvailable = false;
    	boolean connectionAvailable = ensureConnected(context);	// googleAvailable = false;
		if ( connectionAvailable ){
			showProgress( context );
			HttpClient httpclient = new DefaultHttpClient();
			try {
			    HttpResponse response = httpclient.execute( new HttpGet( url ) );
				StatusLine statusLine = response.getStatusLine();
				debug(0, "statusLine is "+statusLine );

				// HTTP status is OK even if not logged in to NTNU
				Toast.makeText( context, "Status-line is "+statusLine, Toast.LENGTH_LONG).show();
				if( statusLine.getStatusCode() == HttpStatus.SC_OK ) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					String responseString = out.toString();
					if ( responseString.matches( url ) ) {	// Connection to url should be checked. TODO
						urlAvailable = true;
					}
				} else {
					//Closes the connection on failure
					response.getEntity().getContent().close();
			
					//throw new IOException( statusLine.getReasonPhrase() );
					Toast.makeText( context, "Cannot connect to the Web... Are you logged in?", Toast.LENGTH_LONG).show();

					Uri uri = Uri.parse( url );
					context.startActivity( new Intent(Intent.ACTION_VIEW, uri) );
					connectionAvailable = true;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IllegalStateException e){	// Caused by bad url for example, missing http:// etc. Can still use cached maps...
				connectionAvailable=false;
				debug(0, "Missing http:// in "+url+" ?" );
			} catch (IOException e) { // e.g. UnknownHostException // try downloading db's from the Web, catch (and print) exceptions
				connectionAvailable=false;
			}
		} // if not already loaded once before
		return urlAvailable;
	}// pingConnection

	public static void showProgress( Activity context, String... msg ){
		String status = "Loading";
		if ( ! (msg == null || msg[0].equals("") ) ){
			status = msg[0];
		}
		pd = ProgressDialog.show( context, "", status+"...", true, false);
//		new Thread() {
//		    public void run() {
//		        handler.sendEmptyMessage( CityExplorer.MSG_DOWNLOADED ); 
//		    }
//		}.start();
	}// showProgress

	@SuppressWarnings("unused")
	private static Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CityExplorer.MSG_DOWNLOADED:
				pd.dismiss();
				//Toast.makeText( context, "What to do when ready", Toast.LENGTH_LONG).show();
				debug(0, "What to do when ready" );
				break;
	        }//switch - case
	    }//handleMessage
	}; // new Handler class
	
	/**
     * Display a dialog that user has no Internet connection
	 * @param requestCode ID for the calling Activity
     *
     * Code from: http://osdir.com/ml/Android-Developers/2009-11/msg05044.html
     */
    public static void showNoConnectionDialog( final Activity context, final String strA, final String strB, final Intent intentB, int requestCode ) {

    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		if ( strA == "" ){
		    builder.setMessage( R.string.no_connection );
		}else{
		    builder.setMessage( strA );
		}
		builder.setTitle( R.string.no_connection_title );
		builder.setPositiveButton( R.string.settings, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        context.startActivity( new Intent(Settings.ACTION_WIRELESS_SETTINGS) );
		    }
		} );
		
		String cancelText = strB;
		if ( cancelText == ""){
			cancelText = context.getResources().getString( R.string.cancel );
		}
		builder.setNegativeButton( cancelText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (intentB != null){
					context.startActivityForResult( intentB, CityExplorer.REQUEST_LOCATION );
					dialog.dismiss();
		    	}
				return;
		    }
		} );

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    public void onCancel(DialogInterface dialog) {
				Toast.makeText( context, "CANCELLED!", Toast.LENGTH_LONG).show();
		        context.startActivity( intentB );
		        return;
		    }
		});
		
		builder.show();
		DATACONNECTION_NOTIFIED = true;
    } // showNoConnectionDialog

    
    // HELPER CLASSES //
    
//    public class LoadingScreen extends Activity{
//        private LoadingScreen loadingScreen;
//        Intent i = new Intent(this, HomeScreen.class);
//         /** Called when the activity is first created. */
//        @Override
//            public void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                setContentView(R.layout.loading);
//
//
//                CountDownTimer timer = new CountDownTimer(10000, 1000) //10seceonds Timer
//                {
//                     @Override
//                      public void onTick(long l) 
//                      {
//
//                      }
//
//                      @Override
//                      public void onFinish() 
//                      {
//
//                          loadingScreen.finishActivity(0);
//                          startActivity(i);
//
//                      };
//                }.start();
//        }
//    }//end of class

    // END HELPER CLASSES //

    
    /* Not valid for Application? Only for "implements LocationListener"
	@Override
	public void onLocationChanged(Location location) {
		System.getProperty("java.version");
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
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
