/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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

package org.ubicompforall.CityExplorer.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.ubicompforall.CityExplorer.CityExplorer;
import org.ubicompforall.CityExplorer.R;
import org.ubicompforall.CityExplorer.gui.PlanActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @description: Class to handle import a database on reception of the VIEW FILE intent.
 * At the moment the file file is copied from ExportImport (or Sharing?) that is text based.
 * 
 * Suggested Functionality: copy to application folder and open it. NOT IMPLEMENTED LIKE THAT YET?
 */
public class ImportDB extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Uri uri = getIntent().getData();
		debug(0, "ImportDB, file intent uri is "+uri );
// JF: I commented this toast because it is confusing for the user to get the message about intent
//		Toast.makeText( this, "ImportDB, file intent uri is "+uri, Toast.LENGTH_LONG).show();
		
		// try opening the file
		if ( uri != null ){
			// open the file for reading
			File file = new File( uri.getPath() );
			String DEFAULT_DBFOLDER = getResources().getText( R.string.default_dbFolderName ).toString();

			InputStream in = null;
			OutputStream out = null;
			try {
				in = new BufferedInputStream( new FileInputStream(file) );
				try{
					out = new FileOutputStream( getDatabasePath( DEFAULT_DBFOLDER )+"/"+file.getName() );
				}catch( FileNotFoundException e ){
					File newOutFile = new File( getDatabasePath( DEFAULT_DBFOLDER + "/"+file.getName() ).getAbsolutePath() );
					newOutFile.mkdirs();
					out = new FileOutputStream( newOutFile );
				}
				debug(0, "Copying from input to " + out ); //.getDatabasePath( CityExplorer.DEFAULT_CITY ) +"/"+ file.getName() );
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;

				//Close this activity, and start the next one: View imported pois
				finish();
				//startActivity(new Intent( this, ImportActivity.class) );
				DBFactory.changeInstance( this, file );
				startActivity(new Intent( this, PlanActivity.class) );
			} catch(Exception e) {
			    debug(0, e.getMessage() );
			    e.printStackTrace();
			}
			
		}else{//if URI given in intent, else uri == null
			Toast.makeText( this, "ImportDB, file intent uri.getPath==null in uri "+uri, Toast.LENGTH_LONG).show();
			debug(0, "file intent uri.getPath==null in uri "+uri );
		}
	}// onCreate
	
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}//copyFile

	
	private void debug(int i, String string) {
		CityExplorer.debug(i, string);
	}// debug

	
	/**
	 * Share pois with another user.
	 *
	 * @param c The context.
	 * @param pois The pois you want to send.
	 */
	public static void send(Context c, ArrayList<Poi> pois)
	{
		OutputStreamWriter osw;
		try
		{
			FileOutputStream fOut = c.openFileOutput(CityExplorer.SHARED_FILE,
					MODE_WORLD_READABLE);
			osw = new OutputStreamWriter(fOut); 
			// Write the string to the file
			for (Poi poi : pois){
				/*
				 * 0  global_id;
				 * 1  title;
				 * 2  description;
				 * 3  street_name;
//				 * x4  zipcode; // ZIP code removed
				 * 4  city;
				 * 5  lat;
				 * 6  lon;
				 * 
				 * 7  category_title;
				 * 8 web_page;
				 * 9 openingHours;
				 * 10 telephone;
				 * 11 image_url
				*/
				osw.write(
						poi.getIdGlobal()	+";"+
						poi.getLabel()		+";"+
						poi.getDescription().replaceAll("\n", "%EOL")+";"+
						poi.getAddress().getStreet()+";"+
//						poi.getAddress().getZipCode()+";"+	// ZIP code removed
						poi.getAddress().getCity()+";"+
						poi.getAddress().getLatitude()+";"+
						poi.getAddress().getLongitude()+";"+
						poi.getCategory()+";"+
						poi.getWebPage()+";"+
						poi.getOpeningHours().replaceAll("\n", "%EOL")+";"+
						poi.getTelephone()+";"+
						poi.getImageURL()+"\n"
						);
			}
			
			/* ensure that everything is
			 * really written out and close */
			osw.flush();
			osw.close();
		} 
		catch (IOException e){
			System.out.println("IO error: "+e.getMessage());
		}
		
		File F = c.getFileStreamPath(CityExplorer.SHARED_FILE);
        Uri U = Uri.fromFile(F);

		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		//sharingIntent.setData(U);
		sharingIntent.putExtra(Intent.EXTRA_STREAM, U);
		//sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This is a test");
		c.startActivity(Intent.createChooser(sharingIntent,"Share ce file using"));
	}//send
	
	public static int countOccurrences(String haystack, char needle){
	    int count = 0;
	    for (int i=0; i < haystack.length(); i++){
	        if (haystack.charAt(i) == needle){
	             count++;
	        }
	    }
	    return count;
	}//countOccurrences

}//ExportImport extends Activity
