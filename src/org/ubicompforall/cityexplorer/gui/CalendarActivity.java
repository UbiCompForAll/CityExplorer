/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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
package org.ubicompforall.cityexplorer.gui;

import java.util.ArrayList;

import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;
import org.ubicompforall.cityexplorer.data.DBFactory;
import org.ubicompforall.cityexplorer.data.IntentPassable;
import org.ubicompforall.cityexplorer.data.Poi;
import org.ubicompforall.cityexplorer.data.Time;
import org.ubicompforall.cityexplorer.data.Trip;
import org.ubicompforall.cityexplorer.gui.ViewDayHourItem.poiTextView;
import org.ubicompforall.cityexplorer.map.MapsActivity;
//import org.ubicompforall.cityexplorer.map.route.GoogleKML;
import org.ubicompforall.cityexplorer.map.route.Road;
import org.ubicompforall.cityexplorer.map.route.Route;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * @description:
 */
@SuppressWarnings("unused")
public class CalendarActivity extends Activity {

	Paint mpt = new Paint();
	int iTextHeight = ViewDayHourItem.GetTextHeight(mpt);
    int iHourWidth = ViewDayHourItem.GetSpaceWidthHour(mpt);
    int iTimeWidth = ViewDayHourItem.GetSpaceWidthTime(mpt);
    int iUSTimeMarkWidth = ViewDayHourItem.GetSpaceWidthUSTimeMark(mpt);
    ScrollView sv;
    LinearLayout ll;
    private static boolean USE24HOURS = true;
    //AlertDialog alert;
    
    Trip trip;
    ArrayAdapter<String> poiAdapter; //Keep entries that have not been given a time (yet!)
    ArrayList<ViewDayHourItem> hourViews = new ArrayList<ViewDayHourItem>();
    public boolean calendarIsEmpty = true;
	
    boolean tryToGoBack = false;
	private boolean saved = true;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		//trip = DBFactory.getInstance(this).getAllTrips(false).get(0);
		trip = this.getIntent().getParcelableExtra("trip");
		if (trip == null){
			Toast.makeText( this, "Calendar-Activity without Trip!?", Toast.LENGTH_LONG).show();
			finish();
		}else{
			debug(2, "free:"+trip.isFreeTrip() );
			
			sv = new ScrollView(this);
			ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.VERTICAL); 
			ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			
			addViews();
		 
