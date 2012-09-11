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

import java.io.File;
import java.net.URL;

import org.ubicompforall.cityexplorer.CityExplorer;

/***
 * Contains the file-connection to a db-file
 * (Or a db-folder, if filename is empty)
 * @author satre
 */
public class DB {
	private final String fullname, category, filename; //, dbName;
	private final URL url;
	
	public DB( String dbPath, String category, String filename, URL url ){
		this.category = category;
		this.filename = filename;
		//this.dbName = dbName;
		this.url = url;

		if ( filename == "" ){
			fullname = dbPath+"/"+category;
		}else{
			fullname = dbPath+"/"+category+"/"+filename;
		}
	}//CONSTRUCTOR
	
	public boolean delete(){
		CityExplorer.debug(0, "Deleting file: "+ fullname );
		File db = new File( fullname );
		try{
			return db.delete();			
		}catch (Error e){
			CityExplorer.debug(-1, "Could not delete "+db );
			e.printStackTrace();
		}//try-catch
		return false;
	}//delete

	public String getFullname() {
		return fullname;
	}

	public String getCategory() {
		return category;
	} // getCategory

	public String getLabel() {
		return filename;
	} // getLabel

	public CharSequence getDescription() {
		return category+" DataBase";
	} //getDescription


//	public void setUrl(String url) {
//		this.dbPath = url;
//	}//setUrl
	
	public String toString(){
		if ( CityExplorer.DEBUG < 2 ){
//			return fullname;
//		}else{
//			return dbName;
		}
		return fullname;
	}//toString

	public URL getURL() {
		return url;
	}

} // class DB
