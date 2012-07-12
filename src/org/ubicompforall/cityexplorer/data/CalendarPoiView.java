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

package org.ubicompforall.cityexplorer.data;

import java.util.ArrayList;

import org.ubicompforall.cityexplorer.gui.CalendarActivity;
import org.ubicompforall.cityexplorer.gui.ViewDayHourItem;

public class CalendarPoiView{
	public CalendarActivity calendar; //int tripPoiIndex;	//int minutes;
	public Poi poi;
	public ViewDayHourItem time;
	public ArrayList< ViewDayHourItem > hourViews;

	public CalendarPoiView( CalendarActivity calendar, Poi poi, ViewDayHourItem time,
			ArrayList< ViewDayHourItem > hourViews ){
		this.calendar = calendar;
		this.poi = poi;
		this.time = time;
	}//CONSTRUCTOR
}//class TripViewPoi