			ll.setBackgroundColor(0xFFEBF2FA);
			sv.addView(ll);
			setContentView(sv);
	
	
			if( ! trip.getFixedTimes().keySet().containsAll( trip.getPois() )
					||	! trip.getPois().containsAll( trip.getFixedTimes().keySet() ) ){
				// (some) pois have not been added.
				poiAdapter = preparePoiList();
			}else{//if some (fixed time) entries have not been given a time yet
				poiAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, new ArrayList<String>()); //Moved to onResume
				debug(1, "no Time on resume" );
			}
			debug(1, "poiAdapter.size is "+poiAdapter.getCount() );
			//add pois that already has times: //HEAVY! Run on a different Thread!
			addPoisWithTime();
		}
	}//onCreate
	
	@Override
	protected void onResume(){
		super.onResume();
	}//onResume
	

	private void debug(int i, String string) {
		CityExplorer.debug(i, string);
	}


	private void addPoisWithTime()	{
		if( trip.getFixedTimes() == null ){
			debug(-1,"fixed time == null" );
		}
		boolean allDone = true;
		
		//if(trip.getFixedTimes().keySet().containsAll(trip.getPois()) &&
			//	trip.getPois().containsAll(trip.getFixedTimes().keySet()))	{ //all pois have time.
			for(Poi poi : trip.getPois()){
				if ( allDone && trip.getFixedTimes().containsKey(poi) ){
					//debug(0, "times: "+poi.getLabel()+" "+trip.getFixedTimes().get(poi).hour+":"+trip.getFixedTimes().get(poi).minute);
				
					ViewDayHourItem time = null;
					for(ViewDayHourItem t : hourViews)	{
						if(t.GetHour() == trip.getFixedTimes().get(poi).hour){
							time = t;
							break;
						}
					}
					if(time == null){
						debug(0, "ERROR in addPoisWithTime");
					}
					time.setMinutes(trip.getFixedTimes().get(poi).minute);
					
					//if( !calendarIsEmpty) { //not first entry. add walking time. //Deal with this in addWalkingTime instead (Write once, handle everywhere!)
						addWalkingTime(trip.getPois().indexOf(poi), time, trip.getFixedTimes().get(poi).minute, poi);
					//}
					
					//Add poi calendar entry:
			    	poiTextView tv =  time.new poiTextView(CalendarActivity.this);
					tv.setMinutes(trip.getFixedTimes().get(poi).minute);
					tv.setPoi(poi);
					tv.setClickable(true);
					tv.setOnClickListener(ocl);
					tv.setAsWalkingEntry(false);
					time.addView(tv);
					time.UpdateHeight();
					
					calendarIsEmpty = false;
				}else{ //skip the rest for manual placement
					allDone = false;
					break;
				}//if allPois have time
			}//for all pois in fixed trip
		//}//all pois have time.
	}//addPoisWithTime
	
	
	private ArrayAdapter<String>
	 preparePoiList(){
		ArrayList<String> poiList = new ArrayList<String>();
		//POIs have not been added.
		for(Poi poi : trip.getPois()){
			debug(0, "Poi: "+poi.getLabel()+" added");
			if( trip.isFreeTrip() ){
				poiList.add(poi.getLabel());
			}else{
				poiList.add( (trip.getPois().indexOf(poi)+1)+" "+poi.getLabel() );
			}
		}
		return new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, poiList);
	}//preparePoiList

	/***
	 * Add the calendar view
	 */
	private void addViews(){
		for (int ihour = 0; ihour < 24; ihour++) 
		{
			ViewDayHourItem view = new ViewDayHourItem(this, ihour, iTextHeight);
			view.SetItemData(USE24HOURS, iHourWidth, iTimeWidth, iUSTimeMarkWidth);
			view.SetItemClick(onNewApptItemClick);
			hourViews.add(view);
			ll.addView(view);
		}
	}
	
	@Override
	public void onBackPressed() {
		// do something on back.
		if ( tryToGoBack || saved  ){
			super.onBackPressed();
		}else{
			Toast.makeText( this, "Save your times first! Try again to discard", Toast.LENGTH_LONG).show();
			tryToGoBack = true;
			openOptionsMenu();	// Show menu
			debug(2, "back pressed!" );
		}
		return;
	} //onBackPressed

	//appointment click listener
	public ViewDayHourItem.OnItemClick onNewApptItemClick = new ViewDayHourItem.OnItemClick(){
		public void OnClick(ViewDayHourItem item){
			debug(0, "you clicked "+item );//.GetHour()+item.GetMinutes() );
			
			final ViewDayHourItem time = item;
			//find next poi
			Poi poi = null;
			int poiIndex = trip.getPois().size() - poiAdapter.getCount();
			if ( poiIndex < trip.getPois().size() ){
		    	poi = trip.getPois().get(poiIndex);
			}
			debug(1, "poiIndex is "+ poiIndex );

			//Add walking time:
			if( poiIndex >= trip.getPois().size() || addWalkingTime( poiIndex, time, time.GetMinutes(), poi ) == false){
				Toast.makeText( CalendarActivity.this, "No more to add...", Toast.LENGTH_SHORT ).show();
				return;
	    	}//if no poi left, or no walking time

	    	//Add poi calendar entry:
	    	poiTextView tv =  time.new poiTextView(CalendarActivity.this);
			tv.setMinutes(time.GetMinutes());
			tv.setPoi(poi);
			tv.setClickable(true);
			tv.setOnClickListener(ocl);
			tv.setAsWalkingEntry(false);
			time.addView(tv);
			time.UpdateHeight();
			
			setTripTime(poi, new Time(time.GetHour(), time.GetMinutes()));
			
			calendarIsEmpty = false;
			//poiAdapter.remove(poiAdapter.getItem(0));	//Remove already placed entries // Moved to ???
			saved = false;
		}//onClick
	}; //OnNewApptItemClick: OnItemClick class
	
	//create poi picker:
