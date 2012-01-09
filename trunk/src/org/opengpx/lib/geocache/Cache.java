package org.opengpx.lib.geocache;

import java.util.ArrayList;

import org.opengpx.lib.Coordinates;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class Cache
{
	// Note: these are public for performance reasons
	public String code;
	public int id;
	public boolean isAvailable;
	public boolean isArchived;
	public String name;
	public String placedBy;
	public String owner;
	public String ownerId;
	public Double difficulty;
	public Double terrain;
	public Integer favoritePoints = -1;
	public String country;
	public String state;
	public boolean shortDescriptionIsHtml;
	public String shortDescription = "";
	public boolean longDescriptionIsHtml;
	public String longDescription = ""; 
	public String hint = "";
	public boolean isBcachingSummary = false;

	// Opencaching.de specific tags (ignored)
	// public String locale;
	// public String licence;
	
	private ArrayList<Waypoint> mWaypoints;
	private CacheType mCacheType;
	private ContainerType mContainerType;
	private ArrayList<LogEntry> mLogEntries;
	private ArrayList<TravelBug> mTravelBugs;
	private ArrayList<Attribute> mAttributes;

	/**
	 * 
	 */
	public Cache()
	{
		this.mContainerType = ContainerType.Unknown;
		this.country = "Unknown";
		this.state = "Unknown";
		this.mLogEntries = new ArrayList<LogEntry>();
	}

	/**
	 * Read the Opencaching.de status text.
	 * @param strText
	 */
	public void parseOpencachingStatus(String strText)
	{
		if (strText.toLowerCase().equals("available"))
			this.isAvailable = true;
	}

	/**
	 * Converts the type string into an element of the CacheType enumeration.
	 * @param strText
	 */
	public void parseCacheTypeString(String type)
	{
    	this.mCacheType = CacheType.parseString(type);		
	}
	
	/**
	 * 
	 * @param type
	 */
	public void setType(CacheType type)
	{
		this.mCacheType = type;
	}
	
	/**
	 * 
	 * @param strContainer
	 */
	public void parseContainerTypeString(String strContainer)
	{
	    this.mContainerType = ContainerType.parseString(strContainer);
	}

	/**
	 * 
	 * @param containerType
	 */
	public void setContainerType(ContainerType containerType)
	{
		this.mContainerType = containerType;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Waypoint> getWaypoints()
	{
		return this.mWaypoints;
	}

	/**
	 * 
	 * @param waypoint
	 */
	public Boolean addWaypoint(Waypoint waypoint)
	{
		return this.addWaypoint(waypoint, true);
	}

	/**
	 * 
	 * @param waypoint
	 * @param checkForDuplicates
	 * @return
	 */
	public Boolean addWaypoint(Waypoint waypoint, Boolean checkForDuplicates)
	{
		if (this.mWaypoints == null)
			this.mWaypoints = new ArrayList<Waypoint>();

		Boolean blnWaypointFound = false;
		if (checkForDuplicates)
		{
			// Coordinates coordinates = waypoint.getCoordinates();
			final Coordinates coordinates = new Coordinates(waypoint.latitude, waypoint.longitude);
			for (Waypoint wpExisting : this.mWaypoints)
			{
				final Coordinates coordsExistingWaypoint = new Coordinates(wpExisting.latitude, wpExisting.longitude);
				if (coordsExistingWaypoint.equals(coordinates))
					blnWaypointFound = true;
			}
		}
		if (!blnWaypointFound)
		{
			this.mWaypoints.add(waypoint);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Waypoint getHeaderWaypoint()
	{
		for (Waypoint wp : this.mWaypoints)
		{
			if (wp.name.equals(this.code))
			{
				return wp;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	// public Logs getLogs()
	public ArrayList<LogEntry> getLogEntries()
	{
		return this.mLogEntries;
	}

	/**
	 * 
	 * @param logEntry
	 */
	public void addLogEntry(LogEntry logEntry)
	{
		this.mLogEntries.add(logEntry);
	}
	
	/**
	 * 
	 * @param travelBug
	 */
	public void addTravelBug(TravelBug travelBug)
	{
		if (this.mTravelBugs == null)
			this.mTravelBugs = new ArrayList<TravelBug>();
		this.mTravelBugs.add(travelBug);
	}
	
	/**
	 * 
	 * @param attribute
	 */
	public void addAttribute(Attribute attribute)
	{
		if (this.mAttributes == null)
			this.mAttributes = new ArrayList<Attribute>();
		this.mAttributes.add(attribute);
	}
	
	/**
	 * 
	 * @return
	 */
	public CacheType getCacheType()
	{
		return this.mCacheType;
	}
	
	/**
	 * 
	 * @return
	 */
	public ContainerType getContainerType()
	{
		return this.mContainerType;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<TravelBug> getTravelBugs()
	{
		return this.mTravelBugs;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Attribute> getAttributes()
	{
		return this.mAttributes;
	}
	
	/**
	 * 
	 */
	@Override public String toString()
	{
		StringBuilder sb = new StringBuilder();
		// sb.append(String.format("ID: %s\n", this.id));
		sb.append(String.format("Name: %s\n", this.name));
		sb.append(String.format("Code: %s\n", this.code));
		sb.append(String.format("Container: %s\n", this.mContainerType.toString()));
		sb.append(String.format("Difficulty: %.1f\n", this.difficulty));
		sb.append(String.format("Terrain: %.1f\n", this.terrain));
		sb.append(String.format("Placed by: %s\n", this.placedBy));
		sb.append(String.format("Archived: %s\n", this.isArchived));
		sb.append(String.format("Owner: %s\n", this.owner));
		sb.append(String.format("Waypoints:\n%s\n", this.mWaypoints));
		sb.append(String.format("Attributes:\n%s\n", this.mAttributes));
		sb.append(String.format("Short description: %s\n", this.shortDescription));
		sb.append(String.format("Long Description: %s\n", this.longDescription));
		return sb.toString();
	}
}
