/**
 * @contributor(s): Christian Skjetne (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:		08 December 2011
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.ubicompforall.CityExplorer.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

/**
 * The Class SQLiteConnector.
 */
public class SQLiteConnector extends SQLiteOpenHelper implements DatabaseInterface
{
	/** The Constant DEBUG. */
	private static final boolean DEBUG = false;

	/** The Constant DB_PATH, which is the path to were the database is saved. */

	//private static final String	DB_PATH = "/data/data/org.ubicompforall.CityExplorer/databases/";
	private String DB_PATH ="";

	/** The Constant DB_NAME, which is our database name. */
	//private static final String	DB_NAME = "CityExplorer.backup.db";
	private static String	DB_NAME = "CityExplorer.sqlite";

	/** The Constant POI indexes */
	private static final int	POI_global_id = 0;
	private static final int	POI_title = 1;
	private static final int	POI_description = 2;
	private static final int	POI_street_name = 3;
//	private static final int	POI_zipcode = 4;	remove
	private static final int	POI_city = 4;
	private static final int	POI_lat = 5;
	private static final int	POI_lon = 6;
	private static final int	POI_favourite = 7;
	private static final int	POI_category = 8;
	private static final int	POI_web_page = 9;
	private static final int	POI_openingHours = 10;
	private static final int	POI_telephone = 11;
	private static final int	POI_image_url = 12;

	/** The Constant POI string for SQL queries */
	private static final String	POI_SQL_id = "POI._id";
	private static final String	POI_SQL_global_id = "POI.global_id";
	private static final String	POI_SQL_title = "POI.title";
	private static final String	POI_SQL_description = "POI.description";
	private static final String	POI_SQL_street_name = "ADDR.street_name";
//	private static final String	POI_SQL_zipcode = "ADDR.zipcode";	not used
	private static final String	POI_SQL_city = "ADDR.city";
	private static final String	POI_SQL_lat = "ADDR.lat";
	private static final String	POI_SQL_lon = "ADDR.lon";
	private static final String	POI_SQL_favourite = "POI.favourite";
	private static final String	POI_SQL_category = "CAT.title";
	private static final String	POI_SQL_web_page = "POI.web_page";
	private static final String	POI_SQL_openingHours = "POI.openingHours";
	private static final String	POI_SQL_telephone = "POI.telephone";
	private static final String	POI_SQL_image_url = "POI.image_url";



	/** The SQLiteDatabase object we are using. */
	private SQLiteDatabase		myDataBase;

	/** The context. */
	private Context				myContext;

	/** The whole path to our database. */
	private String				myPath;

	private static final String POI_TABLE = "poi";
	//private static final String COUNT_ALL_POIS = "SELECT Count(*) FROM "+POI_TABLE;

	/** The Constant SELECT_ALL_POIS, which is a SQL-query for selecting all POIs. */
	private static final String SELECT_ALL_POIS=
		"SELECT " +
		POI_SQL_id + "," +
		POI_SQL_global_id + "," +
		POI_SQL_title + "," + POI_SQL_description + "," +
		POI_SQL_street_name + "," + /* ZIP code removed + */ POI_SQL_city + "," + POI_SQL_lat + "," + POI_SQL_lon + "," +
		POI_SQL_favourite + "," + POI_SQL_category + "," + POI_SQL_web_page + "," +
		POI_SQL_openingHours + "," + POI_SQL_telephone + "," + POI_SQL_image_url + " " +
		"FROM poi as POI, address as ADDR, category as CAT " +
		"WHERE POI.address_id = ADDR._id AND POI.category_id = CAT._id";


	/**
	 * Public constructor that takes and keeps a reference of the passed context
	 * in order to access the application assets and resources.
	 *
	 * @param context The context
	 */
	public SQLiteConnector(Context context) {
		super(context, DB_NAME, null, 2);
		this.myContext = context;

// code from students
//		myPath = DB_PATH + DB_NAME;

		File dbName = context.getDatabasePath(DB_NAME);
		DB_PATH = dbName.getParent();
		myPath = dbName.toString();

		Log.d("CityExplorer", myPath+" starting up");
	}//SQLiteConnector CONSTRUCTOR

	@Override
	public void setContext(Context context) {
		this.myContext = context;
	}

	/**
	 * Creates an empty database on the data/data/project-folder and rewrites it with your own database.
	 *
	 * @throws IOException when the asset database file can not be read,
	 * or the database-destination file can not be written to,
	 * or when parent directories to the database-destination file do not exist.
	 * */

