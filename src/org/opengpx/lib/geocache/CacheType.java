package org.opengpx.lib.geocache;

/**
 * 
 * @author Martin Preishuber
 *
 */
public enum CacheType
{ 
	Unknown (-1),
	Traditional (2), 
	Multi (3), 
	ProjectAPE (9), 
	Mystery (8), 
	Letterbox (5), 
	Whereigo (1858), 
	Event (6),
	MegaEvent (453), 
	CITOEvent (13), 
	Earthcache (137), 
	GPSAME (1304),
	Virtual (4),
	Webcam (11),
	Locationless (12),
	TenYearsEvent (3653),
	GeocacheCourses (605),
	GroundSpeakHQ (3773),
	GroundSpeakLostAndFound (3774),
	GroundSpeakBlockParty (4738);

	private Integer mId;
	
	/**
	 * 
	 * @param id
	 */
	CacheType(Integer id) 
	{
        this.mId = id;
    }
	
	/**
	 * 
	 * @return
	 */
	public Integer id() { return this.mId; }

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static CacheType getById(Integer id)
	{
		CacheType result = CacheType.Unknown;

		for (CacheType cacheType : CacheType.values())
		{
			if (cacheType.id().equals(id)) result = cacheType;
		}

		return result;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static CacheType parseString(String string)
	{
		// Remove trailing " cache" or " hybrid" fragments
		if (string.contains(" "))
			string = string.substring(0, string.indexOf(" "));
		// Remove trailing "-cache" fragments
		if (string.contains("-"))
			string = string.substring(0, string.indexOf("-"));

		// Replace some opencaching.de / geotoad cache types
		if (string.toLowerCase().equals("multicache"))
			string = "Multi";
		if (string.toLowerCase().equals("wherigo")) // note the missing "e"
			string = "Whereigo";
		if (string.toLowerCase().equals("other"))
			string = "Mystery";
		
		// If no cache type is given, use "Unknown"
		if (string.length() == 0)
			string = "Unknown";
		
		try
		{
			return valueOf(string);
		}
		catch (Exception ex)
		{
			CacheType cacheType = CacheType.Unknown;
			Boolean blnCacheTypeFound = false;
			for (CacheType ct : CacheType.values())
			{
				if (ct.toString().toLowerCase().equals(string.toLowerCase()))
				{
					cacheType = ct;
					blnCacheTypeFound = true;
				}
			}
			if (!blnCacheTypeFound)
			{
				System.out.println("Handle cache type: " + string);
			}
			return cacheType;
		}
	}

}
