package org.opengpx.lib.geocache;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class TravelBug
{

	public String name = "";
	public String reference = "";

	// private int mintID = Integer.MIN_VALUE;

	/**
	 * 
	 */
	public TravelBug()
	{
	}

	/**
	 * 
	 * @return
	 */
	/* public String getName()
	{
		return this.name;
	} */

	/**
	 * 
	 * @param name
	 */
	/* public void setName(String name)
	{
		this.name = name;
	} */
	
	/**
	 * 
	 * @param id
	 */
	/* public void setId(Integer id)
	{
		this.mintID = id;
	} */
	
	/**
	 * 
	 * @return
	 */
	/* public int getId()
	{
		return this.mintID;
	} */
	
	/**
	 * 
	 * @return
	 */
	/* public String getReference()
	{
		return this.reference;
	} */
	
	/**
	 * 
	 * @param reference
	 * @return
	 */
	/* public void setReference(String reference)
	{
		this.reference = reference;
	} */

	public String toHtmlString()
	{
		if (this.reference.length() > 0)
			return String.format("%s [<a href=\"http://coord.info/%s\">%s</a>]", this.name, this.reference, this.reference);
		else
			return this.name;
	}	
	
	/**
	 * 
	 */
	@Override 
	public String toString()
	{
		if (this.reference.length() > 0)
			return String.format("%s [%s]", this.name, this.reference);
		else
			return this.name;
	}
}
