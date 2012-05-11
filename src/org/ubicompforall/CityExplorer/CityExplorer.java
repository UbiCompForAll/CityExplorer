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
 * @description: Package for central CityExplorer constants and switches
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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * This is an {@link android.app.Application} class.  Ordinarily you would use
 * a class like this as a central repository for information that might be shared between multiple
 * activities.
 * In this case, we have not defined any specific work for this Application.
 * See samples/tests/ApiDemosApplicationTests for an example of how to perform unit tests on an Application object.
 */
public class CityExplorer extends Application{ // implements LocationListener // For GPS
	
	public static final int DEBUG = 1;
	public static final String C = "CityExplorer";
	
	//SWITCH FOR UBICOMPOSER CONNECTIVITY
	public static final boolean ubiCompose = false;	// true or false ;-)

	// Constant keys for GENERAL SETTINS
	public static final String GENERAL_SETTINGS = "SETTINGS";
	public static final String SETTINGS_DB_NAME = "Name";
	public static final String SETTINGS_DB_URL = "Url";
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
	public static final int REQUEST_KILL_BROWSER = 11;
	//public static final int REQUEST_LOCATION = 12;

	//public static final String ALL = "ALL";
	public static final String FAVORITES = "FAVORITES";
	public static final String MAGIC_URL = "http://www.idi.ntnu.no/~satre/ubicomp/cityexplorer/launchApp.html";
	
	//Public flags
	public static boolean DATACONNECTION_NOTIFIED = false;

	//Other fields
	private static ProgressDialog pd;

	// CONSTANTS for sharing
	public static final String SHARED_FILE = "cityexplorer.txt";
	//public static final String SHARED_FILE_PATH = "/mnt/sdcard/../.."; // introduced as work around for Gmail - but does not seem to work
	public static final String SHARED_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

	
	/***
	 * The global current db connection
	 */
	public static DatabaseInterface db;
	
	@Override
	public void onCreate() {
		debug(0, "Start CityExplorer.java" );
		/*
		 * This populates the default values from the preferences XML file. See
		 * {@link DefaultValues} for more details.
		 */
		PreferenceManager.setDefaultValues( this, R.xml.default_values, false);

		//MapsActivity.initGPS( this ); //RS-120501 Use only when needed (E.g. Maps)
		db = DBFactory.getInstance(this); // DB-loading?  Initialize the single instance here :-) // Or Delay?
		
		//Always warn about missing data connection after startup/restart
		DATACONNECTION_NOTIFIED = false;
		//debug(0, "Started CityExplorer.java" );
	}//onCreate

    @Override
    public void onTerminate() {
    	//Save preferences now? Taken care of automatically by SharedPreferences object?

        //Local debug (stacktrace level = 3)
        StackTraceElement st = Thread.currentThread().getStackTrace()[2];
		Log.d(C, st.getFileName().replace("java", "")+st.getLineNumber()+'~'+st.getMethodName()+'~'+"Save the preferences now?");
    }//onTerminate

////////////////////////////////////////////////////////////////////////////
// STATIC METHODS