//	AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
//	builder.setTitle("Pick next PoI");
//	builder.setAdapter(poiAdapter, new DialogInterface.OnClickListener() {
//	    public void onClick(DialogInterface dialog, int poiIndex) {	
//	    	//find the poi:
//	    	int tripPoiIndex = 0;
//	    	String s = poiAdapter.getItem(poiIndex);
//	    	//the index is in the title formated like this: 1 Trondheim Torg if it is not a free trip
//	    	//and just Trondheim Torg if it is not
//	    	String tripPoiName = ((!trip.isFreeTrip()) ? s.substring(s.indexOf(" ")):s);
//	    	
//	    	Poi poi = null;
//	    	for (Poi p : trip.getPois()){
//				if(p.getLabel().trim().equals(tripPoiName.trim())) poi = p;
//			}
//	    	tripPoiIndex = trip.getPois().indexOf(poi);
//
//			alert.hide();
//			poiAdapter.remove(poiAdapter.getItem(poiIndex));
//	    } // onClick
//	}); // new OnclickListener class
//	alert = builder.create();
//	alert.show();
	
	private void setTripTime(Poi poi, Time time){
		trip.setTime(poi, time);
	}
	
	/**
	 * @param tripPoiIndex
	 * @param time
	 * @param poi
	 * @return success
	 */
	private boolean addWalkingTime(int tripPoiIndex, ViewDayHourItem time,int minutes, Poi poi){
		//debug(1, "tripPoiIndex is "+tripPoiIndex );
		//find the previous poi entry
		poiTextView ptvBeforeOrNull = findPoiViewBefore(trip, hourViews, time);
		poiTextView ptvAfterOrNull  = findPoiViewAfter(trip, hourViews, time);
		//debug(0, "before="+ptvBeforeOrNull+", after="+ptvAfterOrNull );
		
		if ( ! trip.isFreeTrip() ){
			if( tripPoiIndex != 0 ){ //no travel to the first POI

				if(ptvAfterOrNull != null){ //there is another poi in the calendar after this one.
	    			Toast.makeText(CalendarActivity.this, "Please add the PoIs to the calendar in cronological order.", Toast.LENGTH_LONG).show();
	    			debug(1, "after or null..." );
	    			return false;
	    		}//if first poi
	    		
	    		if( !trip.isFreeTrip() ){
	    			if( calendarIsEmpty || ptvBeforeOrNull == null || !ptvBeforeOrNull.getPoi().equals( trip.getPoiAt(tripPoiIndex-1) ) ){
	    				Toast.makeText(CalendarActivity.this, "Please add the PoIs to the calendar in numerical order.", Toast.LENGTH_LONG).show();
	        			debug(-1, "Which one calendarIsEmpty is "+calendarIsEmpty+", ptvBeforeOrNull is "+ptvBeforeOrNull+", tripPoiIndex is "+tripPoiIndex );
	    				return false;
	    			}// if empty, or bad order
	    		}// if FixedTour
	    		
	    		Poi prevPoi = ptvBeforeOrNull.getPoi();
	    		int prevHour = ptvBeforeOrNull.getHourItem().GetHour();
	    		int prevMinutes	= ptvBeforeOrNull.getMinutes();
	    		
	    		double distance = getDistance(poi, prevPoi);
	    		double timeNeededInMin = distance/68;//67 m/min = 4km/h
	    		
	    		//find the correct hour to insert the walk entry.
	    		int numbHoursBack = 0;
	    		if(minutes-timeNeededInMin < 0){ //more than 1 hours travel time.
	    			numbHoursBack = (int) Math.floor(((-1*(minutes-timeNeededInMin)+60)/60));
	    		}
	    		
	    		//add walking entry:
				if( hourViews.indexOf(time) > numbHoursBack-1 ){ //not if it is before the first hour
					ViewDayHourItem Hour = hourViews.get(hourViews.indexOf(time)-numbHoursBack);
					
					//check if the entry is before the prev poi. abort if it is.
					if( (Hour.GetHour() < prevHour) || ((Hour.GetHour() == prevHour)&&((int)(numbHoursBack*60+(minutes-timeNeededInMin)) < prevMinutes)) ){
						//OMG there is no time to walk to this poi
						Toast.makeText(CalendarActivity.this, "You will not have time to walk between this and the previous PoI.", Toast.LENGTH_LONG).show();
						return false;
					}
					//Add calendar walk entry:
			    	poiTextView tv =  Hour.new poiTextView(CalendarActivity.this);
					tv.setMinutes((int)(numbHoursBack*60+(minutes-timeNeededInMin)));
					tv.setText("Walk "+(int)distance+" meters");
					tv.setPoi(poi);
					tv.setClickable(true);
					tv.setOnClickListener(ocl);
					tv.setAsWalkingEntry(true);
					Hour.addView(tv);
					Hour.UpdateHeight();	
				} // if hour > hoursBack-1
	    	} // if not first poi in trip: addWalkingTime
			debug(-1, "poiAdapter.size is "+poiAdapter.getCount() );
			if (poiAdapter.getCount() >0 ){
				poiAdapter.remove( poiAdapter.getItem(0) );	//Remove already placed entries
			}
			return true;
		}//if fixed tour
		return false; //e.g. free trip etc.
	} // addWalkingTime
	
	public poiTextView findPoiViewBefore(Trip trip,ArrayList<ViewDayHourItem> hourViews,ViewDayHourItem newView){
		
		//find the poi before this one.
		for(int i = hourViews.indexOf(newView); i >= 0; i--){//start here and work backwards.
			for (int j = hourViews.get(i).getPoiTextViews().size()-1; j >= 0; j--){
				poiTextView tv = hourViews.get(i).getPoiTextViews().get(j);
				if(tv.isWalkingEntry() || (hourViews.get(i).GetHour() == newView.GetHour() && tv.getMinutes()>newView.GetMinutes()))
					continue;
				//we have the prev entry.
				return tv;
			}
		}
	
		return null;
	}//findPoiViewBefore
	
	public poiTextView findPoiViewAfter(Trip trip,ArrayList<ViewDayHourItem> hourViews,ViewDayHourItem newView){
		
		//find the poi after this one.
		for(int i = hourViews.indexOf(newView); i < hourViews.size(); i++)//start here and work forwards.
		{
			//debug(0, "test hours: "+hourViews.get(i).GetHour());
			for (int j = 0; j < hourViews.get(i).getPoiTextViews().size(); j++) 
			{
				poiTextView tv = hourViews.get(i).getPoiTextViews().get(j);
				if(tv.isWalkingEntry()|| (hourViews.get(i).GetHour() == newView.GetHour() && tv.getMinutes()<=newView.GetMinutes()))
					continue;
				//we have the next entry.
				return tv;
			}
		}
		return null;
	}//findPoiViewAfter
	
	public double getDistance(Poi startPoi, Poi endPoi){
		float[] dist = new float[3];
		//debug(0, "Start, end is "+startPoi+", "+endPoi );
		
		/*if we are offline, use straight line to calculate distance: */
        Location.distanceBetween(
		startPoi.getGeoPoint().getLatitudeE6()/1E6, 
		startPoi.getGeoPoint().getLongitudeE6()/1E6, 
		endPoi.getGeoPoint().getLatitudeE6()/1E6, 
		endPoi.getGeoPoint().getLongitudeE6()/1E6, dist);
//*/
//		Road r = GoogleKML.getRoad(
//				startPoi.getGeoPoint().getLatitudeE6()/1E6, 
//				startPoi.getGeoPoint().getLongitudeE6()/1E6, 
//				endPoi.getGeoPoint().getLatitudeE6()/1E6, 
//				endPoi.getGeoPoint().getLongitudeE6()/1E6);
		
		//dist = route.getDistance();
		//dist[0] = route.getDistance();
		Route route = MapsActivity.directions( startPoi.getGeoPoint(), endPoi.getGeoPoint() );
		
		return dist[0];
	}//getDistance
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		//super.onPrepareOptionsMenu( menu );	//This probably messes up things!
		menu.clear();	// Rebuild the menu every time? // Or move this to on-create?
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.calendar_menu, menu);
		//menu.removeItem(R.id.menuShowMap);

