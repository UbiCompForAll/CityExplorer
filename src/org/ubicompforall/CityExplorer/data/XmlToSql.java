/**
 * @contributor(s): Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			22 November 2011
 * @revised:		15 December 2011
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class XmlToSql extends ListActivity{
    private SQLiteDatabase database;
    //static final String ID="id";
    static final String NAME="name";
    static final String SCORE="score";
    TextView error;

	/** Called when the activity is first created. */
	@SuppressWarnings({ "rawtypes", "unused" })
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.listplaceholder);
		
		//IT'S OPENNING DATABASE. IF DATABASE DOESN'T EXIST IT'S CREATING IT
		database = (new DatabaseHelper(this).getWritableDatabase());
		
		//BETTER CREATE YOU OWN CLASS WITH FIELDS AND ACCESSORS
		ArrayList mylist = new ArrayList();
	}
	
	@SuppressWarnings("unused")
	private void processAdd(String string, String string2, String string3) {
	    ContentValues values = new ContentValues(2);
//	    values.put(DatabaseHelper.NAME, string2);
//	    values.put(DatabaseHelper.SCORE, string3);
	    if(database!=null){
	        //INSERT IF DATABASE IS OPEN
//	        database.insert(DatabaseHelper.TABLE_NAME, null, values);
	    //  Log.i(TAG, "SAVED IN DATABASE");
	    }else{
	        //IF DATABASE IS CLOSED OPEN FIRST THAN INSERT
	        database = (new DatabaseHelper(this).getWritableDatabase());
	        //Log.e(TAG, "DATABASE CLOSED, OPENNING...");
//	        database.insert(DatabaseHelper.TABLE_NAME, null, values);
	        //Log.i(TAG, "INSERTED INTO DATABASE");
	
	    }
	
	
	}
	
	@Override
	protected void onDestroy() {
	    //ALWAYS CLOSE DATABASE IF YOU ARE FINISHING APPLICATION
	    if(database!=null){
	        database.close();
	    }
	    super.onDestroy();
	}

	/////////////////////////////////////////////////////////////////
	
    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = null;
		private static final int DATABASE_VERSION = 3;
		private static final String TAG = null;
		private static final String DB_PATH = null;
		Context helperContext;
		private SQLiteDatabase mDb;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            helperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database!!!!!");
            //db.execSQL("");
            onCreate(db);
        }

        @SuppressWarnings("unused")
		public void createDataBase() throws IOException {
            boolean dbExist = checkDataBase();
            if (dbExist) {
            } else {

                //make sure your database has this table already created in it
                //this does not actually work here
                /*
                 * db.execSQL("CREATE TABLE IF NOT EXISTS \"android_metadata\" (\"locale\" TEXT DEFAULT 'en_US')"
                 * );
                 * db.execSQL("INSERT INTO \"android_metadata\" VALUES ('en_US')"
                 * );
                 */
                this.getReadableDatabase();
                try {
                    copyDataBase();
                } catch (IOException e) {
                    throw new Error("Error copying database");
                }
            }
        }

        @SuppressWarnings("unused")
		public SQLiteDatabase getDatabase() {
            String myPath = DB_PATH + DATABASE_NAME;
            return SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        }

        private boolean checkDataBase() {
            SQLiteDatabase checkDB = null;
            try {
                String myPath = DB_PATH + DATABASE_NAME;
                checkDB = SQLiteDatabase.openDatabase(myPath, null,
                        SQLiteDatabase.OPEN_READONLY);
            } catch (SQLiteException e) {
            }
            if (checkDB != null) {
                checkDB.close();
            }
            return checkDB != null ? true : false;
        }

        private void copyDataBase() throws IOException {

            // Open your local db as the input stream
            InputStream myInput = helperContext.getAssets().open(DATABASE_NAME);

            // Path to the just created empty db
            String outFileName = DB_PATH + DATABASE_NAME;

            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

        @SuppressWarnings("unused")
		public void openDataBase() throws SQLException {
            // Open the database
            String myPath = DB_PATH + DATABASE_NAME;
            mDb = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }

        @Override
        public synchronized void close() {

            if (mDb != null)
                mDb.close();

            super.close();

        }
    } // class DatabaseHelper

} // class DbRemote
