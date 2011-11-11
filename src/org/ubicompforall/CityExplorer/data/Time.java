package org.ubicompforall.CityExplorer.data;

/**
 * This class handles the times of a poi in a trip.
 * @author Christian Skjetne
 */
public class Time
{

	/** The hour of the time object. */
	public int hour;
	
	/** The minute of the time object. */
	public int minute;
	
	/**
	 * Instantiates a new time object.
	 *
	 * @param hour The hour.
	 * @param minute The minute.
	 */
	public Time(int hour, int minute)
	{
		this.hour = hour;
		this.minute = minute;
	}
}