package org.opengpx.lib.geocache;

/**
 * 
 * @author Martin Preishuber
 *
 */
public enum LogType 
{ 
	Unknown (-1),
	
	Unarchive (1),
	Found_it (2),
	Didnt_find_it (3), 
	Write_note (4), 
	Archive (5),
	// Archive (6),
	Needs_Archived (7),
	Mark_Destroyed (8),
	Will_Attend (9),
	Attended (10),
	Webcam_Photo_Taken (11),
	// Unarchive (12),
	Retrieve_It_from_a_Cache (13),
	Dropped_Off (14),
	Transfer (15),
	Mark_Missing (16),
	Recovered (17),
	Post_Reviewer_Note (18),
	Grab_It_Not_from_a_Cache (19),
	Write_Jeep_4x4_Contest_Essay (20),
	Upload_Jeep_4x4_Contest_Photo (21),
	Temporarily_Disable_Listing (22), 
	Enable_Listing (23), 
	Publish_Listing (24),
	Retract_Listing (25),
	Uploaded_Goal_Photo_for_A_True_Original (30),
	Uploaded_Goal_Photo_for_Yellow_Jeep_Wrangler (31),
	Uploaded_Goal_Photo_for_Construction_Site (32),
	Uploaded_Goal_Photo_for_State_Symbol (33),
	Uploaded_Goal_Photo_for_American_Flag (34),
	Uploaded_Goal_Photo_for_Landmark_Memorial (35),
	Uploaded_Goal_Photo_for_Camping (36),
	Uploaded_Goal_Photo_for_Peaks_and_Valleys (37),
	Uploaded_Goal_Photo_for_Hiking (38),
	Uploaded_Goal_Photo_for_Ground_Clearance (39),
	Uploaded_Goal_Photo_for_Water_Fording (40),
	Uploaded_Goal_Photo_for_Traction (41),
	Uploaded_Goal_Photo_for_Tow_Package (42),
	Uploaded_Goal_Photo_for_Ultimate_Makeover (43),
	Uploaded_Goal_Photo_for_Paint_Job (44),
	Needs_Maintenance (45),
	Owner_Maintenance (46),
	Update_Coordinates (47),
	Discovered_It (48),
	Uploaded_Goal_Photo_for_Discovery (49),
	Uploaded_Goal_Photo_for_Freedom (50),
	Uploaded_Goal_Photo_for_Adventure (51),
	Uploaded_Goal_Photo_for_Camaraderie (52),
	Uploaded_Goal_Photo_for_Heritage (53),
	Reviewer_Note (54),
	Lock_User_Ban (55),
	Unlock_User_Unban (56),
	Groundspeak_Note (57),
	Uploaded_Goal_Photo_for_Fun (58),
	Uploaded_Goal_Photo_for_Fitness (59),
	Uploaded_Goal_Photo_for_Fighting_Diabetes (60),
	Uploaded_Goal_Photo_for_American_Heritage (61),
	Uploaded_Goal_Photo_for_No_Boundaries (62),
	Uploaded_Goal_Photo_for_Only_in_a_Jeep (63),
	Uploaded_Goal_Photo_for_Discover_New_Places (64),
	Uploaded_Goal_Photo_for_Definition_of_Freedom (65),
	Uploaded_Goal_Photo_for_Adventure_Starts_Here (66),
	Needs_Attention (67),
	// Post_Reviewer_Note (68),
	Move_To_Collection (69),
	Move_To_Inventory (70),
	Throttle_User (71),
	Enter_CAPTCHA (72),
	Change_Username (73),
	Announcement (74),
	Visited (75),
	
	Cache_Disabled (9999);

	private Integer mId;
	
	/**
	 * 
	 * @param id
	 */
	LogType(Integer id) 
	{
        this.mId = id;
    }
	
	/**
	 * 
	 * @return
	 */
	public Integer id() { return this.mId; }

	public static LogType getById(Integer id)
	{
		LogType result = LogType.Unknown;

		// Handle duplicate IDs
		if (id.equals(6)) id = 5;
		if (id.equals(12)) id = 1;
		if (id.equals(68)) id = 18;
		
		for (LogType logType : LogType.values())
		{
			if (logType.id().equals(id)) result = logType;
		}

		return result;
	}

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
