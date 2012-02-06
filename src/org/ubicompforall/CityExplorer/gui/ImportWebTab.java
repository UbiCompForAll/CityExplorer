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
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.View.OnTouchListener;
import android.webkit.WebView;

public class ImportWebTab extends Activity{ // LocationListener, OnMultiChoiceClickListener, DialogInterface.OnClickListener{

	/*** Field containing all DBs.*/
	//private ArrayList<Poi> allPois = new ArrayList<Poi>();
	// private ArrayList<DB> allDBs = new ArrayList<DB>();

	/*** Field containing the {@link SeparatedListAdapter} that holds all the other adapters.*/
	//private SeparatedListAdapter adapter;

	/*** Field containing this activity's {@link WebView}.*/
	private WebView webview;

	/*** Field containing an {@link ArrayList} of the categoryFolders.*/
	// private ArrayList<String> categoryFolders;

	/*** Field containing this activity's context.*/
	private Context context;  // What context? Context is the activity itself: for drawing output, storing folders etc.

	/*** Field containing the request code from other activities.*/
	private int requestCode;

	private boolean loaded=false;

	/*** Field containing a single DB.*/
	//private POI poi;
	//private DB db;

	/*** Field giving access to databaseUpdater methods.*/
	//private DatabaseUpdater du;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weblayout);

		debug(0, "going to open web-page" );
		init();		
	} // onCreate

	
	private static void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug



	/**
	 * Initializes the activity.
	 */
	private void init() {
		//setContext(this);
		webview = (WebView) findViewById(R.id.webview);
		if (webview == null){ 
			debug(0, "Where is wv? Remember setContentView(R.layout.webLayout)!" );
		}else{
		    //webview.getSettings().setJavaScriptEnabled(true);
			//webview.loadData("Click to load online databases from web<BR>", "text/hml", "utf-8");
			//webview.setOnTouchListener(this);
			
			this.setupWebDBs( webview );
		}// if webView found

//		adapter = new SeparatedListAdapter(this, SeparatedListAdapter.LOCAL_DBS);

//		allDBs = getAllDBs();

		getResources();

		//Init View-adapters etc.
//		makeSections();

		//initGPS(); //For maps?
	}//init

	public static String extractDBs(String text){
		String BASE_URL = "http://www.sintef.no";
		StringBuffer linkTerms = new StringBuffer(); //E.g. "(End-user Development)|(EUD)"
		// Find all the a href's
		Matcher m = Pattern.compile("<a href=\"([^>]+(sqlite|db|db3))\">([^<]+)</a>", Pattern.CASE_INSENSITIVE).matcher(text);
		while (m.find()) {
			linkTerms.append( "<A HREF=\"" + BASE_URL + m.group(1)+ "\">"+m.group(3)+"</A><BR>\n" ); // group 0 is everything
		}
		return linkTerms.toString();
	}//extractDBs

	
	public boolean setupWebDBs(WebView webview) {
		String URL = "http://www.sintef.no/Projectweb/UbiCompForAll/Results/Software/City-Explorer/";
		if ( ! loaded ){
			loaded=true;
			String responseString;
			debug(0, "how touching ;-)");
			HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response;
			try {
				response = httpclient.execute(new HttpGet(URL));
			    StatusLine statusLine = response.getStatusLine();
			    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			        ByteArrayOutputStream out = new ByteArrayOutputStream();
			        response.getEntity().writeTo(out);
			        out.close();
			        responseString = out.toString();
			        //..more logic
					String linkTerms = extractDBs( responseString );
					//debug(1, "found "+linkTerms.size()+", extracted is "+linkTerms );
					webview.loadData(linkTerms, "text/hml", "utf-8" );
			    } else{
			        //Closes the connection.
			        response.getEntity().getContent().close();
			        throw new IOException(statusLine.getReasonPhrase());
			    }
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	} // setupDBs (called from init / from onCreate... Too slow?)

/**	private ArrayList<DB> getAllDBs() {
		if (categoryFolders == null){
			debug(0, "categoryFolders NOT FOUND!" );
		}else{
			categoryFolders.add( getFilesDir().getPath() );
			for ( String path : categoryFolders ){
				File dir = new File(path);
				File[] files = dir.listFiles();
				if (files == null){
					debug(0, "No files found in "+dir.getPath() );
				}else{
					for ( int f=0; f<files.length ; f++ ){
						File file = files[f];
						allDBs.add( new DB( file.getName(), dir.getName() ) );
					}// for each file
				}// if not null-pointer path->files
			} // for each folder
		} // if not null-pointer
		return allDBs;
	}//getAllDBs
**/
	
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

	/**
	 * Updates the category sections in the list, e.g. after choosing filtering.
	@SuppressWarnings("unused")
	private void updateSections(){
		allDBs = new FileSystemConnector().getAllDBs();
		ArrayList<String> sectionsInUse = new ArrayList<String>(); 
//		for (DB db : allDBs){
//			sectionsInUse.add(db.getCategory());
//			if(!adapter.getSectionNames().contains(db.getCategory())){
//				ArrayList<DB> list = new ArrayList<DB>();
//				list.add(db);
//				DBFileAdapter testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
//				adapter.addSection(db.getCategory(), testAdapter);
//			}//if contains category
//		}//for DBs
//		ArrayList<String> ListSections = (ArrayList<String>) adapter.getSectionNames().clone();
//		for(String sec : ListSections){
//			if( !sectionsInUse.contains(sec) && !sec.equalsIgnoreCase("Favourites")){	
//				adapter.removeSection(sec);
//			}
//		}//for sections
		//lv.setAdapter(adapter);
	}//updateSections
	 */


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


	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}



}//ImportLocalTab
