//package org.ubicompforall.CityExplorer.data;
//
//import org.ubicompforall.CityExplorer.CityExplorer;
//
//import android.content.ContentProvider;
//import android.content.ContentValues;
//import android.content.UriMatcher;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteException;
//import android.net.Uri;
//import android.text.TextUtils;
//
//public class CityContentProvider extends ContentProvider{
//	
//	//Activity context;
//
//	//Move to final static Contract class
//	public static final String AUTHORITY = "org.ubicompforall.CityExplorer.provider";
//	public static final String POI_TABLE = "PoiTable";
//	
//	//City-Explorer Internal
//	public static final String SQLITE_ALL_TABLE = SQLiteConnector.POI_MULTITABLE;
//
//	//public static final Uri CONTENT_URI = Uri( AUTHORITY, POI_TABLE );
//	public static final Uri CONTENT_URI = new Uri.Builder().encodedAuthority(AUTHORITY).appendPath(POI_TABLE).build();
////	private static Uri Uri(String authority, String table) {
////		Uri uri = new Uri.Builder().encodedAuthority(authority).appendPath(table).build();
////		CityExplorer.debug(0, "Created Uri: "+uri );
////		return uri;
////	}//uri
//
//	//Content Type: vnd.android.cursor.dir/vnd.google.userword or poi or etc.
//
//	// Creates a UriMatcher object.
//    private static final UriMatcher sUriMatcher = new UriMatcher(0);
//    
//    /*
//	 * Defines a handle to the database helper object. The MainDatabaseHelper class is defined
//	 * in a following snippet.
//	 */
//	private SQLiteConnector sqliteConnector;	//MainDatabaseHelper == SQLiteConnector == DatabaseInterface
//	//private SQLiteDatabase db;    // Holds the database object // Use sqliteConnector.getReadableDatabase() or getWritableDatabase() instead
//
////	public CityContentProvider( Activity context ){
////		this.context = context;
////	}
//	
//	/*
//	 * Creates a new helper object. This method always returns quickly.
//	 * Notice that the database itself isn't created or opened
//	 * until SQLiteOpenHelper.getWritableDatabase is called
//	 */
//	public boolean onCreate() {
//		sqliteConnector = DBFactory.getInstance( getContext() );  //calls new SQLiteConnector()  //mOpenHelper = new DBFactory();
//	    return true;
//	}//onCreate
//
//	@Override
//	// Implements the provider's insert method
//	public Uri insert(Uri uri, ContentValues values) {
//	    /*
//	     * Gets a writable database. This will trigger its creation if it doesn't already exist.
//	     */
//	    //db = sqliteConnector.getWritableDatabase();
//	    // Insert code here to determine which table to open, handle error-checking, and so forth
//		return null;
//	}//insert
//	
//	@Override
//	public int delete(Uri uri, String selection, String[] selectionArgs) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	
//	@Override
//	public String getType(Uri uri) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	/*
//	 * Choose the table to query and a sort order based on the code returned for the incoming
//	 * URI. Here, too, only the statements for PoiTtable are shown.
//	 */
//	public Cursor query(Uri uri, String[] projection, String selection,
//			String[] selectionArgs, String sortOrder) throws SQLiteException {
//		CityExplorer.debug(0, "Looking for "+CONTENT_URI );
//	    /*
//	     * Sets the integer value for multiple rows in PoiTable to 1. Notice that no wildcard is used in the path
//	     */
//	    sUriMatcher.addURI(AUTHORITY, POI_TABLE, 1);
//
//	    /*
//	     * Sets the code for a single row (Poi) to 2. In this case, the "#" wildcard is used.
//	     * "content://org.ubicompforall.CityExplorer.provider/poiTable/3" matches, but
//	     * "content://org.ubicompforall.CityExplorer.provider/poiTable does not.
//	     */
//	    sUriMatcher.addURI(AUTHORITY, POI_TABLE+"/#", 2);
//
//	    switch ( sUriMatcher.match(uri) ) {
//			// If the incoming URI was for all of the PoiTable
//			case 1:
//			    if ( TextUtils.isEmpty(sortOrder) ) sortOrder = "_ID ASC";
//			    break;
//
//			// If the incoming URI was for a single row
//			case 2:
//				/*
//				 * Because this URI was for a single row, the _ID value part is
//				 * present. Get the last path segment from the URI; this is the _ID value.
//				 * Then, append the value to the WHERE clause for the query
//				 */
//				selection = selection + "_ID = " + uri.getLastPathSegment();
//				break;
//				
//			default:
//				CityExplorer.debug(-1, "ERROR! Legal providers are "+AUTHORITY+"/"+POI_TABLE+"/# etc." );
//			    // If the URI is not recognized, you should do some error handling here.
//		}//switch
//	    
//		// call the code to actually do the query
//	    try{
//	    	return sqliteConnector.getReadableDatabase().query( SQLITE_ALL_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
//	    }catch (SQLiteException e){
//	    	e.printStackTrace();
//		    CityExplorer.debug(-1, "Error: "+e.getMessage() );
//		    CityExplorer.debug(-1, "POI_TABLE select "+projection[0]+" etc..." );
//		    throw new SQLiteException(e.toString());
//	    }
//	}//query
//	
//	@Override
//	public int update(Uri uri, ContentValues values, String selection,
//			String[] selectionArgs) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	
//}//class CityContentProvider
