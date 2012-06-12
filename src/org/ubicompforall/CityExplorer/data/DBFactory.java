/**
 * @contributor(s): Christian Skjetne (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.gui.MyPreferencesActivity;

import android.content.Context;
import android.widget.Toast;

public class DBFactory{
	/**
	 * The enum DBType.
	 */
	public enum DBType{
		SQLITE;
	}

	/** The DataBase connector instance. */
	private static DatabaseInterface dbConnectorInstance;
	private static File currentDbFile = null; // Cannot move to SQLiteConnector dbConnectorInstance?
	
	/** The database type. */
	private static DBType databaseType = DBType.SQLITE; //change this to change the database type
	
	/**
	 * Change the single instance of the DBFactory to a new (existing) db-location.
	 *
	 * @param context The context, that will be current from now to next getInstance
	 * @return The single instance of the DBFactory
	 */
	//public static DatabaseInterface changeInstance( Context context, String dbName ){
	public static DatabaseInterface changeInstance( Context context, File newDbFile ){
		if( dbConnectorInstance != null && dbConnectorInstance.isOpen() == true ){
			dbConnectorInstance.close();
		}
		if(databaseType == DBType.SQLITE){
			//currentDbFile = new File( context.getDatabasePath( CityExplorer.DEFAULT_DBFOLDER ).getAbsolutePath()+"/"+dbName );
			currentDbFile = newDbFile;
			CityExplorer.debug(1, "new dbFile is "+currentDbFile );
			dbConnectorInstance = new SQLiteConnector( context, currentDbFile );
		} // if right type
		dbConnectorInstance.openOrCreate( currentDbFile );
		dbConnectorInstance.setContext(context);

		//Store in Settings/Preferences
		MyPreferencesActivity.storeDbNameSetting( context, currentDbFile.getName() );
		MyPreferencesActivity.storeDbFolderSetting( context, currentDbFile.getParentFile().getName() );
		return dbConnectorInstance;
	}//changeInstance

	/**
	 * Creates an empty database on the dbFile location and rewrites it with the database from assets.
	 * dbFile should be set to data/data/project/databases/Downloaded-folder
	 * @return 
	 *
	 * @throws IOException when the asset database file can not be read,
	 * or the database-destination file can not be written to,
	 * or when parent directories to the database-destination file do not exist.
	 */
 	public static File createDataBase( Context myContext, final String ASSETS_DB_NAME, final File dbFile ) throws IOException {
		InputStream 	isAssetDb 	= myContext.getAssets().open( dbFile.getName() );
 		OutputStream 	osDbPath;
		byte[] 			buffer 		= new byte[1024 * 64];
		int 			bytesRead;

		CityExplorer.debug(1, "Make copy of default assets/"+ASSETS_DB_NAME+" to "+dbFile );
		try {
			dbFile.getParentFile().mkdirs();
			CityExplorer.debug(2, "Made folder: "+dbFile );
			osDbPath = new FileOutputStream( dbFile );
			
			while ((bytesRead = isAssetDb.read(buffer))>0){
				try {
					osDbPath.write(buffer, 0, bytesRead);
				} catch (IOException io) {
					CityExplorer.debug(0, "Failed to write to " + dbFile );
					io.printStackTrace();
				}
				CityExplorer.debug(0, "copyDataBase(): wrote " + bytesRead + " bytes");
			}//while more bytes to copy
			osDbPath.flush();
			osDbPath.close();
			buffer = null;
			CityExplorer.debug(0, dbFile.getName()+" successufully copied");
			Toast.makeText(myContext, "Local DB-file was missing, made a new copy from assets/"+dbFile.getName(), Toast.LENGTH_LONG);

			//myDataBase = this.getReadableDatabase(); // Moved back to SQLiteConnector
		} catch (IOException e) {
			CityExplorer.debug(0, "Failed to copy "+ dbFile.getName() + " to " + dbFile.getParent());
			e.printStackTrace();
		}//try catch (making copy)
		return dbFile;
	}//createDataBase


	/**
	 * Gets the single instance of DBFactory.
	 *
	 * @param context The context, that will be current from now to next getInstance
	 * @return Single instance of DBFactory
	 */
	public static DatabaseInterface getInstance( Context context ){
		if ( currentDbFile == null || currentDbFile.equals("") ){
			String currentDbFolder	= MyPreferencesActivity.getSelectedDbFolder( context );
			String currentDbFilename= MyPreferencesActivity.getSelectedDbName( context );
			String currentDbFolderUri = context.getDatabasePath(currentDbFolder).getAbsolutePath();
			if ( ! currentDbFolderUri.matches( ".*"+currentDbFolder ) ){
				currentDbFolderUri += "/" + currentDbFolder;
			}
			String currentDbFileUri = currentDbFolderUri+"/"+currentDbFilename;
			currentDbFile = new File( currentDbFileUri );
			CityExplorer.debug(2, "currentDbFile was set to " + currentDbFile );
		}else{
			//CityExplorer.debug(0, "Skipping with dbFile "+currentDbFile );
		}
		//CityExplorer.debug(1, "currentDbFile is "+currentDbFile); //Over-used?
		if(dbConnectorInstance == null || dbConnectorInstance.isOpen() == false){
			if( databaseType == DBType.SQLITE ){
				CityExplorer.debug(2, "currentDbFile is "+currentDbFile);
				dbConnectorInstance = new SQLiteConnector( context, currentDbFile );
			} // if right type
			dbConnectorInstance.openOrCreate( currentDbFile );
		} // if DB not already open
		dbConnectorInstance.setContext(context);
		return dbConnectorInstance;
	}//getInstance
}//class DBFactory
