package org.opengpx.lib.map;

import java.util.ArrayList;

import org.opengpx.lib.UnitSystem;

import org.opengpx.lib.geocache.Waypoint;

public interface MapViewer 
{
	public void addCaches(ArrayList<String> cacheCodes);
	public void addWaypoint(Waypoint waypoint);
	public void addWaypoints(ArrayList<Waypoint> waypoints);
	
	public void setCenter(Double latitude, Double longitude, String title);
	public void setFollowCurrentPosition(boolean followCurrentPosition);
	
	public int getZoomLevel();
	public void setZoomLevel(int zoomLevel);
	
	public UnitSystem getUnitSystem();
	public void setUnitSystem(UnitSystem unitSystem);
	
	public void startActivity();
}
