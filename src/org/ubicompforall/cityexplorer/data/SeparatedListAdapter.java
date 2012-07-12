/**
 * @contributor(s): Kristian Greve Hagen (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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

package org.ubicompforall.cityexplorer.data;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;

/**
 * @description:
 * 
 * Common Adapter for all list-types, e.g. POIs, Trips, and DBFiles, Both Local and Online/Internet/Web
 */

public class SeparatedListAdapter extends BaseAdapter {  
	
	/*** Field containing all the sections.*/
	private ArrayList<Section> sections = new ArrayList<Section>();
	
	/*** Field containing all the section names.*/
	private LinkedList<String> sectionNames = new LinkedList<String>();
	
	/*** Field containing the favorite section.*/
	public Section Favoritesection;
	
	/*** Constant field describing a section header.*/
	public final static int TYPE_SECTION_HEADER = 0;
	
	/*** Constant field describing a list of PoIs.*/
	public final static int POI_LIST = 1;
	/*** Constant field describing a list of trips.*/
	public final static int TRIP_LIST = 2;
	/*** Constant field describing a list of downloaded pois.*/
	public final static int INTERNET_POIS = 3;
	/*** Constant field describing a list of downloaded pois.*/
	public final static int INTERNET_TRIPS = 4;
	/*** Constant field describing a list of downloaded DataBases.*/
	public final static int LOCAL_DBS = 5;
	/*** Constant field describing a list of available Web DataBases.*/
	public final static int WEB_DBS = 6;
	
	private final static String[] CONSTANTS = { "UNUSED", "POI_LIST", "TRIP_LIST", "INTERNET_POIS", "INTERNET_TRIPS", "LOCAL_DBS", "WEB_DBS" }; // String[] CONSTANTS
	
	/*** Field containing the type of list. */
	private int listType;
	
	/*** Field containing the context of an Activity.*/
	    private Activity ctx;
	  
	    /**
	 * Public constructor for creating an instance of {@link SeparatedListAdapter}
	 * @param context The context of this class.
	 * @param listType The type of list this is.
	 */
	public SeparatedListAdapter(Activity context, int listType) {
		this.ctx = context;
	    this.listType = listType;
	    debug(1, "SeparatedListAdapter~86: I'm listType: "+ CONSTANTS[listType] );
	} // CONSTRUCTOR


	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	  
    /**
	 * Adds a section to an adapter.
	 * @param section The name of the section.
	 * @param adapter The adapter the section will use.
	 */
	public void addSection(String section, Adapter adapter) {  
		if(sectionNames.contains(section))
			return;
		
		if(section.equalsIgnoreCase(CityExplorer.FAVORITES)){
			Favoritesection = new Section(CityExplorer.FAVORITES, adapter);
			sections.add(Favoritesection); 
		}else{
			sections.add(new Section(section, adapter));
		}
		
		//Collections.sort(sections);
		sectionNames.add(section);
		
		if(sectionNames.contains(CityExplorer.FAVORITES)){
			sections.remove(Favoritesection);//take out
			sections.add(0, Favoritesection);//put inn at the top of the list.
		}
	} // addSection
	
	/**
	 * Removes a section from the adapter.
	 * @param section The name of the section to be removed.
	 */
	public void removeSection(String section){
		
		if( !sectionNames.contains(section)){
			return; //not in list.
		}
		Section removeSection = null;
		for (Section sec : sections){
			if(sec.getCaption().equalsIgnoreCase(section)){
				removeSection = sec;
			}
		}
		if(removeSection != null){
			this.sections.remove(removeSection);
		}
		
		if(section.equalsIgnoreCase( CityExplorer.FAVORITES )){
			Favoritesection = null;
		}
		sectionNames.remove(section);
	}//removeSection
	
	/**
	 * Gets the adapter at the specified position.
	 * @param position The position you want to get the adapter from.
	 * @return The Adapter at the specified position
	 */
	    public Adapter getAdapter(int position)
	    {
	    	for (Section section : this.sections) {
	    		Adapter adapter = section.getAdapter();  
	            int size = adapter.getCount() + 1;  
	  
	            // check if position inside this section  
	            if(position == 0) return adapter;  
	            if(position < size) return adapter;  
	  
	            // otherwise jump into next section  
	        position -= size;  
	    }  
	    return null;  
	}
	
	/**
	 * Gets the adapter with the specified name.
	 * @param section The name of the section you want to get.
	 * @return The Adapter with the specified name.
	 */
	public Adapter getAdapter(String section){
		if(section.equalsIgnoreCase(CityExplorer.FAVORITES)){
			return Favoritesection.getAdapter();
		}else{
			for (Section sec : sections){
				if(sec.getCaption().equalsIgnoreCase(section))
					return sec.getAdapter();
			}//for sections
		}//if favorite else proper cat
		return null;
	}//get adapter for a category
	
	/**
	 * Get the names of the section. 
	 * @return Arraylist containing the section names.
	 */
	public LinkedList<String> getSectionNames(){
		return sectionNames;
	}
	
	@Override
	public Object getItem(int position) {  
		for (Section section : this.sections) {
			Adapter adapter = section.getAdapter();  
			int size = adapter.getCount() + 1;  
			
			// check if position inside this section  
			if(position == 0) return section;  
			if(position < size) return adapter.getItem(position - 1);  

			// otherwise jump into next section  
			position -= size;  
		}  
		return null;  
	}  
	  
