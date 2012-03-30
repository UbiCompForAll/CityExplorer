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

import java.util.ArrayList;
import org.ubicompforall.CityExplorer.data.FileSystemConnector;
//import org.ubicompforall.CityExplorer.data.DatabaseUpdater;
import org.ubicompforall.CityExplorer.data.DB;
import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.DBFileAdapter;
import org.ubicompforall.CityExplorer.data.SeparatedListAdapter;
import org.ubicompforall.CityExplorer.map.MapsActivity;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ImportLocalTab extends ListActivity implements OnMultiChoiceClickListener, DialogInterface.OnClickListener{

	/** The location of all the local DBs **/
	//String pathName = "";
	FileSystemConnector fs = null;

	/*** Field containing all DBs.*/
	private ArrayList<DB> allDBs = null;

	/*** Field containing an {@link ArrayList} of the categoryFolders.*/
	private ArrayList<String> categoryFolders;

	/*** Field containing this activity's resources.*/
	private Resources res;

	/*** Field containing the {@link SeparatedListAdapter} that holds all the other adapters.*/
	private SeparatedListAdapter adapter;

	/*** Field containing this activity's {@link ListView}.*/
	private ListView lv;

	/*** Field containing this activity's context.*/
	private Context context;  // What context? Context is the activity itself: for drawing output, storing folders etc.

	/*** Field containing the request code from other activities.*/
	private int requestCode;

	/*** Field containing a single DB.*/
	@SuppressWarnings("unused")
	private DB db;

	/*** Field giving access to databaseUpdater methods.*/
	//private DatabaseUpdater du;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		debug(2, "");

		//INITIALIZE OWN FIELDS
		allDBs = new ArrayList<DB>();
		fs = new FileSystemConnector( this );
		categoryFolders = new ArrayList<String>();
		categoryFolders.add( fs.getDatabasePath() );
		//Collections.sort(categoryFolders);
		debug(0, "categoryFolders is "+categoryFolders );

		init();		
	} // onCreate

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug



	/**
	 * Initializes the activity.
	 */
	private void init() {
		setContext(this);
		requestCode = getIntent().getIntExtra("requestCode",0);
		lv = getListView();
		lv.setOnItemLongClickListener(new DrawPopup());

		adapter = new SeparatedListAdapter(this, SeparatedListAdapter.LOCAL_DBS);
		lv.setAdapter(adapter);
		lv.setCacheColorHint(0);

		//allDBs = getAllDBs();

		res = getResources();

		//Init View-adapters etc.
		makeSections();

		//initGPS(); //For maps?
	}//init

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Makes the category sections that is shown in the list. 
	 */
	private void makeSections(){
		for (DB db : new FileSystemConnector( context ).getAllDBs() ){
			if( !adapter.getSectionNames().contains(db.getCategory())){ //category does not exist, create it.
				ArrayList<DB> list = new ArrayList<DB>();

				DBFileAdapter testAdapter;
				testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
				adapter.addSection(db.getCategory(), testAdapter);
			}
			((DBFileAdapter)adapter.getAdapter( db.getCategory() ) ).add(db);//add to the correct section
			((DBFileAdapter)adapter.getAdapter(db.getCategory())).notifyDataSetChanged();
		}
	}//makeSections

	/**
	 * Updates the category sections in the list, e.g. after choosing filtering.
	 */
	@SuppressWarnings("unchecked")
	private void updateSections(){
		allDBs = new FileSystemConnector( context ).getAllDBs();
		debug(0, "allDBs.size is "+allDBs.size() );

		ArrayList<String> sectionsInUse = new ArrayList<String>();
		// Get section for each DB
		for (DB db : allDBs){
			sectionsInUse.add(db.getCategory());
			if(!adapter.getSectionNames().contains(db.getCategory())){
				ArrayList<DB> list = new ArrayList<DB>();
				list.add(db);
				DBFileAdapter testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
				adapter.addSection(db.getCategory(), testAdapter);
			}//if contains category
		}//for DBs
		ArrayList<String> ListSections = (ArrayList<String>) adapter.getSectionNames().clone();
		for(String sec : ListSections){
			if( !sectionsInUse.contains(sec) && !sec.equalsIgnoreCase(CityExplorer.FAVORITES)){	
				adapter.removeSection(sec);
			}
		}//for sections
		lv.setAdapter(adapter);
	}//updateSections


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (requestCode == 2){
			debug(0, "Code Two!");
			//Smenu.removeItem(R.id.planMenuUpdatePois );
		}
//		else if (requestCode == SHARE_DB)
//		{	
//			menu.removeItem(R.id.planMenuAddDBs);
//			menu.removeItem(R.id.planMenuNewDB);
//			menu.removeItem(R.id.planMenuUpdateDBs);
//		}
//		else if(requestCode == DOWNLOAD_DB){
//			menu.removeItem(R.id.planMenuAddDBs);
//			menu.removeItem(R.id.planMenuNewDB);
//			menu.removeItem(R.id.planMenuShareDBs);
//			menu.removeItem(R.id.planMenuFilter);
//		}
//		else
//		{
//			menu.removeItem(R.id.planMenuAddDBs);
//		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.setGroupVisible(R.id.planMenuGroupTrip, false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