// Old version. The composition tool is invoked from the Start activity.
//		if ( ! CityExplorer.ubiCompose){
//			menu.removeItem( R.id.composePOIs );
//		}

		return true; // ! calendarIsEmpty; // Show menu when no times?
	}//onPrepareOptionsMenu
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ){
		int itemID = item.getItemId();
		debug(0, "Selected: "+item+", id is "+itemID );
		
		switch (itemID){
		case R.id.saveCalendar:
			debug(2,"itemID is "+itemID);
			if( ! trip.getFixedTimes().keySet().containsAll( trip.getPois() ) ){
				Toast.makeText(this, "Some locations still without time", Toast.LENGTH_LONG).show();
			}
			saveSchedule();
			finish();
		break;
		case R.id.clearCalendar:
			debug(2,"itemID is "+itemID);
			ll.removeAllViews();
			addViews(); //Add the calendar view
			poiAdapter.clear();
			preparePoiList();
			
			trip.clearTimes();
			Toast.makeText(this, "Times cleared", Toast.LENGTH_SHORT).show();
			saveSchedule();
			saved=false;
		break;
// Old version. The composition tool is invoked from the Start activity.		
//		case R.id.composePOIs:
//			debug(2,"itemID is "+itemID);
//			ll.removeAllViews(); //Only used for UbiComposer Version, if CityExplorer.ubiCompose == true
//			showComposerInWebView();
//		break;
		default:
			break;
		}
		return true;
	} // onOptionsItemSelected

