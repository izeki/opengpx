package org.opengpx.lib.geocache;

/**
 * 
 * @author Martin Preishuber
 *
 */
public enum LogType 
{ 
	Unknown, 
	Found_it,
	Enable_Listing, 
	Temporarily_Disable_Listing, 
	Write_note, 
	Didnt_find_it, 
	Owner_Maintenance,
	Needs_Maintenance,
	Publish_Listing,
	Update_Coordinates,
	Archive,
	Attended,
	Will_Attend,
	Retract_Listing,
	Webcam_Photo_Taken,
	Needs_Archived,
	Cache_Disabled;
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	public static LogType parseString(String text)
	{
		// Map opencaching log entries to geocaching ones
		if (text.equals("Found"))
			text = "Found it";
		else if (text.equals("Note"))
			text = "Write note";
		else if (text.equals("Not Found"))
			text = "Didn't find it";
		else if (text.equals("Other"))
			text = "Attended"; // opencaching "Other" = "Attended" for event caches

		// Replace some characters
		text = text.replace(" ", "_");
		text = text.replace("'", "");
		text = text.replace("!", "");

		try
		{
			return valueOf(text);
		}
		catch (Exception ex)
		{
			LogType logType = LogType.Unknown;
			Boolean blnCacheTypeFound = false;
			for (LogType lt : LogType.values())
			{
				if (lt.toString().toLowerCase().equals(text.toLowerCase()))
				{
					logType = lt;
					blnCacheTypeFound = true;
				}
			}
			if (!blnCacheTypeFound)
				System.out.println("Handle log type: " + text);
			return logType;
		}

	}
	
}