//		if(item.getItemId() == R.id.planMenuFilter)
//		{
//			categoryFolders = FileSystemConnector.getInstance(this).getUniqueCategoryNames();
//			Collections.sort(categoryFolders);
//
//			AlertDialog.Builder alert = new AlertDialog.Builder(this);
//			alert.setTitle("Filter");
//			ArrayList<String> cat = (ArrayList<String>) categoryFolders.clone();
//			cat.add(0, CityExplorer.FAVORITES);
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
	public void onListItemClick(ListView l, View v, int pos, long id) {
		if(l.getAdapter().getItemViewType(pos) == SeparatedListAdapter.TYPE_SECTION_HEADER){
			//Pressing a header
			debug(0, "Pressed a header... Dummy!");
			return;
		}
		DB dbObject = (DB) l.getAdapter().getItem(pos);
		debug(1, "requestCode is "+ requestCode );
		debug(0, "I just found DB "+dbObject.getLabel() );
		DBFactory.changeInstance( this, dbObject.getLabel() );
		startActivity( new Intent( this, PlanActivity.class) );
	} // onListItemClick

	/**
	 * Shows quick actions when the user long-presses an item.
	 */
	final private class DrawPopup implements AdapterView.OnItemLongClickListener {
		public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {

			if(parent.getAdapter().getItemViewType(pos) == SeparatedListAdapter.TYPE_SECTION_HEADER){
				Intent showInMap = new Intent(ImportLocalTab.this, MapsActivity.class);
				Adapter sectionAd = adapter.getAdapter(parent.getAdapter().getItem(pos).toString());
				ArrayList<DB> selectedDBs = new ArrayList<DB>();
				for (int i = 0; i < sectionAd.getCount(); i++){
					selectedDBs.add((DB) sectionAd.getItem(i));
				}
				//showInMap.putParcelableArrayListExtra(IntentPassable.DBLIST, selectedDBs);
				startActivity(showInMap);
				return true;
			}

			final DB d	= (DB) parent.getAdapter().getItem(pos);
			final AdapterView<?> par = parent;
			final int[] xy 			= new int[2]; v.getLocationInWindow(xy);
			final Rect rect 		= new Rect(	xy[0], 
					xy[1], 
					xy[0]+v.getWidth(), 
					xy[1]+v.getHeight()
				);

			final QuickActionPopup qa = new QuickActionPopup(ImportLocalTab.this, v, rect);
			//Drawable shareIcon		= res.getDrawable(android.R.drawable.ic_menu_share);
			Drawable deleteIcon		= res.getDrawable(android.R.drawable.ic_menu_delete);

			// Declare quick actions 
			Drawable	favIcon	= res.getDrawable(R.drawable.favstar_off);
			
			qa.addItem(favIcon,	"fav",	new OnClickListener(){
				public void onClick(View view){
					//set as favorite
					DB db = d;
//					db = db.modify().favorite(true).build();
//					FileSystemConnector.getInstance(ImportLocalTab.this).editdb(db);//update db;
//					alldbs.remove(d);
//					alldbs.add(db);
					Toast.makeText(ImportLocalTab.this, db.getLabel() + " added to Favorites.", Toast.LENGTH_LONG).show();
					adapter.notifyDataSetChanged();//update list
					qa.dismiss();
				}
			});


			qa.addItem(deleteIcon, "Delete", new OnClickListener(){
				public void onClick(View view){
					new FileSystemConnector( context ).deleteDB(d);
					updateSections();
					((SeparatedListAdapter)par.getAdapter()).notifyDataSetChanged();
					qa.dismiss();
				}
			});
			qa.show();
			return true;
		}//onItemLongClick
	}//DrawPopup

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode==Activity.RESULT_CANCELED){
			return;
		}
	}//onActivityResult


	/**
	 * Handles click events in the filter dialog.
	 */
	@Override
	public void onClick( DialogInterface dialog, int which, boolean isChecked ){
		@SuppressWarnings("unchecked")
		ArrayList<String> cat = (ArrayList<String>) categoryFolders.clone();
		cat.add(0, CityExplorer.FAVORITES);
	}

	/**
	 * Handles the buttons in the filter dialog. 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(DialogInterface dialog, int which){

		ArrayList<String> cat = (ArrayList<String>) categoryFolders.clone();
		cat.add(0, CityExplorer.FAVORITES);

		for (String title : cat){
			//preferences:
			ArrayList<DB> list = new ArrayList<DB>();

			for (DB db : allDBs){
				if (title.equals(CityExplorer.FAVORITES) ){ //add to favorite section
					list.add(db);
				}else if(db.getCategory().equals(title)){
					list.add(db);
				}
			}//for DBs
			DBFileAdapter testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
			adapter.addSection(title, testAdapter);
		}//for categoryFolders

		lv.setAdapter(adapter);
	}//onClick

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}//ImportLocalTab
