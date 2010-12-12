package org.opengpx.lib.geocache;

/**
 * 
 * @author Martin Preishuber
 *
 */
public enum CacheType
{ 
	Unknown, 
	Traditional, 
	Multi, 
	ProjectAPE, 
	Mystery, 
	Letterbox, 
	Whereigo, 
	Event,
	MegaEvent, 
	CITOEvent, 
	Earthcache, 
	GPSAME, 
	Virtual,
	Webcam,
	Locationless,
	TenYearsEvent;
	
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
				System.out.println("Handle cache type: " + string);
			return cacheType;
		}
	}

}
