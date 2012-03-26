/**
 * @contributor(s): Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			22 November 2011
 * @revised:		15 December 2011
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
 * This class tracks existing DBs.
 * This class keeps hold of the tabs used in import mode.
 */

package org.ubicompforall.CityExplorer.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;

public class ImportWebTab extends Activity implements OnTouchListener{ // LocationListener, OnMultiChoiceClickListener, DialogInterface.OnClickListener{

	/*** Field containing all DBs.*/
	//private ArrayList<Poi> allPois = new ArrayList<Poi>();
	// private ArrayList<DB> allDBs = new ArrayList<DB>();

	/*** Field containing the {@link SeparatedListAdapter} that holds all the other adapters.*/
	//private SeparatedListAdapter adapter;

	/*** Field containing this activity's {@link WebView}.*/
	private WebView webview;

	/*** Field containing an {@link ArrayList} with all categoryFolders.*/
	private ArrayList<String> webFolders;

	/*** Field containing this activity's context.*/
	//private Context context;  // What context? Context is the activity itself: for drawing output, storing folders etc.

	/*** Field containing the request code from other activities.*/
	private int requestCode;

	private boolean loaded = false;	// Flag to make sure setupWebDB is only done once

	/*** Field containing a single DB.*/
	//private POI poi;
	//private DB db;

	/*** Field giving access to databaseUpdater methods.*/
	//private DatabaseUpdater du;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView( R.layout.weblayout ); // A very simple, named WebView

		//String URL = "http://www.sintef.no/Projectweb/UbiCompForAll/Results/Software/City-Explorer/";
		webFolders = new ArrayList<String>();
		webFolders.add("http://www.sintef.no/Projectweb/UbiCompForAll/Results/Software/City-Explorer/");
		debug(0, "opening web-pages: "+webFolders );
		
		init(); //webview etc.
		//webviewCache: two private dbopen() are automagically executed here (Maybe to create temp databases for this activity: webview.db and webviewCache.db)
	} // onCreate
	
	private static void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	/**
	 * Initializes the activity.
	 */
	private void init() {
		webview = (WebView) findViewById(R.id.myWebView);
		if (webview == null){
			debug(0, "Where is wv? Remember setContentView(R.layout.webLayout)!" );
		}else{
			webview.getSettings().setJavaScriptEnabled(true);
			showDownloadPage();
		}// if webView found
	}//init
//	adapter = new SeparatedListAdapter(this, SeparatedListAdapter.LOCAL_DBS);
//	allDBs = getAllDBs();
//	//Init View-adapters etc.
//	makeSections();

	/***
	 * Extract database URLs from the web-page source code, given as the string text
	 * @param text	the web-page source code
	 * @return		new web-page source code with multiple "<A HREF='databaseX.sqlite'>databaseX</A>" only
	 */
	public static String extractDBs( String text, String SERVER_URL ){
		//String BASE_URL = "http://www.sintef.no";
		StringBuffer linkTerms = new StringBuffer(); //E.g. "(End-user Development)|(EUD)"
		// Find all the a href's
		Matcher m = Pattern.compile("<a.* href=\"([^>]+(sqlite|db|db3))\">([^<]+)</a>", Pattern.CASE_INSENSITIVE).matcher(text);
		while (m.find()) {
			String URL = m.group(1);	// group 0 is everything
			if ( URL.charAt(0) == '/' ){
				URL = SERVER_URL + URL; 
			}
			linkTerms.append( "<A HREF=\""+ URL +"\">"+m.group(3)+"</A><BR>\n" );
		}

		//linkTerms.append( "<BR><HR><BR>\n" );
		//linkTerms.append( text );

		return linkTerms.toString();
	}//extractDBs

	/***
	 * @param webview
	 * @return
	 */
	public boolean setupWebDBs() {
		if ( ! loaded ){
			String responseString;
			HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response;
		    for (String webURL : webFolders){
		    	debug(0, "Getting webFolder "+webURL);
				try {
					response = httpclient.execute( new HttpGet(webURL) );
				    StatusLine statusLine = response.getStatusLine();
				    if( statusLine.getStatusCode() == HttpStatus.SC_OK ){
				        ByteArrayOutputStream out = new ByteArrayOutputStream();
				        response.getEntity().writeTo(out);
				        out.close();
				        responseString = out.toString();
	
						String SERVER_URL = "http://"+(new URL(webURL).getHost());
						String linkTerms = extractDBs( responseString, SERVER_URL );
						if (linkTerms.equals("") ){
							webview.loadData( responseString, "text/html", "utf-8" );
						}else{
							debug(2, "searching host "+SERVER_URL+", extracted is "+linkTerms );
							webview.loadData(linkTerms, "text/html", "utf-8" );
						}
						//webview.loadData( responseString, "text/html", "utf-8" );
				    }else{
						//Closes the connection on failure
						response.getEntity().getContent().close();
						throw new IOException( statusLine.getReasonPhrase() );
				    }
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} // try downloading db's from the Web, catch and print exceptions
		    }// for all web-locations with DBs on them
			loaded=true;
		} // if not already loaded once before
		return false;
	} // setupWebDBs (called from init / from onCreate... Too slow?)

	@Override
	protected void onResume() {
		super.onResume();
	}

//	/**
//	 * Makes the category sections that is shown in the list. 
//	 */
//	private void makeSections(){
//		for (DB db : allDBs){
//			if( !adapter.getSectionNames().contains(db.getCategory())){ //category does not exist, create it.
//				ArrayList<DB> list = new ArrayList<DB>();
//
//				DBFileAdapter testAdapter;
//				testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
//				adapter.addSection(db.getCategory(), testAdapter);
//			}
//			((DBFileAdapter)adapter.getAdapter(db.getCategory())).add(db);//add to the correct section
//			((DBFileAdapter)adapter.getAdapter(db.getCategory())).notifyDataSetChanged();
//		}
//	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (requestCode == 2){
			debug(0, "Code Two!");
			menu.removeItem(R.id.planMenuNewPoi);
			menu.removeItem(R.id.planMenuSharePois);
			//menu.removeItem(R.id.planMenuUpdatePois);
			menu.removeItem(R.id.planMenuAddPois);
		}
		return true;
	}// onPrepareOptionsMenu

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.setGroupVisible(R.id.planMenuGroupTrip, false);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		return false;
	} // onOptionsItemSelected( MenuItem )


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		showDownloadPage();
		return false;
	}//On touch

	private void showDownloadPage() {
		if ( CityExplorer.ensureConnected(this) ){ //For downloading DBs
			setupWebDBs();
		}else{
			CityExplorer.showNoConnectionDialog( this );
			webview.loadData("Click to load online databases from web<BR>", "text/html", "utf-8");
			webview.setOnTouchListener(this);
		}
	}//showDownloadPage


}//ImportLocalTab
