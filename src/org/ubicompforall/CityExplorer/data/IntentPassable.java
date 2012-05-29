/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:
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
 *
 * 
 */

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