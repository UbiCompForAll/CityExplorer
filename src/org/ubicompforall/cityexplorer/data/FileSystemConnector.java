/**
 * @contributor(s): Rune Sætre (NTNU)
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
 * This class tracks existing DBs.
 * This class keeps hold of the tabs used in import mode.
 */

package org.ubicompforall.cityexplorer.data;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.ubicompforall.cityexplorer.CityExplorer;

import android.content.Context;

public class FileSystemConnector implements FileSystemInterface {

	/*** Field containing an {@link ArrayList} of the categoryFolders.*/
	private ArrayList<String> categoryFolders;

	/** All the DBs in the current categoryFolders **/
	ArrayList<DB> allDBs = null;

	private String dbPath = null;	// remembering the default path for locals DBs
	
	public FileSystemConnector( Context context ){
		//INITIALIZE OWN FIELDS
		//categoryFolders = CityExplorer.CITIES; //e.g. { "Trondheim", "Etc?" }
		dbPath  = context.getDatabasePath( "dummy" ).getParent();
		debug(2, "dbPath is "+dbPath );

		//Store all folders in the database folder
		categoryFolders = new ArrayList<String>(); //e.g. { "Trondheim", "Etc?" }
		File dir = new File( dbPath );
		for ( File file : dir.listFiles() ){
			//debug(1, "test "+file );
			if ( file.isDirectory() ){
				debug(2, "Keep "+file );
				categoryFolders.add( file.getName() );
//			}else{	// Some cleaning up of files left after old bugs
//				file.delete();
			}
		}// for each file

		allDBs = getAllDBs();	// Find all DBs in categoryFolders (the cities)
		debug(2, "allDBs.size is "+allDBs.size() );
	} // CONSTRUCTOR

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	@Override
	public ArrayList<DB>
	 getAllDBs() {
		if ( allDBs == null ){
			allDBs = new ArrayList<DB>();
			if (categoryFolders == null){
				debug(0, "categoryFolders NOT FOUND!" );
			}else{
				//categoryFolders.add( getFilesDir().getPath() ); // Testing RS-120201
				for ( String path : categoryFolders ){
					debug(2, "categoryFolders city is "+path );
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
		File dir = new File( dbPath+"/"+category );
		File[] files = dir.listFiles();
		if (files == null){
			debug(0, "No files found in "+dir.getPath() );
		}else{
			for ( int f=0; f<files.length ; f++ ){
				File file = files[f];
				DB foundDb;
				try {
					foundDb = new DB( dbPath, dir.getName(), file.getName(), new URL( "http://"+file.getName() ) );
					foundDBs.add( foundDb );
				} catch (MalformedURLException e) {
					debug(-1, e.getMessage() );
				}
				debug(2, "Keep "+file );
			}// for each file
		}// if not null-pointer path->files
		return foundDBs;
	}// getAllDBs( category )


	public ArrayList<String> getCityFolders() {
		return categoryFolders;
	}


	@Override
	public boolean deleteDB( DB db ) {
		return db.delete();
	}//deleteDB

}//FileSystemConnector
