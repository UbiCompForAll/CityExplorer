package org.ubicompforall.CityExplorer.data;

import java.util.ArrayList;

import org.ubicompforall.CityExplorer.gui.CalendarActivity;
import org.ubicompforall.CityExplorer.gui.ViewDayHourItem;

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
