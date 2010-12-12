package org.opengpx.lib.geocache;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class User 
{
	private Integer mintId = 0;
    private String mstrName  = "";

    /**
     * 
     */
    public User()
    {	
    }
    
    /**
     * 
     * @param name
     * @param id
     */
    public User(String name, Integer id)
    {
    	this.mstrName = name;
    	this.mintId = id;
    }
    
    /**
     * 
     * @return
     */
    public String getName()
    {
    	return this.mstrName;
    }
    
    /**
     * 
     * @param value
     */
    public void setName(String value)
    {
        value = value.replace("&nbsp;", " ");
        value = value.replace("&amp;", "&");
    	this.mstrName = value;
    }
    
    /**
     * 
     * @return
     */
    public Integer getID()
    {
    	return this.mintId;
    }
    
    /**
     * 
     * @param value
     */
    public void setID(Integer value)
    {
    	this.mintId = value;
    }
    
    /**
     * 
     */
    @Override public String toString()
    {
        return String.format("%s (%d)", this.mstrName, this.mintId);
    }
}