// Old version. The composition tool is invoked from the Start activity.
//	
//	private void showComposerInWebView() {
//		Toast.makeText(this, "Loading UbiComposer", Toast.LENGTH_LONG).show();
//		//Testing how to launch a specific intent for the Firefox browser, Or Use Webview (below)
//		Intent intent = new Intent(Intent.ACTION_MAIN, null);
//		intent.addCategory(Intent.CATEGORY_LAUNCHER);
//		intent.setComponent(new ComponentName("org.mozilla.firefox", "org.mozilla.firefox.App"));
//		intent.setAction("org.mozilla.gecko.BOOKMARK");
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		//intent.putExtra("args", "--url=" + url);
//		//intent.setData(Uri.parse(url));
//		startActivity(intent);
//
//		/*
//		Send (JSON) context:
//			List of URIs: To the available DB (-provider) (with specific Table-names: POIs in TrondheimDB, for example)
//			List of Library-URI: Which Trigger/Building Block to load on invocation
//				Always include Generic.library in the list
//		OR:
//		 http://developer.android.com/reference/android/content/ContentProvider.html
//		Implement ContentProvider
//			query( URI, COLS, CONDITIONS, CONDITION_VALUES, SORTING )
//			URI: cityExplorer/POI or cityExplorer/POI/14
//			COLS: Name ( always include hidden ID_COL, possibly null )
//			CONDITIONS: null
//			COND_VALUES: null
//			SORT: By name - Ascending
//		Other types of queries
//			Pick (Must provide its own User Interface)
//		Composition
//			Trigger:
//				Arriving at POI (Need URI for POI-table, column names, ID_COLUMN)
//			Step:
//				Send SMS with
//					Text with name-reference from Trigger,
//					Phone Number from PhoneBook on the phone, use PICK/ContentProvider
//			Info:
//				Name of the POI,
//				Phone number from AddressBook
//		*/
//	}//showComposerInWebView

	private void saveSchedule() {
		//if( trip.getFixedTimes().keySet().containsAll( trip.getPois() ) ){
		//done
		DBFactory.getInstance(this).addTimesToTrip(trip);
		Intent resultIntent = new Intent();
		resultIntent.putExtra( IntentPassable.TRIP, trip );
		setResult( Activity.RESULT_OK, resultIntent );
		saved=true;
	}//saveSchedule

	public OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v){
			//super.onClick(v); This doesn't work, tells me "OnClickListener" is actually "Object" ?! RS-111220
			/*The problem was actually that Project -> Settings -> Java -> Compiler was set to 1.7 (too high for android!)
			and the project-specific -> Configure -> was set to 1.5 (too LOW to Override Interfaces (only classes)!
			*/ 
			if(v instanceof poiTextView){
				poiTextView tv = (poiTextView)v;
				if( tv.isWalkingEntry() )	{ // walking directions
					Poi prevPoi = trip.getPois().get(trip.getPois().indexOf(tv.getPoi())-1); 
					
					Uri uri = Uri.parse("http://maps.google.com/maps?f=d&source=s_d&saddr="+
							prevPoi.getGeoPoint().getLatitudeE6()/1E6+","+prevPoi.getGeoPoint().getLongitudeE6()/1E6+"&daddr="+tv.getPoi().getGeoPoint().getLatitudeE6()/1E6+","+tv.getPoi().getGeoPoint().getLongitudeE6()/1E6+"&dirflg=w");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
					return;
				}//if walking
				
				debug(0, "Clicked: "+tv.getPoi().getLabel());
				Intent details = new Intent(CalendarActivity.this, PoiDetailsActivity.class);
				details.putExtra("poi", tv.getPoi());
				details.putExtra("trip", trip);
				details.putExtra("poiNumber", trip.getPois().indexOf(tv.getPoi()));

				startActivityForResult(details, PoiDetailsActivity.POI_TRIP_POS);
			}//if poiTV
		}//onclick
	};
	
	public class JavaScriptInterface {
	    Context mContext;

	    /** Instantiate the interface and set the context */
	    public JavaScriptInterface(Context c) {
	        mContext = c;
	    }

	    /** Show a toast from the web page */
	    public void showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	    }//showToast
	}//class JavaScriptInterface

	public Trip getTrip() {
		return trip;
	}//getTrip


} //class CalendarActivity

