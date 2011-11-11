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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * The Class ExportImport.
 * @author Christian Skjetne
 */
public class Sharing extends Activity
{

	/** The context. */
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Uri uri = getIntent().getData();
		
		int[] res = new int[]{0,0};
		
		// try opening the file
		  try {
		    // open the file for reading
			File f = new File(uri.getPath());
		    InputStream instream = new FileInputStream(f);
			//InputStream instream = openFileInput("cityexplorer.txt");
		    
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
	 */
	public static void send(Context c, ArrayList<Poi> pois)
	{
		OutputStreamWriter osw;
		try
		{
			FileOutputStream fOut = c.openFileOutput("cityexplorer.txt",
					MODE_WORLD_READABLE);
			osw = new OutputStreamWriter(fOut); 
			// Write the string to the file
			for (Poi poi : pois)
			{
				/*
				 * 0  global_id;
				 * 1  title;
				 * 2  description;
				 * 3  street_name;
				 * 4  zipcode;
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
				osw.write(
						poi.getIdGlobal()	+";"+
						poi.getLabel()		+";"+
						poi.getDescription().replaceAll("\n", "%EOL")+";"+
						poi.getAddress().getStreet()+";"+
						poi.getAddress().getZipCode()+";"+
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
		catch (IOException e)
		{
			System.out.println("IO error: "+e.getMessage());
		}
		
		File F = c.getFileStreamPath("cityexplorer.txt");
        Uri U = Uri.fromFile(F);

		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		//sharingIntent.setData(U);
		sharingIntent.putExtra(Intent.EXTRA_STREAM, U);
		//sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This is a test");
		c.startActivity(Intent.createChooser(sharingIntent,"Share ce file using"));
	}
	
	public static int countOccurrences(String haystack, char needle)
	{
	    int count = 0;
	    for (int i=0; i < haystack.length(); i++)
	    {
	        if (haystack.charAt(i) == needle)
	        {
	             count++;
	        }
	    }
	    return count;
	}
}