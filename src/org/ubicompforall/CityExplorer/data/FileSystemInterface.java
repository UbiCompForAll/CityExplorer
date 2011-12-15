/**
 * @contributor(s): Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			15 Dec 2011
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
 */

package org.ubicompforall.CityExplorer.data;

import java.util.ArrayList;

/**
 * The interface for abstracting the connection to the fileSystem.
 * @author Christian Skjetne
 */
public interface FileSystemInterface{
	/**
	 * Interface method for fetching all DBs from the fileSystem.
	 * @return ArrayList containing all DBs from the fileSystem.
	 */
	public ArrayList<DB> getAllDBs();
	
	/**
	 * Interface method for fetching all DBs in a category.
	 * @param category To correspond with the fetched DBs.
	 * @return ArrayList containing DBs from fileSystem.
	 */
	public ArrayList<DB> getAllDBs(String category); 
	
	/**
	 * Interface method for fetching a DB from the fileSystem.
	 * @param privateId The ID of the DB to be fetched.
	 * @return The DB from the fileSystem.
	 */
	public DB getDB(int privateId);
	
	/**
	 * Interface method for deleting a DB from the fileSystem.
	 * @param db The DB to be deleted.
	 * @return Boolean as to whether the deletion was successful or not.
	 */
	boolean deleteDB(DB db);
	
	/**
	 * Interface method for setting the Context.
	 * @param context The Context to be set.
	 */
	//void setContext(Context context);  // Which Context? RS-111215
	
}//FileSystemInterface