	@Override
	public int getCount() {  
		// total together all sections, plus one for each section header  
		int total = 0;  
		for (Section section : this.sections) {
			total += section.getAdapter().getCount() + 1;  
		}
		return total;  
	}//getCount
	  
	@Override
	public int getViewTypeCount() {  
		// assume that headers count as one, then total all sections  
		int total = 1;  
		for (Section section : this.sections)
			total += section.getAdapter().getViewTypeCount();  
		return total;  
	}//getViewTypeCount
  
	@Override
	public int getItemViewType(int position) {  
		int type = 1;  
		for (Section section : this.sections) {
			Adapter adapter = section.getAdapter();  
			int size = adapter.getCount() + 1;  
			
			// check if position inside this section  
			if(position == 0) return TYPE_SECTION_HEADER;  
			if(position < size) return type + adapter.getItemViewType(position - 1);  
			
			// otherwise jump into next section  
			position -= size;  
			type += adapter.getViewTypeCount();  
		}  
		return -1;  
	}//getItemViewType
	  
	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {
		debug(2, "position is "+position+", converView is "+convertView );
		int sectionnum = 0;  
		for (Section section : sections) {
			//debug(2, "Section is "+section+", count is "+section.getAdapter().getCount() );
			Adapter adapter = section.getAdapter();  
			int size = adapter.getCount() + 1;  
	        
	        // check if position inside this section
			if(position == 0) return getHeaderView(section.getCaption(), sectionnum, convertView, parent);
			//if(position < size) return adapter.getView(position - 1, convertView, parent);  //convertView messes up things...
	    	if(position < size) return adapter.getView(position - 1, null, parent);  
	  
	    	// otherwise jump into next section  
	        position -= size;  
	        sectionnum++;  
	    }//for each section
	    return null;  
	} // getView
	  
	@Override  
	public long getItemId(int position) {  
		return position;  
	}
	
		/**
	 * Gets a header view.
	 * @param caption The caption of the wanted view.
	 * @param index An int of the index for the wanted view.
	 * @param convertView The convertView View.
	 * @param parent The parent ViewGroup
	 * @return A View of the header.
	 */
	private View getHeaderView(String caption, int index, View convertView,	ViewGroup parent) {
		debug(2, "Caption is "+caption+", index is "+index );
		TextView result = null;
	
		if ( convertView != null && convertView.getClass() == TextView.class ){
			result = (TextView) convertView;
		}else{
			result = (TextView) ctx.getLayoutInflater().inflate(R.layout.header,null);
		}
		result.setText(caption);
		return result;
	} // getHeaderView
	
	@Override
	public void notifyDataSetChanged() {
		debug(2, "ListType is "+ CONSTANTS[listType] );
		if(listType == POI_LIST){
			DatabaseInterface db = DBFactory.getInstance(ctx);
			for (Section s : sections){
	    		if(s.getCaption().equalsIgnoreCase(CityExplorer.FAVORITES)){
	    			((PoiAdapter)Favoritesection.getAdapter()).replaceAll( db.getAllPois(true) );
	    			s = Favoritesection;
	    		}else{
	    			((PoiAdapter)s.getAdapter()).replaceAll( db.getAllPois(s.getCaption()) );
				}
			}
		}else if(listType == TRIP_LIST){
			DatabaseInterface db = DBFactory.getInstance(ctx);
			sections.clear();
			sectionNames.clear();
		
			ArrayList<Trip> freeList = db.getTripsWithPOIs( CityExplorer.TYPE_FREE );
			ArrayList<Trip> fixedList = db.getTripsWithPOIs( CityExplorer.TYPE_FIXED );
			
			TripAdapter freeAdapter, fixedAdapter;//, emptyTripAdapter;
			if(freeList.size() > 0){					
				freeAdapter = new TripAdapter(ctx, R.layout.plan_listitem, freeList);
				addSection("Free Tours", freeAdapter);
				freeAdapter.notifyDataSetChanged();
			}
	
			if(fixedList.size() > 0){					
				fixedAdapter = new TripAdapter(ctx, R.layout.plan_listitem, fixedList);
				addSection("Fixed Tours", fixedAdapter);
				fixedAdapter.notifyDataSetChanged();
			}
	
		}else if(listType == INTERNET_POIS){
			debug(0, "Missing INTERNET_POIS Adapter?");
		}else if(listType == INTERNET_TRIPS){
			debug(0, "Missing INTERNET_TRIPS Adapter?");
	
		}else if(listType == LOCAL_DBS){
			//DatabaseInterface db = DBFactory.getInstance( ctx );	// SQLiteConnector.java
			FileSystemInterface fs = new FileSystemConnector( ctx );// ctx is Activity-Context
			for (Section s : sections){
				String caption = s.getCaption();
				debug(0, "caption is "+caption );
	   			((DBFileAdapter)s.getAdapter()).replaceAll( fs.findAllDBs( caption ) );
			}//for sections
		}else if(listType == WEB_DBS){
			debug(-1, "Something happened to the Web-SeparatedListAdapter!" );
		}//switch on listType
	
		super.notifyDataSetChanged();
	}//notifyDataSetChanged
}//SeparatedListAdapter

