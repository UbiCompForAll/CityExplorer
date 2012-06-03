/**
 * @contributor(s): Christian Skjetne (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:		15 Dec 2011
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.ubicompforall.CityExplorer.CityExplorer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @description: Class to handle import/export of POIs/Trips
 */
public class ExportImport extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Uri uri = getIntent().getData();
		
		Toast.makeText( this, "ExportImport.java, db-file from somewhere else. uri is "+uri, Toast.LENGTH_LONG).show();

		int[] res = new int[]{0,0};
		
		// try opening the file
		  try {
		    // open the file for reading
			File f = new File(uri.getPath());
		    InputStream instream = new FileInputStream(f);
			//InputStream instream = openFileInput(CityExplorer.SHARED_FILE);
		 
		    // if file the available for reading
		    if (instream != null) {
		      // prepare the file for reading
		      InputStreamReader inputreader = new InputStreamReader(instream);
		      BufferedReader buffreader = new BufferedReader(inputreader);
		      
		      StringBuilder pois = new StringBuilder();
		      String line;
		 
		      // read every line of the file into the line-variable, on line at the time
		      while (( line = buffreader.readLine()) != null) {
		        pois.append(line+"\n");
		      }
		      
		      //System.out.println("EXIMP: "+sb.toString());
		      
		      DatabaseUpdater du = new DatabaseUpdater(this);
		      res = du.doFileUpdatePois(pois.toString());
		    } // if instream
		 
		    // close the file again
		    instream.close();
		  } catch (java.io.FileNotFoundException e) {
		    // do something if the myfilename.txt does not exits
			  System.out.println("FileNotFound error: "+e.getMessage());
		  }
		  catch (IOException e) {
			  System.out.println("IO error: "+e.getMessage());
		  }
		  System.out.println(res[0]+" locations added, "+res[1]+" locations updated");
		  Toast.makeText(this, res[0]+" locations added, "+res[1]+" locations updated", Toast.LENGTH_LONG).show();
		  finish();
	}// onCreate
	
	/**
	 * Share pois with another user.
	 *
	 * @param c The context.
	 * @param pois The pois you want to send.
				 * 0  global_id;
				 * 1  title;
				 * 2  description;
				 * 3  street_name;
// ZIP code removed
//				 * x4  zipcode;
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
	public static void send(Context c, ArrayList<Poi> pois){
		OutputStreamWriter osw;
		try{
			//FileOutputStream fOut = c.openFileOutput(CityExplorer.SHARED_FILE, MODE_WORLD_READABLE);
			FileOutputStream fOut = new FileOutputStream( CityExplorer.getSharedFileName(c) );
			osw = new OutputStreamWriter(fOut);
			// Write the string to the file
			for (Poi poi : pois){
				osw.write(
					poi.getIdGlobal()	+";"+
					poi.getLabel()		+";"+
					poi.getDescription().replaceAll("\n", "%EOL")+";"+
					poi.getAddress().getStreet()+";"+
// ZIP code removed
//					poi.getAddress().getZipCode()+";"+
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
		
		File F = c.getFileStreamPath( CityExplorer.getSharedFileName(c) );
        Uri U = Uri.fromFile(F);

		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, U);
		CityExplorer.debug(0, "Sending file "+U.getPath() );
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
