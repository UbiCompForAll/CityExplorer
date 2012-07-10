/**
 * @contributor(s): 
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

package org.ubicompforall.CityExplorer.map;

import java.util.ArrayList;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.data.CalendarPoiView;
import org.ubicompforall.CityExplorer.data.Poi;
import org.ubicompforall.CityExplorer.data.Trip;
import org.ubicompforall.CityExplorer.gui.CalendarActivity;
import org.ubicompforall.CityExplorer.gui.ViewDayHourItem;
import org.ubicompforall.CityExplorer.gui.ViewDayHourItem.poiTextView;

import android.os.AsyncTask;
import android.widget.Toast;

/**
 * @param calendar
 * @param time
 * @param poi
 * @return success
 */
//public class AsyncWalkingCalculation extends AsyncTask<TripPoiHourViews, Integer, Long> {
public class AsyncWalkingCalculation
 extends AsyncTask< CalendarPoiView, Integer, Integer > {
	//The calculated walking time
	private Long walkingTime;


	private void debug(int i, String string) {
		CityExplorer.debug(i, string);
	}//debug


	@Override
	//private boolean addWalkingTime( int tripPoiIndex, ViewDayHourItem time, int minutes, Poi poi ){
	protected Integer doInBackground( CalendarPoiView... datas) {
		if ( datas.length >0 ){
			CityExplorer.debug(0, "pois.length is "+ datas.length );

			CalendarPoiView data = datas[0];
			CalendarActivity calendar = data.calendar;
			Trip trip = calendar.getTrip();
			Poi poi = data.poi;
			ViewDayHourItem time = data.time;
			ArrayList< ViewDayHourItem > hourViews = data.hourViews;
			int tripPoiIndex = trip.getPois().indexOf( poi );
			int minutes = trip.getFixedTimes().get( poi ).minute;
			boolean calendarIsEmpty = calendar.calendarIsEmpty;
			Integer walkingTime = 0;
			//publishProgress((int) ((i / (float) count) * 100));
	
			debug(0, "tripPoiIndex is "+ tripPoiIndex );
			
			//find the previous poi entry
			poiTextView ptvBeforeOrNull = calendar.findPoiViewBefore(trip, hourViews, time);
			poiTextView ptvAfterOrNull  = calendar.findPoiViewAfter(trip, hourViews, time);
			debug(0, "before="+ptvBeforeOrNull+", after="+ptvAfterOrNull );
			
			if( (tripPoiIndex != 0 && !trip.isFreeTrip()) 
				|| ( !calendarIsEmpty && trip.isFreeTrip()) ){ //no travel to the first POI
	    		
	    		if(ptvAfterOrNull != null){ //there is another poi in the calendar after this one.
	    			//Toast.makeText(CalendarActivity.this, "Please add the PoIs to the calendar in chronological order.", Toast.LENGTH_LONG).show();
	    			debug(-1, "Which one" );
	    			return 0;
	    		}//if first poi
	    		
	    		if( !trip.isFreeTrip() ){
	    			if(calendarIsEmpty || ptvBeforeOrNull == null || !ptvBeforeOrNull.getPoi().equals(trip.getPoiAt(tripPoiIndex-1)) ){
	    				Toast.makeText( calendar, "Please add the PoIs to the calendar in numerical order.", Toast.LENGTH_LONG).show();
	        			debug(-1, "Which one calendarIsEmpty is "+calendarIsEmpty+", ptvBeforeOrNull is "+ptvBeforeOrNull+", tripPoiIndex is "+tripPoiIndex );
	    				return 0;
	    			}// if empty, or bad order
	    		}// if FixedTour
	    		
	    		Poi prevPoi = ptvBeforeOrNull.getPoi();
	    		int prevHour = ptvBeforeOrNull.getHourItem().GetHour();
	    		int prevMinutes	= ptvBeforeOrNull.getMinutes();
	    		
	    		double distance = calendar.getDistance(poi, prevPoi);
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
					if( (Hour.GetHour() < prevHour) || ((Hour.GetHour() == prevHour)&&((int)(numbHoursBack*60+(minutes-timeNeededInMin)) < prevMinutes)) ){
						//OMG there is no time to walk to this poi
						Toast.makeText( calendar, "You will not have time to walk between this and the previous PoI.", Toast.LENGTH_LONG).show();
						return 0;
					}
				} // if hour > hoursBack-1
	    	} // if not first poi
			return walkingTime;
		}else{ // addWalkingTime
			return 0;
		}//if input-data was given
	}//doInBackground

	@Override
	protected void onProgressUpdate(Integer... progress) {
		//setProgressPercent(progress[0]);
	}

	@Override
	//Add calendar walk entry:
	protected void onPostExecute( Integer result ) {
		CityExplorer.debug(0, "result is "+ result );
		CityExplorer.debug(0, "walkingTime is "+ walkingTime );

//    	poiTextView tv =  Hour.new poiTextView(CalendarActivity.this);
//		tv.setMinutes( (int)(numbHoursBack*60+(minutes-timeNeededInMin)) );
//		tv.setText("Walk "+ (int)distance +" meters");
//		tv.setPoi(poi);
//		tv.setClickable(true);
//		tv.setOnClickListener(ocl);
//		tv.setAsWalkingEntry(true);
//		Hour.addView(tv);
//		Hour.UpdateHeight();	
	}//onPostExecute

}//class AsyncWalkingCalculation
