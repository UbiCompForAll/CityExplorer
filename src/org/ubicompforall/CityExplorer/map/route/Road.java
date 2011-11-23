/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune S¾tre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:
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
 * This class keeps hold of the tabs used in planning mode.
 * 
 */

package org.ubicompforall.CityExplorer.map.route;

import java.util.ArrayList;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class Road {
	String name;
	String description;
	int color;
	int width;
	double[][] route;
	Point[] points;
	ArrayList<GeoPoint> geoPoints; 	

	Road(){
		route = new double[][] {};
		points = new Point[] {};
	}

	void addPoint(){
		int 	idx 	= 0;
		Point[]	newP 	= new Point[points.length+1];

		for (Point p : points){
			newP[idx++] = p;
		}
		newP[points.length] = new Point();
		points = newP;
	}

	public ArrayList<GeoPoint> getGeoPoints(){
		if (geoPoints == null) {
			geoPoints = new ArrayList<GeoPoint>(route.length);
		
			for (int i = 0; i < route.length; i++) {
				geoPoints.add(new 
					GeoPoint(
						(int)(route[i][1] * 1e6),
						(int)(route[i][0] * 1e6))
					);
			}
		}
		return geoPoints;
	}
	public double[][] 	getRoute(){return route;}
	public Point[]		getPoints(){return points;}
	
	public double getDistance()
	{
		float[] dist = new float[3];
		GeoPoint prevGeop = null;
		double totalDistance = 0;
		
		for (GeoPoint geop : getGeoPoints()) 
		{
			if(prevGeop == null)//first geop
			{
				prevGeop = geop;
				continue;
			}
			Location.distanceBetween(
					geop.getLatitudeE6()/1E6,
					geop.getLongitudeE6()/1E6,
					prevGeop.getLatitudeE6()/1E6, 
					prevGeop.getLongitudeE6()/1E6, dist);
			totalDistance += dist[0];
			prevGeop = geop;
		}
		
		return totalDistance;
	}
}


