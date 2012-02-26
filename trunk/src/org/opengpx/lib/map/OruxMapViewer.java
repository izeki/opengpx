package org.opengpx.lib.map;

import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class OruxMapViewer extends MapViewerBase implements MapViewer 
{

	/**
	 * 
	 * @param context
	 */
	protected OruxMapViewer(Context context) 
	{
		super(context);
	}

	/**
	 * 
	 */
	public void startActivity() 
	{
		// Offline map
		//Intent i = new Intent("com.oruxmaps. VIEW_MAP_OFFLINE ");
		// Online map
		Intent i = new Intent("com.oruxmaps.VIEW_MAP_ONLINE");
		// Waypoints
		double[] targetLat = {33.4,8.3,22.2};
		double [] targetLon = {33.4,8.3,22.3};
		String [] targetNames = {"point alpha","point beta"}; 
		i.putExtra("targetLat", targetLat); 
		i.putExtra("targetLon", targetLon); 
		i.putExtra("targetName", targetNames);
		// Track points
		double[] targetLatPoints = {33.43,8.32,22.24}; 
		double [] targetLonPoints = {33.44,8.35,22.37}; 
		i.putExtra("targetLatPoints", targetLatPoints); 
		i.putExtra("targetLonPoints", targetLonPoints);
		
		this.mContext.startActivity(i);
	}

}
