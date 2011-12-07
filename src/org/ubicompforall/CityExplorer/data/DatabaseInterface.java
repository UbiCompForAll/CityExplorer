/**
 * @contributor(s): Christian Skjetne (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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

package org.ubicompforall.CityExplorer.data;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * The interface for abstracting the connection to the database.
 * 
 * @author Christian Skjetne
 *
 */
public interface DatabaseInterface
{
	/**
	 * Interface method for opening the database connection.
	 * @return Boolean for successful opening or not
	 */
	public boolean	open();
	
	/**
	 * Interface method for closing the database connection.
	 */
	public void 	close();
	
	/**
	 * Interface method for checking if the connection is open.
	 * @return Boolean as to whether the connection is open or not.
	 */
	public boolean	isOpen();

	/**
	 * Interface method for fetching all PoIs from the database.
	 * @return Arraylist containing all PoIs from the database.
	 */
	public ArrayList<Poi> getAllPois();
	
	/**
	 * Interface method for fetching all PoI's in a category.
	 * @param category To correspond with the fetched PoIs.
	 * @return Arraylist containing PoIs from database.
	 */
	public ArrayList<Poi> getAllPois(String category); 
	
	/**
	 * Interface method for fetching favorite PoI's from the database.
	 * @param favorite Status for PoIs to be fetched.
	 * @return Arraylist containing PoIs from database.
	 */
	public ArrayList<Poi> getAllPois(Boolean favorite);
	
	/**
	 * Interface method for fetching all trips from the database.
	 * @return Arraylist containing all trips in the database.
	 */
	public ArrayList<Trip> getAllTrips();
	
	/**
	 * Interface method for fetching free/fixed trips from the database.
	 * @param free The boolean for which type of trips to fetch
	 * @return Arraylist containing Trips from database.
	 */
	public ArrayList<Trip> getAllTrips(Boolean free);
	
	/**
	 * Interface method for creating a new PoI in the database.
	 *
	 * @param poi the object containing the information to be added
	 * @return ID of the newly added PoI
	 */
	public int newPoi(Poi poi);
	
	/**
	 * Interface method for fetching a PoI from the database.
	 * @param privateId The ID of the PoI to be fetched.
	 * @return The PoI from the database.
	 */
	public Poi getPoi(int privateId);
	
	/**
	 * Interface method for editing information about an already existing PoI in the database.
	 *
	 * @param poi The already existing PoI to be edited.
	 */
	public void editPoi(Poi poi);
	
	/**
	 * Interface method for fetching the ID of a category in the database.
	 *
	 * @param title the title
	 * @return the category id
	 */
	int getCategoryId(String title);
	
	/**
	 * Interface method for fetching the names of all the categories in the database.
	 * @return Arraylist containing the names of all categories.
	 */
	public ArrayList<String> getCategoryNames();
	
	/**
	 * Interface method for creating a new trip in the database.
	 * @param t The Trip object to be stored.
	 */
	public void newTrip(Trip t); 	
	
	/**
	 * Interface method for fetching all empty trips from the database.
	 * @return Arraylist ontaining Trips.
	 */
	ArrayList<Trip> getAllEmptyTrips();
	
	/**
	 * Interface method for deleting a Poi from a Trip in the database.
	 * @param trip The Trip to be deleted from.
	 * @param poi The PoI to be deleted.
	 */
	public void deleteFromTrip(Trip trip, Poi poi);
	
	/**
	 * Interface method for adding a Poi to a Trip in the database.
	 * @param trip The Trip to be added to.
	 * @param poi The PoI to be added.
	 */
	public boolean addPoiToTrip(Trip trip, Poi poi);

	/**
	 * Interface method for adding visiting times to a Trip in the database.
	 * @param trip The Trip to be added times to.
	 */
	public void addTimesToTrip(Trip trip);
	
	/**
	 * Interface method for fetching the unique names of all Categories in the database.
	 * @return Arraylist containing all the names of categories.
	 */
	ArrayList<String> getUniqueCategoryNames();
	
	/**
	 * Interface method for fetching the names of all Categories with corresponding icons from the database.
	 * @return HashMap containing the names and icons.
	 */
	HashMap<String, Bitmap> getUniqueCategoryNamesAndIcons();
	
	/**
	 * Interface method for deleting a Trip from the database.
	 * @param trip The Trip to be deleted.
	 */
	void deleteTrip(Trip trip);
	
	/**
	 * Interface method for deleting a Poi from the database.
	 * @param poi The Poi to be deleted.
	 * @return Boolean as to wheter the deletion was successful or not.
	 */
	boolean deletePoi(Poi poi);
	
	/**
	 * Interface method for setting the Context.
	 * @param context The Context to be set.
	 */
	void setContext(Context context);
	
	/**
	 * Interface method for fetching the local ID of a Poi from the corresponding global ID in the database.
	 * @param globalId The global ID of the Poi.
	 * @return The local ID of the Poi.
	 */
	int getPoiPrivateIdFromGlobalId(int globalId);
	
	/**
	 * Interface method for fetching the global ID of a Poi from the corresponding local ID in the database.
	 * @param globalId The global ID of the Poi.
	 * @return The global ID of the Poi.
	 */
	int getTripPrivateIdFromGlobalId(int globalId);
	
	/**
	 * Interface method for fetching a Trip from the databse by using the Trips ID.
	 * @param privateId The local ID of the Trip.
	 * @return The Trip from the database.
	 */
	Trip getTrip(int privateId);	
}