/**
 * @contributor(s): Rune Sætre (NTNU)
 * 					Jacqueline Floch
 * @version: 		0.2
 * @date:			22 November 2011
 * @revised:		11 June 2012
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
 * This class tracks existing DBs.
 * This class keeps hold of the tabs used in import mode.
 */

package org.ubicompforall.CityExplorer.gui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
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
import org.ubicompforall.CityExplorer.data.DB;
import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.DBFileAdapter;
import org.ubicompforall.CityExplorer.data.SeparatedListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.ListView;

public class ImportWebTab extends ListActivity implements OnTouchListener{ // LocationListener, OnMultiChoiceClickListener, DialogInterface.OnClickListener{

	/*** Field containing all DBs.*/
	//ArrayList == java.util.concurrent.CopyOnWriteArrayList (including synchronized access with e.g. drawList)
	private static CopyOnWriteArrayList< DB > webDBs = new CopyOnWriteArrayList<DB>();	//To avoid duplicates!

	/*** Field containing the {@link SeparatedListAdapter} that holds all the other adapters.*/
	//private SeparatedListAdapter adapter;

	/*** Field containing this activity's {@link WebView}.*/
	private WebView webview;
	/*** Field containing this activity's {@link ListView}.*/
	private ListView lView;

	/*** Field containing an {@link ArrayList} with all categoryFolders.*/
	private ArrayList<String> webFolders;

	/*** Field containing this activity's context.*/
	//private Context context;  // What context? Context is the activity itself: for drawing output, storing folders etc.

	/*** Field containing the request code from other activities.*/
	//private int requestCode;

	private boolean loaded;	// Flag to make sure setupWebDB is only done once

	/*** Field containing the {@link SeparatedListAdapter} that holds the list of all the (other) adapters.*/
	private SeparatedListAdapter lAdapter;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setContentView( R.layout.weblayout ); // A very simple layout, named WebView

		//setContentView( R.layout.weblayout ); // A very simple layout, named WebView
		lView = getListView();
		lAdapter = new SeparatedListAdapter(this, SeparatedListAdapter.WEB_DBS);
		lView.setAdapter( lAdapter );
		lView.setCacheColorHint(0);

		webFolders = new ArrayList<String>();
		//webFolders.add("http://www.sintef.no/Projectweb/UbiCompForAll/Results/Software/City-Explorer/");
		webFolders.add( MyPreferencesActivity.getCurrentDbDownloadURL( this ) );
		debug(1, "opening web-pages: "+webFolders );
		init(); //webview, listAdapters etc.
		//webviewCache: two private dbopen() are automagically executed here (Maybe to create temp databases for this activity: webview.db and webviewCache.db)
	} // onCreate
	
	private static void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	/**
	 * Initializes the activity.
	 */
	private void init() {
		if ( webDBs.size() ==0 ){
			debug(0, "Setup webDBs? Remember setContentView(R.layout.webLayout) in other cases!" );
			setupWebDBs();	//Get the webDBs

			DBFileAdapter sintefAdapter;
			if( ! lAdapter.getSectionNames().contains("Sintef") ){ //category does not exist, create it.
				sintefAdapter = new DBFileAdapter(this, R.layout.plan_listitem, webDBs);
				lAdapter.addSection( "Sintef", sintefAdapter );
			}else{
				sintefAdapter = (DBFileAdapter) lAdapter.getAdapter( "Sintef" ); 
			}
			//Already done in the DBFileAdapter CONSTRUCTOR above
		}else{
			webview.getSettings().setJavaScriptEnabled(true);
			showDownloadPage();
		}// if webView found
	}//init
