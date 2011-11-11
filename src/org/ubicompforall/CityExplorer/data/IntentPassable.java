package org.ubicompforall.CityExplorer.data;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * The Class IntentPassable.
 */
public abstract class IntentPassable implements Parcelable {

	/** The Constant POILIST. */
	public static final String POILIST		= "poilist";
	
	/** The Constant TRIP. */
	public static final String TRIP			= "trip";
	
	/** The Constant POI. */
	public static final String POI = "poi";
	
	
	/** The Constant POItype. */
	private static final int POItype		= 1;
	
	/** The Constant TRIPtype. */
	private static final int TRIPtype		= 2;

	@Override
	public int describeContents(){
		if (this instanceof Poi){
			return POItype;
		}else if (this instanceof Trip){
			return TRIPtype;
		}else{
			return 0;
		}
	}
	
	@Override
	abstract public void writeToParcel(Parcel dest, int flags);
}