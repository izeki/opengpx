package org.opengpx.lib.map;

import java.io.Serializable;

import org.opengpx.lib.ResourceHelper;
import org.opengpx.lib.geocache.CacheType;
import org.opengpx.lib.geocache.WaypointType;

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
	 * @author Martin Preishuber
	 *
	 */
	public enum MapOverlayItemType
	{
		Unknown,
		Geocache,
		Waypoint
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8102549452609012590L;

	private int mintLatitude;
	private int mintLongitude;
	
	private String mstrDrawableId = "";
	private int mintDrawableWidth = 0;
	private int mintDrawableHeight = 0;

	private MapOverlayItemType mMapOverlayItemType = MapOverlayItemType.Unknown;
	private String mTitle = "";			// Cache title or waypoint name
	private String mSnippet = "";		// Cache type, difficulty, terrain or waypoint type

	// Geocache properties
	public String GeocacheID;
	// public int GeocacheType;
	public CacheType CacheType = org.opengpx.lib.geocache.CacheType.Unknown;
	// public String Owner;
	// public String PlacedBy;
	// public String ShortDescription;
	// public String LongDescription;
	
	// Waypoint properties
	public WaypointType WaypointType = org.opengpx.lib.geocache.WaypointType.Unknown;
	
	/**
	 * 
	 * @param type
	 */
	public MapOverlayItem(MapOverlayItemType type)
	{
		this.mMapOverlayItemType = type;
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param snippet
	 */
	public MapOverlayItem(MapOverlayItemType type, double latitude, double longitude, String title, String snippet)
	{
		this.mMapOverlayItemType = type;
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
	public MapOverlayItem(MapOverlayItemType type, int latitude, int longitude, String title, String snippet)
	{
		this.mMapOverlayItemType = type;
		this.mTitle = title;
		this.mSnippet = snippet;
		this.mintLatitude = latitude;
		this.mintLongitude = longitude;		
	}

	/**
	 * 
	 * @return
	 */
	public MapOverlayItemType getMapOverlayItemType()
	{
		return this.mMapOverlayItemType;
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
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.mTitle = title;
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
	 * @param value
	 */
	public void setSnippet(String snippet)
	{
		this.mSnippet = snippet;
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