	/***
	 * Debug method to include the filename, line-number and method of the caller
	 */
	public static void debug(int d, String msg) {
		if (DEBUG >= d) {
			StackTraceElement[] st = Thread.currentThread().getStackTrace();
			int stackLevel = 2;
			while ( stackLevel < st.length-1
			 && ( st[stackLevel].getMethodName().equals("debug") || st[stackLevel].getMethodName().matches("access\\$\\d+") ) ){
				//|| st[stackLevel].getMethodName().matches("run")
				stackLevel++;
			}
			StackTraceElement e = st[stackLevel];
			if ( d < 0 ){ //error
				Log.e(C, e.getMethodName() + ": " + msg + " at (" + e.getFileName()+":"+e.getLineNumber() +")" );
			}else{ //debug
				Log.d(C, e.getMethodName() + ": " + msg + " at (" + e.getFileName()+":"+e.getLineNumber() +")" );
			}//if debug, else error
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


	/***
	 * Ping Google
	 * Start a browser if the page contains a (log-in) "redirect="
	 */
    public static boolean pingConnection( Activity context, String url ) {
    	boolean urlAvailable = false;
		if ( ensureConnected(context) ){
			showProgressDialog( context );
			HttpClient httpclient = new DefaultHttpClient();
			try {
			    HttpResponse response = httpclient.execute( new HttpGet( url ) );
				StatusLine statusLine = response.getStatusLine();
				debug(2, "statusLine is "+statusLine );

				// HTTP status is OK even if not logged in to NTNU
				//Toast.makeText( context, "Status-line is "+statusLine, Toast.LENGTH_LONG).show();
				if( statusLine.getStatusCode() == HttpStatus.SC_OK ) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					String responseString = out.toString();
					if ( responseString.contains( "redirect=" ) ) {	// Connection to url should be checked.
						debug(2, "Redirect detected for url: "+url );
						//Toast.makeText( context, "Mismatched url: "+url, Toast.LENGTH_LONG).show();
					}else{
						urlAvailable = true;
					}// if redirect page, else probably OK
				}else{//if status OK, else: Closes the connection on failure
					response.getEntity().getContent().close();
				}//if httpStatus OK, else close

				//Start browser to log in
				if ( ! urlAvailable ) {
					//throw new IOException( statusLine.getReasonPhrase() );

					//String activity = Thread.currentThread().getStackTrace()[3].getClassName();
					Toast.makeText( context, "Web access needed! Are you logged in?", Toast.LENGTH_LONG).show();
					//Uri uri = Uri.parse( url +"#"+ context.getClass().getCanonicalName() );
					Uri uri = Uri.parse( url +"?activity="+ context.getClass().getCanonicalName() );
					debug(0, "Pinging magic url: "+uri );
					debug(0, " Need the web for uri: "+uri );
					context.startActivityForResult( new Intent(Intent.ACTION_VIEW, uri ), REQUEST_KILL_BROWSER );
					//urlAvailable=true;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IllegalStateException e){	// Caused by bad url for example, missing http:// etc. Can still use cached maps...
				urlAvailable=false;
				debug(0, "Missing http:// in "+url+" ?" );
			} catch (IOException e) { // e.g. UnknownHostException // try downloading db's from the Web, catch (and print) exceptions
				e.printStackTrace();
				urlAvailable=false;
			}
		} // if not already loaded once before
		return urlAvailable;
	}// pingConnection

	public static void showProgressDialog( Context context, String... msg ){
		String status = "Loading";
		if ( ! (msg == null || msg.length==0 || msg[0].equals("") ) ){
			status = msg[0];
		}
		pd = ProgressDialog.show( context, "", status+"...", true, false);
		timerDelayRemoveDialog(1000, pd);
//		new Thread() {
//		    public void run() {
//		        handler.sendEmptyMessage( CityExplorer.MSG_DOWNLOADED ); 
//		    }
//		}.start();
	}// showProgress

//	private static Handler handler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case CityExplorer.MSG_DOWNLOADED:
//				pd.dismiss();
//				//Toast.makeText( context, "What to do when ready", Toast.LENGTH_LONG).show();
//				debug(0, "What to do when ready" );
//				break;
//	        }//switch - case
//	    }//handleMessage
//	}; // new Handler class

	/***
	 * 
	 * @param time	In milliseconds
	 * @param d
	 */
	public static void timerDelayRemoveDialog(long time, final Dialog d){
	    new Handler().postDelayed(new Runnable() {
	        public void run(){
	        	if (d!= null){
	        		debug(2, "d is "+ d );
	        		d.dismiss();  
	        	}
	        }
	    }, time); 
	}
	
	/**
     * Display a dialog that user has no Internet connection
     * Code from: http://osdir.com/ml/Android-Developers/2009-11/msg05044.html
     */
	public static void showNoConnectionDialog( final Activity context, final String msg, final String cancelButtonStr, final Intent cancelIntent ) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		if ( msg == "" ){
		    builder.setMessage( R.string.no_connection );
		}else{
		    builder.setMessage( msg );
		}
		builder.setTitle( R.string.no_connection_title );
		builder.setPositiveButton( R.string.settings, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        context.startActivity( new Intent(Settings.ACTION_WIRELESS_SETTINGS) );
		    }
		} );
		
		String cancelText = cancelButtonStr;
		if ( cancelText == ""){
			cancelText = context.getResources().getString( R.string.cancel );
		}
		builder.setNegativeButton( cancelText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if ( cancelIntent != null ){
					context.startActivityForResult( cancelIntent, CityExplorer.REQUEST_LOCATION );
					dialog.dismiss();
		    	}
				return;
		    }
		} );

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    public void onCancel(DialogInterface dialog) {
		    	if ( context == null ){
		    		debug(0, "OOOPS!");
		    	}else{
		    		Toast.makeText( context, "CANCELLED!", Toast.LENGTH_LONG).show();
					if (cancelIntent != null){
						context.startActivity( cancelIntent );
					}
		    	}
		        return;
		    }
		} );
		
		builder.show();
		DATACONNECTION_NOTIFIED = true;
	} // showNoConnectionDialog

	/**
	 * Return the URI of the file used when sharing PoI
	 * @param context of the calling Activity
     */
	public static String getSharedFileName (Context c) {
    	//return SHARED_FILE_PATH + c.getFilesDir() +"/"+ SHARED_FILE;
    	return Environment.getExternalStorageDirectory() + CityExplorer.SHARED_FILE;
	}
    
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