 	public void createDataBase() throws IOException {

		OutputStream 	osDbPath;
		InputStream 	isAssetDb 	= myContext.getAssets().open(DB_NAME);
		byte[] 			buffer 		= new byte[1024 * 64];
		int 			bytesRead;

		Log.d("CityExplorer", "Make copy of default "+DB_NAME+" to "+myPath);
		try {
			osDbPath = new FileOutputStream(myPath);

			while ((bytesRead = isAssetDb.read(buffer))>0){
				try {
					osDbPath.write(buffer, 0, bytesRead);
				} catch (IOException io) {
					Log.d("CityExplorer","Failed to write to " + DB_PATH);
					io.printStackTrace();
				}
				Log.d("CityExplorer","copyDataBase(): wrote " + bytesRead + " bytes");
			}//while more bytes to copy
			osDbPath.flush();
			osDbPath.close();
			buffer = null;
			Log.d("CityExplorer",DB_NAME+" successufully copied");

			myDataBase = this.getReadableDatabase();
		} catch (IOException io) {
			Log.d("CityExplorer","Failed to copy "+ DB_NAME + " to " + DB_PATH);
			io.printStackTrace();
		}//try catch (making copy)
		return;
	}//createDataBase


	@Override
	public ArrayList<Poi> getAllPois() {
		return getPoisFromCursor( myDataBase.rawQuery( SELECT_ALL_POIS, null));
	}

	@Override
	public ArrayList<Poi> getAllPois(String category) {
		return getPoisFromCursor(
				myDataBase.rawQuery(
						SELECT_ALL_POIS + " AND CAT.title = ?",
						new String[]{category}					// replaces ?s
				)
		);
	}//getAllPois(category)

	@Override
	public ArrayList<Poi> getAllPois(Boolean favorite){
		return 	getPoisFromCursor(
				myDataBase.rawQuery(
						SELECT_ALL_POIS + " AND POI.favorite = ?",
						new String[]{"" + (favorite? 1 : 0)}	// replaces ?s
				)
		);
	}//getAllPois(favorite)

	@Override
	public Poi getPoi(int privateId){
		return 	getPoisFromCursor(
				myDataBase.rawQuery(
						SELECT_ALL_POIS + " AND POI._id = ?",
						new String[]{""+privateId}	// replaces ?s
				)
		).get(0);
	}//getPoi(privId)

	/**
	 * Gets the pois from the cursor.
	 *
	 * @param c The cursor to fetch pois from.
	 * @return The pois from the cursor.
	 */

	private ArrayList<Poi> getPoisFromCursor(Cursor c){
		ArrayList<Poi> pois = new ArrayList<Poi>();
		while(c.moveToNext()){
			pois.add(
				new Poi.Builder(
					c.getString(POI_title),
					new PoiAddress.Builder(
						c.getString(POI_city)
					)
//					.zipCode(c.getInt(POI_zipcode)) 	// ZIP code removed
					.street(c.getString(POI_street_name))
					.longitude(c.getDouble(POI_lon))
					.latitude(c.getDouble(POI_lat))
					.build()
				).description(c.getString(POI_description))
				.category(c.getString(POI_category))
				.favourite((1==c.getInt(POI_favourite)))
				.openingHours(c.getString(POI_openingHours))
				.webPage(c.getString(POI_web_page))
				.telephone(c.getString(POI_telephone))
				.idPrivate(c.getInt(0))
				.imageURL(c.getString(POI_image_url))
				.idGlobal(c.isNull(POI_global_id) ? -1:c.getInt(POI_global_id))
				.build()
			);
		}//while more pois
		c.close();
		return pois;
	}//getPoisFromCursor

	@Override
	public ArrayList<Trip> getAllEmptyTrips(){
		ArrayList<Trip> trips = new ArrayList<Trip>();

		String sqlstr = "SELECT " +
		"TRIP._id," +
		"TRIP.title," +
		"Trip.free_trip," +
		"TRIP.description, " +
		"TRIP.global_id "+
		"FROM trip as TRIP " +
		"WHERE TRIP._id NOT IN (" +
		"SELECT " +
		"TRIP._id " +
		"FROM trip as TRIP, poi as POI, trip_poi as TP " +
		"WHERE POI._id = TP.poi_id AND TRIP._id = TP.trip_id)";

		Cursor c = myDataBase.rawQuery(sqlstr, null);
		int currentTripId = -1;
		Trip trip = new Trip.Builder("").build();
		while( c.moveToNext() ){
			if(c.getInt(0) != currentTripId) { //make new trip
				if(DEBUG) System.out.println("Got trip: "+c.getString(1/*"TRIP.title"*/));
				if(currentTripId != -1) { //next trip on the list
					trips.add(trip);
				}
				trip = new Trip.Builder(c.getString(1))
				.idPrivate(c.getInt(0))
				.description(c.getString(3))
				.freeTrip(1==c.getInt(2))
				.idGlobal(c.getInt(4))
				.build();

				currentTripId = trip.getIdPrivate();
			}
		}// while more empty trips
		if(currentTripId != -1) { //next trip on the list
			trips.add(trip);
		}
		c.close();
		return trips;
	}//getAllEmptyTrips()