//	makeSections();

	/***
	 * Extract database URLs from the web-page source code, given as the string text
	 * @param text	the web-page source code
	 * @return		new web-page source code with multiple "<A HREF='databaseX.sqlite'>databaseX</A>" only
	 */
	public static CopyOnWriteArrayList<DB>
	 extractDBs( String text, String SERVER_URL ){
		webDBs = new CopyOnWriteArrayList<DB>();	//To avoid duplicates!
		//String BASE_URL = "http://www.sintef.no";
		//StringBuffer linkTerms = new StringBuffer(); //E.g. "(End-user Development)|(EUD)"
		// Find all the a href's
		Matcher m = Pattern.compile("<a.* href=\"([^>]+(sqlite|db|db3))\">([^<]+)</a>", Pattern.CASE_INSENSITIVE).matcher(text);
		while (m.find()) {
			File URL = new File( m.group(1) );	// group 0 is everything, group 1 is (www.and.so.on)
			if ( URL.getAbsolutePath().charAt(0) == '/' ){
				URL = new File( SERVER_URL + URL ); 
			}
			webDBs.add( new DB(URL.getParentFile().getParent(), URL.getParentFile().getName(), URL.getName(), m.group(3) ) );
			debug(1, "Added: "+ URL+" "+m.group(3) );
		}

		//linkTerms.append( "<BR><HR><BR>\n" );
		//linkTerms.append( text );
		//return linkTerms.toString();
		return webDBs;
	}//extractDBs

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		if(l.getAdapter().getItemViewType(pos) == SeparatedListAdapter.TYPE_SECTION_HEADER){
			//Pressing a header
			debug(0, "Pressed a header... Dummy!");
		}else{
			DB selectedDb = (DB) l.getAdapter().getItem(pos);
			//debug(2, "requestCode is "+ requestCode ); //RequestCode == 0
			debug(1, "I just found DB "+selectedDb.getLabel() );
			String DEFAULT_DBFOLDER = getResources().getText( R.string.default_dbFolderName ).toString();
			File currentDbFile = new File( getDatabasePath( DEFAULT_DBFOLDER ).getAbsolutePath()+"/"+ selectedDb.getLabel() );
			DBFactory.changeInstance( this, currentDbFile );
			//startActivity( new Intent( this, PlanActivity.class) );
			finish();
		}//if header: skip, else select and finish
	} // onListItemClick

	/***
	 * @param webview
	 * @return status: loaded or not
	 */
	public boolean setupWebDBs() {
		if ( ! loaded ){
			String responseString;
			HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response;
		    for (String webURL : webFolders){
		    	debug(1, "Getting webFolder "+webURL);
		    	if ( CityExplorer.pingConnection( this, webURL ) ){
					try {
						response = httpclient.execute( new HttpGet(webURL) );
					    StatusLine statusLine = response.getStatusLine();
					    if( statusLine.getStatusCode() == HttpStatus.SC_OK ){
					        ByteArrayOutputStream out = new ByteArrayOutputStream();
					        response.getEntity().writeTo(out);
					        out.close();
					        responseString = out.toString();
		
							String SERVER_URL = "http://"+(new URL(webURL).getHost());
							webDBs = extractDBs( responseString, SERVER_URL );
							if (webDBs.equals("") ){
								//webview.loadData( responseString, "text/html", "utf-8" );
								webview.loadData( "Loading page...", "text/html", "utf-8" );
								webview.loadUrl( webURL );
							}else{
								debug(1, "searching host "+SERVER_URL+", extracted is "+webDBs );
								loaded=true;
							}
					    }else{
							//Closes the connection on failure
							response.getEntity().getContent().close();
							throw new IOException( statusLine.getReasonPhrase() );
					    }
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
						debug(-1, "Network unavailable? "+ e.getMessage()+"\n...weburl was "+ webURL );
					} // try downloading db's from the Web, catch and print exceptions
		    	}else{
		    		debug(0, "Why!?" );
					CityExplorer.showNoConnectionDialog( this, "", "No Cache!", null );
		    		printNotAvailable();
		    	}
		    }// for all web-locations with DBs on them	
		} // if not already loaded once before
		return loaded;
	} // setupWebDBs (called from init / from onCreate... Too slow?)

	private void printNotAvailable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

//Not in use because everything is on the sintef-url now...
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
	public boolean onTouch(View v, MotionEvent event) {
		debug(-1, "Hello!" );
		showDownloadPage();
		return false;
	}//On touch

	private void showDownloadPage() {
		if ( CityExplorer.ensureConnected(this) ){ //For downloading DBs
			setupWebDBs();
			showDownloadDialog (this);
		}else{
			CityExplorer.showNoConnectionDialog( this, "", "", null );
			webview.loadData("Click to load online databases from web<BR>", "text/html", "utf-8");
			webview.setOnTouchListener(this);
		}
	}//showDownloadPage



	/**
     * Display a dialog for explaining the user the process of downloading a database.
     */
	 private static void showDownloadDialog (Activity context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		
		builder.setMessage( R.string.download_dialog );
		builder.setTitle( R.string.download_dialog_title );

		builder.setPositiveButton( R.string.download_dialog_ok, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	return;
		    }
		} );

		builder.show();
	}//showDownloadDialog
	

}//ImportLocalTab


//OLD CODE: WebView-Based
/***
 * Extract database URLs from the web-page source code, given as the string text
 * @param text	the web-page source code
 * @return		new web-page source code with multiple "<A HREF='databaseX.sqlite'>databaseX</A>" only
 */
//public static String extractDBs( String text, String SERVER_URL ){
//	//String BASE_URL = "http://www.sintef.no";
//	StringBuffer linkTerms = new StringBuffer(); //E.g. "(End-user Development)|(EUD)"
//	// Find all the a href's
//	Matcher m = Pattern.compile("<a.* href=\"([^>]+(sqlite|db|db3))\">([^<]+)</a>", Pattern.CASE_INSENSITIVE).matcher(text);
//	while (m.find()) {
//		String URL = m.group(1);	// group 0 is everything
//		if ( URL.charAt(0) == '/' ){
//			URL = SERVER_URL + URL; 
//		}
//		linkTerms.append( "<A HREF=\""+ URL +"\">"+m.group(3)+"</A><BR>\n" );
//	}
//
//	//linkTerms.append( "<BR><HR><BR>\n" );
//	//linkTerms.append( text );
//
//	return linkTerms.toString();
//}//extractDBs
