package org.ubicompforall.CityExplorer.map;

import org.ubicompforall.CityExplorer.data.Poi;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

// TODO: Auto-generated Javadoc
/**
 * The Class MapIconOverlay.
 */
class MapIconOverlay extends com.google.android.maps.Overlay
{
	
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
	 * @param possition the possition
	 */
	public MapIconOverlay(Context c, int Icon, GeoPoint possition) 
	{
		this.p = possition;
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
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
    {
    	if(mv == null)
    		mv = mapView;
    	
        super.draw(canvas, mapView, shadow);                   

        //---translate the GeoPoint to screen pixels---
        mapView.getProjection().toPixels(p, screenPts);

        //---add the marker---
        canvas.drawBitmap(bmp, screenPts.x-bmp.getWidth()/2, screenPts.y-bmp.getHeight()/*/2*/, null);
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.google.android.maps.Overlay#onTap(com.google.android.maps.GeoPoint, com.google.android.maps.MapView)
     */
    @Override
    public boolean onTap(GeoPoint p, MapView mapView)
    {

    	mapView.getProjection().toPixels(p,clickPoint);

		//Evaluate if the clicked point is inside the icon (+a given tolerance frame outside)
		if(this.evaluateClick(clickPoint.x, clickPoint.y))
		{

			if(poi != null) //this is a poi icon
			{
				context.onPress(this);
				return true;
			}
		}
		return false;
    }
    
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
    public Bitmap getImage()
	{
		return bmp;
	}
    
    /**
     * Sets the image.
     *
     * @param b the new image
     */
    public void setImage(Bitmap b)
	{
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
    public boolean evaluateClick(int x, int y)
    {
    	if(		clickPoint.x > screenPts.x-clickTolerance-bmp.getWidth()/2 &&
    			clickPoint.x < screenPts.x+clickTolerance+bmp.getWidth()/2 &&
    			clickPoint.y > screenPts.y-clickTolerance-bmp.getHeight()/2 &&
    			clickPoint.y < screenPts.y+clickTolerance+bmp.getHeight()/2)
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    

} 

