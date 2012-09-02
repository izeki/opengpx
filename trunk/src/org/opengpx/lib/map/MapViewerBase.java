package org.opengpx.lib.map;

import java.util.ArrayList;

import org.opengpx.lib.UnitSystem;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.CacheIndexItem;
import org.opengpx.lib.CoordinateFormat;
import org.opengpx.lib.Coordinates;
import org.opengpx.lib.geocache.CacheType;
import org.opengpx.lib.geocache.Waypoint;
import org.opengpx.lib.map.MapOverlayItem.MapOverlayItemType;
import org.opengpx.lib.tools.AndroidSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Martin Preishuber
 *
 */
class MapViewerBase
{
	private static final int CACHE_IMAGE_WIDTH = 24;
	private static final int CACHE_IMAGE_HEIGHT = 24;
	private static final int WAYPOINT_IMAGE_WIDTH = 20;
	private static final int WAYPOINT_IMAGE_HEIGHT = 20;
	
	protected Context mContext;
	
	protected int mintZoomLevel = 14;
	protected MapOverlayItem mOverlayItemCenter = null;
	protected ArrayList<MapOverlayItem> mMapOverlayItems = new ArrayList<MapOverlayItem>();
	protected UnitSystem mUnitSystem = UnitSystem.Metric;

	private final Logger mLogger = LoggerFactory.getLogger(MapViewerBase.class);

	/**
	 * 
	 */
	protected MapViewerBase(Context context)
	{
		this.mContext = context;
	}

	/**
	 * 
	 */
	public void addCaches(ArrayList<String> cacheCodes) 
	{
		final CacheDatabase cacheDatabase = CacheDatabase.getInstance();
		boolean blnFirstItem = true;
		boolean blnUseSearchResults = false;
		
		for (final String strCacheCode : cacheCodes)
		{
			// Find out, which database we use for getting cache index items
			if (blnFirstItem)
			{
				if (cacheDatabase.getCacheIndexItem(strCacheCode) == null)
					blnUseSearchResults = true;
				blnFirstItem = false;
			}

			CacheIndexItem cii;
			if (blnUseSearchResults)
			{
				cii = cacheDatabase.getSearchCacheIndexItem(strCacheCode);
				if (cii == null)
					cii = cacheDatabase.getCacheIndexItem(strCacheCode);
			}
			else
			{
				cii = cacheDatabase.getCacheIndexItem(strCacheCode);
				if (cii == null)
					cii = cacheDatabase.getSearchCacheIndexItem(strCacheCode);
			}
			
			if (cii != null)
			{
				final String strSnippet = String.format("%s\n%s / %s [D%.1f,T%.1f]", cii.name, cii.type, cii.container, cii.difficulty, cii.terrain);
				final MapOverlayItem item = new MapOverlayItem(MapOverlayItemType.Geocache, cii.latitude, cii.longitude, cii.name, strSnippet);
				item.GeocacheID = cii.code;
				item.CacheType = CacheType.parseString(cii.type);
				item.setDrawable(cii.type, CACHE_IMAGE_WIDTH, CACHE_IMAGE_HEIGHT);
				this.mMapOverlayItems.add(item);
			}
		}		
	}

	/**
	 * 
	 */
	public void addWaypoint(Waypoint waypoint) 
	{
		final ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		waypoints.add(waypoint);
		this.addWaypoints(waypoints);
	}

	/**
	 * 
	 */
	public void addWaypoints(ArrayList<Waypoint> waypoints) 
	{
		for (Waypoint wp : waypoints)
		{
			final MapOverlayItem mapOverlayItem = new MapOverlayItem(MapOverlayItemType.Waypoint, wp.latitude, wp.longitude, wp.symbol, wp.getSnippet());
			mapOverlayItem.WaypointType = wp.getType();
			mapOverlayItem.setDrawable(wp.getType().toString().toLowerCase(), WAYPOINT_IMAGE_WIDTH, WAYPOINT_IMAGE_HEIGHT);
			this.mMapOverlayItems.add(mapOverlayItem);
		}
	}

	/**
	 * 
	 * @param intent
	 * @param humanReadableName
	 */
	protected void startActivity(final Intent intent, final String humanReadableName)
	{
		final String action = intent.getAction();
		this.mLogger.debug("Mapviewer intent action: " + action);
		final boolean intentAvailable = AndroidSystem.isIntentAvailable(this.mContext, action);
		if (intentAvailable)
		{
			this.mContext.startActivity(intent);
		}
		else
		{
			Toast.makeText(this.mContext, humanReadableName + " is not installed", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 
	 */
	public int getZoomLevel() 
	{
		return this.mintZoomLevel;
	}

	/**
	 * 
	 */
	public void setCenter(final Double latitude, final Double longitude, final String title) 
	{
		final Coordinates coords = new Coordinates(latitude, longitude);
		// final String strMessage = String.format("Map Center: %s (%s)", title, coords.toString(CoordinateFormat.DM));
		// Log.d(TAG, strMessage);
		// Toast.makeText(this.mContext, strMessage, Toast.LENGTH_LONG).show();
		// FIXME: change MapOverlayItemType
		this.mOverlayItemCenter = new MapOverlayItem(MapOverlayItemType.Unknown, latitude, longitude, "Map Center", coords.toString(CoordinateFormat.DM));
	}

	/**
	 * 
	 */
	public void setZoomLevel(int zoomLevel) 
	{
		this.mintZoomLevel = zoomLevel;
	}

	/**
	 * 
	 * @return
	 */
	public UnitSystem getUnitSystem()
	{
		return this.mUnitSystem;
	}

	/**
	 * 
	 * @param unitSystem
	 */
	public void setUnitSystem(UnitSystem unitSystem)
	{
		this.mUnitSystem = unitSystem;
	}
	
	
}
