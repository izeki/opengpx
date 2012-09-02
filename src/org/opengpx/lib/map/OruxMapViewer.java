package org.opengpx.lib.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opengpx.lib.geocache.WaypointType;
import org.opengpx.lib.map.MapOverlayItem.MapOverlayItemType;

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
	 * @author Martin Preishuber
	 *
	 */
	private enum OruxMapIcon
	{
		Waypoint (1),
        Geocache (2),
        Photo (3),
        Airport (4),
        Bar (5),
        Beach (6),
        Bridge (7),
        Campground (8),
        Car (9),
        City (10),
        Crossing (11),
        Dam (12),
        DangerArea (13),
        DrinkingWater (14),
        FinishingPoint (15),
        FishingArea (16),
        Forest (17),
        GasStation (18),
        GliderArea (19),
        Golf (20),
        Heliport (21),
        Hotel (22),
        HuntingArea (23),
        Information (24),
        Marina (25),
        Mine (26),
        ParachuteArea (27),
        Park (28),
        ParkingArea (29),
        PicnicArea (30),
        Residence (31),
        Restaurant (32),
        Restroom (33),
        ScenicArea (34),
        School (35),
        ShoppingCenter (36),
        Shower (37),
        StartingPoint (38),
        SkiingArea (39),
        Summit (40),
        SwimmingArea (41),
        Telephone (42),
        Tunnel (43),
        UltralightArea (44),
        Person (45),
        Dog (46),
        Dot (47);
       
		private final int mIntegerValue;   

		OruxMapIcon(int integerValue) 
		{
			this.mIntegerValue = integerValue;
		}

		public int integerValue() 
		{ 
			return this.mIntegerValue; 
		}
	}
		
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
	private static double[] convertDoubles(List<Double> doubles)
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
	private static int[] convertIntegers(List<Integer> integers)
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
	 * @param waypointType
	 * @return
	 */
	private int getOruxMapIcon(final WaypointType waypointType)
	{
		int oruxMapIcon = OruxMapIcon.Waypoint.integerValue();
		switch (waypointType)
		{
		case Cache:
		case Final:
			oruxMapIcon = OruxMapIcon.Geocache.integerValue();
			break;
		case Parking: 
			oruxMapIcon = OruxMapIcon.ParkingArea.integerValue();
			break;
		case Trailhead: 
			oruxMapIcon = OruxMapIcon.StartingPoint.integerValue();
			break;
		case Question:
		case Stages:
		case Extracted: 
		case Log:
		case UserDefined: 
		case Reference: 
		default:
			oruxMapIcon = OruxMapIcon.Waypoint.integerValue();
			break;
		}
		return oruxMapIcon;
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
				if (moi.getMapOverlayItemType().equals(MapOverlayItemType.Geocache))
					listSymbols.add(OruxMapIcon.Geocache.integerValue());
				else
					listSymbols.add(this.getOruxMapIcon(moi.WaypointType));					
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
			
			this.startActivity(oruxmap, "OruxMaps");
		}
	}
}
