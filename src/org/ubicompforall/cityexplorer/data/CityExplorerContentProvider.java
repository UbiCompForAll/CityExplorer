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

package org.ubicompforall.cityexplorer.data;

import org.ubicompforall.cityexplorer.CityExplorer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;

public class CityExplorerContentProvider extends ContentProvider{

	//Move to final static Contract class
	public static final String AUTHORITY = "org.ubicompforall.cityexplorer.provider";
	public static final String POI_TABLE = "PoiTable";	
	
	//City-Explorer Internal
	public static final String SQLITE_ALL_TABLE = SQLiteConnector.POI_MULTITABLE;

	//Content Type: vnd.android.cursor.dir/vnd.google.userword or poi or etc.
	//public static final Uri CONTENT_URI = Uri( AUTHORITY, POI_TABLE );
	public static final Uri CONTENT_URI = new Uri.Builder().encodedAuthority(AUTHORITY).appendPath(POI_TABLE).build();

	// Creates a UriMatcher object.
	private static final UriMatcher sUriMatcher = new UriMatcher(0);

    /*
	 * Defines a handle to the database helper object. The MainDatabaseHelper class is defined
	 * in a following snippet.
	 */
	private SQLiteConnector sqliteConnector;	//MainDatabaseHelper == SQLiteConnector == DatabaseInterface
	//private SQLiteDatabase db;    // Holds the database object // Use sqliteConnector.getReadableDatabase() or getWritableDatabase() instead

	/*
	 * Creates a new helper object. This method always returns quickly.
	 * Notice that the database itself isn't created or opened
	 * until SQLiteOpenHelper.getWritableDatabase is called
	 */
	public boolean onCreate() {
		sqliteConnector = DBFactory.getInstance( getContext() );  //calls new SQLiteConnector()  //mOpenHelper = new DBFactory();
	    return true;
	}

	@Override
	// Implements the provider's insert method
	public Uri insert(Uri uri, ContentValues values) {
	    /*
	     * Gets a writable database. This will trigger its creation if it doesn't already exist.
	     */
	    //db = sqliteConnector.getWritableDatabase();
	    // Insert code here to determine which table to open, handle error-checking, and so forth
		return null;
	}//insert
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/*
	 * Choose the table to query and a sort order based on the code returned for the incoming
	 * URI. Here, too, only the statements for PoiTtable are shown.
	 */
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) throws SQLiteException {
		CityExplorer.debug(1, "Looking for "+CONTENT_URI );
		
		

	    /*
	     * Sets the integer value for multiple rows in PoiTable to 1. Notice that no wildcard is used in the path
	     * case 1: "content://org.ubicompforall.cityexplorer.provider/PoiTable
	     */
	    sUriMatcher.addURI(AUTHORITY, POI_TABLE, 1);

	    /*
	     * Sets the code for a single row (Poi) to 2. In this case, the "#" wildcard is used.
	     * case 2: "content://org.ubicompforall.cityexplorer.provider/PoiTable/3" matches
	     */
	    sUriMatcher.addURI(AUTHORITY, POI_TABLE+"/#", 2);
	    
	    // Select only matching addresses for a PoI 
	    if (selection == null) {
		    selection = "POI.address_id = ADDR._id"; // " AND POI.category_id = CAT._id";	    	
	    } else {
		    selection = "POI.address_id = ADDR._id AND " + selection; // " AND POI.category_id = CAT._id";
	    }

	    switch ( sUriMatcher.match(uri) ) {
			// If the incoming URI was for all of the PoiTable
			case 1:
			    if ( TextUtils.isEmpty(sortOrder) ) sortOrder = null; // _ID_COL + " ASC";
			    break;

			// If the incoming URI was for a single row
			case 2:
				/*
				 * Because this URI was for a single row, the _ID_COL value part is
				 * present. Get the last path segment from the URI; this is the _ID_COL value.
				 * Then, append the value to the WHERE clause for the query
				 */
// TODO: does owrk beacuse of ambiguity between POI table 
				selection = selection + " AND _id" + " = " + uri.getLastPathSegment();
				break;
				
			default:
				CityExplorer.debug(-1, "ERROR! Legal providers are "+AUTHORITY+"/"+POI_TABLE+"/# etc." );
			    // If the URI is not recognized, you should do some error handling here.
		}//switch
	    
		// call the code to actually do the query
	    //CityExplorer.debug(2, "Go!" );
	    try{
	    	return sqliteConnector.getReadableDatabase().query( SQLITE_ALL_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
	    }catch (SQLiteException e){
	    	e.printStackTrace();
		    CityExplorer.debug(-1, "Error: "+e.getMessage() );
		    CityExplorer.debug(-1, "POI_TABLE select "+projection[0]+" etc..." );
		    throw new SQLiteException(e.toString());
	    }
	}//query
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}//class CityContentProvider
