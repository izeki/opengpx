package org.opengpx.lib.geocache;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class Waypoint 
{

	public double latitude;
	public double longitude;
	public Date time;
	public String name = "";
	public String description = "";
	public String symbol = "";
	public String source = "";
	public int elevation = Integer.MIN_VALUE;
	public String comment = "";

	private WaypointType mWaypointType = WaypointType.Unknown;
	// private String mstrURL = "";
	// private String mstrURLName = "";

	/**
	 * 
	 */
	public Waypoint()
	{		
		final Calendar cal1970 = Calendar.getInstance();
		cal1970.set(1970, 1, 1);
		this.time = cal1970.getTime();
	}

	/**
	 * 
	 * @param strText
	 */
	public void parseTypeString(String strText)
	{
		// Log.d(TAG, "Parsing type string: " + strText);
		
		/* Geocaching.com cache types are in the form
		 * 		Geocache|Multi-cache
		 * 		Waypoint|Question to Answer
		 * 		Waypoint|Stages of a Multicache
		 * Other pages / bcaching.com results do not contain the | separator,
		 * so make sure that the parsing functionality does work with both variants
		 */
		
		final String[] arrSplitted = strText.split("\\|");
		if (arrSplitted[0].toLowerCase().equals("geocache"))
		{
			this.mWaypointType = WaypointType.Cache;
		}
		else
		{
			String strCacheType;
			if (arrSplitted.length > 1)
				strCacheType = arrSplitted[1];
			else
				strCacheType = arrSplitted[0];
				
			String[] strFirstWord = strCacheType.split(" ");
			this.mWaypointType = WaypointType.valueOf(strFirstWord[0]);
		}
		// Log.d(TAG, "Waypoint type: " + this.mWaypointType.toString());
	}

	/**
	 * 
	 * @return
	 */
	public WaypointType getType()
	{
		return this.mWaypointType;
	}
	
	/**
	 * 
	 * @param waypointType
	 */
	public void setType(WaypointType waypointType)
	{
		this.mWaypointType = waypointType;
	}

	/**
	 * Create a human readable representation for maps et al.
	 * @return
	 */
	public String getSnippet()
	{
		if (mWaypointType == WaypointType.Cache)
		{
			final String symbolLC = symbol.toLowerCase();
			if (symbolLC.equals("unknown cache") || symbolLC.equals("event cache"))
				return String.format("Header Coordinates [%s]", symbol);
			else if (symbolLC.equals("traditional cache"))
				return String.format("Cache [%s]", symbol);
			else if (symbolLC.equals("cache"))
				return "Cache";
			else
			{
				/* if (description.length() == 0)
				{
					return symbol;
				} else { */
					final String descriptionLC = description.toLowerCase();
					if (descriptionLC.contains("traditional cache"))
						return String.format("Cache [%s]", symbol);
					else
					{
						return String.format("Header Coordinates [%s]", symbol);
						// return String.format("%s (%s) [%s,%s]", description, symbol, name, mWaypointType.toString());
					}
				// }
			}
		} 
		else 
		{
			return String.format("%s [%s]", description, symbol);			
		}
	}
	
	/**
	 * Returns a readable interpretation of the waypoint
	 */
	@Override public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Name: %s\n", this.name));
		// sb.append(String.format("Coordinates: %s\n", this.mCoordinates));
		sb.append(String.format("Description: %s\n", this.description));
		// sb.append(String.format("URL: %s\n", this.mstrURL));
		sb.append(String.format("Type: %s\n", this.mWaypointType));
		// sb.append(String.format("Source: %s\n", this.source));
		sb.append(String.format("Lat / Long: %.10f / %.10f\n", this.latitude, this.longitude));
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Waypoint wp = new Waypoint();
		wp.parseTypeString("Parking Area");
		System.out.println(wp.getType().toString());
		wp.parseTypeString("Stages of a Multicache");
		System.out.println(wp.getType().toString());
	}

}
