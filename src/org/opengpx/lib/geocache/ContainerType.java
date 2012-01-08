package org.opengpx.lib.geocache;

/**
 * 
 * @author Martin Preishuber
 *
 */
public enum ContainerType
{ 
	Other (6), 
	Micro (2), 
	Small (8), 
	Regular (3), 
	Large (4), 
	Virtual (5), 
	Not_chosen (1), 
	Unknown (-1);

	private Integer mId;
	
	/**
	 * 
	 * @param id
	 */
	ContainerType(Integer id)
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
	public static ContainerType getById(Integer id)
	{
		ContainerType result = ContainerType.Unknown;
		
		for (ContainerType containerType : ContainerType.values())
		{
			if (containerType.id().equals(id)) result = containerType;
		}

		return result;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static ContainerType parseString(String string)
	{
		string = string.replace(" ", "_");
		try
		{
			return valueOf(string);
		}
		catch (Exception ex)
		{
			ContainerType containerType = ContainerType.Unknown;
			Boolean blnContainerTypeFound = false;			
			for (ContainerType ct : ContainerType.values())
			{
				if (ct.toString().toLowerCase().equals(string.toLowerCase()))
				{
					containerType = ct;
					blnContainerTypeFound = true;
				}
			}
			if (!blnContainerTypeFound)
				System.out.println("Handle container type: " + string);
			
			return containerType;
		}
	}

	public String getIconFilename()
	{
		switch (this)
		{
		case Micro:
			return "micro.png";
		case Small:
			return "small.png";
		case Regular:
			return "regular.png";
		case Large:
			return "large.png";
		default:
			return "other.png";
		}
	}
}
