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

import java.io.File;
import java.util.ArrayList;
import org.ubicompforall.CityExplorer.data.FileSystemConnector;
//import org.ubicompforall.CityExplorer.data.DatabaseUpdater;
import org.ubicompforall.CityExplorer.data.DB;
import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class ImportWebTab extends Activity{ // implements LocationListener, OnMultiChoiceClickListener, DialogInterface.OnClickListener{

	/** Field containing the String of the category settings, used in shared preferences. */
	private static String CATEGORY_SETTINGS = "catset";

	/*** Field containing all DBs.*/
	//private ArrayList<Poi> allPois = new ArrayList<Poi>();
	private ArrayList<DB> allDBs = new ArrayList<DB>();

	/*** Field containing the {@link SeparatedListAdapter} that holds all the other adapters.*/
	//private SeparatedListAdapter adapter;

	/*** Field containing this activity's {@link WebView}.*/
	private WebView webview;

	/*** Field containing an {@link ArrayList} of the categoryFolders.*/
	private ArrayList<String> categoryFolders;

	/*** Field containing this activity's context.*/
	private Context context;  // What context? Context is the activity itself: for drawing output, storing folders etc.

	/*** Field containing the request code from other activities.*/
	private int requestCode;

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

	
	private void debug( int level, String message ) {
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
		    webview.getSettings().setJavaScriptEnabled(true);
			webview.loadUrl("http://www.sintef.no/Projectweb/UbiCompForAll/Results/Software/City-Explorer/");
		}

//		adapter = new SeparatedListAdapter(this, SeparatedListAdapter.LOCAL_DBS);

		allDBs = getAllDBs();

		getResources();

		//Init View-adapters etc.
//		makeSections();

		//initGPS(); //For maps?
	}//init

	private ArrayList<DB> getAllDBs() {
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
	 */
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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

//		if(item.getItemId() == R.id.planMenuNewDB){
//			Intent newDB = new Intent(ImportLocalTab.this, NewDBActivity.class);
//			startActivity(newDB);
//		}
//
//		if(item.getItemId() == R.id.planMenuFilter)
//		{
//			categoryFolders = FileSystemConnector.getInstance(this).getUniqueCategoryNames();
//			Collections.sort(categoryFolders);
//
//			AlertDialog.Builder alert = new AlertDialog.Builder(this);
//			alert.setTitle("Filter");
//			ArrayList<String> cat = (ArrayList<String>) categoryFolders.clone();
//			cat.add(0, "Favourites");
//
//
//			boolean[] CheckedCat = new boolean[cat.size()];
//			for (String c : cat)
//			{
//				if(CheckedcategoryFolders.get(c) == null)
//				{
//					CheckedcategoryFolders.put(c, true);
//				}
//				CheckedCat[cat.indexOf(c)] = CheckedcategoryFolders.get(c);
//			}
//
//			String[] array = new String[cat.size()];
//			cat.toArray(array);
//			alert.setMultiChoiceItems(array, CheckedCat, this);
//			alert.setPositiveButton("OK", this);
//			alert.create();
//			alert.show();
//		}
//
//		if(item.getItemId() == R.id.planMenuUpdateDBs)
//		{
//			if(requestCode == DOWNLOAD_DB){
//				if (downloadedDBs==null){
//					Toast.makeText(this, "No locations selected", Toast.LENGTH_LONG).show();
//					return false;
//				}else {
//					int[] res = du.storeDBs(downloadedDBs);
//					Toast.makeText(context, res[0]+" locations added, "+res[1]+" locations updated", Toast.LENGTH_LONG).show();
//				}
//				finish();
//			}else {				
//				Intent downloadDB= new Intent(ImportLocalTab.this, ImportLocalTab.class);
//				downloadDB.putExtra("requestCode", DOWNLOAD_DB);
//				startActivityForResult(downloadDB, DOWNLOAD_DB);
//			}
//		}
//
//		if(item.getItemId() == R.id.planMenuShareDBs)
//		{
//			if(requestCode == SHARE_DB){
//				if (shareDBs==null){
//					Toast.makeText(this, "No locations selected", Toast.LENGTH_LONG).show();
//					return false;
//				}else {
//					Sharing.send(this, shareDBs);
//					shareDBs = null;
//				}
//				finish();
//			}else {
//				Intent shareDB= new Intent(ImportLocalTab.this, ImportLocalTab.class);
//				shareDB.putExtra("requestCode", SHARE_DB);
//				startActivityForResult(shareDB, SHARE_DB);
//			}
//		}
//
//		if(item.getItemId() == R.id.planMenuAddDBs)
//		{
//			if(requestCode == PlanTabTrip.ADD_TO_TRIP || requestCode == TripListActivity.ADD_TO_TRIP){
//				if (selectedDBs==null){
//					Toast.makeText(this, "No locations selected", Toast.LENGTH_LONG).show();
//					return false;
//				}else {
//					for (DB p : selectedDBs) {
//						trip.addDB(p);
//						FileSystemConnector.getInstance(this).addDBToTrip(trip, p);						
//					}
//					Toast.makeText(this, selectedDBs.size() + " locations added to " + trip.getLabel() + ".", Toast.LENGTH_LONG).show();
//					selectedDBs = null;
//				}
//				Intent resultIntent = new Intent();
//				resultIntent.putExtra(IntentPassable.TRIP, trip);
//				setResult( Activity.RESULT_OK, resultIntent );
//				finish();
//			}
//		}

		return true;
	} // onOptionsItemSelected( MenuItem )

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode==Activity.RESULT_CANCELED){
			return;
		}
	}//onActivityResult

	/**
	 * Handles click events in the filter dialog.
	 */
	public void onClick( DialogInterface dialog, int which, boolean isChecked ){
		@SuppressWarnings("unchecked")
		ArrayList<String> cat = (ArrayList<String>) categoryFolders.clone();
		cat.add(0, "Favourites");
	}

	/**
	 * Handles the buttons in the filter dialog. 
	 */
	@SuppressWarnings("unchecked")
	public void onClick(DialogInterface dialog, int which){
		//add selection to settings:
		SharedPreferences settings = getSharedPreferences(CATEGORY_SETTINGS, 0);
		SharedPreferences.Editor editor = settings.edit();
		//editor.putBoolean("key", value);

		ArrayList<String> cat = (ArrayList<String>) categoryFolders.clone();
		cat.add(0, "Favourites");

		for (String title : cat){
			//preferences:
			ArrayList<DB> list = new ArrayList<DB>();

			for (DB db : allDBs){
				if (title.equals("Favourites") ){ //add to favorite section
					list.add(db);
				}else if(db.getCategory().equals(title)){
					list.add(db);
				}
			}//for DBs
			//DBFileAdapter testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
			//adapter.addSection(title, testAdapter);
		}//for categoryFolders

		// Commit the edits!
		editor.commit();

		//lv.setAdapter(adapter);
	}//onClick

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}//ImportLocalTab