	@Override
	public ArrayList<Trip> getAllTrips(Boolean free){
		ArrayList<Trip> trips = new ArrayList<Trip>();

		String sqlstr = "SELECT " +
		"TRIP._id," +			//0
		"TRIP.title," +			//1
		"TRIP.description," +	//2
		"POI._id," +			//3
		"POI.title," +			//4
		"POI.description," +	//5
		"ADDR.street_name," +	//6
// ZIP code removed
//		"ADDR.zipcode," +		//7
		"ADDR.city," +			//8
		"ADDR.lat," +			//9
		"ADDR.lon," +			//10
		"CAT.title," +			//11
		"POI.favorite," +		//12
		"TP.poi_number," +		//13
		"Trip.free_trip, " +	//14
		"Poi.image_url, " +		//15
		"TP.hour, "+			//16
		"TP.minute, "+			//17
		"TRIP.global_id, "+
		"POI.global_id, "+
		"POI.telephone "+
		"FROM poi as POI, address as ADDR, category as CAT, trip as TRIP, trip_poi as TP " +
		"WHERE POI._id = TP.poi_id AND TRIP._id = TP.trip_id AND POI.address_id = ADDR._id AND POI.category_id = CAT._id AND TRIP.free_trip = ? " +
		"ORDER BY TRIP._id, TP.poi_number";
		Cursor c = myDataBase.rawQuery(sqlstr, new String[]{"" + (free? 1 : 0)});

		if(DEBUG) System.out.println("TRIPS: " + c.getCount());
		int currentTripId = -1;
		Trip trip = new Trip.Builder("").build();
		while(c.moveToNext()){
			if(c.getInt(0/*"TRIP.id"*/) != currentTripId) { //make new trip
				if(DEBUG) System.out.println("Got trip: "+c.getString(1/*"TRIP.title"*/));
				if(currentTripId != -1) { //next trip on the list
					trips.add(trip);
				}
				trip = new Trip.Builder(c.getString(1/*"TRIP.title"*/))
				.idPrivate(c.getInt(0/*"TRIP.id"*/))
				.description(c.getString(2/*"TRIP.description"*/))
				.freeTrip(1==c.getInt(14))
				.idGlobal(c.getInt(18))
				.build();
				currentTripId = trip.getIdPrivate();
			}

			Poi poi = new Poi.Builder( c.getString(4/*"POI.title"*/),
				new PoiAddress.Builder( c.getString(8/*"ADDR.city"*/)
				)
// ZIP code removed
//				.zipCode(c.getInt(7/*"ADDR.zipcode"*/))
				.street(c.getString(6/*"ADDR.street_name"*/))
				.longitude(c.getDouble(10/*"ADDR.lon"*/)).latitude(c.getDouble(9/*"ADDR.lat"*/))
				.build()
			).description(c.getString(5/*"POI.description"*/))
			.category(c.getString(11/*"CAT.title"*/))
			.favourite((1==c.getInt(12/*"POI.favorite"*/)))
			.imageURL(c.getString(15))
			.telephone(Integer.toString(c.getInt(20)))
			.idPrivate(c.getInt(3))
			.idGlobal(c.getInt(19)).build();
			trip.addPoi(poi);
			if(c.getInt(16) != -1 && c.getInt(17) != -1)//add time if it is not -1
				trip.setTime(poi, new Time(c.getInt(16), c.getInt(17)));
		}//while more trips
		if(currentTripId != -1) { //next trip on the list
			trips.add(trip);
		}
		c.close();
		return trips;
	}//getAllTrips(free)

