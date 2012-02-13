/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
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

package org.ubicompforall.CityExplorer.gui;

import java.util.ArrayList;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;
import org.ubicompforall.CityExplorer.data.DBFactory;
import org.ubicompforall.CityExplorer.data.IntentPassable;
import org.ubicompforall.CityExplorer.data.Poi;
import org.ubicompforall.CityExplorer.data.Time;
import org.ubicompforall.CityExplorer.data.Trip;
import org.ubicompforall.CityExplorer.gui.ViewDayHourItem.poiTextView;
import org.ubicompforall.CityExplorer.map.route.GoogleKML;
import org.ubicompforall.CityExplorer.map.route.Road;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class CalendarActivity extends Activity {

	/*** Field containing this activity's {@link WebView}.*/
	private WebView webview;

	Paint mpt = new Paint();
	int iTextHeight = ViewDayHourItem.GetTextHeight(mpt);
    int iHourWidth = ViewDayHourItem.GetSpaceWidthHour(mpt);
    int iTimeWidth = ViewDayHourItem.GetSpaceWidthTime(mpt);
    int iUSTimeMarkWidth = ViewDayHourItem.GetSpaceWidthUSTimeMark(mpt);
    ScrollView sv;
    LinearLayout ll;
    private static boolean USE24HOURS = true;
    AlertDialog alert;
    
    Trip trip;
    ArrayAdapter<String> poiAdapter;
    ArrayList<ViewDayHourItem> hourViews = new ArrayList<ViewDayHourItem>();
    boolean calendarIsEmpty = true;
	
    boolean wantToGoBack = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		//trip = DBFactory.getInstance(this).getAllTrips(false).get(0);
		trip = this.getIntent().getParcelableExtra("trip");
		System.out.println("CalAct: free:"+trip.isFreeTrip());
		poiAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, new ArrayList<String>());
		
		
		sv = new ScrollView(this);
		ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL); 
		ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		addViews();
	 
		ll.setBackgroundColor(0xFFEBF2FA);
		sv.addView(ll);
		setContentView(sv);
		
		//add pois that already has times:
		addPoisWithTime();
		
		if( !trip.getFixedTimes().keySet().containsAll(trip.getPois()) ||
				!trip.getPois().containsAll(trip.getFixedTimes().keySet()))
		{
			//pois have not been added.
			preparePoiList();
		}
	}
	

	private void debug(int i, String string) {
		CityExplorer.debug(i, string);
	}


	private void addPoisWithTime()
	{
		if(trip.getFixedTimes() == null)
			System.out.println("CalAct: fixed time == null");
		
		if(trip.getFixedTimes().keySet().containsAll(trip.getPois()) &&
				trip.getPois().containsAll(trip.getFixedTimes().keySet()))//all pois have time.
		{
			for(Poi poi : trip.getPois())
			{	
				System.out.println("CalAct: times: "+poi.getLabel()+" "+trip.getFixedTimes().get(poi).hour+":"+trip.getFixedTimes().get(poi).minute);
				
				ViewDayHourItem time = null;
				for(ViewDayHourItem t : hourViews)
				{
					if(t.GetHour() == trip.getFixedTimes().get(poi).hour)
					{
						time = t;
						break;
					}
				}
				if(time == null)
					System.out.println("ERROR in addPoisWithTime");
							
				time.setMinutes(trip.getFixedTimes().get(poi).minute);
				
				if( !calendarIsEmpty)//not first entry. add walking time.
				{
					addWalkingTime(trip.getPois().indexOf(poi), time, trip.getFixedTimes().get(poi).minute, poi);
				}
				
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
				
			}	
		}
	}//addPoisWithTime
	
	
	private void preparePoiList(){
		ArrayList<String> poiList = new ArrayList<String>();
		
		//pois have not been added.
		for(Poi poi : trip.getPois())
		{
			//System.out.println("Poi: "+poi.getLabel()+" added");
			if(!trip.isFreeTrip()) 	poiList.add((trip.getPois().indexOf(poi)+1)+" "+poi.getLabel());
			else 					poiList.add(poi.getLabel());
		}
		
		poiAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, poiList);
	}

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
		if (wantToGoBack){
			super.onBackPressed();
		}else{
			Toast.makeText( this, "Save your times in Menu first! Press back again to go back", Toast.LENGTH_LONG).show();
			wantToGoBack = true;
			debug(0, "back pressed!" );
		}
		return;
	} //onBackPressed

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event)  {
//	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			Toast.makeText( context, "Don't you want to save your times!?!", Toast.LENGTH_LONG).show();
//			debug(0, "back pressed!" );
//	        return true;
//	    }
//	    return super.onKeyDown(keyCode, event);
//	} // onKeyDown

	//appointment click listener
	public ViewDayHourItem.OnItemClick onNewApptItemClick = new ViewDayHourItem.OnItemClick(){
		public void OnClick(ViewDayHourItem item){
			//System.out.println("you clicked "+item.GetHour()+item.GetMinutes());
			
			//create poi picker:
			
			final ViewDayHourItem time = item;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
			builder.setTitle("Pick a PoI");
			
			builder.setAdapter(poiAdapter, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int poiIndex) {	
			    	//find the poi:
			    	int tripPoiIndex = 0;
			    	String s = poiAdapter.getItem(poiIndex);
			    	//the index is in the title formated like this: 1 Trondheim Torg if it is not a free trip
			    	//and just Trondheim Torg if it is not
			    	String tripPoiName = ((!trip.isFreeTrip()) ? s.substring(s.indexOf(" ")):s);
			    	
			    	Poi poi = null;
			    	for (Poi p : trip.getPois()){
						if(p.getLabel().trim().equals(tripPoiName.trim())) poi = p;
					}
			    	tripPoiIndex = trip.getPois().indexOf(poi);

			    	//Add walking time:
			    	if( addWalkingTime(tripPoiIndex, time, time.GetMinutes(), poi) == false)
			    		return;

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
					alert.hide();
					poiAdapter.remove(poiAdapter.getItem(poiIndex));
			    } // onClick
			}); // new OnclickListener class
			
			alert = builder.create();

			alert.show();
		}
	}; //OnNewApptItemClick: OnItemClick class
	
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
		//find the previous poi entry
		poiTextView ptvBeforeOrNull = findPoiViewBefore(trip, hourViews, time);
		poiTextView ptvAfterOrNull  = findPoiViewAfter(trip, hourViews, time);
		
		if((tripPoiIndex != 0 && !trip.isFreeTrip()) || (!calendarIsEmpty && trip.isFreeTrip())){ //no travel to the first POI
    		
    		if(ptvAfterOrNull != null){ //there is another poi in the calendar after this one.
    			Toast.makeText(CalendarActivity.this, "Please add the PoIs to the calendar in cronological order.", Toast.LENGTH_LONG).show();
    			return false;
    		}
    		
    		if(!trip.isFreeTrip()){
    			if(calendarIsEmpty || ptvBeforeOrNull == null || !ptvBeforeOrNull.getPoi().equals(trip.getPoiAt(tripPoiIndex-1)) ){
    				Toast.makeText(CalendarActivity.this, "Please add the PoIs to the calendar in numerical order.", Toast.LENGTH_LONG).show();
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
			if(hourViews.indexOf(time) > numbHoursBack-1){	//not if it is before the first hour
				ViewDayHourItem Hour = hourViews.get(hourViews.indexOf(time)-numbHoursBack);
				
				//check if the entry is before the prev poi. abort if it is.
				if((Hour.GetHour() < prevHour) || ((Hour.GetHour() == prevHour)&&((int)(numbHoursBack*60+(minutes-timeNeededInMin)) < prevMinutes)))
				{
					//omg there is no time to walk to this poi
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
    	} // addWalkingTime
		return true;
	} // addWalkingTime
	
	private poiTextView findPoiViewBefore(Trip trip,ArrayList<ViewDayHourItem> hourViews,ViewDayHourItem newView){
		//XXX
		
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
	
	private poiTextView findPoiViewAfter(Trip trip,ArrayList<ViewDayHourItem> hourViews,ViewDayHourItem newView){
		//XXX
		
		//find the poi after this one.
		for(int i = hourViews.indexOf(newView); i < hourViews.size(); i++)//start here and work forwards.
		{
			//System.out.println("test hours: "+hourViews.get(i).GetHour());
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
	
	private double getDistance(Poi startPoi, Poi endPoi){
		double dist = 0;
		
		/*if we are offline, use straight line to calculate distance: 
        Location.distanceBetween(
		poi.getGeoPoint().getLatitudeE6()/1E6, 
		poi.getGeoPoint().getLongitudeE6()/1E6, 
		prevPoi.getGeoPoint().getLatitudeE6()/1E6, 
		prevPoi.getGeoPoint().getLongitudeE6()/1E6, dist);*/

		Road r = GoogleKML.getRoad(
				startPoi.getGeoPoint().getLatitudeE6()/1E6, 
				startPoi.getGeoPoint().getLongitudeE6()/1E6, 
				endPoi.getGeoPoint().getLatitudeE6()/1E6, 
				endPoi.getGeoPoint().getLongitudeE6()/1E6);
		dist = r.getDistance();
		
		return dist;
	}//getDistance
	
	/***
	 * Make sure WiFi or Data connection is enabled
	 */
	boolean initWifi(){
		boolean connected = CityExplorer.isConnected(this);
		if ( ! connected ){
			CityExplorer.showNoConnectionDialog( this );
			return false;
		}
		return true;
	} //initWifi

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.calendar, menu);

		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int itemID = item.getItemId();
		
		if(itemID == R.id.saveCalendar){
			if(trip.getFixedTimes().keySet().containsAll(trip.getPois()))
			{
				//done
				DBFactory.getInstance(this).addTimesToTrip(trip);
				Intent resultIntent = new Intent();
				resultIntent.putExtra(IntentPassable.TRIP, trip);
				setResult( Activity.RESULT_OK, resultIntent );
				finish();
			}else{
				Toast.makeText(this, "All locations are not added", Toast.LENGTH_LONG).show();
			}
		} // saveCalendar

		if(itemID == R.id.clearCalendar){
			ll.removeAllViews();
			addViews(); //Add the calendar view
			poiAdapter.clear();
			preparePoiList();
			
			trip.clearTimes();
			Toast.makeText(this, "Times cleared", Toast.LENGTH_SHORT).show();
		} //clearCalendar

		if(itemID == R.id.composePOIs){
			ll.removeAllViews();
			//poiAdapter.clear();
			//preparePoiList();
			//trip.clearTimes();
			Toast.makeText(this, "Going to WebView", Toast.LENGTH_SHORT).show();

			String url = "http://129.241.200.195:8080/UbiComposer"; // make json
			
// Testing how to launch a specific intent for the Firefox browser
//			Intent intent = new Intent(Intent.ACTION_MAIN, null);
//			intent.addCategory(Intent.CATEGORY_LAUNCHER);
//			intent.setComponent(new ComponentName("org.mozilla.firefox", "org.mozilla.firefox.App"));
//			intent.setAction("org.mozilla.gecko.BOOKMARK");
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.putExtra("args", "--url=" + url);
//			intent.setData(Uri.parse(url));
//			startActivity(intent);
			
			setContentView(R.layout.weblayout);
			webview = (WebView) findViewById(R.id.webview);
			if (webview == null){
				debug(0, "Where is wv? Remember setContentView(R.layout.webLayout)!" );
			}else{
				webview.getSettings().setJavaScriptEnabled(true);
				if ( initWifi() ){ //For downloading DBs

					webview.loadUrl(url);

// Verifying that our Javascript Interface class "Android" works
//					webview.loadData(""
//							+"<INPUT type=button onClick=\"showAndroidToast('Hello Android!')\" name\"NAME\"></INPUT>"
//							+"<script type=\"text/javascript\">"
//							+"  function showAndroidToast(toast) {"
//							+"		Android.showToast(toast);"
//							+"	}"
//							+"</script>"
//
//							+"Click a term in the list...", "text/hml", "utf-8");
					webview.addJavascriptInterface(new JavaScriptInterface(this), "Android");
					webview.setWebViewClient( new WebViewClient() );
					webview.getSettings().setJavaScriptEnabled(true);
					webview.getSettings().setBuiltInZoomControls(true);
					
					Toast.makeText(this, "Loading UbiComposer", Toast.LENGTH_LONG).show();
					//OK...
					//setupWebDBs( webview );
				}else{
					webview.loadData("Click to load online databases from web<BR>", "text/html", "utf-8");
					//webview.setOnTouchListener(this);
				}
			}// if webView found
			
		}// if Compose UbiServices

		return true;
	} // onOptionsItemSelected

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
				
				System.out.println("Clicked: "+tv.getPoi().getLabel());
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


} //class CalendarActivity

