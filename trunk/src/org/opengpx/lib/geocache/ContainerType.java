package org.opengpx.lib.geocache;

/**
 * 
 * @author Martin Preishuber
 *
 */
public enum ContainerType
{ 
	Other, 
	Micro, 
	Small, 
	Regular, 
	Large, 
	Virtual, 
	Not_chosen, 
	Unknown;

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
}
