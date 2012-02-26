package org.opengpx.lib.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opengpx.MapOverlayItem;

import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class OruxMapViewer extends MapViewerBase implements MapViewer 
{

	private static final String	ORUX_VIEW_OFFLINE = "com.oruxmaps.VIEW_MAP_OFFLINE";
	private static final String	ORUX_VIEW_ONLINE = "com.oruxmaps.VIEW_MAP_ONLINE";

	/**
	 * 
	 * @param context
	 */
	public OruxMapViewer(Context context) 
	{
		super(context);
	}

	/**
	 * 
	 * @param doubles
	 * @return
	 */
	public static double[] convertDoubles(List<Double> doubles)
	{
	    double[] ret = new double[doubles.size()];
	    Iterator<Double> iterator = doubles.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().doubleValue();
	    }
	    return ret;
	}
	
	/**
	 * 
	 */
	public void startActivity() 
	{
		
		if (this.mMapOverlayItems.size() > 0)
		{
			final ArrayList<Double> listLatitude = new ArrayList<Double>();
			final ArrayList<Double> listLongitude = new ArrayList<Double>();
			final ArrayList<String> listNames = new ArrayList<String>();

			for (MapOverlayItem moi : this.mMapOverlayItems)
			{
				listLatitude.add(moi.getLatitude());
				listLongitude.add(moi.getLongitude());
				listNames.add(moi.getTitle());
			}

			// Offline map
			// Intent i = new Intent("com.oruxmaps. VIEW_MAP_OFFLINE ");
			// Online map
			final Intent oruxmap = new Intent(ORUX_VIEW_OFFLINE);
			final String[] names = listNames.toArray(new String[listNames.size()]);
			
			// Waypoints
			oruxmap.putExtra("targetLat", convertDoubles(listLatitude)); 
			oruxmap.putExtra("targetLon", convertDoubles(listLongitude)); 
			oruxmap.putExtra("targetName", names);
			// Track points
			/* double[] targetLatPoints = {33.43,8.32,22.24}; 
			double [] targetLonPoints = {33.44,8.35,22.37}; 
			oruxmap.putExtra("targetLatPoints", targetLatPoints); 
			oruxmap.putExtra("targetLonPoints", targetLonPoints); */
		
			// Ignored elements:
			// map_center, title, zoom_level, unit_system
			
			this.mContext.startActivity(oruxmap);
		}
		else
		{
			// no map items
		}
		
	}
}