	@Override
	public ArrayList<Trip> getAllTrips(){
		ArrayList<Trip> trips = new ArrayList<Trip>();

		String sqlstr = "SELECT " +
		"TRIP._id," +			//0
		"TRIP.title," +			//1
		"TRIP.description," +	//2
		"POI._id," +			//3
		"POI.title," +			//4
		"POI.description," +	//5
		"ADDR.street_name," +	//6
// ZIP code removed
//		"ADDR.zipcode," +		//7
		"ADDR.city," +			//8
		"ADDR.lat," +			//9
		"ADDR.lon," +			//10
		"CAT.title," +			//11
		"POI.favorite," +		//12
		"TP.poi_number," +		//13
		"Trip.free_trip, " +	//14
		"Poi.image_url, " +		//15
		"TP.hour, "+				//16
		"TP.minute, "+			//17
		"TRIP.global_id, "+
		"POI.global_id, "+
		"POI.telephone "+
		"FROM poi as POI, address as ADDR, category as CAT, trip as TRIP, trip_poi as TP " +
		"WHERE POI._id = TP.poi_id AND TRIP._id = TP.trip_id AND POI.address_id = ADDR._id AND POI.category_id = CAT._id " +
		"ORDER BY TRIP._id, TP.poi_number";
		Cursor c = myDataBase.rawQuery(sqlstr, null);

		System.out.println("TRIPS: " + c.getCount());

		int currentTripId = -1;

		Trip trip = new Trip.Builder("").build();
		while(c.moveToNext()){
			if(c.getInt(0/*"TRIP.id"*/) != currentTripId) { //make new trip
				if(DEBUG) System.out.println("Got trip: "+c.getString(1/*"TRIP.title"*/));
				if(currentTripId != -1) { //next trip on the list
					trips.add(trip);
				}
				trip = new Trip.Builder(c.getString(1/*"TRIP.title"*/))
				.idPrivate(c.getInt(0/*"TRIP.id"*/))
				.description(c.getString(2/*"TRIP.description"*/))
				.freeTrip(1==c.getInt(14))
				.idGlobal(c.getInt(18))
				.build();
				currentTripId = trip.getIdPrivate();
			}//if not current trip - Make new
			Poi poi = new Poi.Builder(c.getString(4/*"POI.title"*/),new PoiAddress.Builder
					(c.getString(8/*"ADDR.city"*/))
// ZIP code removed
//			.zipCode(c.getInt(7/*"ADDR.zipcode"*/))
			.street(c.getString(6/*"ADDR.street_name"*/))
			.longitude(c.getDouble(10/*"ADDR.lon"*/)).latitude(c.getDouble(9/*"ADDR.lat"*/))
			.build()
			).description(c.getString(5/*"POI.description"*/))
			.category(c.getString(11/*"CAT.title"*/))
			.favourite((1==c.getInt(12/*"POI.favorite"*/)))
			.imageURL(c.getString(15))
			.telephone(Integer.toString(c.getInt(20)))
			.idPrivate(c.getInt(3))
			.idGlobal(c.getInt(19))
			.build();
			trip.addPoi(poi);
			trip.setTime(poi, new Time(c.getInt(16), c.getInt(17)));
		}//while more trips
		if(currentTripId != -1)//next trip on the list
		{
			trips.add(trip);
		}

		c.close();
		return trips;
	}//getAllTrips()

