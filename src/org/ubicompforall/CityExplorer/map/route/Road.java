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


