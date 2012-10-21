/**
 * @contributor(s): Rune Sætre (NTNU), Jacqueline Floch (SINTEF)
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

package org.ubicompforall.cityexplorer.buildingblock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.simplelanguage.runtime.AbstractStepInstance;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.content.Context;

public class BusTimeStep extends AbstractStepInstance implements AndroidBuildingBlockInstance {
	Context context;
	//private BusStopFinder bf;

	@Override
	public void setContext(Context context) {
		debug(2, "From here to there" );
		this.context = context;
	}//setContext
	
	private static void debug(int level, String message){
		CityExplorer.debug( level, message );
	}//delegate debug

	@Override
	public void execute() {
		debug( 2, "From here to there" );
		// Get parameters for the building block
		String fromPoiName = getStringPropertyValue ("fromPoiName");
		String toPoiName = getStringPropertyValue ("toPoiName");
		String afterTime = getStringPropertyValue ("afterTime");
		String beforeTime = getStringPropertyValue ("beforeTime");
		debug(2, "From "+fromPoiName+" to "+toPoiName+". afterTime is "+afterTime+", and beforeTime is "+beforeTime+"." );

		// TODO: Replace by domain object
//		DomainObjectReference poiRef = this.getDomainObjectReference("poiName");	
//		debug(0, "Show notification "+ poiRef.getDisplayText() );

		debug(0, "busTime is "+getBusRoute(fromPoiName, toPoiName, afterTime, beforeTime) );
		setPropertyValue("busTime", getBusRoute(fromPoiName, toPoiName, afterTime, beforeTime) );
	}//execute

	// Information Methods
	private String getBusStopForAdr( String... adr ) {
		String address="";
		if ( adr != null && adr[0] != null ){
			address = adr[0].replaceAll( "-\\d+", "" );
			debug(2, "Address: "+address );
			//If address does not contain street number, and there is an alternative address, then use it instead!
			if ( ( ! address.matches( ".*\\d+.*") ) && adr.length>1 && adr[1] != null ){ // && address.equalsIgnoreCase( adr[0] )
				address = adr[1].replaceAll( "-\\d+", "" );
				debug(1, "Address: "+address );
			}
		}
		return address;
	}// getBusStopForAdr

	public String getBusRouteURL( String fromPlace, String toPlace, String afterTime, String beforeTime ){
		String httpGet = "http://busstjener.idi.ntnu.no/busstuc/oracle?q=";
		String after = "", before = "";
		if (! afterTime.equals("") ){
			after = " etter "+afterTime;
		}
		if (! beforeTime.equals("") ){
			before = " før "+beforeTime;
		}
		try {
			httpGet += URLEncoder.encode(" fra "+getBusStopForAdr(fromPlace)+after+" til "+getBusStopForAdr(toPlace)+before, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpGet;
	}//getBusRouteURL

	public String getBusRoute( String fromPlace, String toPlace, String afterTime, String beforeTime ){
		String text="";
		String httpGet = getBusRouteURL(fromPlace, toPlace, afterTime, beforeTime);
		if ( ! CityExplorer.ensureConnected( context ) ){ //For downloading Address and buses
			debug(-1, "Go offline!" );
			//text = bf.getBusstops( 3, lp.getLatitude(), lp.getLongitude() );
			//Toast.makeText(context, "Please activate Data-Connection to get BusTimes!", Toast.LENGTH_LONG ).show();
			text = "Please activate Data-Connection to get BusTimes!";
		}else{
			text = connect( httpGet );
		}
		return text;
	}//getBusRoute


	public static String connect(String url) {
		String result = "";
		
		HttpClient httpclient = new DefaultHttpClient();
		// Prepare a request object
		HttpGet httpget = new HttpGet(url);
		// Execute the request
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			// Examine the response status
			debug(2, response.getStatusLine().toString() );

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			// to worry about connection release

			if (entity != null) {
				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				// now you have the string representation of the HTML request
				instream.close();
			}
		} catch (ClientProtocolException e) {
			debug(-1, e.getCause()+" "+e.getMessage() );
			e.printStackTrace();
		} catch (IOException e) {
			debug(-1, e.getCause()+" "+e.getMessage() );
		}
		return result;
	}//connect
	
	private static String convertStreamToString(InputStream is) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}//convertStreamToString

	/*** Helper Class
	 * @author satre
	 */
	class BusstopFinder {
		//private Context context;
		private TreeMap<String, double[]> pois;
		TreeMap<Integer, String> distances;

		public BusstopFinder( Context ctx ){
			pois = new TreeMap<String, double[]>();
			distances = new TreeMap<Integer, String>();
			BufferedReader br;
			try {
				br = new BufferedReader( new InputStreamReader( ctx.getAssets().open ( "latLngName.txt" ), "UTF-8" ) );
				String text="";
				while ( (text = br.readLine()) != null){
					String[] latLngName = text.split("\t");
					pois.put( latLngName[2], new double[]{ Double.parseDouble(latLngName[0]), Double.parseDouble(latLngName[1]) } );
				}
				debug(-1, "Stopped after "+pois.size()+" busstops?" );
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}//CONSTRUCTOR

//		public String getBusstops( int limit, double latitude, double longitude) {
//			String name="Offline";
//			name = getClosest( new double[]{latitude,longitude}, limit, pois, distances);
//			return name;
//		}
		
	}//Helper class BusstopFinder

}//class PoiNotification