	@Override
	public Trip getTrip(int privateId){
		ArrayList<Trip> trips = new ArrayList<Trip>();

		String sqlstr = "SELECT " +
		"TRIP._id," +			//0
		"TRIP.title," +			//1
		"TRIP.description," +	//2
		"POI._id," +			//3
		"POI.title," +			//4
		"POI.description," +	//5
		"ADDR.street_name," +	//6
// ZIP code removed
//		"ADDR.zipcode," +		//7
		"ADDR.city," +			//8
		"ADDR.lat," +			//9
		"ADDR.lon," +			//10
		"CAT.title," +			//11
		"POI.favorite," +		//12
		"TP.poi_number," +		//13
		"Trip.free_trip, " +	//14
		"Poi.image_url, " +		//15
		"TP.hour, "+			//16
		"TP.minute, "+			//17
		"TRIP.global_id, "+
		"POI.global_id, "+
		"POI.telephone "+
		"FROM poi as POI, address as ADDR, category as CAT, trip as TRIP, trip_poi as TP " +
		"WHERE POI._id = TP.poi_id AND TRIP._id = TP.trip_id AND POI.address_id = ADDR._id AND POI.category_id = CAT._id AND TRIP._id = ? "+
		"ORDER BY TRIP._id, TP.poi_number";

		String sqlstrEMPTY = "SELECT " +
		"TRIP._id," +				//0
		"TRIP.title," +				//1
		"Trip.free_trip," +			//2
		"TRIP.description, " +		//3
		"TRIP.global_id "+
		"FROM trip as TRIP " +
		"WHERE TRIP._id = ? AND TRIP._id NOT IN (" +
		"SELECT " +
		"TRIP._id " +
		"FROM trip as TRIP, poi as POI, trip_poi as TP " +
		"WHERE POI._id = TP.poi_id AND TRIP._id = TP.trip_id)";

		Cursor c = myDataBase.rawQuery(sqlstr, new String[]{""+privateId});

		if(c.getCount() == 0) { //the trip may be empty
			c = myDataBase.rawQuery(sqlstrEMPTY, new String[]{""+privateId});
			if( c.moveToNext() ) {
				Trip trip = new Trip.Builder(c.getString(1/*"TRIP.title"*/))
				.idPrivate(c.getInt(0/*"TRIP.id"*/))
				.description(c.getString(3/*"TRIP.description"*/))
				.freeTrip(1==c.getInt(2))
				.idGlobal(c.getInt(4))
				.build();
				trips.add(trip);
			}
		}else{ // trip is not empty
			int currentTripId = -1;
			Trip trip = new Trip.Builder("").build();
			while(c.moveToNext()){
				if(c.getInt(0/*"TRIP.id"*/) != currentTripId) { //make new trip
					if(DEBUG) System.out.println("Got trip: "+c.getString(1/*"TRIP.title"*/));
					if(currentTripId != -1)	{ //next trip on the list
						trips.add(trip);
					}
					trip = new Trip.Builder(c.getString(1/*"TRIP.title"*/))
					.idPrivate(c.getInt(0/*"TRIP.id"*/))
					.description(c.getString(2/*"TRIP.description"*/))
					.freeTrip(1==c.getInt(14))
					.idGlobal(c.getInt(18))
					.build();
					currentTripId = trip.getIdPrivate();
				}
				Poi poi = new Poi.Builder(c.getString(4/*"POI.title"*/),new PoiAddress.Builder
						(c.getString(8/*"ADDR.city"*/))
// ZIP code removed
//				.zipCode(c.getInt(7/*"ADDR.zipcode"*/))
				.street(c.getString(6/*"ADDR.street_name"*/))
				.longitude(c.getDouble(10/*"ADDR.lon"*/)).latitude(c.getDouble(9/*"ADDR.lat"*/))
				.build()
				).description(c.getString(5/*"POI.description"*/))
				.category(c.getString(11/*"CAT.title"*/))
				.favourite((1==c.getInt(12/*"POI.favorite"*/)))
				.imageURL(c.getString(15))
				.telephone(Integer.toString(c.getInt(20)))
				.idPrivate(c.getInt(3))
				.idGlobal(c.getInt(19))
				.build();
				trip.addPoi(poi);
				trip.setTime(poi, new Time(c.getInt(16), c.getInt(17)));
			}//while more trips
			if(currentTripId != -1)//next trip on the list
			{
				trips.add(trip);
			}
		}//if empty trip - else trip with pois
		c.close();
		if(trips.size() == 0) return null;
		return trips.get(0);
	}//getTrip

	@Override
	public boolean open(){
		try{
			myDataBase = openDataBase();
		}catch (SQLException e){
			Log.d("CityExplorer", "SQLiteConnector~500: FAILED Opening SQLite connector to "+myPath);
			myDataBase=null;
		}
		long poiCount = 0;
		try{
			poiCount = DatabaseUtils.queryNumEntries(myDataBase, POI_TABLE);
		}catch (SQLiteException e){ //No such table: poi (if just create blank DB)
		}
		Log.d("CityExplorer", "poi-count is "+poiCount );
		//JF: ZIP code removed
		if ( poiCount ==0 ){ //No existing POIs, close DB, copy default DB-file, and reopen
			Log.d("CityExplorer", "close myDataBase, before re-open");
			myDataBase.close();
			try{
				Log.d("CityExplorer",DB_NAME+" was missing... now copying");
				createDataBase();
			}catch (IOException e){
				e.printStackTrace();
				return false;
			}
		}
		return (myDataBase == null) ? false : true;
	}//open

	@Override
	public ArrayList<String> getCategoryNames() {
		ArrayList<String> categories = new ArrayList<String>();
		Cursor c = myDataBase.query("category", new String[]{"title"}, null, null, null, null, null);
		while (c.moveToNext()) {
			categories.add(c.getString(0));
		}
		c.close();
		return categories;
	}//getCategoryNames

	@Override
	public ArrayList<String> getUniqueCategoryNames() {

		ArrayList<String> categories = new ArrayList<String>();
		Cursor c = myDataBase.rawQuery("SELECT DISTINCT category.title from category, poi WHERE poi.category_id = category._id ORDER BY category.title", null);
		while (c.moveToNext()) {
			categories.add(c.getString(0));
		}
		c.close();
		return categories;
	}//getUniqueCategoryNames

