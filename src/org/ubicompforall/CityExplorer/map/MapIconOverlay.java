/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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
 * This class shows a single trip in a list.
 * 
 */

package org.ubicompforall.CityExplorer.map;

import org.ubicompforall.CityExplorer.data.Poi;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

/**
 * The Class MapIconOverlay.
 */
class MapIconOverlay extends com.google.android.maps.Overlay{
	
	/** The poi. */
	Poi poi = null;
	
	/** The p. */
	GeoPoint p;
	
	/** The bmp. */
	Bitmap bmp;
	
	/** The screen pts. */
	Point screenPts = new Point();
	
	/** The click point. */
	Point clickPoint = new Point();
	
	/** The click tolerance. */
	int clickTolerance = 10;
	
	/** The context. */
	MapsActivity context;
	
	/** The mv. */
	MapView mv;
	
	/**
	 * Instantiates a new map icon overlay.
	 *
	 * @param c the c
	 * @param Icon the icon
	 * @param position the position
	 */
	public MapIconOverlay(Context c, int Icon, GeoPoint position) 
	{
		this.p = position;
		bmp = BitmapFactory.decodeResource(c.getResources(), Icon);
		context = (MapsActivity) c;
	}
	
	/**
	 * Sets the poi.
	 *
	 * @param poi the new poi
	 */
	public void setPoi(Poi poi)
	{
		this.poi = poi;
	}
	
	/**
	 * Update pos.
	 *
	 * @param p the p
	 */
	public void updatePos(GeoPoint p) {
		this.p = p;
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean, long)
	 */
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when){
		if(mv == null)
			mv = mapView;
		
	    super.draw(canvas, mapView, shadow);                   
	
	    //---translate the GeoPoint to screen pixels---
	    mapView.getProjection().toPixels(p, screenPts);
	
	    //---add the marker---
	    if (bmp != null){
	    	canvas.drawBitmap(bmp, screenPts.x-bmp.getWidth()/2, screenPts.y-bmp.getHeight()/*/2*/, null);
	    }else{
			//Log.d("CityExplorer", "No bmp...");
	    }
	    return true;
	}//draw
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.Overlay#onTap(com.google.android.maps.GeoPoint, com.google.android.maps.MapView)
	 */
	@Override
	public boolean onTap(GeoPoint p, MapView mapView){
		mapView.getProjection().toPixels(p,clickPoint);
	
		//Evaluate if the clicked point is inside the icon (+a given tolerance frame outside)
		if( this.evaluateClick(clickPoint.x, clickPoint.y) ){
			if(poi != null){ //this is a poi icon
				context.onPress(this);
				return true;
			}
		}
		return false;
	}//onTap
	
	/**
	 * Gets the poi.
	 *
	 * @return the poi
	 */
	public Poi getPoi()
	{
		return poi;
	}
	
	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public Bitmap getImage(){
		return bmp;
	}
	
	/**
	 * Sets the image.
	 *
	 * @param b the new image
	 */
	public void setImage(Bitmap b){
		 bmp = b;
	}
	
	/**
	 * Gets the screen pts.
	 *
	 * @return the screen pts
	 */
	public Point getScreenPts()
	{
		return screenPts;
	}
	
	/**
	 * Gets the geo point.
	 *
	 * @return the geo point
	 */
	public GeoPoint getGeoPoint()
	{
		return p;
	}
	
	/**
	 * Evaluate click.
	 *
	 * @param x the x
	 * @param y the y
	 * @return true, if successful
	 */
	public boolean evaluateClick(int x, int y){
		if (bmp==null || clickPoint==null || screenPts==null){
			Log.d("CityExplorer", "OOOps: bmp is "+bmp);
		}else{
			if(		clickPoint.x > screenPts.x-clickTolerance-bmp.getWidth()/2 &&
					clickPoint.x < screenPts.x+clickTolerance+bmp.getWidth()/2 &&
					clickPoint.y > screenPts.y-clickTolerance-bmp.getHeight()/2 &&
					clickPoint.y < screenPts.y+clickTolerance+bmp.getHeight()/2)
			{
				return true;
			}
		}
		return false;
	}//evaluateClick

}//MapIconOverlay

