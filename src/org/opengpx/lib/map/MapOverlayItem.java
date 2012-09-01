package org.opengpx.lib.map;

import java.io.Serializable;

import org.opengpx.lib.ResourceHelper;

import android.graphics.drawable.Drawable;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class MapOverlayItem implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8102549452609012590L;

	private int mintLatitude;
	private int mintLongitude;
	
	private String mstrDrawableId = "";
	private int mintDrawableWidth = 0;
	private int mintDrawableHeight = 0;

	final private String mTitle;	// Cache title or waypoint name
	final private String mSnippet;	// Cache type, difficulty, terrain or waypoint type

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param snippet
	 */
	public MapOverlayItem(double latitude, double longitude, String title, String snippet)
	{
		this.mTitle = title;
		this.mSnippet = snippet;
		this.mintLatitude = ((Double) (latitude * 1E6)).intValue();
		this.mintLongitude = ((Double) (longitude * 1E6)).intValue();
	}

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param snippet
	 */
	public MapOverlayItem(int latitude, int longitude, String title, String snippet)
	{
		this.mTitle = title;
		this.mSnippet = snippet;
		this.mintLatitude = latitude;
		this.mintLongitude = longitude;		
	}

	/**
	 * 
	 * @return
	 */
	public int getLatitudeE6()
	{
		return this.mintLatitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getLatitude()
	{
		return (this.mintLatitude / 1E6);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLongitudeE6()
	{
		return this.mintLongitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getLongitude()
	{
		return (this.mintLongitude / 1E6);
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle()
	{
		return this.mTitle;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSnippet()
	{
		return this.mSnippet;
	}
	
	/**
	 * 
	 * @param id
	 * @param width
	 * @param height
	 */
	public void setDrawable(String id, int width, int height)
	{
		this.mstrDrawableId = id;
		this.mintDrawableWidth = width;
		this.mintDrawableHeight = height;		
	}

	/**
	 * 
	 * @param resourceHelper
	 * @return
	 */
	public Drawable getDrawable(ResourceHelper resourceHelper)
	{
		return this.getDrawable(resourceHelper, true);
	}

	/**
	 * 
	 * @param resourceHelper
	 * @param scaleImage
	 * @return
	 */
	public Drawable getDrawable(ResourceHelper resourceHelper, boolean scaleImage)
	{
		if (this.mstrDrawableId.length() > 0)
			return resourceHelper.getDrawable(this.mstrDrawableId, this.mintDrawableWidth, this.mintDrawableHeight);
		else
			return null;
	}
}
