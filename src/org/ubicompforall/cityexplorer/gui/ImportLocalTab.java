/**
 * @contributor(s): Rune Sætre (NTNU)
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

package org.ubicompforall.cityexplorer.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

//import org.ubicompforall.cityexplorer.data.DatabaseUpdater;

import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;
import org.ubicompforall.cityexplorer.data.DB;
import org.ubicompforall.cityexplorer.data.DBFactory;
import org.ubicompforall.cityexplorer.data.DBFileAdapter;
import org.ubicompforall.cityexplorer.data.FileSystemConnector;
import org.ubicompforall.cityexplorer.data.SeparatedListAdapter;
import org.ubicompforall.cityexplorer.map.MapsActivity;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ImportLocalTab extends ListActivity implements OnMultiChoiceClickListener, DialogInterface.OnClickListener{

	/** A connection to the location of all the local DBs **/
	FileSystemConnector fs = null;

	/*** Field containing all DBs.*/
	private ArrayList<DB> allDBs = null;

	/*** Field containing an {@link ArrayList} of the categoryFolders.*/
	//private ArrayList<String> categoryFolders;

	/*** Field containing this activity's resources.*/
	private Resources res;

	/*** Field containing the {@link SeparatedListAdapter} that holds the list of all the (other) adapters.*/
	private SeparatedListAdapter adapter;

	/*** Field containing this activity's {@link ListView}.*/
	private ListView lv;

	/*** Field containing this activity's context.*/
	//private Context context;  // What context? Context is the activity itself: for drawing output, storing folders etc.

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

		//INITIALIZE OWN FIELDS
		fs = new FileSystemConnector(this);

		allDBs = new ArrayList<DB>();
		//categoryFolders = new ArrayList<String>();
		//categoryFolders.add( getDatabasePath( selected ).getAbsolutePath() );
		//Collections.sort(categoryFolders);

		//categoryFolders.add( getDatabasePath( selected ).getAbsolutePath() );
		debug(1, "localeDbFolder is "+fs.getCityFolders() );

		init();		
	} // onCreate

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	/**
	 * Initializes the activity.
	 */
	private void init() {
		//setContext(this); //Using ImportLocalTab.this below instead
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
	}//init

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Makes the category sections that is shown in the list. 
	 */
	private void makeSections(){
		for (DB db : fs.getAllDBs() ){
			if( !adapter.getSectionNames().contains(db.getCategory())){ //category does not exist, create it.
				CopyOnWriteArrayList<DB> list = new CopyOnWriteArrayList<DB>();
				DBFileAdapter testAdapter;
				testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
				adapter.addSection(db.getCategory(), testAdapter);
			}
			((DBFileAdapter)adapter.getAdapter( db.getCategory() ) ).add(db);//add to the correct section
			((DBFileAdapter)adapter.getAdapter(db.getCategory())).notifyDataSetChanged(); //In case the DBs belong to different Categories
		}
	}//makeSections

	/**
	 * Updates the category sections in the list, e.g. after choosing filtering.
	 */
	@SuppressWarnings("unchecked")
	private void updateSections(){
		allDBs = fs.getAllDBs();
		debug(0, "allDBs.size is "+allDBs.size() );

		ArrayList<String> sectionsInUse = new ArrayList<String>();
		// Get section for each DB
		for (DB db : allDBs){
			sectionsInUse.add(db.getCategory());
			if(!adapter.getSectionNames().contains(db.getCategory())){
				CopyOnWriteArrayList<DB> list = new CopyOnWriteArrayList<DB>();
				list.add(db);
				DBFileAdapter testAdapter = new DBFileAdapter(this, R.layout.plan_listitem, list);
				adapter.addSection(db.getCategory(), testAdapter);
			}//if contains category
		}//for DBs
		LinkedList<String> ListSections = (LinkedList<String>) adapter.getSectionNames().clone();
		//LinkedList<String> ListSections = adapter.getSectionNames();
		for(String sec : ListSections){
			if( !sectionsInUse.contains(sec) && !sec.equalsIgnoreCase(CityExplorer.FAVORITES)){	
				adapter.removeSection(sec);
			}
		}//for sections
		lv.setAdapter(adapter);
	}//updateSections


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.setGroupVisible(R.id.planMenuGroupTrip, false);
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		if(l.getAdapter().getItemViewType(pos) == SeparatedListAdapter.TYPE_SECTION_HEADER){
			//Pressing a header
			debug(0, "Pressed a header... Dummy!");
		}else{
			DB selectedDb = (DB) l.getAdapter().getItem(pos);
			debug(2, "requestCode is "+ requestCode ); //RequestCode == 0
			debug(2, "I just found DB "+selectedDb.getLabel() );
			String DEFAULT_DBFOLDER = getResources().getText( R.string.default_dbFolderName ).toString();
			File currentDbFile = new File( getDatabasePath( DEFAULT_DBFOLDER ).getAbsolutePath()+"/"+ selectedDb.getLabel() );
			DBFactory.changeInstance( this, currentDbFile );
			//startActivity( new Intent( this, PlanActivity.class) );
			finish();
		}//if header: skip, else select and finish
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
//					fs.getInstance(ImportLocalTab.this).editdb(db);//update db;
//					alldbs.remove(d);
//					alldbs.add(db);
					Toast.makeText(ImportLocalTab.this, db.getLabel() + " added to Favorites.", Toast.LENGTH_LONG).show();
					adapter.notifyDataSetChanged();//update list
					qa.dismiss();
				}
			});


			qa.addItem(deleteIcon, "Delete", new OnClickListener(){
				public void onClick(View view){
					fs.deleteDB(d);
					updateSections();
					((SeparatedListAdapter)par.getAdapter()).notifyDataSetChanged();
					qa.dismiss();
				}
			});
			qa.show();
			return true;
		}//onItemLongClick
	}//DrawPopup

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data){
//		if(resultCode==Activity.RESULT_CANCELED){
//			return;
//		}
//	}//onActivityResult


	/**
	 * Handles click events in the filter dialog.
	 */
	@Override
	public void onClick( DialogInterface dialog, int which, boolean isChecked ){
		ArrayList<String> cat = (ArrayList<String>) fs.getCityFolders(); //categoryFolders.clone();
		cat.add(0, CityExplorer.FAVORITES);
	}

	/**
	 * Handles the buttons in the filter dialog. 
	 */
	@Override
	public void onClick(DialogInterface dialog, int which){

		//ArrayList<String> cat = (ArrayList<String>) categoryFolders.clone();
		ArrayList<String> cat = fs.getCityFolders();
		cat.add(0, CityExplorer.FAVORITES);
		for (String title : cat){
			//preferences:
			CopyOnWriteArrayList<DB> list = new CopyOnWriteArrayList<DB>();

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

	/***
	 * ImportLocalTab context, for the helper classes
	 */
//	public Context getContext() {
//		return context;
//	}

//	public void setContext(Context context) {
//		this.context = context;
//	}
}//ImportLocalTab
