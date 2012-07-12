/**
 * @contributor(s): Christian Skjetne (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.ubicompforall.cityexplorer.CityExplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class Sharing extends Activity
{

	/** The context. */
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Uri uri = getIntent().getData();
		
		int[] res = new int[]{0,0};
		
		// try opening the file
		  try {
		    // open the file for reading
			File f = new File(uri.getPath());
		    InputStream instream = new FileInputStream(f);
			//InputStream instream = openFileInput(CityExplorer.SHARED_FILE);
		    
		    handleInputStream(instream);
		   
		  } 
		  catch (java.io.FileNotFoundException e) {
			  // do something if the myfilename.txt does not exits
			  System.out.println("FNF error: "+e.getMessage());
			  try {
				  if (getIntent().getScheme().equals("content")) {
                      InputStream is = getContentResolver().openInputStream(getIntent().getData());
                      res = handleInputStream(is);
				  }
			  } catch (Exception e2) {
				  System.out.println("error: "+e2.getMessage());
			  }
		  }
		  catch (IOException e) {
			  System.out.println("IO error: "+e.getMessage());
		  }
		  System.out.println(res[0]+" locations added, "+res[1]+" locations updated");
		  Toast.makeText(this, res[0]+" locations added, "+res[1]+" locations updated", Toast.LENGTH_LONG).show();
		  finish();
	}
	
	private int[] handleInputStream(InputStream instream) throws java.io.FileNotFoundException, IOException
	{
		int[] res = new int[]{0,0};
		
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
	 
	    }
	 
	    // close the file again
	    instream.close();
	    
	    return res;
	}
	
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
//				 * 4  zipcode;
				 * 5  city;
				 * 6  lat;
				 * 7  lon;
				 * 
				 * 8  category_title;
				 * 9 web_page;
				 * 10 openingHours;
				 * 11 telephone;
				 * 12 image_url
	 */
	public static void send(Context c, ArrayList<Poi> pois){
		OutputStreamWriter osw;
		try{
			FileOutputStream fOut = new FileOutputStream( CityExplorer.getSharedFileName() );
			CityExplorer.debug(0, "Storing pois to "+fOut );
			osw = new OutputStreamWriter(fOut); 
			// Write the string to the file
			for (Poi poi : pois){
				String outputStr = 
					poi.getIdGlobal()	+";"+
					poi.getLabel()		+";"+
					poi.getDescription().replaceAll("\n", "%EOL")+";"+
					poi.getAddress().getStreet()+";"+
					poi.getAddress().getCity()+";"+
					poi.getAddress().getLatitude()+";"+
					poi.getAddress().getLongitude()+";"+
					poi.getCategory()+";"+
					poi.getWebPage()+";"+
					poi.getOpeningHours().replaceAll("\n", "%EOL")+";"+
					poi.getTelephone()+";"+
					poi.getImageURL()+"\n";
				CityExplorer.debug(0, "outputstring is "+ outputStr );
				osw.write( outputStr );
				//	poi.getAddress().getZipCode()+";"+	// ZIP code removed
				
			}//for each poi
			
			/* ensure that everything is
			 * really written out and close */
			osw.flush();
			osw.close();
		}catch (IOException e){
			System.out.println("IO error: "+e.getMessage());
		}
		
		File F = c.getFileStreamPath (CityExplorer.SHARED_FILE);
        Uri U = Uri.fromFile(F);
        //U = Uri.parse(CityExplorer.SHARED_FILE_PATH + U.getPath());
        //U = Uri.parse("file://" + U.getPath());

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, U);
		sharingIntent.setFlags (Intent.FLAG_GRANT_READ_URI_PERMISSION);
		// sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This is a test");
		c.startActivity(Intent.createChooser(sharingIntent,"Share ce file using"));
	}
	
	public static int countOccurrences(String haystack, char needle){
	    int count = 0;
	    for (int i=0; i < haystack.length(); i++){
	        if (haystack.charAt(i) == needle){
	             count++;
	        }
	    }
	    return count;
	}
}//class Sharing
