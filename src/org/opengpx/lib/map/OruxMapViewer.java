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

	private boolean mUseOfflineMap = true;
	
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
	    final double[] ret = new double[doubles.size()];
	    final Iterator<Double> iterator = doubles.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().doubleValue();
	    }
	    return ret;
	}
	
	/**
	 * 
	 * @param integers
	 * @return
	 */
	public static int[] convertIntegers(List<Integer> integers)
	{
	    final int[] ret = new int[integers.size()];
	    final Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;		
	}
	
	/**
	 * 
	 * @param useOfflineMap
	 */
	public void setUseOfflineMap(boolean useOfflineMap)
	{
		this.mUseOfflineMap = useOfflineMap;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getUseOfflineMap()
	{
		return this.mUseOfflineMap;
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
			final ArrayList<Integer> listSymbols = new ArrayList<Integer>();

			for (final MapOverlayItem moi : this.mMapOverlayItems)
			{
				listLatitude.add(moi.getLatitude());
				listLongitude.add(moi.getLongitude());
				listNames.add(moi.getTitle());
				listSymbols.add(4);
			}

			final String intent = this.mUseOfflineMap ? ORUX_VIEW_OFFLINE : ORUX_VIEW_ONLINE;
			final Intent oruxmap = new Intent(intent);
			final String[] names = listNames.toArray(new String[listNames.size()]);
			
			// Waypoints
			oruxmap.putExtra("targetLat", convertDoubles(listLatitude)); 
			oruxmap.putExtra("targetLon", convertDoubles(listLongitude)); 
			oruxmap.putExtra("targetName", names);
			oruxmap.putExtra("targetType", convertIntegers(listSymbols));

			// Ignored elements:
			// map_center, title, zoom_level, unit_system
			
			// FIXME: check whether intent exists
			
			this.mContext.startActivity(oruxmap);
		}
		else
		{
			// no map items
		}	
	}
}
