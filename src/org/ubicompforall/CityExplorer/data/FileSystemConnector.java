/**
 * @contributor(s): Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			15 December 2011
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
 * This class tracks existing DBs.
 * This class keeps hold of the tabs used in import mode.
 */

package org.ubicompforall.CityExplorer.data;

import java.io.File;
import java.util.ArrayList;
import org.ubicompforall.CityExplorer.CityExplorer;

import android.content.Context;

public class FileSystemConnector implements FileSystemInterface {

	/*** Field containing an {@link ArrayList} of the categoryFolders.*/
	private ArrayList<String> categoryFolders;

	/** All the DBs in the current categoryFolders **/
	ArrayList<DB> allDBs = null;

	private String dbPath = null;	// remembering the default path for locals DBs
	
	private Context ctx = null;	//context for display etc.

	public FileSystemConnector( Context context ){
		//INITIALIZE OWN FIELDS
		ctx = context;
		dbPath  = getDatabasePath();
		if (dbPath.equals("") ){
			debug(0, "What!? No dbPath given!");
		}else{
			debug(0, "dbPath is "+dbPath );
			categoryFolders = new ArrayList<String>();
			categoryFolders.add( dbPath );
			//Collections.sort(categoryFolders);
		}
		debug(0, "categoryFolders is "+categoryFolders );
		allDBs = getAllDBs();	// Find all DBs in categoryFolders
		debug(0, "allDBs.size is "+allDBs.size() );
	} // CONSTRUCTOR

	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	@Override
	public ArrayList<DB> getAllDBs() {
		if ( allDBs == null ){
			allDBs = new ArrayList<DB>();
			if (categoryFolders == null){
				debug(0, "categoryFolders NOT FOUND!" );
			}else{
				//categoryFolders.add( getFilesDir().getPath() ); // Testing RS-120201
				for ( String path : categoryFolders ){
					allDBs.addAll( findAllDBs(path) );
				} // for each folder
			} // if not null-pointer
		}
		return allDBs;
	}//getAllDBs

	@Override
	public ArrayList<DB>
	 findAllDBs( String category ) {
		ArrayList<DB> foundDBs = new ArrayList<DB>();
		File dir = new File( category );
		File[] files = dir.listFiles();
		if (files == null){
			debug(0, "No files found in "+dir.getPath() );
		}else{
			for ( int f=0; f<files.length ; f++ ){
				File file = files[f];
//Just cleaning up after an old Bug ;-)
				if ( file.getName().matches( ".*webview(Cache)?.db" ) ){
					file.delete();
				}else{
					debug(1, "Keep "+file );
				}
				foundDBs.add( new DB( file.getName(), dir.getName() ) );
			}// for each file
		}// if not null-pointer path->files
		return foundDBs;
	}// getAllDBs( category )

	@Override
	public DB getDB(int privateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteDB(DB db) {
		// TODO Auto-generated method stub
		return false;
	}//deleteDB

	public String getDatabasePath() {
		if (dbPath == null || dbPath.equals("") ){
			dbPath = ctx.getDatabasePath("dummy").getParent();
		}
		return dbPath;
	} // return the default db-path

}//FileSystemConnector