	@Override
	public HashMap<String, Bitmap> getUniqueCategoryNamesAndIcons() {
		HashMap<String, Bitmap> categories = new HashMap<String, Bitmap>();
		Cursor c = myDataBase.rawQuery("SELECT DISTINCT category.title, category.icon FROM category, poi WHERE poi.category_id = category._id ORDER BY category.title", null);
		byte[] imgData;
		Bitmap defaultBmp = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.new_location);
		while (c.moveToNext()) {
			imgData = c.getBlob(1);
			Bitmap bmp = defaultBmp;
			if (imgData.length>20){
				Log.d("CityExplorer", "imgData.length is "+imgData.length);
				bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
			}else{
				bmp = defaultBmp;
			}
			categories.put(c.getString(0), bmp);
		}//while more categories
		c.close();
		return categories;
	}//getUniqueCategoryNamesAndIcons

	@Override
	public int getCategoryId(String title) {
		Cursor c = myDataBase.query("category", new String[]{"_id"}, "title = ?", new String[]{title}, null, null, null);
		while (c.moveToNext()) {
			int catID = c.getInt(0);
			c.close();
			return catID;
		}
		c.close();
		return -1;
	}//getCategoryId

	@Override
	public void deleteFromTrip(Trip trip, Poi poi){
		myDataBase.delete("trip_poi", "poi_id = ? AND trip_id = ?", new String[]{""+poi.getIdPrivate(), ""+trip.getIdPrivate()});
		Toast.makeText(myContext, poi.getLabel() + " deleted from tour.", Toast.LENGTH_LONG).show();
	}

	@Override
	public void deleteTrip(Trip trip){
		myDataBase.delete("trip_poi", "trip_id = ?", new String[]{""+trip.getIdPrivate()});
		myDataBase.delete("trip", "_id = ?", new String[]{""+trip.getIdPrivate()});
		Toast.makeText(myContext, trip.getLabel() + " deleted.", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean deletePoi(Poi poi){
		//Check if the poi is in a trip first.
		Cursor c = myDataBase.query("trip_poi", new String[]{"trip_id"}, "poi_id = ?", new String[]{""+poi.getIdPrivate()}, null, null, null);

		if(c.getCount() > 0)
		{
			//the poi is in a trip. abort!
			StringBuilder trips = new StringBuilder();
			while(c.moveToNext()) //make a list of all the trips this poi is in.
			{
				if(trips.length() != 0)
					trips.append(", ");
				trips.append(this.getTrip(c.getInt(0)).getLabel());
			}

			Toast.makeText(myContext, poi.getLabel() + " not deleted, because it is in "+c.getCount()+" tour"+((c.getCount() > 1) ? "s":"")+" ("+trips.toString()+")", Toast.LENGTH_LONG).show();
			c.close();
			return false;
		}

		if(myDataBase.delete("poi", "_id = ?", new String[]{""+poi.getIdPrivate()}) > 0)
		{
			//poi deleted
			Toast.makeText(myContext, poi.getLabel() + " deleted", Toast.LENGTH_LONG).show();
			return true;
		}
		else
		{
			//the poi is not found
			Toast.makeText(myContext, poi.getLabel() + " not deleted, because it is not found", Toast.LENGTH_LONG).show();
			return false;
		}
	}//deletePoi

	@Override
	public int newPoi(Poi poi) {
		//Category:
		int categoryId = -1;
		if(getCategoryNames().contains(poi.getCategory()))//category exists
			categoryId = getCategoryId(poi.getCategory());
		else //does not exist, create it:
		{
			ContentValues values1 = new ContentValues();
			values1.put("title", poi.getCategory());
			myDataBase.insertOrThrow("category", null, values1);

			categoryId = getCategoryId(poi.getCategory());
		}

		int AddressId = -1;
		//does the address exist?
		Cursor c = myDataBase.query("address", new String[]{"_id"},
				"street_name = ? AND " +
// ZIP code removed
//				"zipcode = ? AND " +
				"city = ? AND " +
				"lat = ? AND " +
				"lon = ?",
// ZIP code removed
				new String[]{poi.getAddress().getStreet()/*,""+poi.getAddress().getZipCode()*/,poi.getAddress().getCity(),""+poi.getAddress().getLatitude(),""+poi.getAddress().getLongitude()},
				null, null, null);
		if(c.moveToFirst())
			AddressId = c.getInt(0);
		else
		{//add the address
			ContentValues values2 = new ContentValues();
			values2.put("street_name", poi.getAddress().getStreet());
// ZIP code removed
//			values2.put("zipcode", poi.getAddress().getZipCode());
			values2.put("city", poi.getAddress().getCity());
			values2.put("lat", poi.getAddress().getLatitude());
			values2.put("lon", poi.getAddress().getLongitude());
			myDataBase.insertOrThrow("address", null, values2);

			//get the id
			c = myDataBase.query("address", new String[]{"_id"},
					"street_name = ? AND " +
// ZIP code removed
//					"zipcode = ? AND " +
					"city = ? AND " +
					"lat = ? AND " +
					"lon = ?",
// ZIP code removed
					new String[]{poi.getAddress().getStreet()/*,""+poi.getAddress().getZipCode()*/,poi.getAddress().getCity(),""+poi.getAddress().getLatitude(),""+poi.getAddress().getLongitude()},
					null, null, null);
			if(c.moveToFirst())
				AddressId = c.getInt(0);
		}

		if(AddressId == -1)
			System.out.println("Error getting the address_id");

		//POI:
		ContentValues values3= new ContentValues();
		if(poi.getIdGlobal() != -1) values3.put("global_id", poi.getIdGlobal());
		values3.put("title", poi.getLabel());
		values3.put("description", poi.getDescription());
		values3.put("category_id", categoryId);
		values3.put("favorite", poi.isFavourite());
		values3.put("address_id", AddressId);
		values3.put("web_page", poi.getWebPage());
		values3.put("openingHours", poi.getOpeningHours());
		values3.put("telephone",poi.getTelephone());
		values3.put("image_url", poi.getImageURL());

		try {
			myDataBase.insertOrThrow("poi", null, values3);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}

		c.close();
		return 1;
	}//newPoi

	@Override
	public void newTrip(Trip t) {
		//TRIP:
		ContentValues values = new ContentValues();
		if(t.getIdGlobal() != -1) values.put("global_id", t.getIdGlobal());
		values.put("title", t.getLabel());
		values.put("description", t.getDescription());
		values.put("free_trip", t.isFreeTrip());

		try {
			myDataBase.insertOrThrow("trip", null, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the poi's private id from global id.
	 *
	 * @param global_id The global id
	 * @return The private id for the poi with the given global id.
	 * Returns -1 if the poi is not in the database.
	 */
	@Override
	public int getPoiPrivateIdFromGlobalId(int global_id)
	{
		Cursor c = myDataBase.query("poi", new String[]{"_id"},
				"global_id = ?", new String[]{""+global_id}, null, null, null);

		if(DEBUG) System.out.println("ids found: "+c.getCount());
		while (c.moveToNext()) {
			int private_id = c.getInt(0);

			c.close();
			return private_id;
		}
		c.close();
		return -1;
	}//getPoiPrivateIdFromGlobalId

	/**
	 * Get the trip's private id from global id.
	 *
	 * @param global_id The global id
	 * @return The private id for the trip with the given global id.
	 * Returns -1 if the trip is not in the database.
	 */
	@Override
	public int getTripPrivateIdFromGlobalId(int global_id){
		Cursor c = myDataBase.query("trip", new String[]{"_id"},
				"global_id = ?", new String[]{""+global_id}, null, null, null);

		if(DEBUG) System.out.println("ids found: "+c.getCount());
		while (c.moveToNext()) {
			int private_id = c.getInt(0);

			c.close();
			return private_id;
		}
		c.close();
		return -1;
	}//getTripPrivateIdFromGlobalId

	/**
	 * Method for editing an already existing PoI.
	 *
	 * @param poi The modified poi, that is going to be saved in the database
	 */
	@Override
	public void editPoi(Poi poi){
		ContentValues poiValues = new ContentValues();

		int addressId = -1;
		Cursor c = myDataBase.query("address", new String[]{"_id"},
				"street_name = ? AND " +
// ZIP code removed
//				"zipcode = ? AND " +
				"city = ? AND " +
				"lat = ? AND " +
				"lon = ?",
// ZIP code removed
				new String[]{poi.getAddress().getStreet()/*,""+poi.getAddress().getZipCode()*/,poi.getAddress().getCity(),""+poi.getAddress().getLatitude(),""+poi.getAddress().getLongitude()},
				null, null, null);
		if(c.moveToFirst()){
			addressId = c.getInt(0);
		}
		else{//add the address
			ContentValues addrValues = new ContentValues();
			addrValues.put("street_name", poi.getAddress().getStreet());
// ZIP code removed
//			addrValues.put("zipcode", poi.getAddress().getZipCode());
			addrValues.put("city", poi.getAddress().getCity());
			addrValues.put("lat", poi.getAddress().getLatitude());
			addrValues.put("lon", poi.getAddress().getLongitude());
			myDataBase.insertOrThrow("address", null, addrValues);

			//get the id
			c = myDataBase.query("address", new String[]{"_id"},
					"street_name = ? AND " +
// ZIP code removed
//					"zipcode = ? AND " +
					"city = ? AND " +
					"lat = ? AND " +
					"lon = ?",
// ZIP code removed
					new String[]{poi.getAddress().getStreet()/*,""+poi.getAddress().getZipCode()*/,poi.getAddress().getCity(),""+poi.getAddress().getLatitude(),""+poi.getAddress().getLongitude()},
					null, null, null);
			if(c.moveToFirst()){
				addressId = c.getInt(0);
			}
		}

		//Category
		int categoryId = -1;
		if(getCategoryNames().contains(poi.getCategory()))//category exists
			categoryId = getCategoryId(poi.getCategory());
		else //does not exist, create it:
		{
			ContentValues catValues = new ContentValues();
			catValues.put("title", poi.getCategory());
			myDataBase.insertOrThrow("category", null, catValues);

			categoryId = getCategoryId(poi.getCategory());
		}

		if(categoryId == -1){
			System.out.println("Error getting the category_id");
		}

		int poiID = poi.getIdPrivate();
		if(poi.getIdGlobal() != -1) poiValues.put("global_id", poi.getIdGlobal());
		poiValues.put("favorite", poi.isFavourite());
		poiValues.put("description", poi.getDescription());
		poiValues.put("title", poi.getLabel());
		poiValues.put("telephone", poi.getTelephone());
		poiValues.put("openingHours", poi.getOpeningHours());
		poiValues.put("web_page", poi.getWebPage());
		poiValues.put("image_url", poi.getImageURL());

		myDataBase.update("poi", poiValues, "_id = ?", new String[]{""+poiID} );
		//		myDataBase.replaceOrThrow("poi", null, dbValues);

		poiValues.put("address_id", addressId);
		poiValues.put("category_id", categoryId);

		c.close();
	}//editPoi

	@Override
	public boolean addPoiToTrip(Trip t, Poi poi) {
		Trip trip = t;

		ContentValues values = new ContentValues();
		values.put("trip_id", trip.getIdPrivate());
		values.put("poi_id", poi.getIdPrivate());
		values.put("poi_number", trip.getPois().indexOf(poi)+1);

		try	{
			myDataBase.insertOrThrow("trip_poi", null, values);
			System.out.println("POI added to DB: "+
					"trip_id("+trip.getIdPrivate()+") poi_id("+poi.getIdPrivate()+") poi_number("+(trip.getPois().indexOf(poi)+1)+")");
			return true;
		} catch (Exception e) {
			System.out.println("ERROR in SQL: "+e.getMessage()+" SQL values: "+
					"trip_id("+trip.getIdPrivate()+") poi_id("+poi.getIdPrivate()+") poi_number("+(trip.getPois().indexOf(poi)+1)+")");
			e.printStackTrace();
			return false;
		}
	}//addPoiToTrip

	@Override
	public void addTimesToTrip(Trip trip)
	{
		ContentValues values = new ContentValues();
		values.put("trip_id", trip.getIdPrivate());

		for (Poi poi : trip.getFixedTimes().keySet())
		{
			values.put("poi_id", poi.getIdPrivate());
			values.put("poi_number", trip.getPois().indexOf(poi)+1);
			values.put("hour", trip.getFixedTimes().get(poi).hour);
			values.put("minute", trip.getFixedTimes().get(poi).minute);
			myDataBase.update("trip_poi", values, "trip_id = ? and poi_id = ?", new String[]{Integer.toString(trip.getIdPrivate()), Integer.toString(poi.getIdPrivate())});
		}

	}

	@Override
	public void onCreate(SQLiteDatabase db) { }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

	@Override
	public boolean isOpen(){
		if (myDataBase == null) {
			return false;
		}
		return myDataBase.isOpen();
	}//isOpen

	@Override
	public synchronized void close() {
		if (myDataBase != null){
			myDataBase.close();
		}
		super.close();
	}//close

	/**
	 * Opens the database.
	 *
	 * @throws SQLException if the database cannot be opened.
	 * @return The SQLite database that has been opened.
	 */
	public SQLiteDatabase openDataBase() throws SQLException {
		// (Re-) create DB folder in case it has been removed by the system, or for first time runs
		File catalog = new File (DB_PATH);
		if ( !catalog.isDirectory() ){
			Log.d("CityExplorer","Making Catalog is "+catalog);
			catalog.mkdir();
			Log.d("CityExplorer","made folder for: "+myPath);
		}//if folder missing

		Log.d("CityExplorer", "opening db: "+myPath);
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE+SQLiteDatabase.CREATE_IF_NECESSARY);

		return myDataBase;
	}//openDataBase

}//class
